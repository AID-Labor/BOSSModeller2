package de.snaggly.bossmodellerfx.view.factory;

import de.snaggly.bossmodellerfx.model.BOSSModel;
import de.snaggly.bossmodellerfx.view.viewtypes.CustomNode;

import java.io.IOException;

/**
 * Factory for simple views.
 *
 * @param <T> The passing model which the view works with.
 * @param <K> The desired View Class which should be build.
 * @author Omar Emshani
 */
public interface ViewFactory<T extends BOSSModel, K extends CustomNode<T>> {
    K buildView(T model) throws IOException;
}
