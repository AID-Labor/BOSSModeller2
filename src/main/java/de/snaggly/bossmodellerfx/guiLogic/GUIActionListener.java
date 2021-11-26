package de.snaggly.bossmodellerfx.guiLogic;

import de.snaggly.bossmodellerfx.model.DataModel;

public interface GUIActionListener<T extends DataModel> {
    void notify(T dataset);
}
