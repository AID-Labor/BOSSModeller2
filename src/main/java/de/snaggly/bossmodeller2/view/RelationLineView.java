package de.snaggly.bossmodeller2.view;

import de.snaggly.bossmodeller2.guiLogic.GUIMethods;
import de.snaggly.bossmodeller2.guiLogic.RelationLineClickHandler;
import de.snaggly.bossmodeller2.model.Relation;
import de.snaggly.bossmodeller2.view.viewtypes.Controllable;
import de.snaggly.bossmodeller2.view.viewtypes.Highlightable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

public class RelationLineView extends Line implements Controllable, Highlightable {
    private final RelationLineClickHandler clicker;
    private final Relation parentRelation;
    public RelationLineView(Relation parent, RelationLineClickHandler relationLineClickHandler) {
        this.setStrokeWidth(3.0);
        clicker = relationLineClickHandler;
        parentRelation = parent;

        this.addEventFilter(MouseEvent.MOUSE_CLICKED, mouseEvent -> setOnClick());
    }

    public void setWeakConnection() {
        this.getStrokeDashArray().clear();
    }

    public void setStrongConnection() {
        this.getStrokeDashArray().addAll(3.0, 8.0);
    }

    @Override
    public void setOnClick() {
        clicker.handleClick(parentRelation);
    }

    @Override
    public void highlight() {
        this.setStroke(Color.rgb(3,158,211));
    }

    @Override
    public void deHighlight() {
        this.setStroke(Color.BLACK);
    }
}
