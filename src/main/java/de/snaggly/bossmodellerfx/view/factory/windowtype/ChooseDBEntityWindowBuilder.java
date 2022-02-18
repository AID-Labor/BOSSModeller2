package de.snaggly.bossmodellerfx.view.factory.windowtype;

import de.snaggly.bossmodellerfx.Main;
import de.snaggly.bossmodellerfx.model.adapter.DBLAHolder;
import de.snaggly.bossmodellerfx.view.controller.ChooseDBEntityWindowController;
import de.snaggly.bossmodellerfx.view.factory.WindowFactory;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.Map;

public class ChooseDBEntityWindowBuilder implements WindowFactory<DBLAHolder, ChooseDBEntityWindowController> {
    private final static ChooseDBEntityWindowBuilder instance = new ChooseDBEntityWindowBuilder();

    private ChooseDBEntityWindowBuilder() {}

    @Override
    public Map.Entry<Scene, ChooseDBEntityWindowController> buildWindow(DBLAHolder model) throws IOException {
        var fxmlLoader = new FXMLLoader(Main.class.getResource("view/ChooseDBEntityWindow.fxml"));
        var scene = new Scene(fxmlLoader.load());
        var controller = (ChooseDBEntityWindowController)(fxmlLoader.getController());
        controller.loadModel(model);

        return new AbstractMap.SimpleEntry<>(scene, controller);
    }

    public static Map.Entry<Scene, ChooseDBEntityWindowController> buildDBChooserWindow(DBLAHolder model) throws IOException {
        return instance.buildWindow(model);
    }
}
