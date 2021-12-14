package de.snaggly.bossmodellerfx;

import de.snaggly.bossmodellerfx.model.subdata.Relation;
import de.snaggly.bossmodellerfx.model.view.Comment;
import de.snaggly.bossmodellerfx.model.view.Entity;
import de.snaggly.bossmodellerfx.view.CrowsFootShape;
import de.snaggly.bossmodellerfx.guiLogic.GUIMethods;
import de.snaggly.bossmodellerfx.guiLogic.Project;
import de.snaggly.bossmodellerfx.struct.relations.ConnectingOrientation;
import de.snaggly.bossmodellerfx.struct.relations.EntityViewConnections;
import de.snaggly.bossmodellerfx.view.RelationViewNode;
import de.snaggly.bossmodellerfx.view.CommentView;
import de.snaggly.bossmodellerfx.view.EntityView;
import de.snaggly.bossmodellerfx.view.RelationLineView;
import de.snaggly.bossmodellerfx.view.factory.nodetype.CommentBuilder;
import de.snaggly.bossmodellerfx.view.factory.nodetype.EntityBuilder;
import de.snaggly.bossmodellerfx.view.factory.windowtype.EntityEditorWindowBuilder;
import de.snaggly.bossmodellerfx.view.factory.windowtype.RelationEditorWindowBuilder;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.io.*;
import java.util.HashMap;

public class MainController {
    @FXML
    private Pane mainWorkbench;

    private Project currentProject;
    private final HashMap<Entity, EntityView> entitiesOverview = new HashMap<>();
    private final HashMap<Relation, RelationViewNode> relationsOverview = new HashMap<>();

    private final ContextMenu mainWorkbenchContextMenu = new ContextMenu();

    private void relationLineDrawer() { //For future: Follow State-Pattern
        var entityViewConnectionsOverview = new HashMap<Entity, EntityViewConnections>();

        for (var relation : currentProject.getRelations()) {
            var entityAConnections = entityViewConnectionsOverview.get(relation.getTableA());
            if (entityAConnections == null) {
                entityAConnections = new EntityViewConnections();
                entityViewConnectionsOverview.put(relation.getTableA(), entityAConnections);
            }
            var entityBConnections = entityViewConnectionsOverview.get(relation.getTableB());
            if (entityBConnections == null) {
                entityBConnections = new EntityViewConnections();
                entityViewConnectionsOverview.put(relation.getTableB(), entityBConnections);
            }
            var node1 = entitiesOverview.get(relation.getTableA());
            var node2 = entitiesOverview.get(relation.getTableB());

            var relationViewNode = relationsOverview.get(relation);

            if (node1 == null || node2 == null) {
                currentProject.removeRelation(relation);

                if (relationViewNode != null) {
                    if (relationViewNode.crowsFootA != null) {
                        mainWorkbench.getChildren().removeAll(relationViewNode.crowsFootA.getAllNodes());
                    }
                    if (relationViewNode.crowsFootB != null) {
                        mainWorkbench.getChildren().removeAll(relationViewNode.crowsFootB.getAllNodes());
                    }
                    if (relationViewNode.line1 != null) {
                        mainWorkbench.getChildren().remove(relationViewNode.line1);
                    }
                    if (relationViewNode.line2 != null) {
                        mainWorkbench.getChildren().remove(relationViewNode.line2);
                    }
                    if (relationViewNode.line3 != null) {
                        mainWorkbench.getChildren().remove(relationViewNode.line3);
                    }
                }
                continue;
            }

            if (relationViewNode == null) {
                relationViewNode = new RelationViewNode(relation);
                relationViewNode.line1 = new RelationLineView(relationViewNode, currentProject::setCurrentSelected);
                relationViewNode.line2 = new RelationLineView(relationViewNode, currentProject::setCurrentSelected);
                relationViewNode.line3 = new RelationLineView(relationViewNode, currentProject::setCurrentSelected);
                relationViewNode.line4 = new RelationLineView(relationViewNode, currentProject::setCurrentSelected);
                relationsOverview.put(relation, relationViewNode);

                mainWorkbench.getChildren().addAll(
                        relationViewNode.line1,
                        relationViewNode.line2,
                        relationViewNode.line3,
                        relationViewNode.line4);
            }

            var crowsFootA = relationViewNode.crowsFootA;
            var crowsFootB = relationViewNode.crowsFootB;

            if (crowsFootA != null) {
                mainWorkbench.getChildren().removeAll(crowsFootA.getAllNodes());
            }
            if (crowsFootB != null) {
                mainWorkbench.getChildren().removeAll(crowsFootB.getAllNodes());
            }

            if (node1 == node2) {
                entityAConnections.increaseEastConnections();
                entityBConnections.increaseSouthConnections();
                relation.orientation = ConnectingOrientation.SELF;
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
            if (node1mx <= node2mx && node1my >= node2my) { //1Q
                if ((node2y + node2h) >= node1y && (node2y + node2h) <= Math.abs(node2h - node1h) + (node1y + node1h)) { //R
                    entityAConnections.increaseEastConnections();
                    entityBConnections.increaseWestConnections();
                    relation.orientation = ConnectingOrientation.Q1_R;
                }
                else if (Math.abs(node2w - node1w) + node2x >= node1x && node2x <= (node1x + node1w)) { //O
                    entityAConnections.increaseNorthConnections();
                    entityBConnections.increaseSouthConnections();
                    relation.orientation = ConnectingOrientation.Q1_O;
                }
                else {
                    while (midLineX < node2mx && midLineY > node2my) {
                        midLineX++;
                        midLineY--;
                    }
                    if (midLineY <= node2my) { //MatchYin1R
                        entityAConnections.increaseEastConnections();
                        entityBConnections.increaseSouthConnections();
                        relation.orientation = ConnectingOrientation.Q1_R1;
                    }
                    else if (midLineX >= node2mx) { //MatchXin4R
                        entityAConnections.increaseNorthConnections();
                        entityBConnections.increaseWestConnections();
                        relation.orientation = ConnectingOrientation.Q1_R4;
                    }
                }
            }
            else if (node1mx <= node2mx && node1my <= node2my) { //2Q
                if (node2y + Math.abs(node2h - node1h) >= node1y && node2y <= (node1y + node1h)) { //R
                    entityAConnections.increaseEastConnections();
                    entityBConnections.increaseWestConnections();
                    relation.orientation = ConnectingOrientation.Q2_R;
                }
                else if (Math.abs(node2h - node1h) + node2x >= node1x && node2x <= (node1x + node1w)) { //U
                    entityAConnections.increaseSouthConnections();
                    entityBConnections.increaseNorthConnections();
                    relation.orientation = ConnectingOrientation.Q2_U;
                }
                else {
                    while (midLineX < node2mx && midLineY < node2my) {
                        midLineX++;
                        midLineY++;
                    }
                    if (midLineY >= node2my) { //MatchYin1R
                        entityAConnections.increaseEastConnections();
                        entityBConnections.increaseNorthConnections();
                        relation.orientation = ConnectingOrientation.Q2_R1;
                    }
                    else if (midLineX >= node2mx) { //MatchXin2R
                        entityAConnections.increaseSouthConnections();
                        entityBConnections.increaseWestConnections();
                        relation.orientation = ConnectingOrientation.Q2_R2;
                    }
                }
            }
            else if (node1mx >= node2mx && node1my <= node2my) { //3Q
                if ((node2y + Math.abs(node2h - node1h)) >= node1y && node2y <= (node1y + node1h)) { //L
                    entityAConnections.increaseWestConnections();
                    entityBConnections.increaseEastConnections();
                    relation.orientation = ConnectingOrientation.Q3_L;
                }
                else if ((node2x + node2w) >= node1x && (node2x + node2w) <= Math.abs(node2w - node1w) + (node1x + node1w)) { //U
                    entityAConnections.increaseSouthConnections();
                    entityBConnections.increaseNorthConnections();
                    relation.orientation = ConnectingOrientation.Q3_U;
                }
                else {
                    while (midLineX > node2mx && midLineY < node2my) {
                        midLineX--;
                        midLineY++;
                    }
                    if (midLineY >= node2my) { //MatchYin3R
                        entityAConnections.increaseWestConnections();
                        entityBConnections.increaseNorthConnections();
                        relation.orientation = ConnectingOrientation.Q3_R3;
                    }
                    else if (midLineX <= node2mx) { //MatchXin2R
                        entityAConnections.increaseSouthConnections();
                        entityBConnections.increaseEastConnections();
                        relation.orientation = ConnectingOrientation.Q3_R2;
                    }
                }
            }
            else if (node1mx >= node2mx && node1my >= node2my) { //4Q
                if ((node2y + node2h) >= node1y && (node2y + node2h) <= Math.abs(node2h - node1h) + (node1y + node1h)) { //L
                    entityAConnections.increaseWestConnections();
                    entityBConnections.increaseEastConnections();
                    relation.orientation = ConnectingOrientation.Q4_L;
                }
                else if ((node2x + node2w) >= node1x && (node2x + node2w) <= Math.abs(node2w - node1w) + (node1x + node1w)) { //O
                    entityAConnections.increaseNorthConnections();
                    entityBConnections.increaseSouthConnections();
                    relation.orientation = ConnectingOrientation.Q4_O;
                }
                else {
                    while (midLineX > node2mx && midLineY > node2my) {
                        midLineX--;
                        midLineY--;
                    }
                    if (midLineY <= node2my) { //MatchYin3R
                        entityAConnections.increaseWestConnections();
                        entityBConnections.increaseSouthConnections();
                        relation.orientation = ConnectingOrientation.Q4_R3;
                    }
                    else if (midLineX <= node2mx) { //MatchXin4R
                        entityAConnections.increaseNorthConnections();
                        entityBConnections.increaseEastConnections();
                        relation.orientation = ConnectingOrientation.Q4_R4;
                    }
                }
            }
        }

        for (var relation : currentProject.getRelations()) {
            var entityAConnections = entityViewConnectionsOverview.get(relation.getTableA());
            var entityBConnections = entityViewConnectionsOverview.get(relation.getTableB());

            double entityALeftNorth;
            double entityALeftSouth;
            double entityALeftWest;
            double entityALeftEast;
            double entityBLeftNorth;
            double entityBLeftSouth;
            double entityBLeftWest;
            double entityBLeftEast;

            var node1 = entitiesOverview.get(relation.getTableA());
            var node2 = entitiesOverview.get(relation.getTableB());

            var relationViewStruct = relationsOverview.get(relation);

            var node1w = node1.getWidth();
            var node1h = node1.getHeight();
            var node2w = node2.getWidth();
            var node2h = node2.getHeight();

            var node1x = node1.getLayoutX();
            var node1y = node1.getLayoutY();
            var node2x = node2.getLayoutX();
            var node2y = node2.getLayoutY();

            double midPointX = node2x > node1x ? node1x + node1w + (node2x - (node1x + node1w)) / 2 : node2x + node2w + (node1x - (node2x + node2w)) / 2;
            double midPointY = node2y > node1y ? node1y + node1h + (node2y - (node1y + node1h)) / 2 : node2y + node2h + (node1y - (node2y + node2h)) / 2;

            var line1 = relationViewStruct.line1;
            var line2 = relationViewStruct.line2;
            var line3 = relationViewStruct.line3;
            var line4 = relationViewStruct.line4;
            var crowsFootA = relationViewStruct.crowsFootA;
            var crowsFootB = relationViewStruct.crowsFootB;

            line1.setVisible(false);
            line2.setVisible(false);
            line3.setVisible(false);
            line4.setVisible(false);
            if (relation.getTableA().isWeakType() || relation.getTableB().isWeakType()) {
                line1.setWeakConnection();
                line2.setWeakConnection();
                line3.setWeakConnection();
                line4.setWeakConnection();
            }
            else {
                line1.setStrongConnection();
                line2.setStrongConnection();
                line3.setStrongConnection();
                line4.setStrongConnection();
            }

            switch (relation.orientation){
                case Q1_R:
                    entityALeftEast = entityAConnections.getEastConnectionsLeft();
                    entityBLeftWest = entityBConnections.getWestConnectionsLeft();
                    line1.setStartX(node1x + node1w);
                    line1.setStartY(node1y + (node1h * entityALeftEast / entityAConnections.getEastConnections()));
                    line1.setEndX(midPointX);
                    line1.setEndY(node1y + (node1h * entityALeftEast / entityAConnections.getEastConnections()));
                    line2.setStartX(midPointX);
                    line2.setStartY(node1y + (node1h * entityALeftEast / entityAConnections.getEastConnections()));
                    line2.setEndX(midPointX);
                    line2.setEndY(node2y + (node2h * entityBLeftWest / entityBConnections.getWestConnections()));
                    line3.setStartX(midPointX);
                    line3.setStartY(node2y + (node2h * entityBLeftWest / entityBConnections.getWestConnections()));
                    line3.setEndX(node2x);
                    line3.setEndY(node2y + (node2h * entityBLeftWest / entityBConnections.getWestConnections()));
                    line1.setVisible(true);
                    line2.setVisible(true);
                    line3.setVisible(true);
                    crowsFootA = new CrowsFootShape.East(node1, entityALeftEast / entityAConnections.getEastConnections());
                    crowsFootB = new CrowsFootShape.West(node2, entityBLeftWest / entityBConnections.getWestConnections());
                    break;
                case Q1_O:
                    entityALeftNorth = entityAConnections.getNorthConnectionsLeft();
                    entityBLeftSouth = entityBConnections.getSouthConnectionsLeft();
                    line1.setStartX(node1x + (node1w * entityALeftNorth / entityAConnections.getNorthConnections()));
                    line1.setStartY(node1y);
                    line1.setEndX(node1x + (node1w * entityALeftNorth / entityAConnections.getNorthConnections()));
                    line1.setEndY(midPointY);
                    line2.setStartX(node1x + (node1w * entityALeftNorth / entityAConnections.getNorthConnections()));
                    line2.setStartY(midPointY);
                    line2.setEndX(node2x + (node2w * entityBLeftSouth / entityBConnections.getSouthConnections()));
                    line2.setEndY(midPointY);
                    line3.setStartX(node2x + (node2w * entityBLeftSouth / entityBConnections.getSouthConnections()));
                    line3.setStartY(midPointY);
                    line3.setEndX(node2x + (node2w * entityBLeftSouth / entityBConnections.getSouthConnections()));
                    line3.setEndY(node2y + node2h);
                    line1.setVisible(true);
                    line2.setVisible(true);
                    line3.setVisible(true);
                    crowsFootA = new CrowsFootShape.North(node1, entityALeftNorth / entityAConnections.getNorthConnections());
                    crowsFootB = new CrowsFootShape.South(node2, entityBLeftSouth / entityBConnections.getSouthConnections());
                    break;
                case Q1_R1:
                    entityALeftEast = entityAConnections.getEastConnectionsLeft();
                    entityBLeftSouth = entityBConnections.getSouthConnectionsLeft();
                    line1.setStartX(node1x + node1w);
                    line1.setStartY(node1y + (node1h * entityALeftEast / entityAConnections.getEastConnections()));
                    line1.setEndX(node2x + (node2w * entityBLeftSouth / entityBConnections.getSouthConnections()));
                    line1.setEndY(node1y + (node1h * entityALeftEast / entityAConnections.getEastConnections()));

                    line2.setStartX(node2x + (node2w * entityBLeftSouth / entityBConnections.getSouthConnections()));;
                    line2.setStartY(node1y + (node1h * entityALeftEast / entityAConnections.getEastConnections()));
                    line2.setEndX(node2x + (node2w * entityBLeftSouth / entityBConnections.getSouthConnections()));;
                    line2.setEndY(node2y + node2h);
                    line1.setVisible(true);
                    line2.setVisible(true);
                    crowsFootA = new CrowsFootShape.East(node1, entityALeftEast / entityAConnections.getEastConnections());
                    crowsFootB = new CrowsFootShape.South(node2, entityBLeftSouth / entityBConnections.getSouthConnections());
                    break;
                case Q1_R4:
                    entityALeftNorth = entityAConnections.getNorthConnectionsLeft();
                    entityBLeftWest = entityBConnections.getWestConnectionsLeft();
                    line1.setStartX(node1x + (node1w * entityALeftNorth / entityAConnections.getNorthConnections()));
                    line1.setStartY(node1y);
                    line1.setEndX(node1x + (node1w * entityALeftNorth / entityAConnections.getNorthConnections()));
                    line1.setEndY(node2y + (node2h * entityBLeftWest / entityBConnections.getWestConnections()));

                    line2.setStartX(node1x + (node1w * entityALeftNorth / entityAConnections.getNorthConnections()));
                    line2.setStartY(node2y + (node2h * entityBLeftWest / entityBConnections.getWestConnections()));
                    line2.setEndX(node2x);
                    line2.setEndY(node2y + (node2h * entityBLeftWest / entityBConnections.getWestConnections()));
                    line1.setVisible(true);
                    line2.setVisible(true);
                    crowsFootA = new CrowsFootShape.North(node1, entityALeftNorth / entityAConnections.getNorthConnections());
                    crowsFootB = new CrowsFootShape.West(node2, entityBLeftWest / entityBConnections.getWestConnections());
                    break;
                case Q2_R:
                    entityALeftEast = entityAConnections.getEastConnectionsLeft();
                    entityBLeftWest = entityBConnections.getWestConnectionsLeft();
                    line1.setStartX(node1x + node1w);
                    line1.setStartY(node1y + (node1h * entityALeftEast / entityAConnections.getEastConnections()));
                    line1.setEndX(midPointX);
                    line1.setEndY(node1y + (node1h * entityALeftEast / entityAConnections.getEastConnections()));
                    line2.setStartX(midPointX);
                    line2.setStartY(node1y + (node1h * entityALeftEast / entityAConnections.getEastConnections()));
                    line2.setEndX(midPointX);
                    line2.setEndY(node2y + (node2h * entityBLeftWest / entityBConnections.getWestConnections()));
                    line3.setStartX(midPointX);
                    line3.setStartY(node2y + (node2h * entityBLeftWest / entityBConnections.getWestConnections()));
                    line3.setEndX(node2x);
                    line3.setEndY(node2y + (node2h * entityBLeftWest / entityBConnections.getWestConnections()));
                    line1.setVisible(true);
                    line2.setVisible(true);
                    line3.setVisible(true);
                    crowsFootA = new CrowsFootShape.East(node1, entityALeftEast / entityAConnections.getEastConnections());
                    crowsFootB = new CrowsFootShape.West(node2, entityBLeftWest / entityBConnections.getWestConnections());
                    break;
                case Q2_U:
                    entityALeftSouth = entityAConnections.getSouthConnectionsLeft();
                    entityBLeftNorth = entityBConnections.getNorthConnectionsLeft();
                    line1.setStartX(node1x + (node1w * entityALeftSouth / entityAConnections.getSouthConnections()));
                    line1.setStartY(node1y + node1h);
                    line1.setEndX(node1x + (node1w * entityALeftSouth / entityAConnections.getSouthConnections()));
                    line1.setEndY(midPointY);
                    line2.setStartX(node1x + (node1w * entityALeftSouth / entityAConnections.getSouthConnections()));
                    line2.setStartY(midPointY);
                    line2.setEndX(node2x + (node2w * entityBLeftNorth / entityBConnections.getNorthConnections()));
                    line2.setEndY(midPointY);
                    line3.setStartX(node2x + (node2w * entityBLeftNorth / entityBConnections.getNorthConnections()));
                    line3.setStartY(midPointY);
                    line3.setEndX(node2x + (node2w * entityBLeftNorth / entityBConnections.getNorthConnections()));
                    line3.setEndY(node2y);
                    line1.setVisible(true);
                    line2.setVisible(true);
                    line3.setVisible(true);
                    crowsFootA = new CrowsFootShape.South(node1, entityALeftSouth / entityAConnections.getSouthConnections());
                    crowsFootB = new CrowsFootShape.North(node2, entityBLeftNorth / entityBConnections.getNorthConnections());
                    break;
                case Q2_R1:
                    entityALeftEast = entityAConnections.getEastConnectionsLeft();
                    entityBLeftNorth = entityBConnections.getNorthConnectionsLeft();
                    line1.setStartX(node1x + node1w);
                    line1.setStartY(node1y + (node1h * entityALeftEast / entityAConnections.getEastConnections()));
                    line1.setEndX(node2x + (node2w * entityBLeftNorth / entityBConnections.getNorthConnections()));
                    line1.setEndY(node1y + (node1h * entityALeftEast / entityAConnections.getEastConnections()));

                    line2.setStartX(node2x + (node2w * entityBLeftNorth / entityBConnections.getNorthConnections()));
                    line2.setStartY(node1y + (node1h * entityALeftEast / entityAConnections.getEastConnections()));
                    line2.setEndX(node2x + (node2w * entityBLeftNorth / entityBConnections.getNorthConnections()));
                    line2.setEndY(node2y);
                    line1.setVisible(true);
                    line2.setVisible(true);
                    crowsFootA = new CrowsFootShape.East(node1, entityALeftEast / entityAConnections.getEastConnections());
                    crowsFootB = new CrowsFootShape.North(node2, entityBLeftNorth / entityBConnections.getNorthConnections());
                    break;
                case Q2_R2:
                    entityALeftSouth = entityAConnections.getSouthConnectionsLeft();
                    entityBLeftWest = entityBConnections.getWestConnectionsLeft();
                    line1.setStartX(node1x + (node1w * entityALeftSouth / entityAConnections.getSouthConnections()));
                    line1.setStartY(node1y + node1h);
                    line1.setEndX(node1x + (node1w * entityALeftSouth / entityAConnections.getSouthConnections()));
                    line1.setEndY(node2y + (node2h * entityBLeftWest / entityBConnections.getWestConnections()));

                    line2.setStartX(node1x + (node1w * entityALeftSouth / entityAConnections.getSouthConnections()));
                    line2.setStartY(node2y + (node2h * entityBLeftWest / entityBConnections.getWestConnections()));
                    line2.setEndX(node2x);
                    line2.setEndY(node2y + (node2h * entityBLeftWest / entityBConnections.getWestConnections()));
                    line1.setVisible(true);
                    line2.setVisible(true);
                    crowsFootA = new CrowsFootShape.South(node1, entityALeftSouth / entityAConnections.getSouthConnections());
                    crowsFootB = new CrowsFootShape.West(node2, entityBLeftWest / entityBConnections.getWestConnections());
                    break;
                case Q3_L:
                    entityALeftWest = entityAConnections.getWestConnectionsLeft();
                    entityBLeftEast = entityBConnections.getEastConnectionsLeft();
                    line1.setStartX(node1x);
                    line1.setStartY(node1y + (node1h * entityALeftWest / entityAConnections.getWestConnections()));
                    line1.setEndX(midPointX);
                    line1.setEndY(node1y + (node1h * entityALeftWest / entityAConnections.getWestConnections()));
                    line2.setStartX(midPointX);
                    line2.setStartY(node1y + (node1h * entityALeftWest / entityAConnections.getWestConnections()));
                    line2.setEndX(midPointX);
                    line2.setEndY(node2y + (node2h * entityBLeftEast / entityBConnections.getEastConnections()));
                    line3.setStartX(midPointX);
                    line3.setStartY(node2y + (node2h * entityBLeftEast / entityBConnections.getEastConnections()));
                    line3.setEndX(node2x + node2w);
                    line3.setEndY(node2y + (node2h * entityBLeftEast / entityBConnections.getEastConnections()));
                    line1.setVisible(true);
                    line2.setVisible(true);
                    line3.setVisible(true);
                    crowsFootA = new CrowsFootShape.West(node1, entityALeftWest / entityAConnections.getWestConnections());
                    crowsFootB = new CrowsFootShape.East(node2, entityBLeftEast / entityBConnections.getEastConnections());
                    break;
                case Q3_U:
                    entityALeftSouth = entityAConnections.getSouthConnectionsLeft();
                    entityBLeftNorth = entityBConnections.getNorthConnectionsLeft();
                    line1.setStartX(node1x + (node1w * entityALeftSouth / entityAConnections.getSouthConnections()));
                    line1.setStartY(node1y + node1h);
                    line1.setEndX(node1x + (node1w * entityALeftSouth / entityAConnections.getSouthConnections()));
                    line1.setEndY(midPointY);
                    line2.setStartX(node1x + (node1w * entityALeftSouth / entityAConnections.getSouthConnections()));
                    line2.setStartY(midPointY);
                    line2.setEndX(node2x + (node2w * entityBLeftNorth / entityBConnections.getNorthConnections()));
                    line2.setEndY(midPointY);
                    line3.setStartX(node2x + (node2w * entityBLeftNorth / entityBConnections.getNorthConnections()));
                    line3.setStartY(midPointY);
                    line3.setEndX(node2x + (node2w * entityBLeftNorth / entityBConnections.getNorthConnections()));
                    line3.setEndY(node2y);
                    line1.setVisible(true);
                    line2.setVisible(true);
                    line3.setVisible(true);
                    crowsFootA = new CrowsFootShape.South(node1, entityALeftSouth / entityAConnections.getSouthConnections());
                    crowsFootB = new CrowsFootShape.North(node2, entityBLeftNorth / entityBConnections.getNorthConnections());
                    break;
                case Q3_R3:
                    entityALeftWest = entityAConnections.getWestConnectionsLeft();
                    entityBLeftNorth = entityBConnections.getNorthConnectionsLeft();
                    line1.setStartX(node1x);
                    line1.setStartY(node1y + (node1h * entityALeftWest / entityAConnections.getWestConnections()));
                    line1.setEndX(node2x + (node2w * entityBLeftNorth / entityBConnections.getNorthConnections()));
                    line1.setEndY(node1y + (node1h * entityALeftWest / entityAConnections.getWestConnections()));

                    line2.setStartX(node2x + (node2w * entityBLeftNorth / entityBConnections.getNorthConnections()));
                    line2.setStartY(node1y + (node1h * entityALeftWest / entityAConnections.getWestConnections()));
                    line2.setEndX(node2x + (node2w * entityBLeftNorth / entityBConnections.getNorthConnections()));
                    line2.setEndY(node2y);
                    line1.setVisible(true);
                    line2.setVisible(true);
                    crowsFootA = new CrowsFootShape.West(node1, entityALeftWest / entityAConnections.getWestConnections());
                    crowsFootB = new CrowsFootShape.North(node2, entityBLeftNorth / entityBConnections.getNorthConnections());
                    break;
                case Q3_R2:
                    entityALeftSouth = entityAConnections.getSouthConnectionsLeft();
                    entityBLeftEast = entityBConnections.getEastConnectionsLeft();
                    line1.setStartX(node1x + (node1w * entityALeftSouth / entityAConnections.getSouthConnections()));
                    line1.setStartY(node1y + node1h);
                    line1.setEndX(node1x + (node1w * entityALeftSouth / entityAConnections.getSouthConnections()));
                    line1.setEndY(node2y + (node2h * entityBLeftEast / entityBConnections.getEastConnections()));

                    line2.setStartX(node1x + (node1w * entityALeftSouth / entityAConnections.getSouthConnections()));
                    line2.setStartY(node2y + (node2h * entityBLeftEast / entityBConnections.getEastConnections()));
                    line2.setEndX(node2x + node2w);
                    line2.setEndY(node2y + (node2h * entityBLeftEast / entityBConnections.getEastConnections()));
                    line1.setVisible(true);
                    line2.setVisible(true);
                    crowsFootA = new CrowsFootShape.South(node1, entityALeftSouth / entityAConnections.getSouthConnections());
                    crowsFootB = new CrowsFootShape.East(node2, entityBLeftEast / entityBConnections.getEastConnections());
                    break;
                case Q4_L:
                    entityALeftWest = entityAConnections.getWestConnectionsLeft();
                    entityBLeftEast = entityBConnections.getEastConnectionsLeft();
                    line1.setStartX(node1x);
                    line1.setStartY(node1y + (node1h * entityALeftWest / entityAConnections.getWestConnections()));
                    line1.setEndX(midPointX);
                    line1.setEndY(node1y + (node1h * entityALeftWest / entityAConnections.getWestConnections()));
                    line2.setStartX(midPointX);
                    line2.setStartY(node1y + (node1h * entityALeftWest / entityAConnections.getWestConnections()));
                    line2.setEndX(midPointX);
                    line2.setEndY(node2y + (node2h * entityBLeftEast / entityBConnections.getEastConnections()));
                    line3.setStartX(midPointX);
                    line3.setStartY(node2y + (node2h * entityBLeftEast / entityBConnections.getEastConnections()));
                    line3.setEndX(node2x + node2w);
                    line3.setEndY(node2y + (node2h * entityBLeftEast / entityBConnections.getEastConnections()));
                    line1.setVisible(true);
                    line2.setVisible(true);
                    line3.setVisible(true);
                    crowsFootA = new CrowsFootShape.West(node1, entityALeftWest / entityAConnections.getWestConnections());
                    crowsFootB = new CrowsFootShape.East(node2, entityBLeftEast / entityBConnections.getEastConnections());
                    break;
                case Q4_O:
                    entityALeftNorth = entityAConnections.getNorthConnectionsLeft();
                    entityBLeftSouth = entityBConnections.getSouthConnectionsLeft();
                    line1.setStartX(node1x + (node1w * entityALeftNorth / entityAConnections.getNorthConnections()));
                    line1.setStartY(node1y);
                    line1.setEndX(node1x + (node1w * entityALeftNorth / entityAConnections.getNorthConnections()));
                    line1.setEndY(midPointY);
                    line2.setStartX(node1x + (node1w * entityALeftNorth / entityAConnections.getNorthConnections()));
                    line2.setStartY(midPointY);
                    line2.setEndX(node2x + (node2w * entityBLeftSouth / entityBConnections.getSouthConnections()));
                    line2.setEndY(midPointY);
                    line3.setStartX(node2x + (node2w * entityBLeftSouth / entityBConnections.getSouthConnections()));
                    line3.setStartY(midPointY);
                    line3.setEndX(node2x + (node2w * entityBLeftSouth / entityBConnections.getSouthConnections()));
                    line3.setEndY(node2y + node2h);
                    line1.setVisible(true);
                    line2.setVisible(true);
                    line3.setVisible(true);
                    crowsFootA = new CrowsFootShape.North(node1, entityALeftNorth / entityAConnections.getNorthConnections());
                    crowsFootB = new CrowsFootShape.South(node2, entityBLeftSouth / entityBConnections.getSouthConnections());
                    break;
                case Q4_R3:
                    entityALeftWest = entityAConnections.getWestConnectionsLeft();
                    entityBLeftSouth = entityBConnections.getSouthConnectionsLeft();
                    line1.setStartX(node1x);
                    line1.setStartY(node1y + (node1h * entityALeftWest / entityAConnections.getWestConnections()));
                    line1.setEndX(node2x + (node2w * entityBLeftSouth / entityBConnections.getSouthConnections()));
                    line1.setEndY(node1y + (node1h * entityALeftWest / entityAConnections.getWestConnections()));

                    line2.setStartX(node2x + (node2w * entityBLeftSouth / entityBConnections.getSouthConnections()));
                    line2.setStartY(node1y + (node1h * entityALeftWest / entityAConnections.getWestConnections()));
                    line2.setEndX(node2x + (node2w * entityBLeftSouth / entityBConnections.getSouthConnections()));
                    line2.setEndY(node2y + node2h);
                    line1.setVisible(true);
                    line2.setVisible(true);
                    crowsFootA = new CrowsFootShape.West(node1, entityALeftWest / entityAConnections.getWestConnections());
                    crowsFootB = new CrowsFootShape.South(node2, entityBLeftSouth / entityBConnections.getSouthConnections());
                    break;
                case Q4_R4:
                    entityALeftNorth = entityAConnections.getNorthConnectionsLeft();
                    entityBLeftEast = entityBConnections.getEastConnectionsLeft();
                    line1.setStartX(node1x + (node1w * entityALeftNorth / entityAConnections.getNorthConnections()));
                    line1.setStartY(node1y);
                    line1.setEndX(node1x + (node1w * entityALeftNorth / entityAConnections.getNorthConnections()));
                    line1.setEndY(node2y + (node2h * entityBLeftEast / entityBConnections.getEastConnections()));

                    line2.setStartX(node1x + (node1w * entityALeftNorth / entityAConnections.getNorthConnections()));
                    line2.setStartY(node2y + (node2h * entityBLeftEast / entityBConnections.getEastConnections()));
                    line2.setEndX(node2x + node2w);
                    line2.setEndY(node2y + (node2h * entityBLeftEast / entityBConnections.getEastConnections()));
                    line1.setVisible(true);
                    line2.setVisible(true);
                    crowsFootA = new CrowsFootShape.North(node1, entityALeftNorth / entityAConnections.getNorthConnections());
                    crowsFootB = new CrowsFootShape.East(node2, entityBLeftEast / entityBConnections.getEastConnections());
                    break;
                case SELF:
                    var selfDistance = 35;
                    entityALeftEast = entityAConnections.getEastConnectionsLeft();
                    entityBLeftSouth = entityBConnections.getSouthConnectionsLeft();
                    line1.setStartX(node1x + node1w);
                    line1.setStartY(node1y + (node1h * entityALeftEast / entityAConnections.getEastConnections()));
                    line1.setEndX(node1x + node1w + selfDistance);
                    line1.setEndY(node1y + (node1h * entityALeftEast / entityAConnections.getEastConnections()));
                    line2.setStartX(node1x + node1w + selfDistance);
                    line2.setStartY(node1y + (node1h * entityALeftEast / entityAConnections.getEastConnections()));
                    line2.setEndX(node1x + node1w + selfDistance);
                    line2.setEndY(node1y + node1h + selfDistance);
                    line3.setStartX(node1x + node1w + selfDistance);
                    line3.setStartY(node1y + node1h + selfDistance);
                    line3.setEndX(node1x + (node1w * entityBLeftSouth / entityBConnections.getSouthConnections()));
                    line3.setEndY(node1y + node1h + selfDistance);
                    line4.setStartX(node1x + (node1w * entityBLeftSouth / entityBConnections.getSouthConnections()));
                    line4.setStartY(node1y + node1h + selfDistance);
                    line4.setEndX(node1x + (node1w * entityBLeftSouth / entityBConnections.getSouthConnections()));
                    line4.setEndY(node1y + node1h);
                    line1.setVisible(true);
                    line2.setVisible(true);
                    line3.setVisible(true);
                    line4.setVisible(true);
                    crowsFootA = new CrowsFootShape.East(node1, entityALeftEast / entityAConnections.getEastConnections());
                    crowsFootB = new CrowsFootShape.South(node2, entityBLeftSouth / entityBConnections.getSouthConnections());
            }

            relationViewStruct.crowsFootA = crowsFootA;
            relationViewStruct.crowsFootB = crowsFootB;

            if (crowsFootA != null && crowsFootB != null){
                crowsFootA.draw(mainWorkbench, relation.getTableA_Cardinality(), relation.getTableA_Obligation(), 0, 0);
                crowsFootB.draw(mainWorkbench, relation.getTableB_Cardinality(), relation.getTableB_Obligation(), 0, 0);
            }

            relationViewStruct.toBack();
        }
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
        } catch (IOException e) {
            GUIMethods.showError(MainController.class.getSimpleName(), "BOSSModellerFX", e.getLocalizedMessage());
        }
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
            entityBuilder.getValue().parentObserver = resultedEntity -> selectedEntityView.getController().loadModel(resultedEntity);
        } catch (Exception e) {
            GUIMethods.showError(MainController.class.getSimpleName(), "BOSSModellerFX", e.getLocalizedMessage());
        }
    }

    private void createNewComment(double xCoordinate, double yCoordinate) {
        try {
            var commentModel = new Comment("", xCoordinate, yCoordinate);
            var commentView = CommentBuilder.buildComment(commentModel, mainWorkbench, currentProject.getSelectionHandler);
            showNewComment(commentView);

            currentProject.addComment(commentModel);
        } catch (IOException e) {
            GUIMethods.showError(MainController.class.getSimpleName(), "BOSSModellerFX", e.getLocalizedMessage());
        }
    }

    private void showNewComment(CommentView commentView) {
        mainWorkbench.getChildren().add(commentView);
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
            var relationBuilderWindow = RelationEditorWindowBuilder.buildRelationEditor(currentProject);
            relationBuilderWindow.getValue().parentObserver = this::saveNewRelation;
            var stage = new Stage();
            stage.setTitle("Neue Relation");
            stage.setScene(relationBuilderWindow.getKey());
            stage.show();
        } catch (IOException e) {
            GUIMethods.showError(MainController.class.getSimpleName(), "BOSSModellerFX", e.getLocalizedMessage());
        }
    }


    private void deleteComment(CommentView selectedCommentView) {
        var selectedComment = selectedCommentView.getModel();
        mainWorkbench.getChildren().remove(selectedCommentView);
        currentProject.removeComment(selectedComment);
    }

    private void saveNewEntity(EntityView entityView) {
        currentProject.addEntity(entityView.getModel());
        showNewEntity(entityView);
    }

    private void showNewEntity(EntityView entityView) {
        entitiesOverview.put(entityView.getModel(), entityView);
        GUIMethods.bindEntityToRelationLineHandler(entityView, this::relationLineDrawer);
        mainWorkbench.getChildren().add(entityView);
    }

    private void deleteEntity(EntityView selectedEntityView) {
        var selectedEntity = selectedEntityView.getModel();
        for (var relatedRelation : selectedEntity.getInvolvedRelations(currentProject.getRelations())) {
            deleteRelation(relationsOverview.get(relatedRelation));
        }

        entitiesOverview.remove(selectedEntity);
        mainWorkbench.getChildren().remove(selectedEntityView);
        currentProject.removeEntity(selectedEntity);
    }

    private void saveNewRelation(Relation dataset) {
        currentProject.addRelation(dataset);
        showNewRelation(dataset);
    }

    private void showNewRelation(Relation relation) {
        var tableAView = entitiesOverview.get(relation.getTableA());
        var tableBView = entitiesOverview.get(relation.getTableB());
        tableAView.getController().loadModel(relation.getTableA());
        if (tableAView != tableBView) {
            tableBView.getController().loadModel(relation.getTableB());
        }

        relationLineDrawer();
    }

    private void deleteRelation(RelationViewNode relationView) {
        mainWorkbench.getChildren().removeAll(relationView.getAllNodes());

        var relation = relationView.getModel();
        relation.getTableA().setWeakType(false);
        relation.getTableB().setWeakType(false);
        var fkA = relation.getFkAttributeA();
        if (fkA != null) {
            relation.getTableA().getAttributes().remove(fkA);
        }
        var fkB = relation.getFkAttributeB();
        if (fkB != null) {
            relation.getTableB().getAttributes().remove(fkB);
            var entityView = entitiesOverview.get(relation.getTableB());
            entityView.getController().loadModel(relation.getTableB());
        }
        var entityViewA = entitiesOverview.get(relation.getTableA());
        entityViewA.getController().loadModel(relation.getTableA());
        var entityViewB = entitiesOverview.get(relation.getTableB());
        entityViewB.getController().loadModel(relation.getTableB());

        relationsOverview.remove(relation);
        currentProject.getRelations().remove(relationView.getModel());
        relationLineDrawer();
    }

    private void editRelation(RelationViewNode relationView) {
        var selectedRelationModel = relationView.getModel();

        try {
            var relationBuilderWindow = RelationEditorWindowBuilder.buildRelationEditor(selectedRelationModel, currentProject);
            relationBuilderWindow.getValue().parentObserver = dataset -> {
                deleteRelation(relationView);
                saveNewRelation(dataset);
            };
            var stage = new Stage();
            stage.setTitle("Relation bearbeiten");
            stage.setScene(relationBuilderWindow.getKey());
            stage.show();
        } catch (IOException e) {
            GUIMethods.showError(MainController.class.getSimpleName(), "BOSSModellerFX", e.getLocalizedMessage());
        }
    }

    private void reInitProject() throws IOException {
        for (var entity : currentProject.getEntities()) {
            var entityView = EntityBuilder.buildEntity(entity, mainWorkbench, currentProject.getSelectionHandler);
            showNewEntity(entityView);
        }

        for (var comment : currentProject.getComments()) {
            var commentView = CommentBuilder.buildComment(comment, mainWorkbench, currentProject.getSelectionHandler);
            showNewComment(commentView);
        }

        for (var relation : currentProject.getRelations()) {
            showNewRelation(relation);
        }
    }

    private void clearProject() {
        currentProject.clear();
        relationsOverview.clear();
        entitiesOverview.clear();
    }

    @FXML
    private void openFileClick(ActionEvent actionEvent) {
        var file = GUIMethods.showJSONFileOpenDialog("Projekt öffnen", mainWorkbench.getScene().getWindow());
        if (file == null)
            return;
        try {
            var bufferedReader = new BufferedReader(new FileReader(file));
            StringBuilder json = new StringBuilder();
            while (bufferedReader.ready()) {
                json.append(bufferedReader.readLine());
            }
            bufferedReader.close();
            clearProject();
            currentProject = Project.deserializeFromJson(json.toString(), mainWorkbench);
            reInitProject();

        } catch (Exception e) {
            GUIMethods.showError(MainController.class.getSimpleName(), "BOSSModellerFX", e.getLocalizedMessage());
        }
    }

    @FXML
    private void saveFileClick(ActionEvent actionEvent) {
        var file = GUIMethods.showJSONFileSaveDialog("Projekt speichern", mainWorkbench.getScene().getWindow());
        if (file == null)
            return;

        var json = currentProject.serializeToJson();
        try {
            var fileWriter = new BufferedWriter(new FileWriter(file));
            fileWriter.write(json);
            fileWriter.close();
        } catch (Exception e) {
            GUIMethods.showError(MainController.class.getSimpleName(), "BOSSModellerFX", e.getLocalizedMessage());
        }
    }

    @FXML
    private void exportPictureClick(ActionEvent actionEvent) {
        var snapshot = mainWorkbench.snapshot(new SnapshotParameters(), null);
        var file = GUIMethods.showPNGFileSaveDialog("Bild exportieren", mainWorkbench.getScene().getWindow());
        if (file == null)
            return;
        try {
            ImageIO.write(SwingFXUtils.fromFXImage(snapshot, null), "png", file);
        }
        catch (Exception e) {
            GUIMethods.showError(MainController.class.getSimpleName(), "BOSSModellerFX", e.getLocalizedMessage());
        }
    }

    private final static String jsonTest = "{\"entities\":[{\"uniqueCombination\":{\"attributeCombination\":[]},\"name\":\"Test1\",\"attributes\":[{\"name\":\"Attr1\",\"type\":\"\",\"isPrimary\":true,\"isNonNull\":true,\"isUnique\":true,\"checkName\":\"\",\"defaultName\":\"\"},{\"name\":\"Attr1\",\"type\":\"\",\"isPrimary\":false,\"isNonNull\":true,\"isUnique\":true,\"checkName\":\"\",\"defaultName\":\"\",\"fkTableColumn\":{\"name\":\"Attr1\",\"type\":\"\",\"isPrimary\":true,\"isNonNull\":true,\"isUnique\":true,\"checkName\":\"\",\"defaultName\":\"\"}}],\"isWeakType\":false,\"xCoordinate\":10.0,\"yCoordinate\":10.0},{\"uniqueCombination\":{\"attributeCombination\":[{\"attributeCombinations\":[1,2],\"combinationName\":\"Combo1\"}]},\"name\":\"Test2\",\"attributes\":[{\"name\":\"Attr1\",\"type\":\"\",\"isPrimary\":true,\"isNonNull\":true,\"isUnique\":true,\"checkName\":\"\",\"defaultName\":\"\"},{\"name\":\"Attr2\",\"type\":\"\",\"isPrimary\":false,\"isNonNull\":false,\"isUnique\":false,\"checkName\":\"\",\"defaultName\":\"\"},{\"name\":\"Attr3\",\"type\":\"\",\"isPrimary\":false,\"isNonNull\":false,\"isUnique\":false,\"checkName\":\"\",\"defaultName\":\"\"}],\"isWeakType\":false,\"xCoordinate\":118.0,\"yCoordinate\":182.0},{\"uniqueCombination\":{\"attributeCombination\":[]},\"name\":\"Test3\",\"attributes\":[{\"name\":\"Attr1\",\"type\":\"\",\"isPrimary\":true,\"isNonNull\":true,\"isUnique\":true,\"checkName\":\"\",\"defaultName\":\"\"},{\"name\":\"Attr2\",\"type\":\"\",\"isPrimary\":false,\"isNonNull\":true,\"isUnique\":true,\"checkName\":\"dfs\",\"defaultName\":\"dsf\"},{\"name\":\"Attr1\",\"type\":\"\",\"isPrimary\":false,\"isNonNull\":true,\"isUnique\":true,\"checkName\":\"\",\"defaultName\":\"\",\"fkTableColumn\":{\"name\":\"Attr1\",\"type\":\"\",\"isPrimary\":true,\"isNonNull\":true,\"isUnique\":true,\"checkName\":\"\",\"defaultName\":\"\"}}],\"isWeakType\":true,\"xCoordinate\":293.0,\"yCoordinate\":54.0}],\"comments\":[{\"text\":\"Comment\\n1\\n2\\n\\u003c\\u003e\",\"width\":0.0,\"height\":0.0,\"xCoordinate\":272.0,\"yCoordinate\":210.0}],\"relations\":[{\"tableAIndex\":0,\"tableBIndex\":1,\"tableA_Cardinality\":\"ONE\",\"tableB_Cardinality\":\"MANY\",\"tableA_Obligation\":\"CAN\",\"tableB_Obligation\":\"CAN\",\"orientation\":\"Q2_R2\"},{\"tableAIndex\":1,\"tableBIndex\":2,\"tableA_Cardinality\":\"MANY\",\"tableB_Cardinality\":\"ONE\",\"tableA_Obligation\":\"CAN\",\"tableB_Obligation\":\"CAN\",\"orientation\":\"Q1_R\"}]}";

    @FXML
    private void onKeyPressed(KeyEvent keyEvent) {
        currentProject.addPressedKey(keyEvent.getCode());
    }

    @FXML
    private void onKeyReleased(KeyEvent keyEvent) {
        currentProject.removePressedKey(keyEvent.getCode());
    }

    @FXML
    private void startNewProject(ActionEvent actionEvent) {
        clearProject();
    }
}
