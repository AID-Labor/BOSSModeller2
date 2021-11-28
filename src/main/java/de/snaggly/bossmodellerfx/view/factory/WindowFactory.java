package de.snaggly.bossmodellerfx.view.factory;

import de.snaggly.bossmodellerfx.model.BOSSModel;
import de.snaggly.bossmodellerfx.view.controller.ModelController;
import javafx.scene.Scene;

import java.io.IOException;
import java.util.Map;

public interface WindowFactory<T extends BOSSModel, K extends ModelController> {
    Map.Entry<Scene, K> buildWindow(T model) throws IOException;
}
