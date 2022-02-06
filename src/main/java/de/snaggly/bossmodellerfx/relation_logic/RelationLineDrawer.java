package de.snaggly.bossmodellerfx.relation_logic;

import de.snaggly.bossmodellerfx.guiLogic.Project;
import de.snaggly.bossmodellerfx.model.subdata.Relation;
import de.snaggly.bossmodellerfx.model.view.Entity;
import de.snaggly.bossmodellerfx.view.CrowsFootShape;
import de.snaggly.bossmodellerfx.view.EntityView;
import de.snaggly.bossmodellerfx.view.RelationLineView;
import de.snaggly.bossmodellerfx.view.RelationViewNode;

import java.util.HashMap;

public class RelationLineDrawer {
    public static void drawAllLines(Project currentProject, HashMap<Entity, EntityView> entitiesOverview, HashMap<Relation, RelationViewNode> relationsOverview) { //For future: Follow State-Pattern
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
                        currentProject.getWorkField().getChildren().removeAll(relationViewNode.crowsFootA.getAllNodes());
                    }
                    if (relationViewNode.crowsFootB != null) {
                        currentProject.getWorkField().getChildren().removeAll(relationViewNode.crowsFootB.getAllNodes());
                    }
                    if (relationViewNode.line1 != null) {
                        currentProject.getWorkField().getChildren().remove(relationViewNode.line1);
                    }
                    if (relationViewNode.line2 != null) {
                        currentProject.getWorkField().getChildren().remove(relationViewNode.line2);
                    }
                    if (relationViewNode.line3 != null) {
                        currentProject.getWorkField().getChildren().remove(relationViewNode.line3);
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

                currentProject.getWorkField().getChildren().addAll(
                        relationViewNode.line1,
                        relationViewNode.line2,
                        relationViewNode.line3,
                        relationViewNode.line4);
            }

            var crowsFootA = relationViewNode.crowsFootA;
            var crowsFootB = relationViewNode.crowsFootB;

            if (crowsFootA != null) {
                currentProject.getWorkField().getChildren().removeAll(crowsFootA.getAllNodes());
            }
            if (crowsFootB != null) {
                currentProject.getWorkField().getChildren().removeAll(crowsFootB.getAllNodes());
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
                crowsFootA.draw(currentProject.getWorkField(), relation.getTableA_Cardinality(), relation.getTableA_Obligation(), 0, 0);
                crowsFootB.draw(currentProject.getWorkField(), relation.getTableB_Cardinality(), relation.getTableB_Obligation(), 0, 0);
            }

            relationViewStruct.toBack();
        }
    }
}
