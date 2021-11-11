package de.snaggly.bossmodeller2.view;

import de.snaggly.bossmodeller2.view.viewtypes.Controllable;
import javafx.scene.shape.Line;

public class RelationLineView extends Line implements Controllable {
    public RelationLineView() {
        this.setStrokeWidth(3.0);
    }

    public void setWeakConnection() {
        this.getStrokeDashArray().clear();
    }

    public void setStrongConnection() {
        this.getStrokeDashArray().addAll(3.0, 8.0);
    }

    @Override
    public void setOnClick() {
        //To Do: Highlight Fk on EntityView -> Make relation editable
    }
}
