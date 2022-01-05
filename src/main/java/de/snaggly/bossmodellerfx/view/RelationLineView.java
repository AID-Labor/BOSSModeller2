package de.snaggly.bossmodellerfx.view;

import de.snaggly.bossmodellerfx.guiLogic.SelectionHandler;
import de.snaggly.bossmodellerfx.view.viewtypes.Controllable;
import de.snaggly.bossmodellerfx.view.viewtypes.Highlightable;
import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

public class RelationLineView extends Line implements Controllable, Highlightable {
    private final SelectionHandler clicker;
    private final RelationViewNode parentRelation;
    public RelationLineView(RelationViewNode parent, SelectionHandler relationLineClickHandler) {
        this.setStrokeWidth(2.0);
        clicker = relationLineClickHandler;
        parentRelation = parent;

        this.addEventFilter(MouseEvent.MOUSE_ENTERED, mouseEvent -> setCursor(Cursor.HAND));
        this.addEventFilter(MouseEvent.MOUSE_CLICKED, mouseEvent -> setOnClick());
    }

    public void setWeakConnection() {
        this.getStrokeDashArray().clear();
    }

    public void setStrongConnection() {
        this.getStrokeDashArray().addAll(10.0, 8.0);
    }

    @Override
    public void setOnClick() {
        clicker.setCurrentSelected(parentRelation);
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
