package de.snaggly.bossmodeller2.guiLogic;

import de.snaggly.bossmodeller2.model.DataModel;
import de.snaggly.bossmodeller2.view.viewtypes.CustomNode;

public interface SelectionHandler {
    public <T extends DataModel> void setCurrentSelected(CustomNode<T> nodeToDrag);
}
