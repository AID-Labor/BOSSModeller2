package de.snaggly.bossmodellerfx.view;

import de.snaggly.bossmodellerfx.model.Relation;
import de.snaggly.bossmodellerfx.view.viewtypes.Highlightable;
import javafx.beans.value.ChangeListener;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;

import java.util.ArrayList;
import java.util.Collection;

public abstract class CrowsFootShape implements Highlightable {
    private static final int scale = 5;

    private final double positionShift;
    private final EntityView entity;

    private final ArrayList<Shape> elementsList = new ArrayList<>();
    private final MoveStrategy[] movers = new MoveStrategy[2];

    public Line multiplicityLineOne = new Line();
    public Line multiplicityLineMultiple1 = new Line();
    public Line multiplicityLineMultiple2 = new Line();

    public Line mandatoryLine = new Line();
    public Circle optionalCircle = new Circle();

    @Override
    public void highlight() {
        multiplicityLineOne.setStroke(Color.rgb(3,158,211));
        multiplicityLineMultiple1.setStroke(Color.rgb(3,158,211));
        multiplicityLineMultiple2.setStroke(Color.rgb(3,158,211));
        mandatoryLine.setStroke(Color.rgb(3,158,211));
        optionalCircle.setStroke(Color.rgb(3,158,211));
    }

    @Override
    public void deHighlight() {
        multiplicityLineOne.setStroke(Color.BLACK);
        multiplicityLineMultiple1.setStroke(Color.BLACK);
        multiplicityLineMultiple2.setStroke(Color.BLACK);
        mandatoryLine.setStroke(Color.BLACK);
        optionalCircle.setStroke(Color.BLACK);
    }

    public CrowsFootShape(EntityView entity, double positionShift) {
        multiplicityLineOne.setStrokeWidth(3);
        multiplicityLineMultiple1.setStrokeWidth(3);
        multiplicityLineMultiple2.setStrokeWidth(3);
        mandatoryLine.setStrokeWidth(3);
        optionalCircle.setStrokeWidth(3);
        optionalCircle.setRadius(scale);
        optionalCircle.setFill(Color.WHITE);
        optionalCircle.setStroke(Color.BLACK);

        this.entity = entity;
        this.positionShift = positionShift;

        elementsList.add(multiplicityLineOne);
        elementsList.add(multiplicityLineMultiple1);
        elementsList.add(multiplicityLineMultiple2);
        elementsList.add(mandatoryLine);
        elementsList.add(optionalCircle);
    }

    public Collection<Shape> getAllNodes() {
        return elementsList;
    }

    public abstract ChangeListener<Number> getMultiplicityMany_XListener(Pane parentPane);
    public abstract void drawMultiplicityMany_X(double xOffset);
    public abstract ChangeListener<Number> getMultiplicityMany_YListener(Pane parentPane);
    public abstract void drawMultiplicityMany_Y(double yOffset);
    public abstract ChangeListener<Number> getMultiplicityOne_XListener(Pane parentPane);
    public abstract void drawMultiplicityOne_X(double xOffset);
    public abstract ChangeListener<Number> getMultiplicityOne_YListener(Pane parentPane);
    public abstract void drawMultiplicityOne_Y(double yOffset);

    public abstract ChangeListener<Number> getMandatory_XListener(Pane parentPane);
    public abstract void drawMandatory_X(double xOffset);
    public abstract ChangeListener<Number> getMandatory_YListener(Pane parentPane);
    public abstract void drawMandatory_Y(double yOffset);
    public abstract ChangeListener<Number> getOptional_XListener(Pane parentPane);
    public abstract void drawOptional_X(double xOffset);
    public abstract ChangeListener<Number> getOptional_YListener(Pane parentPane);
    public abstract void drawOptional_Y(double yOffset);

    private final ArrayList<ChangeListener<Number>> listeners = new ArrayList<>();

    public void bindCrowsFootView(Pane parentPane, Relation.Cardinality cardinality, Relation.Obligation obligation) {
        if (cardinality == Relation.Cardinality.MANY) {
            parentPane.getChildren().addAll(this.multiplicityLineMultiple1, this.multiplicityLineMultiple2);
            entity.layoutXProperty().addListener(this.getMultiplicityMany_XListener((Pane) entity.getParent()));
            entity.widthProperty().addListener(this.getMultiplicityMany_XListener((Pane) entity.getParent()));
            entity.layoutYProperty().addListener(this.getMultiplicityMany_YListener((Pane) entity.getParent()));
            entity.heightProperty().addListener(this.getMultiplicityMany_YListener((Pane) entity.getParent()));
        } else {
            parentPane.getChildren().add(this.multiplicityLineOne);
            entity.layoutXProperty().addListener(this.getMultiplicityOne_XListener((Pane) entity.getParent()));
            entity.widthProperty().addListener(this.getMultiplicityOne_XListener((Pane) entity.getParent()));
            entity.layoutYProperty().addListener(this.getMultiplicityOne_YListener((Pane) entity.getParent()));
            entity.heightProperty().addListener(this.getMultiplicityOne_YListener((Pane) entity.getParent()));
        }

        if (obligation == Relation.Obligation.CAN) {
            parentPane.getChildren().add(this.optionalCircle);
            entity.layoutXProperty().addListener(this.getOptional_XListener((Pane) entity.getParent()));
            entity.widthProperty().addListener(this.getOptional_XListener((Pane) entity.getParent()));
            entity.layoutYProperty().addListener(this.getOptional_YListener((Pane) entity.getParent()));
            entity.heightProperty().addListener(this.getOptional_YListener((Pane) entity.getParent()));
        } else {
            parentPane.getChildren().add(this.mandatoryLine);
            entity.layoutXProperty().addListener(this.getMandatory_XListener((Pane) entity.getParent()));
            entity.widthProperty().addListener(this.getMandatory_XListener((Pane) entity.getParent()));
            entity.layoutYProperty().addListener(this.getMandatory_YListener((Pane) entity.getParent()));
            entity.heightProperty().addListener(this.getMandatory_YListener((Pane) entity.getParent()));
        }
    }

    public void draw(Pane parentPane, Relation.Cardinality cardinality, Relation.Obligation obligation, double xOffset, double yOffset) {
        if (cardinality == Relation.Cardinality.MANY) {
            parentPane.getChildren().addAll(this.multiplicityLineMultiple1, this.multiplicityLineMultiple2);
            movers[0] = () -> {
                this.drawMultiplicityMany_X(xOffset);
                this.drawMultiplicityMany_Y(yOffset);
            };
        } else {
            parentPane.getChildren().add(this.multiplicityLineOne);
            movers[0] = () -> {
                this.drawMultiplicityOne_X(xOffset);
                this.drawMultiplicityOne_Y(yOffset);
            };
        }

        if (obligation == Relation.Obligation.CAN) {
            parentPane.getChildren().add(this.optionalCircle);
            movers[1] = () -> {
                this.drawOptional_X(xOffset);
                this.drawOptional_Y(yOffset);
            };
        } else {
            parentPane.getChildren().add(this.mandatoryLine);
            movers[1] = () -> {
                this.drawMandatory_X(xOffset);
                this.drawMandatory_Y(yOffset);
            };
        }

        try {
            move();
        } catch (NotDrawnException e) {
            e.printStackTrace();
        }
    }

    public void move() throws NotDrawnException {
        try {
            movers[0].move();
            movers[1].move();
        }
        catch (NullPointerException npe) {
            throw new NotDrawnException();
        }
    }

    public void unbindCrowsFootView(Pane parentPane) {
        entity.layoutXProperty().removeListener(this.getMultiplicityMany_XListener(parentPane));
        entity.widthProperty().removeListener(this.getMultiplicityMany_XListener(parentPane));
        entity.layoutYProperty().removeListener(this.getMultiplicityMany_YListener(parentPane));
        entity.heightProperty().removeListener(this.getMultiplicityMany_YListener(parentPane));
        entity.layoutXProperty().removeListener(this.getMultiplicityOne_XListener(parentPane));
        entity.widthProperty().removeListener(this.getMultiplicityOne_XListener(parentPane));
        entity.layoutYProperty().removeListener(this.getMultiplicityOne_YListener(parentPane));
        entity.heightProperty().removeListener(this.getMultiplicityOne_YListener(parentPane));
        entity.layoutXProperty().removeListener(this.getOptional_XListener(parentPane));
        entity.widthProperty().removeListener(this.getOptional_XListener(parentPane));
        entity.layoutYProperty().removeListener(this.getOptional_YListener(parentPane));
        entity.heightProperty().removeListener(this.getOptional_YListener(parentPane));
        entity.layoutXProperty().removeListener(this.getMandatory_XListener(parentPane));
        entity.widthProperty().removeListener(this.getMandatory_XListener(parentPane));
        entity.layoutYProperty().removeListener(this.getMandatory_YListener(parentPane));
        entity.heightProperty().removeListener(this.getMandatory_YListener(parentPane));
        parentPane.getChildren().removeAll(
                this.multiplicityLineMultiple1,
                this.multiplicityLineMultiple2,
                this.multiplicityLineOne,
                this.optionalCircle,
                this.mandatoryLine
        );
    }

    private interface MoveStrategy {
        void move();
    }

    public static class NotDrawnException extends Exception {}

    public static class East extends CrowsFootShape {
        public East(EntityView entity, double positionShift) {
            super(entity, positionShift);
        }

        @Override
        public ChangeListener<Number> getMultiplicityMany_XListener(Pane parent) {
            return (observableValue, number, t1) -> {
                drawMultiplicityMany_X(parent.getLayoutX());
            };
        }

        @Override
        public void drawMultiplicityMany_X(double xCoordinateOffset) {
            multiplicityLineMultiple1.setStartX(super.entity.getLayoutX() + super.entity.getWidth() + xCoordinateOffset);
            multiplicityLineMultiple1.setEndX(super.entity.getLayoutX() + super.entity.getWidth()+ xCoordinateOffset + (scale * 3));
            multiplicityLineMultiple2.setStartX(super.entity.getLayoutX() + super.entity.getWidth() + xCoordinateOffset);
            multiplicityLineMultiple2.setEndX(super.entity.getLayoutX() + super.entity.getWidth()+ xCoordinateOffset + (scale * 3));
        }

        @Override
        public ChangeListener<Number> getMultiplicityMany_YListener(Pane parentPane) {
            return (observableValue, number, t1) -> {
                drawMultiplicityMany_Y(parentPane.getLayoutY());
            };
        }

        @Override
        public void drawMultiplicityMany_Y(double yCoordinateOffset) {
            multiplicityLineMultiple1.setStartY((super.entity.getHeight() * super.positionShift) + super.entity.getLayoutY() - (scale * 2) + yCoordinateOffset);
            multiplicityLineMultiple1.setEndY((super.entity.getHeight() * super.positionShift) + super.entity.getLayoutY() + yCoordinateOffset);
            multiplicityLineMultiple2.setStartY((super.entity.getHeight() * super.positionShift) + super.entity.getLayoutY() + (scale * 2) + yCoordinateOffset);
            multiplicityLineMultiple2.setEndY((super.entity.getHeight() * super.positionShift) + super.entity.getLayoutY() + yCoordinateOffset);
        }

        @Override
        public ChangeListener<Number> getMultiplicityOne_XListener(Pane parentPane) {
            return (observableValue, number, t1) -> {
                drawMultiplicityOne_X(parentPane.getLayoutX());
            };
        }

        @Override
        public void drawMultiplicityOne_X(double xCoordinateOffset) {
            multiplicityLineOne.setStartX(super.entity.getLayoutX() + super.entity.getWidth() + (scale * 1.5) + xCoordinateOffset);
            multiplicityLineOne.setEndX(super.entity.getLayoutX() + super.entity.getWidth() + (scale * 1.5) + xCoordinateOffset);
        }

        @Override
        public ChangeListener<Number> getMultiplicityOne_YListener(Pane parentPane) {
            return (observableValue, number, t1) -> {
                drawMultiplicityOne_Y(parentPane.getLayoutY());
            };
        }

        @Override
        public void drawMultiplicityOne_Y(double yOffset) {
            multiplicityLineOne.setStartY((super.entity.getHeight() * super.positionShift) + super.entity.getLayoutY() - (scale * 2) + yOffset);
            multiplicityLineOne.setEndY((super.entity.getHeight() * super.positionShift) + super.entity.getLayoutY() + (scale * 2) + yOffset);
        }

        @Override
        public ChangeListener<Number> getMandatory_XListener(Pane parentPane) {
            return (observableValue, number, t1) -> {
                drawMandatory_X(parentPane.getLayoutX());
            };
        }

        @Override
        public void drawMandatory_X(double xOffset) {
            mandatoryLine.setStartX(super.entity.getLayoutX() + super.entity.getWidth() + xOffset + (scale * 3));
            mandatoryLine.setEndX(super.entity.getLayoutX() + super.entity.getWidth() + xOffset + (scale * 3));
        }

        @Override
        public ChangeListener<Number> getMandatory_YListener(Pane parentPane) {
            return (observableValue, number, t1) -> {
                drawMandatory_Y(parentPane.getLayoutY());
            };
        }

        @Override
        public void drawMandatory_Y(double yOffset) {
            mandatoryLine.setStartY((super.entity.getHeight() * super.positionShift) + super.entity.getLayoutY() - (scale * 2) + yOffset);
            mandatoryLine.setEndY((super.entity.getHeight() * super.positionShift) + super.entity.getLayoutY() + (scale * 2) + yOffset);
        }

        @Override
        public ChangeListener<Number> getOptional_XListener(Pane parentPane) {
            return (observableValue, number, t1) -> {
                drawOptional_X(parentPane.getLayoutX());
            };
        }

        @Override
        public void drawOptional_X(double xOffset) {
            optionalCircle.setLayoutX(super.entity.getLayoutX() + super.entity.getWidth() + xOffset + (scale * 4));
        }

        @Override
        public ChangeListener<Number> getOptional_YListener(Pane parentPane) {
            return (observableValue, number, t1) -> {
                drawOptional_Y(parentPane.getLayoutY());
            };
        }

        @Override
        public void drawOptional_Y(double yOffset) {
            optionalCircle.setLayoutY(super.entity.getLayoutY() + (super.entity.getHeight() * super.positionShift) + yOffset);
        }
    }

    public static class West extends CrowsFootShape {
        public West(EntityView entity, double positionShift) {
            super(entity, positionShift);
        }

        @Override
        public ChangeListener<Number> getMultiplicityMany_XListener(Pane parentPane) {
            return (observableValue, number, t1) -> {
                drawMultiplicityMany_X(parentPane.getLayoutX());
            };
        }

        @Override
        public void drawMultiplicityMany_X(double xOffset) {
            multiplicityLineMultiple1.setStartX(super.entity.getLayoutX() + xOffset);
            multiplicityLineMultiple1.setEndX(super.entity.getLayoutX() + xOffset - (scale * 3));
            multiplicityLineMultiple2.setStartX(super.entity.getLayoutX() + xOffset);
            multiplicityLineMultiple2.setEndX(super.entity.getLayoutX() + xOffset - (scale * 3));
        }

        @Override
        public ChangeListener<Number> getMultiplicityMany_YListener(Pane parentPane) {
            return (observableValue, number, t1) -> {
                drawMultiplicityMany_Y(parentPane.getLayoutY());
            };
        }

        @Override
        public void drawMultiplicityMany_Y(double yOffset) {
            multiplicityLineMultiple1.setStartY((super.entity.getHeight() * super.positionShift) + super.entity.getLayoutY() - (scale * 2) + yOffset);
            multiplicityLineMultiple1.setEndY((super.entity.getHeight() * super.positionShift) + super.entity.getLayoutY() + yOffset);
            multiplicityLineMultiple2.setStartY((super.entity.getHeight() * super.positionShift) + super.entity.getLayoutY() + (scale * 2) + yOffset);
            multiplicityLineMultiple2.setEndY((super.entity.getHeight() * super.positionShift) + super.entity.getLayoutY() + yOffset);
        }

        @Override
        public ChangeListener<Number> getMultiplicityOne_XListener(Pane parentPane) {
            return (observableValue, number, t1) -> {
                drawMultiplicityOne_X(parentPane.getLayoutX());
            };
        }

        @Override
        public void drawMultiplicityOne_X(double xOffset) {
            multiplicityLineOne.setStartX(super.entity.getLayoutX() + xOffset - (scale * 1.5));
            multiplicityLineOne.setEndX(super.entity.getLayoutX() + xOffset - (scale * 1.5));
        }

        @Override
        public ChangeListener<Number> getMultiplicityOne_YListener(Pane parentPane) {
            return (observableValue, number, t1) -> {
                drawMultiplicityOne_Y(parentPane.getLayoutY());
            };
        }

        @Override
        public void drawMultiplicityOne_Y(double yOffset) {
            multiplicityLineOne.setStartY((super.entity.getHeight() * super.positionShift) + super.entity.getLayoutY() - (scale * 2) + yOffset);
            multiplicityLineOne.setEndY((super.entity.getHeight() * super.positionShift) + super.entity.getLayoutY() + (scale * 2) + yOffset);
        }

        @Override
        public ChangeListener<Number> getMandatory_XListener(Pane parentPane) {
            return (observableValue, number, t1) -> {
                drawMandatory_X(parentPane.getLayoutX());
            };
        }

        @Override
        public void drawMandatory_X(double xOffset) {
            mandatoryLine.setStartX(super.entity.getLayoutX() + xOffset - (scale * 3));
            mandatoryLine.setEndX(super.entity.getLayoutX() + xOffset - (scale * 3));
        }

        @Override
        public ChangeListener<Number> getMandatory_YListener(Pane parentPane) {
            return (observableValue, number, t1) -> {
                drawMandatory_Y(parentPane.getLayoutY());
            };
        }

        @Override
        public void drawMandatory_Y(double yOffset) {
            mandatoryLine.setStartY((super.entity.getHeight() * super.positionShift) + super.entity.getLayoutY() - (scale * 2) + yOffset);
            mandatoryLine.setEndY((super.entity.getHeight() * super.positionShift) + super.entity.getLayoutY() + (scale * 2) + yOffset);
        }

        @Override
        public ChangeListener<Number> getOptional_XListener(Pane parentPane) {
            return (observableValue, number, t1) -> {
                drawOptional_X(parentPane.getLayoutX());
            };
        }

        @Override
        public void drawOptional_X(double xOffset) {
            optionalCircle.setLayoutX(super.entity.getLayoutX() + xOffset - (scale * 4));
        }

        @Override
        public ChangeListener<Number> getOptional_YListener(Pane parentPane) {
            return (observableValue, number, t1) -> {
                drawOptional_Y(parentPane.getLayoutY());
            };
        }

        @Override
        public void drawOptional_Y(double yOffset) {
            optionalCircle.setLayoutY(super.entity.getLayoutY() + (super.entity.getHeight() * super.positionShift) + yOffset);
        }
    }

    public static class North extends CrowsFootShape {
        public North(EntityView entity, double positionShift) {
            super(entity, positionShift);
        }

        @Override
        public ChangeListener<Number> getMultiplicityMany_XListener(Pane parentPane) {
            return (observableValue, number, t1) -> {
                drawMultiplicityMany_X(parentPane.getLayoutX());
            };
        }

        @Override
        public void drawMultiplicityMany_X(double xOffset) {
            multiplicityLineMultiple1.setStartX((super.entity.getWidth() * super.positionShift) - (scale * 2) + super.entity.getLayoutX() + xOffset);
            multiplicityLineMultiple1.setEndX((super.entity.getWidth() * super.positionShift) + super.entity.getLayoutX() + xOffset);
            multiplicityLineMultiple2.setStartX((super.entity.getWidth() * super.positionShift) + (scale * 2) + super.entity.getLayoutX() + xOffset);
            multiplicityLineMultiple2.setEndX((super.entity.getWidth() * super.positionShift) + super.entity.getLayoutX() + xOffset);
        }

        @Override
        public ChangeListener<Number> getMultiplicityMany_YListener(Pane parentPane) {
            return (observableValue, number, t1) -> {
                drawMultiplicityMany_Y(parentPane.getLayoutY());
            };
        }

        @Override
        public void drawMultiplicityMany_Y(double yOffset) {
            multiplicityLineMultiple1.setStartY(super.entity.getLayoutY() + yOffset);
            multiplicityLineMultiple1.setEndY(super.entity.getLayoutY() - (scale * 3) + yOffset);
            multiplicityLineMultiple2.setStartY(super.entity.getLayoutY() + yOffset);
            multiplicityLineMultiple2.setEndY(super.entity.getLayoutY() - (scale * 3) + yOffset);
        }

        @Override
        public ChangeListener<Number> getMultiplicityOne_XListener(Pane parentPane) {
            return (observableValue, number, t1) -> {
                drawMultiplicityOne_X(parentPane.getLayoutX());
            };
        }

        @Override
        public void drawMultiplicityOne_X(double xOffset) {
            multiplicityLineOne.setStartX((super.entity.getWidth() * super.positionShift) - (scale * 2) + super.entity.getLayoutX() + xOffset);
            multiplicityLineOne.setEndX((super.entity.getWidth() * super.positionShift) + (scale * 2) + super.entity.getLayoutX() + xOffset);
        }

        @Override
        public ChangeListener<Number> getMultiplicityOne_YListener(Pane parentPane) {
            return (observableValue, number, t1) -> {
                drawMultiplicityOne_Y(parentPane.getLayoutY());
            };
        }

        @Override
        public void drawMultiplicityOne_Y(double yOffset) {
            multiplicityLineOne.setStartY(super.entity.getLayoutY() + yOffset - (scale * 1.5));
            multiplicityLineOne.setEndY(super.entity.getLayoutY() + yOffset - (scale * 1.5));
        }

        @Override
        public ChangeListener<Number> getMandatory_XListener(Pane parentPane) {
            return (observableValue, number, t1) -> {
                drawMandatory_X(parentPane.getLayoutX());
            };
        }

        @Override
        public void drawMandatory_X(double xOffset) {
            mandatoryLine.setStartX((super.entity.getWidth() * super.positionShift) + super.entity.getLayoutX() - (scale * 2) + xOffset);
            mandatoryLine.setEndX((super.entity.getWidth() * super.positionShift) + super.entity.getLayoutX() + (scale * 2) + xOffset);
        }

        @Override
        public ChangeListener<Number> getMandatory_YListener(Pane parentPane) {
            return (observableValue, number, t1) -> {
                drawMandatory_Y(parentPane.getLayoutY());
            };
        }

        @Override
        public void drawMandatory_Y(double yOffset) {
            mandatoryLine.setStartY(super.entity.getLayoutY() + yOffset - (scale * 3));
            mandatoryLine.setEndY(super.entity.getLayoutY() + yOffset - (scale * 3));
        }

        @Override
        public ChangeListener<Number> getOptional_XListener(Pane parentPane) {
            return (observableValue, number, t1) -> {
                drawOptional_X(parentPane.getLayoutX());
            };
        }

        @Override
        public void drawOptional_X(double xOffset) {
            optionalCircle.setLayoutX(super.entity.getLayoutX() + (super.entity.getWidth() * super.positionShift) + xOffset);
        }

        @Override
        public ChangeListener<Number> getOptional_YListener(Pane parentPane) {
            return (observableValue, number, t1) -> {
                drawOptional_Y(parentPane.getLayoutY());
            };
        }

        @Override
        public void drawOptional_Y(double xOffset) {
            optionalCircle.setLayoutY(super.entity.getLayoutY() + xOffset - (scale * 4));
        }
    }

    public static class South extends CrowsFootShape {
        public South(EntityView entity, double positionShift) {
            super(entity, positionShift);
        }

        @Override
        public ChangeListener<Number> getMultiplicityMany_XListener(Pane parentPane) {
            return (observableValue, number, t1) -> {
                drawMultiplicityMany_X(parentPane.getLayoutX());
            };
        }

        @Override
        public void drawMultiplicityMany_X(double xOffset) {
            multiplicityLineMultiple1.setStartX((super.entity.getWidth() * super.positionShift) - (scale * 2) + super.entity.getLayoutX() + xOffset);
            multiplicityLineMultiple1.setEndX((super.entity.getWidth() * super.positionShift) + super.entity.getLayoutX() + xOffset);
            multiplicityLineMultiple2.setStartX((super.entity.getWidth() * super.positionShift) + (scale * 2) + super.entity.getLayoutX() + xOffset);
            multiplicityLineMultiple2.setEndX((super.entity.getWidth() * super.positionShift) + super.entity.getLayoutX() + xOffset);
        }

        @Override
        public ChangeListener<Number> getMultiplicityMany_YListener(Pane parentPane) {
            return (observableValue, number, t1) -> {
                drawMultiplicityMany_Y(parentPane.getLayoutY());
            };
        }

        @Override
        public void drawMultiplicityMany_Y(double yOffset) {
            multiplicityLineMultiple1.setStartY(super.entity.getLayoutY() + yOffset + super.entity.getHeight());
            multiplicityLineMultiple1.setEndY(super.entity.getLayoutY() + (scale * 3) + yOffset + super.entity.getHeight());
            multiplicityLineMultiple2.setStartY(super.entity.getLayoutY() + yOffset + super.entity.getHeight());
            multiplicityLineMultiple2.setEndY(super.entity.getLayoutY() + (scale * 3) + yOffset + super.entity.getHeight());
        }

        @Override
        public ChangeListener<Number> getMultiplicityOne_XListener(Pane parentPane) {
            return (observableValue, number, t1) -> {
                drawMultiplicityOne_X(parentPane.getLayoutX());
            };
        }

        @Override
        public void drawMultiplicityOne_X(double xOffset) {
            multiplicityLineOne.setStartX((super.entity.getWidth() * super.positionShift) - (scale * 2) + super.entity.getLayoutX() + xOffset);
            multiplicityLineOne.setEndX((super.entity.getWidth() * super.positionShift) + (scale * 2) + super.entity.getLayoutX() + xOffset);
        }

        @Override
        public ChangeListener<Number> getMultiplicityOne_YListener(Pane parentPane) {
            return (observableValue, number, t1) -> {
                drawMultiplicityOne_Y(parentPane.getLayoutY());
            };
        }

        @Override
        public void drawMultiplicityOne_Y(double yOffset) {
            multiplicityLineOne.setStartY(super.entity.getLayoutY() + yOffset + (scale * 1.5) + super.entity.getHeight());
            multiplicityLineOne.setEndY(super.entity.getLayoutY() + yOffset + (scale * 1.5) + super.entity.getHeight());
        }

        @Override
        public ChangeListener<Number> getMandatory_XListener(Pane parentPane) {
            return (observableValue, number, t1) -> {
                drawMandatory_X(parentPane.getLayoutX());
            };
        }

        @Override
        public void drawMandatory_X(double xOffset) {
            mandatoryLine.setStartX((super.entity.getWidth() * super.positionShift) + super.entity.getLayoutX() - (scale * 2) + xOffset);
            mandatoryLine.setEndX((super.entity.getWidth() * super.positionShift) + super.entity.getLayoutX() + (scale * 2) + xOffset);
        }

        @Override
        public ChangeListener<Number> getMandatory_YListener(Pane parentPane) {
            return (observableValue, number, t1) -> {
                drawMandatory_Y(parentPane.getLayoutY());
            };
        }

        @Override
        public void drawMandatory_Y(double yOffset) {
            mandatoryLine.setStartY(super.entity.getLayoutY() + yOffset + (scale * 3) + super.entity.getHeight());
            mandatoryLine.setEndY(super.entity.getLayoutY() + yOffset + (scale * 3) + super.entity.getHeight());
        }

        @Override
        public ChangeListener<Number> getOptional_XListener(Pane parentPane) {
            return (observableValue, number, t1) -> {
                drawOptional_X(parentPane.getLayoutX());
            };
        }

        @Override
        public void drawOptional_X(double xOffset) {
            optionalCircle.setLayoutX(super.entity.getLayoutX() + (super.entity.getWidth() * super.positionShift) + xOffset);
        }

        @Override
        public ChangeListener<Number> getOptional_YListener(Pane parentPane) {
            return (observableValue, number, t1) -> {
                drawOptional_Y(parentPane.getLayoutY());
            };
        }

        @Override
        public void drawOptional_Y(double xOffset) {
            optionalCircle.setLayoutY(super.entity.getLayoutY() + xOffset + (scale * 4) + super.entity.getHeight());
        }
    }
}
