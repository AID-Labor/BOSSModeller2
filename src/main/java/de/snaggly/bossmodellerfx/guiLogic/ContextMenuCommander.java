package de.snaggly.bossmodellerfx.guiLogic;

/**
 * When context menu is requested, the event fires before a click event.
 * This has the effect that the new selection will not be picked up by the context menu.
 * This commander is a workaround.
 * -> Context menu is requested
 * -> Selection handler will receive an implementation of this interface.
 * -> Selection handler processes new selection and issues context menu based on new selection.
 * @author Omar Emshani
 */
public interface ContextMenuCommander {
    void requestContextMenu();
}
