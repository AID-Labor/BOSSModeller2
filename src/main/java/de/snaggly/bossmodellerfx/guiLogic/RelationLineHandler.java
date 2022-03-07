package de.snaggly.bossmodellerfx.guiLogic;

/**
 * This instance is used as a callback to draw relation lines in a workbench.
 * The implementing instance shall handle the drawings when handleRelationLines() is called.
 * @author Omar Emshani
 */
public interface RelationLineHandler {
    void handleRelationLines();
}
