package de.snaggly.bossmodellerfx.view.viewtypes;

/**
 * Views that implement this feature can highlight themselves when a user selects them.
 * Prerequisite: The view must be also controllable to be able to click on them.
 *
 * @author Omar Emshani
 */
public interface Selectable {
    void setFocusStyle();
    void setDeFocusStyle();
}
