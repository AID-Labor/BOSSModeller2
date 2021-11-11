package de.snaggly.bossmodeller2.guiLogic;

import de.snaggly.bossmodeller2.model.DataModel;

public interface GUIActionListener<T extends DataModel> {
    void notify(T dataset);
}
