package de.snaggly.bossmodellerfx.view.factory.windowtype;

import de.snaggly.bossmodellerfx.BOSS_Strings;
import de.snaggly.bossmodellerfx.Main;
import de.snaggly.bossmodellerfx.model.adapter.DBLAHolder;
import de.snaggly.bossmodellerfx.view.controller.ChooseDBEntityWindowController;
import de.snaggly.bossmodellerfx.view.factory.WindowFactory;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.Map;

/**
 * Builds a new Database Import Window. This window is used to import selected tables from an active Database Connection.
 *
 * @author Omar Emshani
 */
public class ChooseDBEntityWindowBuilder implements WindowFactory<DBLAHolder, ChooseDBEntityWindowController> {
    private final static ChooseDBEntityWindowBuilder instance = new ChooseDBEntityWindowBuilder();

    private ChooseDBEntityWindowBuilder() {}

    @Override
    public Map.Entry<Scene, ChooseDBEntityWindowController> buildWindow(DBLAHolder model) throws IOException {
        var fxmlLoader = new FXMLLoader(Main.class.getResource("view/ChooseDBEntityWindow.fxml"), BOSS_Strings.resourceBundle);
        var scene = new Scene(fxmlLoader.load());
        var controller = (ChooseDBEntityWindowController)(fxmlLoader.getController());
        controller.loadModel(model);

        return new AbstractMap.SimpleEntry<>(scene, controller);
    }

    /**
     * Use this method to build a new window.
     * @param model Existing model to load on window. Can be null to create new model.
     * @return Returns the scene and controller.
     * @throws IOException When the file is not found.
     */
    public static Map.Entry<Scene, ChooseDBEntityWindowController> buildDBChooserWindow(DBLAHolder model) throws IOException {
        return instance.buildWindow(model);
    }
}
