package de.snaggly.bossmodellerfx.guiLogic;

import de.snaggly.bossmodellerfx.model.BOSSModel;

public interface GUIActionListener<T extends BOSSModel> {
    void notify(T dataset);
}
