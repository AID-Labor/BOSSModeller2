package de.snaggly.bossmodellerfx.view.factory;

import de.snaggly.bossmodellerfx.model.DataModel;
import de.snaggly.bossmodellerfx.view.viewtypes.CustomNode;

import java.io.IOException;

public interface ViewFactory<T extends DataModel, K extends CustomNode<T>> {
    K buildView(T model) throws IOException;
}
