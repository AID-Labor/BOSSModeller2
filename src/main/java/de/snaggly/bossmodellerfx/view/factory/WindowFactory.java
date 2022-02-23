package de.snaggly.bossmodellerfx.view.factory;

import de.snaggly.bossmodellerfx.model.BOSSModel;
import de.snaggly.bossmodellerfx.view.controller.ModelController;
import javafx.scene.Scene;

import java.io.IOException;
import java.util.Map;

/**
 * Factory for windows. The builder returns a scene of the window and its controller the same time!
 *
 * @param <T> The passing model which the window works with.
 * @param <K> The desired Window Class which should be build.
 * @author Omar Emshani
 */
public interface WindowFactory<T extends BOSSModel, K extends ModelController<T>> {
    Map.Entry<Scene, K> buildWindow(T model) throws IOException;
}
