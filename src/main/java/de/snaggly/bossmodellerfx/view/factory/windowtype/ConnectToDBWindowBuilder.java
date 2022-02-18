package de.snaggly.bossmodellerfx.view.factory.windowtype;

import de.snaggly.bossmodellerfx.Main;
import de.snaggly.bossmodellerfx.model.adapter.DBLAHolder;
import de.snaggly.bossmodellerfx.view.controller.ConnectToDBWindowController;
import de.snaggly.bossmodellerfx.view.factory.WindowFactory;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.Map;

public class ConnectToDBWindowBuilder implements WindowFactory<DBLAHolder, ConnectToDBWindowController> {
    private static final ConnectToDBWindowBuilder instance = new ConnectToDBWindowBuilder();

    private ConnectToDBWindowBuilder() { }

    public static Map.Entry<Scene, ConnectToDBWindowController> buildDBConnectorWindow() throws IOException {
        return instance.buildWindow(null);
    }

    public static Map.Entry<Scene, ConnectToDBWindowController> buildDBConnectorWindow(DBLAHolder holder) throws IOException {
        return instance.buildWindow(holder);
    }

    @Override
    public Map.Entry<Scene, ConnectToDBWindowController> buildWindow(DBLAHolder model) throws IOException {
        var fxmlLoader = new FXMLLoader(Main.class.getResource("view/ConnectToDBWindow.fxml"));
        var scene = new Scene(fxmlLoader.load());
        var controller = (ConnectToDBWindowController)(fxmlLoader.getController());
        if (model!=null)
            controller.loadModel(model);

        return new AbstractMap.SimpleEntry<>(scene, controller);
    }
}
