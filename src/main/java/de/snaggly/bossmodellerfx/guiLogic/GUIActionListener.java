package de.snaggly.bossmodellerfx.guiLogic;

import de.snaggly.bossmodellerfx.model.BOSSModel;

/**
 * This instance is primarily used for a callback in a sub window.
 * The sub window notifies the underlying receiver the new BOSSModel dataset.
 * @param <T> The Datatype which the sub window notifies. E.g: Entity
 * @author Omar Emshani
 */
public interface GUIActionListener<T extends BOSSModel> {
    void notify(T dataset);
}
