package de.snaggly.bossmodellerfx.guiLogic;

import de.snaggly.bossmodellerfx.model.BOSSModel;
import de.snaggly.bossmodellerfx.view.viewtypes.CustomNode;

public interface SelectionHandler {
    <T extends BOSSModel> void setCurrentSelected(CustomNode<T> nodeToDrag);
}
