package de.snaggly.bossmodeller2.guiLogic;

import de.snaggly.bossmodeller2.model.Relation;
import de.snaggly.bossmodeller2.view.EntityView;
import javafx.beans.value.ChangeListener;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;

import java.util.ArrayList;
import java.util.Collection;

public abstract class CrowsFootShape {
    private static final int scale = 5;

    private final int connectingAmount;
    private final EntityView entity;

    private final ArrayList<Shape> elementsList = new ArrayList<>();

    public Line multiplicityLineOne = new Line();
    public Line multiplicityLineMultiple1 = new Line();
    public Line multiplicityLineMultiple2 = new Line();

    public Line mandatoryLine = new Line();
    public Circle optionalCircle = new Circle();

    public CrowsFootShape(EntityView entity, int connectingAmount) {
        multiplicityLineOne.setStrokeWidth(3);
        multiplicityLineMultiple1.setStrokeWidth(3);
        multiplicityLineMultiple2.setStrokeWidth(3);
        mandatoryLine.setStrokeWidth(3);
        optionalCircle.setStrokeWidth(3);
        optionalCircle.setRadius(scale);
        optionalCircle.setFill(Color.WHITE);
        optionalCircle.setStroke(Color.BLACK);

        this.entity = entity;
        this.connectingAmount = connectingAmount;

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
    public abstract ChangeListener<Number> getMultiplicityMany_YListener(Pane parentPane);
    public abstract ChangeListener<Number> getMultiplicityOne_XListener(Pane parentPane);
    public abstract ChangeListener<Number> getMultiplicityOne_YListener(Pane parentPane);

    public abstract ChangeListener<Number> getMandatory_XListener(Pane parentPane);
    public abstract ChangeListener<Number> getMandatory_YListener(Pane parentPane);
    public abstract ChangeListener<Number> getOptional_XListener(Pane parentPane);
    public abstract ChangeListener<Number> getOptional_YListener(Pane parentPane);

    private ArrayList<ChangeListener<Number>> listeners = new ArrayList<>();

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

    public static class East extends CrowsFootShape {
        public East(EntityView entity, int connectingAmount) {
            super(entity, connectingAmount);
        }

        @Override
        public ChangeListener<Number> getMultiplicityMany_XListener(Pane parent) {
            return (observableValue, number, t1) -> {
                multiplicityLineMultiple1.setStartX(super.entity.getLayoutX() + super.entity.getWidth() + parent.getLayoutX());
                multiplicityLineMultiple1.setEndX(super.entity.getLayoutX() + super.entity.getWidth()+ parent.getLayoutX() + (scale * 3));
                multiplicityLineMultiple2.setStartX(super.entity.getLayoutX() + super.entity.getWidth() + parent.getLayoutX());
                multiplicityLineMultiple2.setEndX(super.entity.getLayoutX() + super.entity.getWidth()+ parent.getLayoutX() + (scale * 3));
            };
        }

        @Override
        public ChangeListener<Number> getMultiplicityMany_YListener(Pane parentPane) {
            return (observableValue, number, t1) -> {
                multiplicityLineMultiple1.setStartY((super.entity.getHeight() / super.connectingAmount) + super.entity.getLayoutY() - (scale * 2) + parentPane.getLayoutY());
                multiplicityLineMultiple1.setEndY((super.entity.getHeight() / super.connectingAmount) + super.entity.getLayoutY() + parentPane.getLayoutY());
                multiplicityLineMultiple2.setStartY((super.entity.getHeight() / super.connectingAmount) + super.entity.getLayoutY() + (scale * 2) + parentPane.getLayoutY());
                multiplicityLineMultiple2.setEndY((super.entity.getHeight() / super.connectingAmount) + super.entity.getLayoutY() + parentPane.getLayoutY());
            };
        }

        @Override
        public ChangeListener<Number> getMultiplicityOne_XListener(Pane parentPane) {
            return (observableValue, number, t1) -> {
                multiplicityLineOne.setStartX(super.entity.getLayoutX() + super.entity.getWidth() + parentPane.getLayoutX() + (scale * 1.5));
                multiplicityLineOne.setEndX(super.entity.getLayoutX() + super.entity.getWidth() + parentPane.getLayoutX() + (scale * 1.5));
            };
        }

        @Override
        public ChangeListener<Number> getMultiplicityOne_YListener(Pane parentPane) {
            return (observableValue, number, t1) -> {
                multiplicityLineOne.setStartY((super.entity.getHeight() / super.connectingAmount) + super.entity.getLayoutY() - (scale * 2) + parentPane.getLayoutY());
                multiplicityLineOne.setEndY((super.entity.getHeight() / super.connectingAmount) + super.entity.getLayoutY() + (scale * 2) + parentPane.getLayoutY());
            };
        }

        @Override
        public ChangeListener<Number> getMandatory_XListener(Pane parentPane) {
            return (observableValue, number, t1) -> {
                mandatoryLine.setStartX(super.entity.getLayoutX() + super.entity.getWidth() + parentPane.getLayoutX() + (scale * 3));
                mandatoryLine.setEndX(super.entity.getLayoutX() + super.entity.getWidth() + parentPane.getLayoutX() + (scale * 3));
            };
        }

        @Override
        public ChangeListener<Number> getMandatory_YListener(Pane parentPane) {
            return (observableValue, number, t1) -> {
                mandatoryLine.setStartY((super.entity.getHeight() / super.connectingAmount) + super.entity.getLayoutY() - (scale * 2) + parentPane.getLayoutY());
                mandatoryLine.setEndY((super.entity.getHeight() / super.connectingAmount) + super.entity.getLayoutY() + (scale * 2) + parentPane.getLayoutY());
            };
        }

        @Override
        public ChangeListener<Number> getOptional_XListener(Pane parentPane) {
            return (observableValue, number, t1) -> {
                optionalCircle.setLayoutX(super.entity.getLayoutX() + super.entity.getWidth() + parentPane.getLayoutX() + (scale * 4));
            };
        }

        @Override
        public ChangeListener<Number> getOptional_YListener(Pane parentPane) {
            return (observableValue, number, t1) -> {
                optionalCircle.setLayoutY(super.entity.getLayoutY() + (super.entity.getHeight() / super.connectingAmount) + parentPane.getLayoutY());
            };
        }
    }

    public static class West extends CrowsFootShape {
        public West(EntityView entity, int connectingAmount) {
            super(entity, connectingAmount);
        }

        @Override
        public ChangeListener<Number> getMultiplicityMany_XListener(Pane parentPane) {
            return (observableValue, number, t1) -> {
                multiplicityLineMultiple1.setStartX(super.entity.getLayoutX() + parentPane.getLayoutX());
                multiplicityLineMultiple1.setEndX(super.entity.getLayoutX() + parentPane.getLayoutX() - (scale * 3));
                multiplicityLineMultiple2.setStartX(super.entity.getLayoutX() + parentPane.getLayoutX());
                multiplicityLineMultiple2.setEndX(super.entity.getLayoutX() + parentPane.getLayoutX() - (scale * 3));
            };
        }

        @Override
        public ChangeListener<Number> getMultiplicityMany_YListener(Pane parentPane) {
            return (observableValue, number, t1) -> {
                multiplicityLineMultiple1.setStartY((super.entity.getHeight() / super.connectingAmount) + super.entity.getLayoutY() - (scale * 2) + parentPane.getLayoutY());
                multiplicityLineMultiple1.setEndY((super.entity.getHeight() / super.connectingAmount) + super.entity.getLayoutY() + parentPane.getLayoutY());
                multiplicityLineMultiple2.setStartY((super.entity.getHeight() / super.connectingAmount) + super.entity.getLayoutY() + (scale * 2) + parentPane.getLayoutY());
                multiplicityLineMultiple2.setEndY((super.entity.getHeight() / super.connectingAmount) + super.entity.getLayoutY() + parentPane.getLayoutY());
            };
        }

        @Override
        public ChangeListener<Number> getMultiplicityOne_XListener(Pane parentPane) {
            return (observableValue, number, t1) -> {
                multiplicityLineOne.setStartX(super.entity.getLayoutX() + parentPane.getLayoutX() - (scale * 1.5));
                multiplicityLineOne.setEndX(super.entity.getLayoutX() + parentPane.getLayoutX() - (scale * 1.5));
            };
        }

        @Override
        public ChangeListener<Number> getMultiplicityOne_YListener(Pane parentPane) {
            return (observableValue, number, t1) -> {
                multiplicityLineOne.setStartY((super.entity.getHeight() / super.connectingAmount) + super.entity.getLayoutY() - (scale * 2) + parentPane.getLayoutY());
                multiplicityLineOne.setEndY((super.entity.getHeight() / super.connectingAmount) + super.entity.getLayoutY() + (scale * 2) + parentPane.getLayoutY());
            };
        }

        @Override
        public ChangeListener<Number> getMandatory_XListener(Pane parentPane) {
            return (observableValue, number, t1) -> {
                mandatoryLine.setStartX(super.entity.getLayoutX() + parentPane.getLayoutX() - (scale * 3));
                mandatoryLine.setEndX(super.entity.getLayoutX() + parentPane.getLayoutX() - (scale * 3));
            };
        }

        @Override
        public ChangeListener<Number> getMandatory_YListener(Pane parentPane) {
            return (observableValue, number, t1) -> {
                mandatoryLine.setStartY((super.entity.getHeight() / super.connectingAmount) + super.entity.getLayoutY() - (scale * 2) + parentPane.getLayoutY());
                mandatoryLine.setEndY((super.entity.getHeight() / super.connectingAmount) + super.entity.getLayoutY() + (scale * 2) + parentPane.getLayoutY());
            };
        }

        @Override
        public ChangeListener<Number> getOptional_XListener(Pane parentPane) {
            return (observableValue, number, t1) -> {
                optionalCircle.setLayoutX(super.entity.getLayoutX() + parentPane.getLayoutX() - (scale * 4));
            };
        }

        @Override
        public ChangeListener<Number> getOptional_YListener(Pane parentPane) {
            return (observableValue, number, t1) -> {
                optionalCircle.setLayoutY(super.entity.getLayoutY() + (super.entity.getHeight() / super.connectingAmount) + parentPane.getLayoutY());
            };
        }
    }

    public static class North extends CrowsFootShape {
        public North(EntityView entity, int connectingAmount) {
            super(entity, connectingAmount);
        }

        @Override
        public ChangeListener<Number> getMultiplicityMany_XListener(Pane parentPane) {
            return (observableValue, number, t1) -> {
                multiplicityLineMultiple1.setStartX((super.entity.getWidth() / super.connectingAmount) - (scale * 2) + super.entity.getLayoutX() + parentPane.getLayoutX());
                multiplicityLineMultiple1.setEndX((super.entity.getWidth() / super.connectingAmount) + super.entity.getLayoutX() + parentPane.getLayoutX());
                multiplicityLineMultiple2.setStartX((super.entity.getWidth() / super.connectingAmount) + (scale * 2) + super.entity.getLayoutX() + parentPane.getLayoutX());
                multiplicityLineMultiple2.setEndX((super.entity.getWidth() / super.connectingAmount) + super.entity.getLayoutX() + parentPane.getLayoutX());
            };
        }

        @Override
        public ChangeListener<Number> getMultiplicityMany_YListener(Pane parentPane) {
            return (observableValue, number, t1) -> {
                multiplicityLineMultiple1.setStartY(super.entity.getLayoutY() + parentPane.getLayoutY());
                multiplicityLineMultiple1.setEndY(super.entity.getLayoutY() - (scale * 3) + parentPane.getLayoutY());
                multiplicityLineMultiple2.setStartY(super.entity.getLayoutY() + parentPane.getLayoutY());
                multiplicityLineMultiple2.setEndY(super.entity.getLayoutY() - (scale * 3) + parentPane.getLayoutY());
            };
        }

        @Override
        public ChangeListener<Number> getMultiplicityOne_XListener(Pane parentPane) {
            return (observableValue, number, t1) -> {
                multiplicityLineOne.setStartX((super.entity.getWidth() / super.connectingAmount) - (scale * 2) + super.entity.getLayoutX() + parentPane.getLayoutX());
                multiplicityLineOne.setEndX((super.entity.getWidth() / super.connectingAmount) + (scale * 2) + super.entity.getLayoutX() + parentPane.getLayoutX());
            };
        }

        @Override
        public ChangeListener<Number> getMultiplicityOne_YListener(Pane parentPane) {
            return (observableValue, number, t1) -> {
                multiplicityLineOne.setStartY(super.entity.getLayoutY() + parentPane.getLayoutY() - (scale * 1.5));
                multiplicityLineOne.setEndY(super.entity.getLayoutY() + parentPane.getLayoutY() - (scale * 1.5));
            };
        }

        @Override
        public ChangeListener<Number> getMandatory_XListener(Pane parentPane) {
            return (observableValue, number, t1) -> {
                mandatoryLine.setStartX((super.entity.getWidth() / super.connectingAmount) + super.entity.getLayoutX() - (scale * 2) + parentPane.getLayoutX());
                mandatoryLine.setEndX((super.entity.getWidth() / super.connectingAmount) + super.entity.getLayoutX() + (scale * 2) + parentPane.getLayoutX());
            };
        }

        @Override
        public ChangeListener<Number> getMandatory_YListener(Pane parentPane) {
            return (observableValue, number, t1) -> {
                mandatoryLine.setStartY(super.entity.getLayoutY() + parentPane.getLayoutY() - (scale * 3));
                mandatoryLine.setEndY(super.entity.getLayoutY() + parentPane.getLayoutY() - (scale * 3));
            };
        }

        @Override
        public ChangeListener<Number> getOptional_XListener(Pane parentPane) {
            return (observableValue, number, t1) -> {
                optionalCircle.setLayoutX(super.entity.getLayoutX() + (super.entity.getWidth() / super.connectingAmount) + parentPane.getLayoutX());
            };
        }

        @Override
        public ChangeListener<Number> getOptional_YListener(Pane parentPane) {
            return (observableValue, number, t1) -> {
                optionalCircle.setLayoutY(super.entity.getLayoutY() + parentPane.getLayoutY() - (scale * 4));
            };
        }
    }

    public static class South extends CrowsFootShape {
        public South(EntityView entity, int connectingAmount) {
            super(entity, connectingAmount);
        }

        @Override
        public ChangeListener<Number> getMultiplicityMany_XListener(Pane parentPane) {
            return (observableValue, number, t1) -> {
                multiplicityLineMultiple1.setStartX((super.entity.getWidth() / super.connectingAmount) - (scale * 2) + super.entity.getLayoutX() + parentPane.getLayoutX());
                multiplicityLineMultiple1.setEndX((super.entity.getWidth() / super.connectingAmount) + super.entity.getLayoutX() + parentPane.getLayoutX());
                multiplicityLineMultiple2.setStartX((super.entity.getWidth() / super.connectingAmount) + (scale * 2) + super.entity.getLayoutX() + parentPane.getLayoutX());
                multiplicityLineMultiple2.setEndX((super.entity.getWidth() / super.connectingAmount) + super.entity.getLayoutX() + parentPane.getLayoutX());
            };
        }

        @Override
        public ChangeListener<Number> getMultiplicityMany_YListener(Pane parentPane) {
            return (observableValue, number, t1) -> {
                multiplicityLineMultiple1.setStartY(super.entity.getLayoutY() + parentPane.getLayoutY() + super.entity.getHeight());
                multiplicityLineMultiple1.setEndY(super.entity.getLayoutY() + (scale * 3) + parentPane.getLayoutY() + super.entity.getHeight());
                multiplicityLineMultiple2.setStartY(super.entity.getLayoutY() + parentPane.getLayoutY() + super.entity.getHeight());
                multiplicityLineMultiple2.setEndY(super.entity.getLayoutY() + (scale * 3) + parentPane.getLayoutY() + super.entity.getHeight());
            };
        }

        @Override
        public ChangeListener<Number> getMultiplicityOne_XListener(Pane parentPane) {
            return (observableValue, number, t1) -> {
                multiplicityLineOne.setStartX((super.entity.getWidth() / super.connectingAmount) - (scale * 2) + super.entity.getLayoutX() + parentPane.getLayoutX());
                multiplicityLineOne.setEndX((super.entity.getWidth() / super.connectingAmount) + (scale * 2) + super.entity.getLayoutX() + parentPane.getLayoutX());
            };
        }

        @Override
        public ChangeListener<Number> getMultiplicityOne_YListener(Pane parentPane) {
            return (observableValue, number, t1) -> {
                multiplicityLineOne.setStartY(super.entity.getLayoutY() + parentPane.getLayoutY() + (scale * 1.5) + super.entity.getHeight());
                multiplicityLineOne.setEndY(super.entity.getLayoutY() + parentPane.getLayoutY() + (scale * 1.5) + super.entity.getHeight());
            };
        }

        @Override
        public ChangeListener<Number> getMandatory_XListener(Pane parentPane) {
            return (observableValue, number, t1) -> {
                mandatoryLine.setStartX((super.entity.getWidth() / super.connectingAmount) + super.entity.getLayoutX() - (scale * 2) + parentPane.getLayoutX());
                mandatoryLine.setEndX((super.entity.getWidth() / super.connectingAmount) + super.entity.getLayoutX() + (scale * 2) + parentPane.getLayoutX());
            };
        }

        @Override
        public ChangeListener<Number> getMandatory_YListener(Pane parentPane) {
            return (observableValue, number, t1) -> {
                mandatoryLine.setStartY(super.entity.getLayoutY() + parentPane.getLayoutY() + (scale * 3) + super.entity.getHeight());
                mandatoryLine.setEndY(super.entity.getLayoutY() + parentPane.getLayoutY() + (scale * 3) + super.entity.getHeight());
            };
        }

        @Override
        public ChangeListener<Number> getOptional_XListener(Pane parentPane) {
            return (observableValue, number, t1) -> {
                optionalCircle.setLayoutX(super.entity.getLayoutX() + (super.entity.getWidth() / super.connectingAmount) + parentPane.getLayoutX());
            };
        }

        @Override
        public ChangeListener<Number> getOptional_YListener(Pane parentPane) {
            return (observableValue, number, t1) -> {
                optionalCircle.setLayoutY(super.entity.getLayoutY() + parentPane.getLayoutY() + (scale * 4) + super.entity.getHeight());
            };
        }
    }
}
