package de.snaggly.bossmodellerfx.guiLogic;

import de.snaggly.bossmodellerfx.model.BOSSModel;
import de.snaggly.bossmodellerfx.view.viewtypes.CustomNode;

/**
 * This instance declared an implementation to handle a selection click.
 * This is usually used for clickable ViewNodes and enabled by GUIMethods.enableClick()
 *
 * @author Omar Emshani
 */
public interface SelectionHandler {
    <T extends BOSSModel> void setCurrentSelected(CustomNode<T> nodeToDrag);
}
