package de.snaggly.bossmodellerfx.view.viewtypes;

import de.snaggly.bossmodellerfx.model.DataModel;
import de.snaggly.bossmodellerfx.view.controller.ViewController;
import javafx.scene.layout.AnchorPane;

public abstract class CustomNode<T extends DataModel> extends AnchorPane {
    public abstract T getModel();
    public abstract ViewController<T> getController();
    public abstract void setFocusStyle();
    public abstract void setDeFocusStyle();
}
