package de.snaggly.bossmodellerfx.guiLogic;

import de.snaggly.bossmodellerfx.model.DataModel;
import de.snaggly.bossmodellerfx.view.viewtypes.CustomNode;

public interface SelectionHandler {
    public <T extends DataModel> void setCurrentSelected(CustomNode<T> nodeToDrag);
}
