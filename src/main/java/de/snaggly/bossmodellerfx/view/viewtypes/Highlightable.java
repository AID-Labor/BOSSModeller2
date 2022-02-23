package de.snaggly.bossmodellerfx.view.viewtypes;

/**
 * Views that implement this feature can highlight themselves from outside source.
 *
 * @author Omar Emshani
 */
public interface Highlightable {
    void highlight();
    void deHighlight();
}
