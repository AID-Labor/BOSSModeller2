package de.snaggly.bossmodellerfx.view.viewtypes;

/**
 * Views that implement this feature allows further commands on click.
 * Usual behaviour is that a selection handler manages a mouse click on these views.
 *
 * @author Omar Emshani
 */
public interface Controllable {
    void setOnClick();
}
