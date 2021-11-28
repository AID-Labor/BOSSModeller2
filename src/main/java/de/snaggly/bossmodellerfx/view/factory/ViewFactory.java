package de.snaggly.bossmodellerfx.view.factory;

import de.snaggly.bossmodellerfx.model.BOSSModel;
import de.snaggly.bossmodellerfx.view.viewtypes.CustomNode;

import java.io.IOException;

public interface ViewFactory<T extends BOSSModel, K extends CustomNode<T>> {
    K buildView(T model) throws IOException;
}
