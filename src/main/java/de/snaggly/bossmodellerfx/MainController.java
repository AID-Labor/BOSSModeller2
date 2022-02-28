package de.snaggly.bossmodellerfx;

import de.snaggly.bossmodellerfx.model.adapter.DBLAHolder;
import de.snaggly.bossmodellerfx.model.subdata.Attribute;
import de.snaggly.bossmodellerfx.model.subdata.AttributeCombination;
import de.snaggly.bossmodellerfx.model.subdata.Relation;
import de.snaggly.bossmodellerfx.model.view.Comment;
import de.snaggly.bossmodellerfx.model.view.Entity;
import de.snaggly.bossmodellerfx.relation_logic.ForeignKeyHandler;
import de.snaggly.bossmodellerfx.relation_logic.RelationLineDrawer;
import de.snaggly.bossmodellerfx.view.*;
import de.snaggly.bossmodellerfx.guiLogic.GUIMethods;
import de.snaggly.bossmodellerfx.guiLogic.Project;
import de.snaggly.bossmodellerfx.view.factory.nodetype.CommentBuilder;
import de.snaggly.bossmodellerfx.view.factory.nodetype.EntityBuilder;
import de.snaggly.bossmodellerfx.view.factory.windowtype.*;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.Window;

import javax.imageio.ImageIO;
import java.io.*;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.LinkedList;

import static de.snaggly.bossmodellerfx.guiLogic.KeyCombos.keyComboOpen;
import static de.snaggly.bossmodellerfx.guiLogic.KeyCombos.keyComboSave;

/**
 * Controller for Main Window
 *
 * @author Omar Emshani
 */
public class MainController {
    @FXML
    private Accordion leftNavigationAccordion;
    @FXML
    private TabPane projectsTabPane;

    private Project currentProject;
    private DBLAHolder previousDBLA = null;

    private final HashMap<Entity, EntityView> entitiesOverview = new HashMap<>();
    private final HashMap<Relation, RelationViewNode> relationsOverview = new HashMap<>();

    private final ArrayList<Window> subWindows = new ArrayList<>();

    private final ContextMenu mainWorkbenchContextMenu = new ContextMenu();

    private void relationLineDrawer(Project project) { //For future: Follow State-Pattern
        RelationLineDrawer.drawAllLines(project, entitiesOverview, relationsOverview);
    }

    @FXML
    private void showAboutUsWindow() {
        try {
            var fxmlLoader = new FXMLLoader(Main.class.getResource("view/AboutUs.fxml"), BOSS_Strings.resourceBundle);
            var scene = new Scene(fxmlLoader.load());
            var stage = new Stage();
            stage.setTitle(BOSS_Strings.ABOUT_US);
            stage.setScene(scene);
            stage.show();
            addSubWindow(scene.getWindow());
        } catch (IOException e) {
            GUIMethods.showError(MainController.class.getSimpleName(), BOSS_Strings.PRODUCT_NAME, e.getLocalizedMessage());
        }
    }

    @FXML
    private void initialize() {
        leftNavigationAccordion.setExpandedPane(leftNavigationAccordion.getPanes().get(0));
        projectsTabPane.getSelectionModel().selectedIndexProperty().addListener((_obj, _old, _new) -> {
            currentProject = Project.getProject(_new.intValue());
            initializeContextMenu(currentProject.getWorkField());
        });
        var newTabMenu = new MenuItem(BOSS_Strings.NEW_PROJECT);
        newTabMenu.setOnAction(actionEvent -> addNewProjectTab(new WorkbenchPane(this::onMainWorkbenchClick), false));
        projectsTabPane.setOnContextMenuRequested(contextMenuEvent -> {
            var target = contextMenuEvent.getTarget();
            if (target instanceof TabPane || target instanceof StackPane) {
                var tabContextMenu = new ContextMenu(newTabMenu);
                assert target instanceof Pane;
                tabContextMenu.show(((Pane) target), contextMenuEvent.getScreenX(), contextMenuEvent.getScreenY());
            }
        });
        addNewProjectTab(new WorkbenchPane(this::onMainWorkbenchClick), true);
    }

    private void initializeContextMenu(Pane workBench) {
        workBench.setOnContextMenuRequested(contextMenuEvent -> {
            mainWorkbenchContextMenu.getItems().clear();
            var currentSelection = currentProject.getCurrentSelected();

            if (currentSelection instanceof EntityView){
                var entityView = (EntityView)(currentProject.getCurrentSelected());
                var editEntityMenu = new MenuItem(BOSS_Strings.EDIT_ENTITY);
                editEntityMenu.setOnAction(actionEvent -> editEntity(entityView));
                var removeEntityMenu = new MenuItem(BOSS_Strings.DELETE_ENTITY);
                removeEntityMenu.setOnAction(actionEvent -> deleteEntity(entityView));
                var separator = new SeparatorMenuItem();
                mainWorkbenchContextMenu.getItems().addAll(editEntityMenu, removeEntityMenu, separator);
            } else if (currentSelection instanceof CommentView) {
                var commentView = (CommentView)(currentProject.getCurrentSelected());
                var editCommentMenu = new MenuItem(BOSS_Strings.EDIT_COMMENT);
                editCommentMenu.setOnAction(actionEvent -> editComment(commentView));
                var removeCommentMenu = new MenuItem(BOSS_Strings.DELETE_COMMENT);
                removeCommentMenu.setOnAction(actionEvent -> deleteComment(commentView));
                var separator = new SeparatorMenuItem();
                mainWorkbenchContextMenu.getItems().addAll(editCommentMenu, removeCommentMenu, separator);
            } else if (currentSelection instanceof RelationViewNode) {
                var relationView = (RelationViewNode)(currentProject.getCurrentSelected());
                var editRelationMenu = new MenuItem(BOSS_Strings.EDIT_RELATION);
                editRelationMenu.setOnAction(actionEvent -> editRelation(relationView));
                var removeRelation = new MenuItem(BOSS_Strings.DELETE_RELATION);
                removeRelation.setOnAction(actionEvent -> deleteRelation(relationView));
                var separator = new SeparatorMenuItem();
                mainWorkbenchContextMenu.getItems().addAll(editRelationMenu, removeRelation, separator);
            }

            if (currentSelection instanceof EntityView || currentSelection instanceof CommentView) {
                var editCommentMenu = new MenuItem(BOSS_Strings.MOVE_TO_FRONT);
                editCommentMenu.setOnAction(actionEvent -> currentSelection.toFront());
                var removeCommentMenu = new MenuItem(BOSS_Strings.MOVE_TO_BACK);
                removeCommentMenu.setOnAction(actionEvent -> currentSelection.toBack());
                var separator = new SeparatorMenuItem();
                mainWorkbenchContextMenu.getItems().addAll(editCommentMenu, removeCommentMenu, separator);
            }

            var newEntityMenu = new MenuItem(BOSS_Strings.NEW_ENTITY);
            newEntityMenu.setOnAction(actionEvent -> createNewEntity(
                    contextMenuEvent.getX(),
                    contextMenuEvent.getY()
            ));

            var newRelationMenu = new MenuItem(BOSS_Strings.NEW_RELATION);
            newRelationMenu.setOnAction(actionEvent -> createNewRelation());

            var newCommentMenu = new MenuItem(BOSS_Strings.NEW_COMMENT);
            newCommentMenu.setOnAction(actionEvent -> createNewComment(
                    contextMenuEvent.getX(),
                    contextMenuEvent.getY()
            ));
            mainWorkbenchContextMenu.getItems().addAll(newEntityMenu, newCommentMenu, newRelationMenu);
            mainWorkbenchContextMenu.show(
                    currentProject.getWorkField(),
                    contextMenuEvent.getScreenX(),
                    contextMenuEvent.getScreenY()
            );
        });
    }

    @FXML
    private void onMainWorkbenchClick(MouseEvent mouseEvent) {
        if (mouseEvent.getTarget() == currentProject.getWorkField()) {
            if (currentProject.getCurrentSelected() instanceof CommentView) {
                ((CommentView) currentProject.getCurrentSelected()).getController().disableEdit();
            }

            currentProject.setCurrentSelected(currentProject.getWorkField());
        }

        if (MouseButton.PRIMARY == mouseEvent.getButton() && mainWorkbenchContextMenu.isShowing()) {
            mainWorkbenchContextMenu.hide();
        }
    }

    @FXML
    public void closeApp() {
        for (var window : subWindows) {
            ((Stage)window).close();
        }
        System.exit(0);
    }

    @FXML
    private void newEntityClick() {
        createNewEntity(10.0, 10.0);
    }

    @FXML
    private void editEntityClick() {
        var currentSelection = currentProject.getCurrentSelected();
        if (!(currentSelection instanceof EntityView)){
            GUIMethods.showWarning(MainController.class.getSimpleName(), BOSS_Strings.PRODUCT_NAME, BOSS_Strings.NO_ENTITY_SELECTED);
            return;
        }
        editEntity((EntityView)currentSelection);
    }

    @FXML
    private void deleteEntityClick() {
        var currentSelection = currentProject.getCurrentSelected();
        if (!(currentProject.getCurrentSelected() instanceof EntityView)){
            GUIMethods.showWarning(MainController.class.getSimpleName(), BOSS_Strings.PRODUCT_NAME, BOSS_Strings.NO_ENTITY_SELECTED);
            return;
        }
        deleteEntity((EntityView) currentSelection);
    }

    @FXML
    private void newCommentClick() {
        createNewComment(10, 10);
    }

    @FXML
    private void editCommentClick() {
        var currentSelection = currentProject.getCurrentSelected();
        if (!(currentProject.getCurrentSelected() instanceof CommentView)){
            GUIMethods.showWarning(MainController.class.getSimpleName(), BOSS_Strings.PRODUCT_NAME, BOSS_Strings.NO_COMMENT_SELECTED);
            return;
        }
        editComment((CommentView) currentSelection);
    }

    @FXML
    private void deleteCommentClick() {
        var currentSelection = currentProject.getCurrentSelected();
        if (!(currentProject.getCurrentSelected() instanceof CommentView)){
            GUIMethods.showWarning(MainController.class.getSimpleName(), BOSS_Strings.PRODUCT_NAME, BOSS_Strings.NO_COMMENT_SELECTED);
            return;
        }
        deleteComment((CommentView) currentSelection);
    }

    @FXML
    private void newRelationClick() {
        createNewRelation();
    }

    @FXML
    private void editRelationClick() {
        var currentSelection = currentProject.getCurrentSelected();
        if (!(currentProject.getCurrentSelected() instanceof RelationViewNode)){
            GUIMethods.showWarning(MainController.class.getSimpleName(), BOSS_Strings.PRODUCT_NAME, BOSS_Strings.NO_RELATION_SELECTED);
            return;
        }
        editRelation((RelationViewNode) currentSelection);
    }

    @FXML
    private void deleteRelationClick() {
        var currentSelection = currentProject.getCurrentSelected();
        if (!(currentProject.getCurrentSelected() instanceof RelationViewNode)){
            GUIMethods.showWarning(MainController.class.getSimpleName(), BOSS_Strings.PRODUCT_NAME, BOSS_Strings.NO_RELATION_SELECTED);
            return;
        }
        deleteRelation((RelationViewNode) currentSelection);
    }

    private void createNewEntity(double xCoordinate, double yCoordinate) {
        try {
            var entityBuilder = EntityEditorWindowBuilder.buildEntityEditor(null);
            var stage = new Stage();
            stage.setTitle(BOSS_Strings.NEW_ENTITY);
            stage.setScene(entityBuilder.getKey());
            stage.show();
            addSubWindow(entityBuilder.getKey().getWindow());
            entityBuilder.getValue().parentObserver = resultedEntity -> {
                try {
                    resultedEntity.setXCoordinate(xCoordinate);
                    resultedEntity.setYCoordinate(yCoordinate);
                    var entityView = EntityBuilder.buildEntity(resultedEntity, currentProject.getWorkField(), currentProject.getSelectionHandler);
                    saveNewEntity(entityView);
                } catch (IOException e) {
                    GUIMethods.showError(MainController.class.getSimpleName(), BOSS_Strings.PRODUCT_NAME, e.getLocalizedMessage());
                }
            };
        }
        catch (Exception e) {
            e.printStackTrace();
            GUIMethods.showError(MainController.class.getSimpleName(), BOSS_Strings.PRODUCT_NAME, e.getLocalizedMessage());
        }
    }

    private void editEntity(EntityView selectedEntityView) {
        try {
            var selectedEntity = selectedEntityView.getModel();
            var entityBuilder = EntityEditorWindowBuilder.buildEntityEditor(selectedEntity);
            var stage = new Stage();
            stage.setTitle(BOSS_Strings.EDIT_ENTITY);
            stage.setScene(entityBuilder.getKey());
            stage.show();
            addSubWindow(entityBuilder.getKey().getWindow());
            entityBuilder.getValue().parentObserver = resultedEntity -> {
                selectedEntityView.getController().loadModel(resultedEntity);
                showNewRelation(currentProject);
            };
        } catch (Exception e) {
            GUIMethods.showError(MainController.class.getSimpleName(), BOSS_Strings.PRODUCT_NAME, e.getLocalizedMessage());
        }
    }

    private void createNewComment(double xCoordinate, double yCoordinate) {
        try {
            var commentModel = new Comment(BOSS_Strings.DEFAULT_COMMENT_STRING, xCoordinate, yCoordinate);
            var commentView = CommentBuilder.buildComment(commentModel, currentProject.getWorkField(), currentProject.getSelectionHandler);
            showNewComment(commentView, currentProject);

            currentProject.addComment(commentModel);
        } catch (IOException e) {
            GUIMethods.showError(MainController.class.getSimpleName(), BOSS_Strings.PRODUCT_NAME, e.getLocalizedMessage());
        }
    }

    private void showNewComment(CommentView commentView, Project project) {
        project.getWorkField().getChildren().add(commentView);
        commentView.toBack();
    }

    private void editComment(CommentView commentView) {
        commentView.getController().enableEdit();
    }

    private void createNewRelation() {
        if (currentProject.getEntities().size() < 1) {
            GUIMethods.showWarning(MainController.class.getSimpleName(), BOSS_Strings.PRODUCT_NAME, BOSS_Strings.NO_ENTITIES_WARNING);
            return;
        }
        try {
            var relationBuilderWindow = RelationEditorWindowBuilder.buildRelationEditor(null);
            relationBuilderWindow.getValue().parentObserver = this::saveNewRelation;
            var stage = new Stage();
            stage.setTitle(BOSS_Strings.NEW_RELATION);
            stage.setScene(relationBuilderWindow.getKey());
            stage.show();
            addSubWindow(relationBuilderWindow.getKey().getWindow());
        } catch (IOException e) {
            e.printStackTrace();
            GUIMethods.showError(MainController.class.getSimpleName(), BOSS_Strings.PRODUCT_NAME, e.getLocalizedMessage());
        }
    }


    private void deleteComment(CommentView selectedCommentView) {
        var selectedComment = selectedCommentView.getModel();
        currentProject.getWorkField().getChildren().remove(selectedCommentView);
        currentProject.removeComment(selectedComment);
    }

    private void saveNewEntity(EntityView entityView) {
        currentProject.addEntity(entityView.getModel());
        showNewEntity(entityView, currentProject);
    }

    private void showNewEntity(EntityView entityView, Project project) {
        entitiesOverview.put(entityView.getModel(), entityView);
        GUIMethods.bindEntityToRelationLineHandler(entityView, () -> relationLineDrawer(currentProject));
        project.getWorkField().getChildren().add(entityView);
    }

    private void deleteEntity(EntityView selectedEntityView) {
        var selectedEntity = selectedEntityView.getModel();
        for (var relatedRelation : selectedEntity.getInvolvedRelations(currentProject.getRelations())) {
            deleteRelation(relationsOverview.get(relatedRelation));
        }

        entitiesOverview.remove(selectedEntity);
        currentProject.getWorkField().getChildren().remove(selectedEntityView);
        currentProject.removeEntity(selectedEntity);
    }

    private void saveNewRelation(Relation dataset) {
        currentProject.addRelation(dataset);
        showNewRelation(currentProject);
    }

    private void showNewRelation(Project project) {
        //Reload all relations to get the FKs more properly set at strong connections
        for (var projRelation : project.getRelations()) {
            Entity prefFkTable = null;
            if (projRelation.getFkAttributesA().size() > 0) {
                prefFkTable = projRelation.getTableA();
            } else {
                prefFkTable = projRelation.getTableB();
            }

            var removedKeysList = ForeignKeyHandler.removeAllForeignKeys(projRelation);
            ForeignKeyHandler.addForeignKeys(projRelation, prefFkTable);

            //Adapt the new Objects on UniqueList to prevent ghosts
            ForeignKeyHandler.readjustForeignKeysInUniqueLists(projRelation.getTableA(), removedKeysList, projRelation.getFkAttributesA());
            ForeignKeyHandler.readjustForeignKeysInUniqueLists(projRelation.getTableB(), removedKeysList, projRelation.getFkAttributesB());


            var tableAView = entitiesOverview.get(projRelation.getTableA());
            var tableBView = entitiesOverview.get(projRelation.getTableB());
            tableAView.getController().loadModel(projRelation.getTableA());
            if (tableAView != tableBView) {
                tableBView.getController().loadModel(projRelation.getTableB());
            }
        }

        relationLineDrawer(project);
    }

    private void deleteRelation(RelationViewNode relationView) {
        currentProject.getWorkField().getChildren().removeAll(relationView.getAllNodes());

        var relation = relationView.getModel();
        var modifiedEntities = new LinkedList<Entity>();
        clearForeignKeys(relation.getFkAttributesA(), modifiedEntities);
        clearForeignKeys(relation.getFkAttributesB(), modifiedEntities);

        ForeignKeyHandler.setWeakType(relation);

        //Redraw the modified entities
        for (var modifiedEntity : modifiedEntities) {
            var entityView = entitiesOverview.get(modifiedEntity);
            entityView.getController().loadModel(modifiedEntity);
        }

        relationsOverview.remove(relation);
        currentProject.getRelations().remove(relationView.getModel());
        relationLineDrawer(currentProject);
    }

    /**
     * Iteratively find all the columns in Project that also reference the same FK
     * @param foreignKeys ForeignKeys to clear from all entities in current Project
     * @param resultedModifiedEntities Adds all modified entities, to keep track of. Used to redraw after removal of foreign keys.
     */
    private void clearForeignKeys(LinkedList<Attribute> foreignKeys, LinkedList<Entity> resultedModifiedEntities) {
        if (foreignKeys.size() <= 0)
            return;
        for (var entity : currentProject.getEntities()) { //Repeat for other table
            var itemsToRemove = new LinkedList<Attribute>();
            for (var attribute : entity.getAttributes()) {
                var fk = attribute;
                while (fk != null && !foreignKeys.contains(fk)) {
                    fk = fk.getFkTableColumn();
                }
                if (fk != null) {
                    itemsToRemove.add(attribute);
                    resultedModifiedEntities.add(entity);
                }
            }
            entity.getAttributes().removeAll(itemsToRemove);

            //There might be ghosts left in UniqueLists
            var emptyAttrCombo = new LinkedList<AttributeCombination>();
            for (var uniqueCombo : entity.getUniqueCombination().getCombinations()) {
                uniqueCombo.getAttributes().removeAll(foreignKeys);
                if (uniqueCombo.getAttributes().size() < 1) {
                    emptyAttrCombo.add(uniqueCombo);
                }
            }
            entity.getUniqueCombination().getCombinations().removeAll(emptyAttrCombo);
        }
    }

    private void editRelation(RelationViewNode relationView) {
        var selectedRelationModel = relationView.getModel();

        try {
            var relationBuilderWindow = RelationEditorWindowBuilder.buildRelationEditor(selectedRelationModel);
            relationBuilderWindow.getValue().parentObserver = (resultedRelation) -> showNewRelation(currentProject);
            var stage = new Stage();
            stage.setTitle(BOSS_Strings.EDIT_RELATION);
            stage.setScene(relationBuilderWindow.getKey());
            stage.show();
            addSubWindow(relationBuilderWindow.getKey().getWindow());
        } catch (IOException e) {
            GUIMethods.showError(MainController.class.getSimpleName(), BOSS_Strings.PRODUCT_NAME, e.getLocalizedMessage());
        }
    }

    private void reInitProject() throws IOException {
        initProject(currentProject);
    }

    private void initProject(Project project) throws IOException {
        for (var entity : project.getEntities()) {
            var entityView = EntityBuilder.buildEntity(entity, project.getWorkField(), project.getSelectionHandler);
            showNewEntity(entityView, project);
        }

        for (var comment : project.getComments()) {
            var commentView = CommentBuilder.buildComment(comment, project.getWorkField(), project.getSelectionHandler);
            showNewComment(commentView, project);
        }

        showNewRelation(project);
    }

    private void clearProject() {
        currentProject.clear();
        relationsOverview.clear();
        entitiesOverview.clear();
    }

    @FXML
    private void openFileClick() {
        var file = GUIMethods.showJSONFileOpenDialog(BOSS_Strings.OPEN_PROJECT, currentProject.getWorkField().getScene().getWindow());
        if (file == null)
            return;
        try {
            var bufferedReader = new BufferedReader(new FileReader(file));
            StringBuilder json = new StringBuilder();
            while (bufferedReader.ready()) {
                json.append(bufferedReader.readLine());
            }
            bufferedReader.close();
            var newProject = Project.deserializeFromJson(json.toString(), new WorkbenchPane(this::onMainWorkbenchClick));
            addNewProjectTab(file.getName(), newProject, subWindows.size() == 0);
            newProject.activeFile = file;
            initProject(newProject);

        } catch (Exception e) {
            GUIMethods.showError(MainController.class.getSimpleName(), BOSS_Strings.PRODUCT_NAME, e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void saveFileClick() {
        if (currentProject.activeFile == null) {
            saveUnderFileClick();
        }
        else {
            saveFile(currentProject.activeFile);
        }
    }

    @FXML
    private void saveUnderFileClick() {
        var file = GUIMethods.showJSONFileSaveDialog(BOSS_Strings.SAVE_PROJECT, currentProject.getWorkField().getScene().getWindow());
        if (file == null)
            return;
        currentProject.activeFile = file;
        saveFile(file);
    }

    private void saveFile(File file) {
        var json = currentProject.serializeToJson();
        try {
            var fileWriter = new BufferedWriter(new FileWriter(file));
            fileWriter.write(json);
            fileWriter.close();
        } catch (Exception e) {
            GUIMethods.showError(MainController.class.getSimpleName(), BOSS_Strings.PRODUCT_NAME, e.getLocalizedMessage());
        } finally {
            projectsTabPane.getTabs().get(projectsTabPane.getSelectionModel().getSelectedIndex()).setText(file.getName());
        }
    }

    @FXML
    private void exportPictureClick() {
        var snapshot = currentProject.getWorkField().snapshot(new SnapshotParameters(), null);
        var file = GUIMethods.showPNGFileSaveDialog(BOSS_Strings.EXPORT_TO_PICTURE, currentProject.getWorkField().getScene().getWindow());
        if (file == null)
            return;
        try {
            ImageIO.write(SwingFXUtils.fromFXImage(snapshot, null), "png", file);
        }
        catch (Exception e) {
            GUIMethods.showError(MainController.class.getSimpleName(), BOSS_Strings.PRODUCT_NAME, e.getLocalizedMessage());
        }
    }

    @FXML
    private void onKeyPressed(KeyEvent keyEvent) {
        currentProject.addPressedKey(keyEvent.getCode());

        if (currentProject.getPressedKeys().containsAll(keyComboOpen)) {
            currentProject.getPressedKeys().clear();
            openFileClick();
        }
        else if (currentProject.getPressedKeys().containsAll(keyComboSave)) {
            currentProject.getPressedKeys().clear();
            saveFileClick();
        }
    }

    @FXML
    private void onKeyReleased(KeyEvent keyEvent) {
        currentProject.removePressedKey(keyEvent.getCode());
    }

    @FXML
    private void startNewProject() {
        addNewProjectTab(new WorkbenchPane(this::onMainWorkbenchClick), subWindows.size() == 0);
    }

    private void addNewProjectTab(WorkbenchPane workPane, boolean switchTo) {
        var newProject = Project.createNewProject(workPane);
        addNewProjectTab(BOSS_Strings.DEFAULT_NEWPROJECT_NAME, newProject, switchTo);
    }

    private void addNewProjectTab(String tabName, Project newProject, boolean switchTo) {
        var workbench = newProject.getWorkFieldWrapper();
        var newTab = new Tab(tabName, workbench);

        projectsTabPane.getTabs().add(newTab);

        var tabTooltip = new Tooltip();
        tabTooltip.textProperty().bindBidirectional(newTab.textProperty());
        newTab.setTooltip(tabTooltip);

        var renameMenu = new MenuItem(BOSS_Strings.RENAME);
        renameMenu.setOnAction(actionEvent -> {
            var textInputDialog = new TextInputDialog();
            textInputDialog.setResizable(true);
            textInputDialog.setContentText(BOSS_Strings.ENTER_NEW_PROJECTNAME);
            textInputDialog.setTitle(BOSS_Strings.PRODUCT_NAME);
            textInputDialog.setHeaderText(BOSS_Strings.PROJECT_NAME);
            textInputDialog.showAndWait().ifPresent(newTab::setText);
        });

        var closeMenu = new MenuItem(BOSS_Strings.CLOSE);
        closeMenu.setOnAction(actionEvent -> {
            if (Project.getProjectsAmount() <= 1) {
                GUIMethods.showWarning(MainController.class.getSimpleName(), BOSS_Strings.PRODUCT_NAME, BOSS_Strings.LAST_TAB_WARNING);
                return;
            }

            Project.removeProject(newProject);
            projectsTabPane.getTabs().remove(newTab);
        });
        newTab.setContextMenu(new ContextMenu(renameMenu, closeMenu));

        if (switchTo) {
            projectsTabPane.getSelectionModel().selectLast();
        }
    }

    private void addSubWindow(Window window) {
        projectsTabPane.setDisable(true);
        subWindows.add(window);
        window.setOnCloseRequest(windowEvent -> {
            subWindows.remove(window);
            projectsTabPane.setDisable(subWindows.size() > 0);
        });
    }

    @FXML
    private void importFromDBClick(ActionEvent actionEvent) {
        try {
            var window = ConnectToDBWindowBuilder.buildDBConnectorWindow(previousDBLA);
            var connectWindowStage = new Stage();
            window.getValue().parentObserver = resultedConnection -> {
                previousDBLA = resultedConnection;
                GUIMethods.closeWindow(connectWindowStage);
                try {
                    var chooserWindow = ChooseDBEntityWindowBuilder.buildDBChooserWindow(resultedConnection);
                    var chooseWindowStage = new Stage();
                    chooserWindow.getValue().parentObserver = resultedProjectData -> {
                        GUIMethods.closeWindow(chooseWindowStage);
                        var newProject = Project.createNewProject(new WorkbenchPane(this::onMainWorkbenchClick));
                        for (var entity : resultedProjectData.entities) {
                            newProject.addEntity(entity);
                        }
                        for (var relation : resultedProjectData.relations) {
                            newProject.addRelation(relation);
                        }
                        addNewProjectTab(
                                resultedProjectData.projectName.equals("") ? BOSS_Strings.IMPORTED_PROJECT : resultedProjectData.projectName,
                                newProject,
                                subWindows.size() == 0);
                        try {
                            initProject(newProject);
                        } catch (IOException e) {
                            GUIMethods.showError(MainController.class.getSimpleName(), BOSS_Strings.PRODUCT_NAME, e.getLocalizedMessage());
                        }
                    };
                    chooseWindowStage.setScene(chooserWindow.getKey());
                    chooseWindowStage.setTitle(BOSS_Strings.CHOOSE_TABLES);
                    chooseWindowStage.show();
                    addSubWindow(chooseWindowStage);
                } catch (IOException e) {
                    GUIMethods.showError(MainController.class.getSimpleName(), BOSS_Strings.PRODUCT_NAME, e.getLocalizedMessage());
                }
            };
            connectWindowStage.setScene(window.getKey());
            connectWindowStage.setTitle(BOSS_Strings.CONNECT_TO_DATABASE);
            connectWindowStage.show();
            addSubWindow(connectWindowStage);
        } catch (IOException e) {
            GUIMethods.showError(MainController.class.getSimpleName(), BOSS_Strings.PRODUCT_NAME, e.getLocalizedMessage());
        }
    }

    @FXML
    private void exportToDBClick(ActionEvent actionEvent) {
        try {
            var window = ConnectToDBWindowBuilder.buildDBConnectorWindow(previousDBLA);
            var connectWindowStage = new Stage();
            window.getValue().prepForExport(true);
            window.getValue().parentObserver = resultedConnection -> {
                previousDBLA = resultedConnection;
                GUIMethods.closeWindow(connectWindowStage);
                try {
                    var exportWindow = ChooseDBExportWindowBuilder.buildDBChooserWindow(resultedConnection);
                    var exportWindowStage = new Stage();
                    exportWindowStage.setScene(exportWindow.getKey());
                    exportWindowStage.setTitle(BOSS_Strings.CHOOSE_DATABASE_AND_SCHEMA);
                    exportWindowStage.show();
                    addSubWindow(exportWindowStage);
                } catch (IOException e) {
                    GUIMethods.showError(MainController.class.getSimpleName(), BOSS_Strings.PRODUCT_NAME, e.getLocalizedMessage());
                }
            };
            connectWindowStage.setScene(window.getKey());
            connectWindowStage.setTitle(BOSS_Strings.CONNECT_TO_DATABASE);
            connectWindowStage.show();
            addSubWindow(connectWindowStage);
        } catch (IOException e) {
            GUIMethods.showError(MainController.class.getSimpleName(), BOSS_Strings.PRODUCT_NAME, e.getLocalizedMessage());
        }
    }
}
