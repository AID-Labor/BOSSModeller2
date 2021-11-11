package de.snaggly.bossmodeller2.view.factory;

import de.snaggly.bossmodeller2.model.DataModel;
import de.snaggly.bossmodeller2.view.viewtypes.CustomNode;

import java.io.IOException;

public interface ViewFactory<T extends DataModel, K extends CustomNode<T>> {
    K buildView(T model) throws IOException;
}
