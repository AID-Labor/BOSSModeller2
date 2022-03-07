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
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
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
import java.util.LinkedList;
import java.util.Locale;
import java.util.Objects;

import static de.snaggly.bossmodellerfx.guiLogic.KeyCombos.*;

/**
 * Controller for Main Window
 *
 * @author Omar Emshani
 */
public class MainController {
    @FXML
    private Menu languageMenuTab;
    @FXML
    private Label infoLabel;
    @FXML
    private Button startNewProjectBtn;
    @FXML
    private Button openFileBtn;
    @FXML
    private Button saveFileBtn;
    @FXML
    private Button exportPictureBtn;
    @FXML
    private Button importFromDBBtn;
    @FXML
    private Button exportToDBBtn;
    @FXML
    private Button exportSQLBtn;
    @FXML
    private Button newEntityBtn;
    @FXML
    private Button editEntityBtn;
    @FXML
    private Button deleteEntityBtn;
    @FXML
    private Button newRelationBtn;
    @FXML
    private Button editRelationBtn;
    @FXML
    private Button deleteRelationBtn;
    @FXML
    private Button newCommentBtn;
    @FXML
    private Button deleteCommentBtn;
    @FXML
    private Accordion leftNavigationAccordion;
    @FXML
    private TabPane projectsTabPane;

    private Project currentProject;
    private DBLAHolder previousDBLA = null;

    private final HashMap<Entity, EntityView> entitiesOverview = new HashMap<>();
    private final HashMap<Relation, RelationViewNode> relationsOverview = new HashMap<>();

    private final HashMap<SubWindowType, Window> subWindows = new HashMap<>();

    private final ContextMenu mainWorkbenchContextMenu = new ContextMenu();

    private void relationLineDrawer(Project project) { //For future: Follow State-Pattern
        RelationLineDrawer.drawAllLines(project, entitiesOverview, relationsOverview);
    }

    @FXML
    private void showAboutUsWindow() {
        if (subWindows.get(SubWindowType.Misc) != null){
            subWindows.get(SubWindowType.Misc).requestFocus();
            return;
        }
        try {
            var fxmlLoader = new FXMLLoader(Main.class.getResource("view/AboutUs.fxml"), BOSS_Strings.resourceBundle);
            var scene = new Scene(fxmlLoader.load());
            var stage = new Stage();
            stage.setTitle(BOSS_Strings.ABOUT_US);
            stage.getIcons().add(new Image(Objects.requireNonNull(Main.class.getResourceAsStream("view/bossfx_icon.png"))));
            stage.setScene(scene);
            stage.show();
            addSubWindow(SubWindowType.Misc, scene.getWindow());
        } catch (IOException e) {
            GUIMethods.showError(MainController.class.getSimpleName(), BOSS_Strings.PRODUCT_NAME, e.getLocalizedMessage());
        }
    }

    @FXML
    private void initialize() {
        leftNavigationAccordion.setExpandedPane(leftNavigationAccordion.getPanes().get(0));
        projectsTabPane.getSelectionModel().selectedIndexProperty().addListener((_obj, _old, _new) -> {
            currentProject = Project.getProject(_new.intValue());
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

        //Setting up InfoLabel
        startNewProjectBtn.setOnMouseEntered(mouseEvent -> infoLabel.setText(BOSS_Strings.DESCRIPTOR_START_NEW_PROJECT));
        startNewProjectBtn.setOnMouseExited(mouseEvent -> infoLabel.setText(""));
        openFileBtn.setOnMouseEntered(mouseEvent -> infoLabel.setText(BOSS_Strings.DESCRIPTOR_OPEN_PROJECT));
        openFileBtn.setOnMouseExited(mouseEvent -> infoLabel.setText(""));
        saveFileBtn.setOnMouseEntered(mouseEvent -> infoLabel.setText(BOSS_Strings.DESCRIPTOR_SAVE_PROJECT));
        saveFileBtn.setOnMouseExited(mouseEvent -> infoLabel.setText(""));
        exportPictureBtn.setOnMouseEntered(mouseEvent -> infoLabel.setText(BOSS_Strings.DESCRIPTOR_EXPORT_PICTURE));
        exportPictureBtn.setOnMouseExited(mouseEvent -> infoLabel.setText(""));
        importFromDBBtn.setOnMouseEntered(mouseEvent -> infoLabel.setText(BOSS_Strings.DESCRIPTOR_DB_IMPORT));
        importFromDBBtn.setOnMouseExited(mouseEvent -> infoLabel.setText(""));
        exportToDBBtn.setOnMouseEntered(mouseEvent -> infoLabel.setText(BOSS_Strings.DESCRIPTOR_DB_EXPORT));
        exportToDBBtn.setOnMouseExited(mouseEvent -> infoLabel.setText(""));
        exportSQLBtn.setOnMouseEntered(mouseEvent -> infoLabel.setText(BOSS_Strings.DESCRIPTOR_EXPORT_SQL));
        exportSQLBtn.setOnMouseExited(mouseEvent -> infoLabel.setText(""));
        newEntityBtn.setOnMouseEntered(mouseEvent -> infoLabel.setText(BOSS_Strings.DESCRIPTOR_NEW_ENTITY));
        newEntityBtn.setOnMouseExited(mouseEvent -> infoLabel.setText(""));
        editEntityBtn.setOnMouseEntered(mouseEvent -> infoLabel.setText(BOSS_Strings.DESCRIPTOR_EDIT_ENTITY));
        editEntityBtn.setOnMouseExited(mouseEvent -> infoLabel.setText(""));
        deleteEntityBtn.setOnMouseEntered(mouseEvent -> infoLabel.setText(BOSS_Strings.DESCRIPTOR_DELETE_ENTITY));
        deleteEntityBtn.setOnMouseExited(mouseEvent -> infoLabel.setText(""));
        newRelationBtn.setOnMouseEntered(mouseEvent -> infoLabel.setText(BOSS_Strings.DESCRIPTOR_NEW_RELATION));
        newRelationBtn.setOnMouseExited(mouseEvent -> infoLabel.setText(""));
        editRelationBtn.setOnMouseEntered(mouseEvent -> infoLabel.setText(BOSS_Strings.DESCRIPTOR_EDIT_RELATION));
        editRelationBtn.setOnMouseExited(mouseEvent -> infoLabel.setText(""));
        deleteRelationBtn.setOnMouseEntered(mouseEvent -> infoLabel.setText(BOSS_Strings.DESCRIPTOR_DELETE_RELATION));
        deleteRelationBtn.setOnMouseExited(mouseEvent -> infoLabel.setText(""));
        newCommentBtn.setOnMouseEntered(mouseEvent -> infoLabel.setText(BOSS_Strings.DESCRIPTOR_NEW_COMMENT));
        newCommentBtn.setOnMouseExited(mouseEvent -> infoLabel.setText(""));
        deleteCommentBtn.setOnMouseEntered(mouseEvent -> infoLabel.setText(BOSS_Strings.DESCRIPTOR_DELETE_COMMENT));
        deleteCommentBtn.setOnMouseExited(mouseEvent -> infoLabel.setText(""));

        buildLanguageMenuList();
    }

    private void buildLanguageMenuList() {
        languageMenuTab.getItems().clear();
        for (var supportedLanguage : BOSS_Config.supportedLanguages) {
            var isCurrentLanguageCheckMark = "";
            if (Locale.getDefault().getLanguage().equals(supportedLanguage.getLanguage()))
                isCurrentLanguageCheckMark = " ✔";
            //Here comes the one line everyone hates ;)
            languageMenuTab.getItems().add(new MenuItem(supportedLanguage.getDisplayLanguage() + isCurrentLanguageCheckMark){{setOnAction(_e -> manageNewLanguageSelectionClick(supportedLanguage));}});
        }
    }

    private void manageNewLanguageSelectionClick(Locale newLang) {
        if (BOSS_Config.setLanguage(newLang)) {
            Locale.setDefault(newLang);
            GUIMethods.showInfo(BOSS_Strings.PRODUCT_NAME, BOSS_Strings.LANGUAGE_CHANGE, BOSS_Strings.LANGUAGE_CHANGE_SUCCESS);
        }
        else {
            GUIMethods.showInfo(BOSS_Strings.PRODUCT_NAME, BOSS_Strings.LANGUAGE_CHANGE, BOSS_Strings.LANGUAGE_CHANGE_ERROR);
        }
    }

    private void showContextMenu(MouseEvent mouseEvent) {
        mainWorkbenchContextMenu.getItems().clear();
        var currentSelection = currentProject.getCurrentSelected();

        if (currentSelection instanceof EntityView){
            if (currentProject.getCurrentSecondSelection() instanceof EntityView) {
                var newRelationMenu = new MenuItem(BOSS_Strings.NEW_RELATION);
                newRelationMenu.setOnAction(actionEvent -> createNewRelation());
                var separator = new SeparatorMenuItem();
                mainWorkbenchContextMenu.getItems().addAll(newRelationMenu, separator);
            }
            var entityView = (EntityView)(currentProject.getCurrentSelected());
            var editEntityMenu = new MenuItem(BOSS_Strings.EDIT_ENTITY);
            editEntityMenu.setOnAction(actionEvent -> editEntity(entityView));
            var removeEntityMenu = new MenuItem(BOSS_Strings.DELETE_ENTITY);
            removeEntityMenu.setOnAction(actionEvent -> deleteEntity(entityView));
            mainWorkbenchContextMenu.getItems().addAll(editEntityMenu, removeEntityMenu);
        } else if (currentSelection instanceof CommentView) {
            var commentView = (CommentView)(currentProject.getCurrentSelected());
            var editCommentMenu = new MenuItem(BOSS_Strings.EDIT_COMMENT);
            editCommentMenu.setOnAction(actionEvent -> editComment(commentView));
            var removeCommentMenu = new MenuItem(BOSS_Strings.DELETE_COMMENT);
            removeCommentMenu.setOnAction(actionEvent -> deleteComment(commentView));
            mainWorkbenchContextMenu.getItems().addAll(editCommentMenu, removeCommentMenu);
        } else if (currentSelection instanceof RelationViewNode) {
            var relationView = (RelationViewNode)(currentProject.getCurrentSelected());
            var editRelationMenu = new MenuItem(BOSS_Strings.EDIT_RELATION);
            editRelationMenu.setOnAction(actionEvent -> editRelation(relationView));
            var removeRelation = new MenuItem(BOSS_Strings.DELETE_RELATION);
            removeRelation.setOnAction(actionEvent -> deleteRelation(relationView));
            mainWorkbenchContextMenu.getItems().addAll(editRelationMenu, removeRelation);
        } else {
            var newEntityMenu = new MenuItem(BOSS_Strings.NEW_ENTITY);
            newEntityMenu.setOnAction(actionEvent -> createNewEntity(
                    mouseEvent.getX(),
                    mouseEvent.getY()
            ));

            var newRelationMenu = new MenuItem(BOSS_Strings.NEW_RELATION);
            newRelationMenu.setOnAction(actionEvent -> createNewRelation());

            var newCommentMenu = new MenuItem(BOSS_Strings.NEW_COMMENT);
            newCommentMenu.setOnAction(actionEvent -> createNewComment(
                    mouseEvent.getX(),
                    mouseEvent.getY()
            ));
            mainWorkbenchContextMenu.getItems().addAll(newEntityMenu, newCommentMenu, newRelationMenu);

            if (currentProject.getEntities().size() > 0) {
                var separator = new SeparatorMenuItem();
                mainWorkbenchContextMenu.getItems().add(separator);
                var generateSQLMenu = new MenuItem(BOSS_Strings.GENERATE_SQL_SCRIPT);
                generateSQLMenu.setOnAction(actionEvent -> exportSQLClick());
                mainWorkbenchContextMenu.getItems().add(generateSQLMenu);
                var exportPicture = new MenuItem(BOSS_Strings.EXPORT_TO_PICTURE);
                exportPicture.setOnAction(actionEvent -> exportPictureClick());
                mainWorkbenchContextMenu.getItems().add(exportPicture);
            }
        }

        if (currentSelection instanceof EntityView || currentSelection instanceof CommentView) {
            var separator = new SeparatorMenuItem();
            var editCommentMenu = new MenuItem(BOSS_Strings.MOVE_TO_FRONT);
            editCommentMenu.setOnAction(actionEvent -> currentSelection.toFront());
            var removeCommentMenu = new MenuItem(BOSS_Strings.MOVE_TO_BACK);
            removeCommentMenu.setOnAction(actionEvent -> currentSelection.toBack());
            mainWorkbenchContextMenu.getItems().addAll(separator, editCommentMenu, removeCommentMenu);
        }

        mainWorkbenchContextMenu.show(
                currentProject.getWorkField(),
                mouseEvent.getScreenX(),
                mouseEvent.getScreenY()
        );
    }

    @FXML
    private void onMainWorkbenchClick(MouseEvent mouseEvent) {
        if (mouseEvent.getTarget() == currentProject.getWorkField()) {
            if (currentProject.getCurrentSelected() instanceof CommentView) {
                ((CommentView) currentProject.getCurrentSelected()).getController().disableEdit();
            }
            currentProject.setCurrentSelected(currentProject.getWorkField());
        }

        if (MouseButton.PRIMARY == mouseEvent.getButton()) {
            if (mainWorkbenchContextMenu.isShowing()) {
                mainWorkbenchContextMenu.hide();
            }
            if (mouseEvent.getClickCount() == 2) {
                var selectedItem = currentProject.getCurrentSelected();
                if (selectedItem instanceof EntityView) {
                    editEntity((EntityView) selectedItem);
                }
                else if (selectedItem instanceof RelationViewNode) {
                    editRelation((RelationViewNode) selectedItem);
                }
            }
        } else if (MouseButton.SECONDARY == mouseEvent.getButton()) {
            showContextMenu(mouseEvent);
        }
    }

    @FXML
    public void closeApp() {
        for (var windowSet : subWindows.entrySet()) {
            ((Stage) windowSet.getValue()).close();
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
        if (subWindows.get(SubWindowType.Editor) != null) {
            subWindows.get(SubWindowType.Editor).requestFocus();
            return;
        }
        try {
            var entityBuilder = EntityEditorWindowBuilder.buildEntityEditor(null);
            var stage = new Stage();
            stage.setTitle(BOSS_Strings.NEW_ENTITY);
            stage.getIcons().add(new Image(Objects.requireNonNull(Main.class.getResourceAsStream("view/bossfx_icon.png"))));
            stage.setScene(entityBuilder.getKey());
            stage.show();
            addSubWindow(SubWindowType.Editor, entityBuilder.getKey().getWindow());
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
        if (subWindows.get(SubWindowType.Editor) != null) {
            subWindows.get(SubWindowType.Editor).requestFocus();
            return;
        }
        try {
            var selectedEntity = selectedEntityView.getModel();
            var entityBuilder = EntityEditorWindowBuilder.buildEntityEditor(selectedEntity);
            var stage = new Stage();
            stage.setTitle(BOSS_Strings.EDIT_ENTITY);
            stage.getIcons().add(new Image(Objects.requireNonNull(Main.class.getResourceAsStream("view/bossfx_icon.png"))));
            stage.setScene(entityBuilder.getKey());
            stage.show();
            addSubWindow(SubWindowType.Editor, entityBuilder.getKey().getWindow());
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
        if (subWindows.get(SubWindowType.Editor) != null) {
            subWindows.get(SubWindowType.Editor).requestFocus();
            return;
        }
        if (currentProject.getEntities().size() < 1) {
            GUIMethods.showWarning(MainController.class.getSimpleName(), BOSS_Strings.PRODUCT_NAME, BOSS_Strings.NO_ENTITIES_WARNING);
            return;
        }
        try {
            var relationBuilderWindow = RelationEditorWindowBuilder.buildRelationEditor(null);
            relationBuilderWindow.getValue().parentObserver = this::saveNewRelation;
            var stage = new Stage();
            stage.setTitle(BOSS_Strings.NEW_RELATION);
            stage.getIcons().add(new Image(Objects.requireNonNull(Main.class.getResourceAsStream("view/bossfx_icon.png"))));
            stage.setScene(relationBuilderWindow.getKey());
            stage.show();
            addSubWindow(SubWindowType.Editor, relationBuilderWindow.getKey().getWindow());
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

            //Reapply user inputs
            var adaptedKeysInA = ForeignKeyHandler.reApplyUserDataInNewForeignKeys(removedKeysList.getKey(), projRelation.getFkAttributesA());
            var adaptedKeysInB = ForeignKeyHandler.reApplyUserDataInNewForeignKeys(removedKeysList.getKey(), projRelation.getFkAttributesB());

            //Adapt the new Objects on UniqueList to prevent ghosts
            ForeignKeyHandler.readjustForeignKeysInUniqueLists(projRelation.getTableA(), removedKeysList.getKey(), projRelation.getFkAttributesA());
            ForeignKeyHandler.readjustForeignKeysInUniqueLists(projRelation.getTableB(), removedKeysList.getKey(), projRelation.getFkAttributesB());

            //Reorder FKeys in Attribute list
            if (adaptedKeysInA.size() > 0)
                ForeignKeyHandler.reOrderDeletedFKeys(projRelation.getTableA(), removedKeysList.getKey(), adaptedKeysInA, removedKeysList.getValue());
            if (adaptedKeysInB.size() > 0)
                ForeignKeyHandler.reOrderDeletedFKeys(projRelation.getTableB(), removedKeysList.getKey(), adaptedKeysInB, removedKeysList.getValue());

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
        currentProject.syncRelationOrder();
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
                if (uniqueCombo.getAttributes().size() <= 1) {
                    emptyAttrCombo.add(uniqueCombo);
                }
            }
            entity.getUniqueCombination().getCombinations().removeAll(emptyAttrCombo);
        }
    }

    private void editRelation(RelationViewNode relationView) {
        if (subWindows.get(SubWindowType.Editor) != null) {
            subWindows.get(SubWindowType.Editor).requestFocus();
            return;
        }
        var selectedRelationModel = relationView.getModel();

        try {
            var relationBuilderWindow = RelationEditorWindowBuilder.buildRelationEditor(selectedRelationModel);
            relationBuilderWindow.getValue().parentObserver = (resultedRelation) -> {
                showNewRelation(currentProject);
                currentProject.syncRelationOrder();
            };
            var stage = new Stage();
            stage.setTitle(BOSS_Strings.EDIT_RELATION);
            stage.getIcons().add(new Image(Objects.requireNonNull(Main.class.getResourceAsStream("view/bossfx_icon.png"))));
            stage.setScene(relationBuilderWindow.getKey());
            stage.show();
            addSubWindow(SubWindowType.Editor, relationBuilderWindow.getKey().getWindow());
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
            saveAsFileClick();
        }
        else {
            saveFile(currentProject.activeFile);
        }
    }

    @FXML
    private void saveAsFileClick() {
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
            GUIMethods.showInfo(MainController.class.getSimpleName(), BOSS_Strings.PRODUCT_NAME, BOSS_Strings.PROJECT_SAVED_SUCCESSFULLY);
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

        if (currentProject.getPressedKeys().containsAll(keyComboNewProject)) {
            currentProject.getPressedKeys().clear(); //Focuses on new pane before the key release is registered
            startNewProject();
        }
        else if (currentProject.getPressedKeys().containsAll(keyComboImportDB)) {
            currentProject.getPressedKeys().clear(); //Focuses on new window before the key release is registered
            importFromDBClick();
        }
        else if (currentProject.getPressedKeys().containsAll(keyComboOpen)) {
            currentProject.getPressedKeys().clear(); //Focuses on new window before the key release is registered
            openFileClick();
        }
        else if (currentProject.getPressedKeys().containsAll(keyComboSaveAs)) {
            currentProject.getPressedKeys().clear(); //Focuses on new window before the key release is registered
            saveAsFileClick();
        }
        else if (currentProject.getPressedKeys().containsAll(keyComboSave)) {
            currentProject.getPressedKeys().clear(); //Focuses on new window before the key release is registered
            saveFileClick();
        }
        else if (currentProject.getPressedKeys().containsAll(keyComboExportToDB)) {
            currentProject.getPressedKeys().clear(); //Focuses on new window before the key release is registered
            exportToDBClick();
        }
        else if (currentProject.getPressedKeys().containsAll(keyComboExportToPicture)) {
            currentProject.getPressedKeys().clear(); //Focuses on new window before the key release is registered
            exportPictureClick();
        }
        else if (currentProject.getPressedKeys().containsAll(keyComboGenSQL)) {
            currentProject.getPressedKeys().clear(); //Focuses on new window before the key release is registered
            exportSQLClick();
        }
        else if (currentProject.getPressedKeys().containsAll(keyComboZoomIn)) {
            zoomInClick();
        }
        else if (currentProject.getPressedKeys().containsAll(keyComboZoomOut)) {
            zoomOutClick();
        }
        else if (currentProject.getPressedKeys().containsAll(keyComboZoomRestore)) {
            zoomResetClick();
        }
        else if (currentProject.getPressedKeys().contains(KeyCode.DELETE)) {
            if (currentProject.getCurrentSelected() instanceof EntityView) {
                deleteEntityClick();
            }
            else if (currentProject.getCurrentSelected() instanceof RelationViewNode) {
                deleteRelationClick();
            }
            else if (currentProject.getCurrentSelected() instanceof CommentView) {
                deleteCommentClick();
            }
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

    private void addSubWindow(SubWindowType type, Window window) {
        projectsTabPane.setDisable(true);
        subWindows.put(type, window);
        window.setOnCloseRequest(windowEvent -> {
            subWindows.remove(type);
            projectsTabPane.setDisable(subWindows.size() > 0);
        });
    }

    @FXML
    private void importFromDBClick() {
        if (subWindows.get(SubWindowType.DBConnector) != null) {
            subWindows.get(SubWindowType.DBConnector).requestFocus();
            return;
        }
        try {
            var window = ConnectToDBWindowBuilder.buildDBConnectorWindow(previousDBLA);
            var connectWindowStage = new Stage();
            window.getValue().parentObserver = resultedConnection -> {
                previousDBLA = resultedConnection;
                GUIMethods.closeWindow(connectWindowStage);
                try {
                    var chooserWindow = ChooseDBImportWindowBuilder.buildDBChooserWindow(resultedConnection);
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
                    chooseWindowStage.getIcons().add(new Image(Objects.requireNonNull(Main.class.getResourceAsStream("view/bossfx_icon.png"))));
                    chooseWindowStage.show();
                    addSubWindow(SubWindowType.DBConnector, chooseWindowStage);
                } catch (IOException e) {
                    GUIMethods.showError(MainController.class.getSimpleName(), BOSS_Strings.PRODUCT_NAME, e.getLocalizedMessage());
                }
            };
            connectWindowStage.setScene(window.getKey());
            connectWindowStage.setTitle(BOSS_Strings.CONNECT_TO_DATABASE);
            connectWindowStage.getIcons().add(new Image(Objects.requireNonNull(Main.class.getResourceAsStream("view/bossfx_icon.png"))));
            connectWindowStage.show();
            addSubWindow(SubWindowType.DBConnector, connectWindowStage);
        } catch (IOException e) {
            GUIMethods.showError(MainController.class.getSimpleName(), BOSS_Strings.PRODUCT_NAME, e.getLocalizedMessage());
        }
    }

    @FXML
    private void exportToDBClick() {
        if (subWindows.get(SubWindowType.DBConnector) != null) {
            subWindows.get(SubWindowType.DBConnector).requestFocus();
            return;
        }
        try {
            var window = ConnectToDBWindowBuilder.buildDBConnectorWindow(previousDBLA);
            var connectWindowStage = new Stage();
            window.getValue().parentObserver = resultedConnection -> {
                previousDBLA = resultedConnection;
                GUIMethods.closeWindow(connectWindowStage);
                try {
                    var exportWindow = ChooseDBExportWindowBuilder.buildDBChooserWindow(resultedConnection);
                    var exportWindowStage = new Stage();
                    exportWindowStage.setScene(exportWindow.getKey());
                    exportWindowStage.setTitle(BOSS_Strings.CHOOSE_DATABASE_AND_SCHEMA);
                    exportWindowStage.getIcons().add(new Image(Objects.requireNonNull(Main.class.getResourceAsStream("view/bossfx_icon.png"))));
                    exportWindowStage.show();
                    addSubWindow(SubWindowType.DBConnector, exportWindowStage);
                } catch (IOException e) {
                    GUIMethods.showError(MainController.class.getSimpleName(), BOSS_Strings.PRODUCT_NAME, e.getLocalizedMessage());
                }
            };
            connectWindowStage.setScene(window.getKey());
            connectWindowStage.setTitle(BOSS_Strings.CONNECT_TO_DATABASE);
            connectWindowStage.getIcons().add(new Image(Objects.requireNonNull(Main.class.getResourceAsStream("view/bossfx_icon.png"))));
            connectWindowStage.show();
            addSubWindow(SubWindowType.DBConnector, connectWindowStage);
        } catch (IOException e) {
            GUIMethods.showError(MainController.class.getSimpleName(), BOSS_Strings.PRODUCT_NAME, e.getLocalizedMessage());
        }
    }

    @FXML
    private void zoomInClick() {
        currentProject.getWorkField().setScaleX(currentProject.getWorkField().getScaleX() + 0.1);
        currentProject.getWorkField().setScaleY(currentProject.getWorkField().getScaleY() + 0.1);
    }

    @FXML
    private void zoomOutClick() {
        currentProject.getWorkField().setScaleX(currentProject.getWorkField().getScaleX() - 0.1);
        currentProject.getWorkField().setScaleY(currentProject.getWorkField().getScaleY() - 0.1);
    }

    @FXML
    private void zoomResetClick() {
        currentProject.getWorkField().setScaleX(1);
        currentProject.getWorkField().setScaleY(1);
    }

    @FXML
    public void exportSQLClick() {
        if (subWindows.get(SubWindowType.Misc) != null) {
            subWindows.get(SubWindowType.Misc).requestFocus();
            return;
        }
        try {
            var window = SQLViewerBuilder.buildSQLViewer(currentProject);
            var sqlWindowStage = new Stage();
            sqlWindowStage.setScene(window.getKey());
            sqlWindowStage.setTitle(BOSS_Strings.SQL_DISPLAY);
            sqlWindowStage.getIcons().add(new Image(Objects.requireNonNull(Main.class.getResourceAsStream("view/bossfx_icon.png"))));
            sqlWindowStage.show();
            addSubWindow(SubWindowType.Misc, sqlWindowStage);
        } catch (IOException e) {
            GUIMethods.showError(MainController.class.getSimpleName(), BOSS_Strings.PRODUCT_NAME, e.getLocalizedMessage());
        }
    }
}
