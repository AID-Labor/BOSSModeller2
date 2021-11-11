package de.snaggly.bossmodeller2.view.viewtypes;

import de.snaggly.bossmodeller2.model.DataModel;
import de.snaggly.bossmodeller2.view.controller.ViewController;
import javafx.scene.layout.AnchorPane;

public abstract class CustomNode<T extends DataModel> extends AnchorPane {
    public abstract T getModel();
    public abstract ViewController<T> getController();
    public abstract void setFocusStyle();
    public abstract void setDeFocusStyle();
}
