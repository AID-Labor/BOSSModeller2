package de.snaggly.bossmodellerfx.view.viewtypes;

import de.snaggly.bossmodellerfx.model.BOSSModel;
import de.snaggly.bossmodellerfx.view.controller.ModelController;
import javafx.scene.layout.AnchorPane;

public abstract class CustomNode<T extends BOSSModel> extends AnchorPane {
    public abstract T getModel();
    public abstract ModelController<T> getController();
    public abstract void setFocusStyle();
    public abstract void setDeFocusStyle();
}
