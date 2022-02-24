package de.snaggly.bossmodellerfx;

import de.snaggly.bossmodellerfx.model.adapter.DBLAHolder;
import de.snaggly.bossmodellerfx.model.subdata.Attribute;
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
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableNumberValue;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
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
import java.util.concurrent.atomic.AtomicReference;

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
            var fxmlLoader = new FXMLLoader(Main.class.getResource("view/AboutUs.fxml"));
            var scene = new Scene(fxmlLoader.load());
            var stage = new Stage();
            stage.setTitle("Über uns");
            stage.setScene(scene);
            stage.show();
            addSubWindow(scene.getWindow());
        } catch (IOException e) {
            GUIMethods.showError(MainController.class.getSimpleName(), "BOSSModellerFX", e.getLocalizedMessage());
        }
    }

    @FXML
    private void initialize() {
        leftNavigationAccordion.setExpandedPane(leftNavigationAccordion.getPanes().get(0));
        projectsTabPane.getSelectionModel().selectedIndexProperty().addListener((_obj, _old, _new) -> {
            currentProject = Project.getProject(_new.intValue());
            initializeContextMenu(currentProject.getWorkField());
        });
        var newTabMenu = new MenuItem("Neues Projekt");
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

    private void initializeContextMenu(WorkbenchPane workBench) {
        workBench.setOnContextMenuRequested(contextMenuEvent -> {
            mainWorkbenchContextMenu.getItems().clear();
            var currentSelection = currentProject.getCurrentSelected();

            if (currentSelection instanceof EntityView){
                var entityView = (EntityView)(currentProject.getCurrentSelected());
                var editEntityMenu = new MenuItem("Entität bearbeiten");
                editEntityMenu.setOnAction(actionEvent -> editEntity(entityView));
                var removeEntityMenu = new MenuItem("Entität löschen");
                removeEntityMenu.setOnAction(actionEvent -> deleteEntity(entityView));
                var separator = new SeparatorMenuItem();
                mainWorkbenchContextMenu.getItems().addAll(editEntityMenu, removeEntityMenu, separator);
            } else if (currentSelection instanceof CommentView) {
                var commentView = (CommentView)(currentProject.getCurrentSelected());
                var editCommentMenu = new MenuItem("Kommentar bearbeiten");
                editCommentMenu.setOnAction(actionEvent -> editComment(commentView));
                var removeCommentMenu = new MenuItem("Kommentar löschen");
                removeCommentMenu.setOnAction(actionEvent -> deleteComment(commentView));
                var separator = new SeparatorMenuItem();
                mainWorkbenchContextMenu.getItems().addAll(editCommentMenu, removeCommentMenu, separator);
            } else if (currentSelection instanceof RelationViewNode) {
                var relationView = (RelationViewNode)(currentProject.getCurrentSelected());
                var editRelationMenu = new MenuItem("Relation bearbeiten");
                editRelationMenu.setOnAction(actionEvent -> editRelation(relationView));
                var removeRelation = new MenuItem("Relation löschen");
                removeRelation.setOnAction(actionEvent -> deleteRelation(relationView));
                var separator = new SeparatorMenuItem();
                mainWorkbenchContextMenu.getItems().addAll(editRelationMenu, removeRelation, separator);
            }

            if (currentSelection instanceof EntityView || currentSelection instanceof CommentView) {
                var editCommentMenu = new MenuItem("Element vor rücken");
                editCommentMenu.setOnAction(actionEvent -> currentSelection.toFront());
                var removeCommentMenu = new MenuItem("Element zu rücken");
                removeCommentMenu.setOnAction(actionEvent -> currentSelection.toBack());
                var separator = new SeparatorMenuItem();
                mainWorkbenchContextMenu.getItems().addAll(editCommentMenu, removeCommentMenu, separator);
            }

            var newEntityMenu = new MenuItem("Neue Entität");
            newEntityMenu.setOnAction(actionEvent -> createNewEntity(
                    ((MenuItem)(actionEvent.getSource())).getParentPopup().getX() - (currentProject.getWorkField().getScene().getWindow().getX() + currentProject.getWorkField().getLayoutX()),
                    ((MenuItem)(actionEvent.getSource())).getParentPopup().getY() - (currentProject.getWorkField().getScene().getWindow().getY() + currentProject.getWorkField().getLayoutY())
            ));

            var newRelationMenu = new MenuItem("Neue Relation");
            newRelationMenu.setOnAction(actionEvent -> createNewRelation());

            var newCommentMenu = new MenuItem("Neues Kommentar");
            newCommentMenu.setOnAction(actionEvent -> createNewComment(
                    ((MenuItem)(actionEvent.getSource())).getParentPopup().getX() - (currentProject.getWorkField().getScene().getWindow().getX() + currentProject.getWorkField().getLayoutX()),
                    ((MenuItem)(actionEvent.getSource())).getParentPopup().getY() - (currentProject.getWorkField().getScene().getWindow().getY() + currentProject.getWorkField().getLayoutY())
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
            GUIMethods.showWarning(MainController.class.getSimpleName(), "BOSSModellerFX", "Keine Entität ausgewählt!");
            return;
        }
        editEntity((EntityView)currentSelection);
    }

    @FXML
    private void deleteEntityClick() {
        var currentSelection = currentProject.getCurrentSelected();
        if (!(currentProject.getCurrentSelected() instanceof EntityView)){
            GUIMethods.showWarning(MainController.class.getSimpleName(), "BOSSModellerFX", "Keine Entität ausgewählt!");
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
            GUIMethods.showWarning(MainController.class.getSimpleName(), "BOSSModellerFX", "Kein Kommentar ausgewählt!");
            return;
        }
        editComment((CommentView) currentSelection);
    }

    @FXML
    private void deleteCommentClick() {
        var currentSelection = currentProject.getCurrentSelected();
        if (!(currentProject.getCurrentSelected() instanceof CommentView)){
            GUIMethods.showWarning(MainController.class.getSimpleName(), "BOSSModellerFX", "Kein Kommentar ausgewählt!");
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
            GUIMethods.showWarning(MainController.class.getSimpleName(), "BOSSModeller FX", "Keine Relation ausgewählt!");
            return;
        }
        editRelation((RelationViewNode) currentSelection);
    }

    @FXML
    private void deleteRelationClick() {
        var currentSelection = currentProject.getCurrentSelected();
        if (!(currentProject.getCurrentSelected() instanceof RelationViewNode)){
            GUIMethods.showWarning(MainController.class.getSimpleName(), "BOSSModeller FX", "Keine Relation ausgewählt!");
            return;
        }
        deleteRelation((RelationViewNode) currentSelection);
    }

    private void createNewEntity(double xCoordinate, double yCoordinate) {
        try {
            var entityBuilder = EntityEditorWindowBuilder.buildEntityEditor(null);
            var stage = new Stage();
            stage.setTitle("Neue Entität");
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
                    GUIMethods.showError(MainController.class.getSimpleName(), "BOSSModellerFX", e.getLocalizedMessage());
                }
            };
        }
        catch (Exception e) {
            e.printStackTrace();
            GUIMethods.showError(MainController.class.getSimpleName(), "BOSSModellerFX", e.getLocalizedMessage());
        }
    }

    private void editEntity(EntityView selectedEntityView) {
        try {
            var selectedEntity = selectedEntityView.getModel();
            var entityBuilder = EntityEditorWindowBuilder.buildEntityEditor(selectedEntity);
            var stage = new Stage();
            stage.setTitle("Entität bearbeiten");
            stage.setScene(entityBuilder.getKey());
            stage.show();
            addSubWindow(entityBuilder.getKey().getWindow());
            entityBuilder.getValue().parentObserver = resultedEntity -> selectedEntityView.getController().loadModel(resultedEntity);
        } catch (Exception e) {
            GUIMethods.showError(MainController.class.getSimpleName(), "BOSSModellerFX", e.getLocalizedMessage());
        }
    }

    private void createNewComment(double xCoordinate, double yCoordinate) {
        try {
            var commentModel = new Comment("", xCoordinate, yCoordinate);
            var commentView = CommentBuilder.buildComment(commentModel, currentProject.getWorkField(), currentProject.getSelectionHandler);
            showNewComment(commentView, currentProject);

            currentProject.addComment(commentModel);
        } catch (IOException e) {
            GUIMethods.showError(MainController.class.getSimpleName(), "BOSSModellerFX", e.getLocalizedMessage());
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
            GUIMethods.showWarning(MainController.class.getSimpleName(), "BOSSModeller FX", "Es muss mindestens eine Entität existieren!");
            return;
        }
        try {
            var relationBuilderWindow = RelationEditorWindowBuilder.buildRelationEditor(null);
            relationBuilderWindow.getValue().parentObserver = this::saveNewRelation;
            var stage = new Stage();
            stage.setTitle("Neue Relation");
            stage.setScene(relationBuilderWindow.getKey());
            stage.show();
            addSubWindow(relationBuilderWindow.getKey().getWindow());
        } catch (IOException e) {
            GUIMethods.showError(MainController.class.getSimpleName(), "BOSSModellerFX", e.getLocalizedMessage());
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
        showNewRelation(dataset, currentProject);
    }

    private void showNewRelation(Relation relation, Project project) {
        //Reload all relations to get the FKs more properly set at strong connections
        for (var profRelation : project.getRelations()) {
            ForeignKeyHandler.removeAllForeignKeys(profRelation);
            ForeignKeyHandler.addForeignKeys(profRelation);

            var tableAView = entitiesOverview.get(profRelation.getTableA());
            var tableBView = entitiesOverview.get(profRelation.getTableB());
            tableAView.getController().loadModel(profRelation.getTableA());
            if (tableAView != tableBView) {
                tableBView.getController().loadModel(profRelation.getTableB());
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
        }
    }

    private void editRelation(RelationViewNode relationView) {
        var selectedRelationModel = relationView.getModel();

        try {
            var relationBuilderWindow = RelationEditorWindowBuilder.buildRelationEditor(selectedRelationModel);
            relationBuilderWindow.getValue().parentObserver = (resultedRelation) -> showNewRelation(resultedRelation, currentProject);
            var stage = new Stage();
            stage.setTitle("Relation bearbeiten");
            stage.setScene(relationBuilderWindow.getKey());
            stage.show();
            addSubWindow(relationBuilderWindow.getKey().getWindow());
        } catch (IOException e) {
            GUIMethods.showError(MainController.class.getSimpleName(), "BOSSModellerFX", e.getLocalizedMessage());
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

        for (var relation : project.getRelations()) {
            showNewRelation(relation, project);
        }
    }

    private void clearProject() {
        currentProject.clear();
        relationsOverview.clear();
        entitiesOverview.clear();
    }

    @FXML
    private void openFileClick() {
        var file = GUIMethods.showJSONFileOpenDialog("Projekt öffnen", currentProject.getWorkField().getScene().getWindow());
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
            GUIMethods.showError(MainController.class.getSimpleName(), "BOSSModellerFX", e.getLocalizedMessage());
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
        var file = GUIMethods.showJSONFileSaveDialog("Projekt speichern", currentProject.getWorkField().getScene().getWindow());
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
            GUIMethods.showError(MainController.class.getSimpleName(), "BOSSModellerFX", e.getLocalizedMessage());
        } finally {
            projectsTabPane.getTabs().get(projectsTabPane.getSelectionModel().getSelectedIndex()).setText(file.getName());
        }
    }

    @FXML
    private void exportPictureClick() {
        var snapshot = currentProject.getWorkField().snapshot(new SnapshotParameters(), null);
        var file = GUIMethods.showPNGFileSaveDialog("Bild exportieren", currentProject.getWorkField().getScene().getWindow());
        if (file == null)
            return;
        try {
            ImageIO.write(SwingFXUtils.fromFXImage(snapshot, null), "png", file);
        }
        catch (Exception e) {
            GUIMethods.showError(MainController.class.getSimpleName(), "BOSSModellerFX", e.getLocalizedMessage());
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
        addNewProjectTab("*Neues Projekt", newProject, switchTo);
    }

    private void addNewProjectTab(String tabName, Project newProject, boolean switchTo) {
        var workbench = newProject.getWorkField();
        //var scrollPane = new ScrollPane(workbench); ScrollPane implementation yet does not work.
        var newTab = new Tab(tabName, workbench);

        //Automatically increase Workbench size when resizing. Keep size when shrinking.
        //TODO YET TO IMPLEMENT PROPERLY!
        //TODO When dragging Entity over edge, also increase bench size
        /*scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        var initialTabTotalWidth = new AtomicReference<>(0);
        var initialTabTotalHeight = new AtomicReference<>(0);
        //Get initial Tab size when stage has been drawn
        projectsTabPane.widthProperty().addListener(new ChangeListener<>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                initialTabTotalWidth.set(t1.intValue());
                projectsTabPane.widthProperty().removeListener(this);
            }
        });
        projectsTabPane.heightProperty().addListener(new ChangeListener<>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                initialTabTotalHeight.set(t1.intValue());
                projectsTabPane.widthProperty().removeListener(this);
            }
        });

        //Get delta of bench to bad size
        workbench.widthProperty().addListener(new ChangeListener<>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                workbench.widthProperty().removeListener(this);

                var delta = initialTabTotalWidth.get() - t1.intValue();
                final int[] previousVal = {0};
                scrollPane.setFitToWidth(false);
                //When resizing window -> Tab gets resized -> explicitly delegate Bench resize
                projectsTabPane.widthProperty().addListener((observableValue1, number1, t11) -> {
                    var newVal = t11.intValue() - delta;
                    if (newVal > previousVal[0]) {
                        previousVal[0] = newVal;
                        workbench.setPrefWidth(newVal);
                    }
                });
            }
        });
        workbench.heightProperty().addListener(new ChangeListener<>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                workbench.heightProperty().removeListener(this);

                var delta = initialTabTotalHeight.get() - t1.intValue();
                final int[] previousVal = {0};
                scrollPane.setFitToHeight(false);
                projectsTabPane.heightProperty().addListener((observableValue1, number1, t11) -> {
                    var newVal = t11.intValue() - delta;
                    if (newVal > previousVal[0]) {
                        previousVal[0] = newVal;
                        workbench.setPrefHeight(newVal);
                    }
                });
            }
        });

        scrollPane.setPannable(true);*/

        projectsTabPane.getTabs().add(newTab);

        var tabTooltip = new Tooltip();
        tabTooltip.textProperty().bindBidirectional(newTab.textProperty());
        newTab.setTooltip(tabTooltip);

        var renameMenu = new MenuItem("Umbenennen");
        renameMenu.setOnAction(actionEvent -> {
            var textInputDialog = new TextInputDialog();
            textInputDialog.setResizable(true);
            textInputDialog.setContentText("Neuen Projektnamen eingeben:");
            textInputDialog.setTitle("BOSSModellerFX");
            textInputDialog.setHeaderText("Projektname");
            textInputDialog.showAndWait().ifPresent(newTab::setText);
        });

        var closeMenu = new MenuItem("Schließen");
        closeMenu.setOnAction(actionEvent -> {
            if (Project.getProjectsAmount() <= 1) {
                GUIMethods.showWarning(MainController.class.getSimpleName(), "BOSSModeller FX", "Das ist der letzte Tab");
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
                                resultedProjectData.projectName.equals("") ? "Importieres Projekelt" : resultedProjectData.projectName,
                                newProject,
                                subWindows.size() == 0);
                        try {
                            initProject(newProject);
                        } catch (IOException e) {
                            GUIMethods.showError(MainController.class.getSimpleName(), "BOSSModellerFX", e.getLocalizedMessage());
                        }
                    };
                    chooseWindowStage.setScene(chooserWindow.getKey());
                    chooseWindowStage.setTitle("Tabellen auswählen");
                    chooseWindowStage.show();
                    addSubWindow(chooseWindowStage);
                } catch (IOException e) {
                    GUIMethods.showError(MainController.class.getSimpleName(), "BOSSModellerFX", e.getLocalizedMessage());
                }
            };
            connectWindowStage.setScene(window.getKey());
            connectWindowStage.setTitle("Mit Datenbank verbinden");
            connectWindowStage.show();
            addSubWindow(connectWindowStage);
        } catch (IOException e) {
            GUIMethods.showError(MainController.class.getSimpleName(), "BOSSModellerFX", e.getLocalizedMessage());
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
                    exportWindowStage.setTitle("DB und Schema auswählen");
                    exportWindowStage.show();
                    addSubWindow(exportWindowStage);
                } catch (IOException e) {
                    GUIMethods.showError(MainController.class.getSimpleName(), "BOSSModellerFX", e.getLocalizedMessage());
                }
            };
            connectWindowStage.setScene(window.getKey());
            connectWindowStage.setTitle("Mit Datenbank verbinden");
            connectWindowStage.show();
            addSubWindow(connectWindowStage);
        } catch (IOException e) {
            GUIMethods.showError(MainController.class.getSimpleName(), "BOSSModellerFX", e.getLocalizedMessage());
        }
    }
}
