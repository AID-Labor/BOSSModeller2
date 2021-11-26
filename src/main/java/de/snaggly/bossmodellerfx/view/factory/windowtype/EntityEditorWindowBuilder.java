package de.snaggly.bossmodellerfx.view.factory.windowtype;

import de.snaggly.bossmodellerfx.Main;
import de.snaggly.bossmodellerfx.model.Entity;
import de.snaggly.bossmodellerfx.view.controller.EditEntityWindowController;
import de.snaggly.bossmodellerfx.view.factory.WindowFactory;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.Map;

public class EntityEditorWindowBuilder implements WindowFactory<Entity, EditEntityWindowController> {
    private static EntityEditorWindowBuilder instance;

    private EntityEditorWindowBuilder() { }

    @Override
    public Map.Entry<Scene, EditEntityWindowController> buildWindow(Entity model) throws IOException {
        var fxmlLoader = new FXMLLoader(Main.class.getResource("view/EditEntityWindow.fxml"));
        var scene = new Scene(fxmlLoader.load());
        var controller = (EditEntityWindowController)(fxmlLoader.getController());
        if (model != null) {
            controller.loadModel(model);
        }
        return new AbstractMap.SimpleEntry<>(scene, controller);
    }

    public static Map.Entry<Scene, EditEntityWindowController> buildEntityEditor() throws IOException {
        if (instance == null)
            instance = new EntityEditorWindowBuilder();
        return instance.buildWindow(null);
    }

    public static Map.Entry<Scene, EditEntityWindowController> buildEntityEditor(Entity model) throws IOException {
        if (instance == null)
            instance = new EntityEditorWindowBuilder();
        return instance.buildWindow(model);
    }
}
