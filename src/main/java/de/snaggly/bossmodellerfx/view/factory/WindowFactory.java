package de.snaggly.bossmodellerfx.view.factory;

import de.snaggly.bossmodellerfx.model.DataModel;
import de.snaggly.bossmodellerfx.view.controller.ViewController;
import javafx.scene.Scene;

import java.io.IOException;
import java.util.Map;

public interface WindowFactory<T extends DataModel, K extends ViewController> {
    Map.Entry<Scene, K> buildWindow(T model) throws IOException;
}
