package de.snaggly.bossmodeller2;

import de.snaggly.bossmodeller2.guiLogic.CrowsFootShape;
import de.snaggly.bossmodeller2.guiLogic.GUIMethods;
import de.snaggly.bossmodeller2.guiLogic.Project;
import de.snaggly.bossmodeller2.model.Attribute;
import de.snaggly.bossmodeller2.model.Comment;
import de.snaggly.bossmodeller2.model.Entity;
import de.snaggly.bossmodeller2.view.CommentView;
import de.snaggly.bossmodeller2.view.EntityView;
import de.snaggly.bossmodeller2.view.RelationLineView;
import de.snaggly.bossmodeller2.view.factory.*;
import de.snaggly.bossmodeller2.view.viewtypes.CustomNode;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class MainController {
    @FXML
    private Pane mainWorkbench;

    private Project currentProject;
    private final HashMap<Entity, EntityView> entitiesOverview = new HashMap<>();

    private final ContextMenu mainWorkbenchContextMenu = new ContextMenu();

    CrowsFootShape crowsFootA = null; //ToDo Map&Move
    CrowsFootShape crowsFootB = null;
    private void relationLineDrawer() { //For future: Follow State-Pattern
        for (var relation : currentProject.getRelations()) {
            var node1 = entitiesOverview.get(relation.getTableA());
            var node2 = entitiesOverview.get(relation.getTableB());
            if (node1 == null || node2 == null) {
                currentProject.removeRelation(relation);
                continue;
            }

            var node1w = node1.getWidth();
            var node1h = node1.getHeight();
            var node2w = node2.getWidth();
            var node2h = node2.getHeight();

            var node1x = node1.getLayoutX();
            var node1y = node1.getLayoutY();
            var node2x = node2.getLayoutX();
            var node2y = node2.getLayoutY();

            var node1mx = node1x + (node1w / 2.0);
            var node1my = node1y + (node1h / 2.0);
            var node2mx = node2x + (node2w / 2.0);
            var node2my = node2y + (node2h / 2.0);

            var midLineX = node1mx;
            var midLineY = node1my;

            var line1 = new RelationLineView();
            var line2 = new RelationLineView();
            var line3 = new RelationLineView();

            if (relation.getTableA().isWeakType() || relation.getTableB().isWeakType()) {
                line1.setWeakConnection();
                line2.setWeakConnection();
                line3.setWeakConnection();
            }
            else {
                line1.setStrongConnection();
                line2.setStrongConnection();
                line3.setStrongConnection();
            }

            if (crowsFootA != null) {
                mainWorkbench.getChildren().removeAll(crowsFootA.getAllNodes());
            }
            if (crowsFootB != null) {
                mainWorkbench.getChildren().removeAll(crowsFootB.getAllNodes());
            }
            mainWorkbench.getChildren().removeIf(node -> node instanceof RelationLineView);
            mainWorkbench.getChildren().addAll(line1, line2, line3);

            double midPointX = node1x + node1w + (node2x - (node1x + node1w)) / 2;
            double midPointY = node1y + node1h + (node2y - (node1y + node1h)) / 2;
            if (node1mx <= node2mx && node1my >= node2my) { //1Q
                if ((node2y + node2h) >= node1y && (node2y + node2h) <= (node1y + node1h)) { //R
                    line1.setStartX(node1mx + (node1w / 2));
                    line1.setStartY(node1my);
                    line1.setEndX(midPointX);
                    line1.setEndY(node1my);
                    line2.setStartX(midPointX);
                    line2.setStartY(node1my);
                    line2.setEndX(midPointX);
                    line2.setEndY(node2my);
                    line3.setStartX(midPointX);
                    line3.setStartY(node2my);
                    line3.setEndX(node2mx - (node2w / 2));
                    line3.setEndY(node2my);
                    line1.setVisible(true);
                    line2.setVisible(true);
                    line3.setVisible(true);
                    crowsFootA = new CrowsFootShape.East(node1, 2);
                    crowsFootB = new CrowsFootShape.West(node2, 2);
                }
                else if (node2x >= node1x && node2x <= (node1x + node1w)) { //O
                    line1.setStartX(node1mx);
                    line1.setStartY(node1my - (node1h / 2));
                    line1.setEndX(node1mx);
                    line1.setEndY(midPointY);
                    line2.setStartX(node1mx);
                    line2.setStartY(midPointY);
                    line2.setEndX(node2mx);
                    line2.setEndY(midPointY);
                    line3.setStartX(node2mx);
                    line3.setStartY(midPointY);
                    line3.setEndX(node2mx);
                    line3.setEndY(node2my + (node2h / 2));
                    line1.setVisible(true);
                    line2.setVisible(true);
                    line3.setVisible(true);
                    crowsFootA = new CrowsFootShape.North(node1, 2);
                    crowsFootB = new CrowsFootShape.South(node2, 2);
                }
                else {
                    while (midLineX < node2mx && midLineY > node2my) {
                        midLineX++;
                        midLineY--;
                    }
                    if (midLineY <= node2my) { //MatchYin1R
                        line1.setStartX(node1mx + (node1w / 2));
                        line1.setStartY(node1my);
                        line1.setEndX(node2mx);
                        line1.setEndY(node1my);

                        line2.setStartX(node2mx);
                        line2.setStartY(node1my);
                        line2.setEndX(node2mx);
                        line2.setEndY(node2my + (node2h / 2));
                        line1.setVisible(true);
                        line2.setVisible(true);
                        crowsFootA = new CrowsFootShape.East(node1, 2);
                        crowsFootB = new CrowsFootShape.South(node2, 2);
                    }
                    else if (midLineX >= node2mx) { //MatchXin4R
                        line1.setStartX(node1mx);
                        line1.setStartY(node1my - (node1h / 2));
                        line1.setEndX(node1mx);
                        line1.setEndY(node2my);

                        line2.setStartX(node1mx);
                        line2.setStartY(node2my);
                        line2.setEndX(node2mx - (node2w / 2));
                        line2.setEndY(node2my);
                        line1.setVisible(true);
                        line2.setVisible(true);
                        crowsFootA = new CrowsFootShape.North(node1, 2);
                        crowsFootB = new CrowsFootShape.West(node2, 2);
                    }
                }
            }
            else if (node1mx <= node2mx && node1my <= node2my) { //2Q
                if (node2y >= node1y && node2y <= (node1y + node1h)) { //R
                    line1.setStartX(node1mx + (node1w / 2));
                    line1.setStartY(node1my);
                    line1.setEndX(midPointX);
                    line1.setEndY(node1my);
                    line2.setStartX(midPointX);
                    line2.setStartY(node1my);
                    line2.setEndX(midPointX);
                    line2.setEndY(node2my);
                    line3.setStartX(midPointX);
                    line3.setStartY(node2my);
                    line3.setEndX(node2mx - (node2w / 2));
                    line3.setEndY(node2my);
                    line1.setVisible(true);
                    line2.setVisible(true);
                    line3.setVisible(true);
                    crowsFootA = new CrowsFootShape.East(node1, 2);
                    crowsFootB = new CrowsFootShape.West(node2, 2);
                }
                else if (node2x >= node1x && node2x <= (node1x + node1w)) { //U
                    line1.setStartX(node1mx);
                    line1.setStartY(node1my + (node1h / 2));
                    line1.setEndX(node1mx);
                    line1.setEndY(midPointY);
                    line2.setStartX(node1mx);
                    line2.setStartY(midPointY);
                    line2.setEndX(node2mx);
                    line2.setEndY(midPointY);
                    line3.setStartX(node2mx);
                    line3.setStartY(midPointY);
                    line3.setEndX(node2mx);
                    line3.setEndY(node2my - (node2h / 2));
                    line1.setVisible(true);
                    line2.setVisible(true);
                    line3.setVisible(true);
                    crowsFootA = new CrowsFootShape.South(node1, 2);
                    crowsFootB = new CrowsFootShape.North(node2, 2);
                }
                else {
                    while (midLineX < node2mx && midLineY < node2my) {
                        midLineX++;
                        midLineY++;
                    }
                    if (midLineY >= node2my) { //MatchYin1R
                        line1.setStartX(node1mx + (node1w / 2));
                        line1.setStartY(node1my);
                        line1.setEndX(node2mx);
                        line1.setEndY(node1my);

                        line2.setStartX(node2mx);
                        line2.setStartY(node1my);
                        line2.setEndX(node2mx);
                        line2.setEndY(node2my - (node2h / 2));
                        line1.setVisible(true);
                        line2.setVisible(true);
                        crowsFootA = new CrowsFootShape.East(node1, 2);
                        crowsFootB = new CrowsFootShape.North(node2, 2);
                    }
                    else if (midLineX >= node2mx) { //MatchXin2R
                        line1.setStartX(node1mx);
                        line1.setStartY(node1my + (node1h / 2));
                        line1.setEndX(node1mx);
                        line1.setEndY(node2my);

                        line2.setStartX(node1mx);
                        line2.setStartY(node2my);
                        line2.setEndX(node2mx - (node2w / 2));
                        line2.setEndY(node2my);
                        line1.setVisible(true);
                        line2.setVisible(true);
                        crowsFootA = new CrowsFootShape.South(node1, 2);
                        crowsFootB = new CrowsFootShape.West(node2, 2);
                    }
                }
            }
            else if (node1mx >= node2mx && node1my <= node2my) { //3Q
                if (node2y >= node1y && node2y <= (node1y + node1h)) { //L
                    line1.setStartX(node1mx - (node1w / 2));
                    line1.setStartY(node1my);
                    line1.setEndX(midPointX);
                    line1.setEndY(node1my);
                    line2.setStartX(midPointX);
                    line2.setStartY(node1my);
                    line2.setEndX(midPointX);
                    line2.setEndY(node2my);
                    line3.setStartX(midPointX);
                    line3.setStartY(node2my);
                    line3.setEndX(node2mx + (node2w / 2));
                    line3.setEndY(node2my);
                    line1.setVisible(true);
                    line2.setVisible(true);
                    line3.setVisible(true);
                    crowsFootA = new CrowsFootShape.West(node1, 2);
                    crowsFootB = new CrowsFootShape.East(node2, 2);
                }
                else if ((node2x + node2w) >= node1x && (node2x + node2w) <= (node1x + node1w)) { //U
                    line1.setStartX(node1mx);
                    line1.setStartY(node1my + (node1h / 2));
                    line1.setEndX(node1mx);
                    line1.setEndY(midPointY);
                    line2.setStartX(node1mx);
                    line2.setStartY(midPointY);
                    line2.setEndX(node2mx);
                    line2.setEndY(midPointY);
                    line3.setStartX(node2mx);
                    line3.setStartY(midPointY);
                    line3.setEndX(node2mx);
                    line3.setEndY(node2my - (node2h / 2));
                    line1.setVisible(true);
                    line2.setVisible(true);
                    line3.setVisible(true);
                    crowsFootA = new CrowsFootShape.South(node1, 2);
                    crowsFootB = new CrowsFootShape.North(node2, 2);
                }
                else {
                    while (midLineX > node2mx && midLineY < node2my) {
                        midLineX--;
                        midLineY++;
                    }
                    if (midLineY >= node2my) { //MatchYin3R
                        line1.setStartX(node1mx - (node1w / 2));
                        line1.setStartY(node1my);
                        line1.setEndX(node2mx);
                        line1.setEndY(node1my);

                        line2.setStartX(node2mx);
                        line2.setStartY(node1my);
                        line2.setEndX(node2mx);
                        line2.setEndY(node2my - (node2h / 2));
                        line1.setVisible(true);
                        line2.setVisible(true);
                        crowsFootA = new CrowsFootShape.West(node1, 2);
                        crowsFootB = new CrowsFootShape.North(node2, 2);
                    }
                    else if (midLineX <= node2mx) { //MatchXin2R
                        line1.setStartX(node1mx);
                        line1.setStartY(node1my + (node1h / 2));
                        line1.setEndX(node1mx);
                        line1.setEndY(node2my);

                        line2.setStartX(node1mx);
                        line2.setStartY(node2my);
                        line2.setEndX(node2mx + (node2w / 2));
                        line2.setEndY(node2my);
                        line1.setVisible(true);
                        line2.setVisible(true);
                        crowsFootA = new CrowsFootShape.South(node1, 2);
                        crowsFootB = new CrowsFootShape.East(node2, 2);
                    }
                }
            }
            else if (node1mx >= node2mx && node1my >= node2my) { //4Q
                if ((node2y + node2h) >= node1y && (node2y + node2h) <= (node1y + node1h)) { //L
                    line1.setStartX(node1mx - (node1w / 2));
                    line1.setStartY(node1my);
                    line1.setEndX(midPointX);
                    line1.setEndY(node1my);
                    line2.setStartX(midPointX);
                    line2.setStartY(node1my);
                    line2.setEndX(midPointX);
                    line2.setEndY(node2my);
                    line3.setStartX(midPointX);
                    line3.setStartY(node2my);
                    line3.setEndX(node2mx + (node2w / 2));
                    line3.setEndY(node2my);
                    line1.setVisible(true);
                    line2.setVisible(true);
                    line3.setVisible(true);
                    crowsFootA = new CrowsFootShape.West(node1, 2);
                    crowsFootB = new CrowsFootShape.East(node2, 2);
                }
                else if ((node2x + node2w) >= node1x && (node2x + node2w) <= (node1x + node1w)) { //O
                    line1.setStartX(node1mx);
                    line1.setStartY(node1my - (node1h / 2));
                    line1.setEndX(node1mx);
                    line1.setEndY(midPointY);
                    line2.setStartX(node1mx);
                    line2.setStartY(midPointY);
                    line2.setEndX(node2mx);
                    line2.setEndY(midPointY);
                    line3.setStartX(node2mx);
                    line3.setStartY(midPointY);
                    line3.setEndX(node2mx);
                    line3.setEndY(node2my + (node2h / 2));
                    line1.setVisible(true);
                    line2.setVisible(true);
                    line3.setVisible(true);
                    crowsFootA = new CrowsFootShape.North(node1, 2);
                    crowsFootB = new CrowsFootShape.South(node2, 2);
                }
                else {
                    while (midLineX > node2mx && midLineY > node2my) {
                        midLineX--;
                        midLineY--;
                    }
                    if (midLineY <= node2my) { //MatchYin3R
                        line1.setStartX(node1mx - (node1w / 2));
                        line1.setStartY(node1my);
                        line1.setEndX(node2mx);
                        line1.setEndY(node1my);

                        line2.setStartX(node2mx);
                        line2.setStartY(node1my);
                        line2.setEndX(node2mx);
                        line2.setEndY(node2my + (node2h / 2));
                        line1.setVisible(true);
                        line2.setVisible(true);
                        crowsFootA = new CrowsFootShape.West(node1, 2);
                        crowsFootB = new CrowsFootShape.South(node2, 2);
                    }
                    else if (midLineX <= node2mx) { //MatchXin4R
                        line1.setStartX(node1mx);
                        line1.setStartY(node1my - (node1h / 2));
                        line1.setEndX(node1mx);
                        line1.setEndY(node2my);

                        line2.setStartX(node1mx);
                        line2.setStartY(node2my);
                        line2.setEndX(node2mx + (node2w / 2));
                        line2.setEndY(node2my);
                        line1.setVisible(true);
                        line2.setVisible(true);
                        crowsFootA = new CrowsFootShape.North(node1, 2);
                        crowsFootB = new CrowsFootShape.East(node2, 2);
                    }
                }
            }

            if (crowsFootA != null && crowsFootB != null){
                crowsFootA.draw(mainWorkbench, relation.getTableA_Cardinality(), relation.getTableA_Obligation(), 0, 0);
                crowsFootB.draw(mainWorkbench, relation.getTableB_Cardinality(), relation.getTableB_Obligation(), 0, 0);
            }
        }
    }

    @FXML
    private void testCusNode() {
        var attribute1 = new Attribute("Primär Attribut 1", "integer", true, true, true, "", "", "", "");
        var attribute2 = new Attribute("Attribut 2", "integer", false, true, true, "", "", "", "");
        var attribute3 = new Attribute("Attribut 3", "integer", false, false, true, "", "", "", "");
        var attributesList = new ArrayList<Attribute>();
        attributesList.add(attribute1);
        attributesList.add(attribute2);
        attributesList.add(attribute3);
        var testEntity = new Entity("Entität", attributesList, false);

        EntityView entityView = null;
        try {
            entityView = EntityBuilder.buildEntity(testEntity, mainWorkbench, currentProject.getSelectionHandler);
        } catch (IOException e) {
            e.printStackTrace();
        }
        entityView.setLayoutX(10);
        entityView.setLayoutY(10);
        saveNewEntity(entityView);
    }

    @FXML
    private void initialize() {
        mainWorkbench.setOnContextMenuRequested(contextMenuEvent -> {
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
            } else if (currentSelection instanceof RelationLineView) {

            }

            if (currentSelection instanceof CustomNode) {
                var editCommentMenu = new MenuItem("Element vor rücken");
                editCommentMenu.setOnAction(actionEvent -> currentSelection.toFront());
                var removeCommentMenu = new MenuItem("Element zu rücken");
                removeCommentMenu.setOnAction(actionEvent -> currentSelection.toBack());
                var separator = new SeparatorMenuItem();
                mainWorkbenchContextMenu.getItems().addAll(editCommentMenu, removeCommentMenu, separator);
            }

            var newEntityMenu = new MenuItem("Neue Entität");
            newEntityMenu.setOnAction(actionEvent -> createNewEntity(
                    ((MenuItem)(actionEvent.getSource())).getParentPopup().getX() - (mainWorkbench.getScene().getWindow().getX() + mainWorkbench.getLayoutX()),
                    ((MenuItem)(actionEvent.getSource())).getParentPopup().getY() - (mainWorkbench.getScene().getWindow().getY() + mainWorkbench.getLayoutY())
            ));

            var newRelationMenu = new MenuItem("Neue Relation");
            newRelationMenu.setOnAction(actionEvent -> createNewRelation());

            var newCommentMenu = new MenuItem("Neues Kommentar");
            newCommentMenu.setOnAction(actionEvent -> createNewComment(
                    ((MenuItem)(actionEvent.getSource())).getParentPopup().getX() - (mainWorkbench.getScene().getWindow().getX() + mainWorkbench.getLayoutX()),
                    ((MenuItem)(actionEvent.getSource())).getParentPopup().getY() - (mainWorkbench.getScene().getWindow().getY() + mainWorkbench.getLayoutY())
            ));
            mainWorkbenchContextMenu.getItems().addAll(newEntityMenu, newCommentMenu, newRelationMenu);
            mainWorkbenchContextMenu.show(
                    mainWorkbench,
                    contextMenuEvent.getScreenX(),
                    contextMenuEvent.getScreenY()
            );
        });

        currentProject = new Project(mainWorkbench);
    }

    @FXML
    private void onMainWorkbenchClick(MouseEvent mouseEvent) {
        if (mouseEvent.getTarget() == mainWorkbench) {
            if (currentProject.getCurrentSelected() instanceof CommentView) {
                ((CommentView) currentProject.getCurrentSelected()).getController().disableEdit();
            }

            currentProject.setCurrentSelected(mainWorkbench);
        }

        if (MouseButton.PRIMARY == mouseEvent.getButton() && mainWorkbenchContextMenu.isShowing()) {
            mainWorkbenchContextMenu.hide();
        }
    }

    @FXML
    private void closeApp() {
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
            GUIMethods.showWarning(MainController.class.getSimpleName(), "BOSSModeller 2", "Keine Entität ausgewählt!");
            return;
        }
        editEntity((EntityView)currentSelection);
    }

    @FXML
    private void deleteEntityClick() {
        var currentSelection = currentProject.getCurrentSelected();
        if (!(currentProject.getCurrentSelected() instanceof EntityView)){
            GUIMethods.showWarning(MainController.class.getSimpleName(), "BOSSModeller 2", "Keine Entität ausgewählt!");
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
            GUIMethods.showWarning(MainController.class.getSimpleName(), "BOSSModeller 2", "Kein Kommentar ausgewählt!");
            return;
        }
        editComment((CommentView) currentSelection);
    }

    @FXML
    private void deleteCommentClick() {
        var currentSelection = currentProject.getCurrentSelected();
        if (!(currentProject.getCurrentSelected() instanceof CommentView)){
            GUIMethods.showWarning(MainController.class.getSimpleName(), "BOSSModeller 2", "Kein Kommentar ausgewählt!");
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
    }

    @FXML
    private void deleteRelationClick() {
    }

    private void createNewEntity(double xCoordinate, double yCoordinate) {
        try {
            var entityBuilder = EntityEditorWindowBuilder.buildEntityEditor();
            var stage = new Stage();
            stage.setTitle("Neue Entität");
            stage.setScene(entityBuilder.getKey());
            stage.show();
            entityBuilder.getValue().parentObserver = resultedEntity -> {
                try {
                    resultedEntity.setXCoordinate(xCoordinate);
                    resultedEntity.setYCoordinate(yCoordinate);
                    var entityView = EntityBuilder.buildEntity(resultedEntity, mainWorkbench, currentProject.getSelectionHandler);
                    saveNewEntity(entityView);
                } catch (IOException e) {
                    GUIMethods.showError(MainController.class.getSimpleName(), "BOSSModeller 2", e.getLocalizedMessage());
                }
            };
        }
        catch (Exception e) {
            e.printStackTrace();
            GUIMethods.showError(MainController.class.getSimpleName(), "BOSSModeller 2", e.getLocalizedMessage());
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
            entityBuilder.getValue().parentObserver = resultedEntity -> {
                try {
                    var entityView = EntityBuilder.buildEntity(resultedEntity, mainWorkbench, currentProject.getSelectionHandler);
                    deleteEntity(selectedEntityView);

                    saveNewEntity(entityView);
                } catch (IOException e) {
                    GUIMethods.showError(MainController.class.getSimpleName(), "BOSSModeller 2", e.getLocalizedMessage());
                }
            };
        } catch (Exception e) {
            GUIMethods.showError(MainController.class.getSimpleName(), "BOSSModeller 2", e.getLocalizedMessage());
        }
    }

    private void createNewComment(double xCoordinate, double yCoordinate) {
        try {
            var commentModel = new Comment("", xCoordinate, yCoordinate);
            var commentView = CommentBuilder.buildComment(commentModel, mainWorkbench, currentProject.getSelectionHandler);

            currentProject.addComment(commentModel);
            mainWorkbench.getChildren().add(commentView);
        } catch (IOException e) {
            GUIMethods.showError(MainController.class.getSimpleName(), "BOSSModeller 2", e.getLocalizedMessage());
        }
    }

    private void editComment(CommentView commentView) {
        commentView.getController().enableEdit();
    }

    private void createNewRelation() {
        if (currentProject.getEntities().size() < 1) {
            GUIMethods.showWarning(MainController.class.getSimpleName(), "BOSSModeller 2", "Es muss mindestens eine Entität existieren!");
            return;
        }
        try {
            var relationBuilderWindow = RelationEditorWindowBuilder.buildRelationEditor(currentProject);
            relationBuilderWindow.getValue().parentObserver = dataset -> {
                currentProject.addRelation(dataset);
                var tableAView = entitiesOverview.get(dataset.getTableA());
                var tableBView = entitiesOverview.get(dataset.getTableB());
                try {
                    deleteEntity(tableAView);
                    saveNewEntity(EntityBuilder.buildEntity(dataset.getTableA(), mainWorkbench, currentProject.getSelectionHandler));
                    if (tableAView != tableBView) {
                        deleteEntity(tableBView);
                        saveNewEntity(EntityBuilder.buildEntity(dataset.getTableB(), mainWorkbench, currentProject.getSelectionHandler));
                    }

                    relationLineDrawer();
                } catch (IOException e) {
                    GUIMethods.showError(MainController.class.getSimpleName(), "BOSSModeller 2", e.getLocalizedMessage());
                }
            };
            var stage = new Stage();
            stage.setTitle("Neue Relation");
            stage.setScene(relationBuilderWindow.getKey());
            stage.show();
        } catch (IOException e) {
            GUIMethods.showError(MainController.class.getSimpleName(), "BOSSModeller 2", e.getLocalizedMessage());
        }
    }


    private void deleteComment(CommentView selectedCommentView) {
        var selectedComment = selectedCommentView.getModel();
        mainWorkbench.getChildren().remove(selectedCommentView);
        currentProject.removeComment(selectedComment);
    }

    private void saveNewEntity(EntityView entityView) {
        entitiesOverview.put(entityView.getModel(), entityView);
        currentProject.addEntity(entityView.getModel());
        GUIMethods.bindEntityToRelationLineHandler(entityView, this::relationLineDrawer);
        mainWorkbench.getChildren().add(entityView);
    }

    private void deleteEntity(EntityView selectedEntityView) {
        var selectedEntity = selectedEntityView.getModel();
        entitiesOverview.remove(selectedEntity);
        mainWorkbench.getChildren().remove(selectedEntityView);
        currentProject.removeEntity(selectedEntity);
    }

    public void testRelation(ActionEvent actionEvent) {
        relationLineDrawer();
    }
}
