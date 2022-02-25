package de.snaggly.bossmodellerfx.view.factory.windowtype;

import de.snaggly.bossmodellerfx.BOSS_Strings;
import de.snaggly.bossmodellerfx.Main;
import de.snaggly.bossmodellerfx.model.view.Entity;
import de.snaggly.bossmodellerfx.view.controller.EditEntityWindowController;
import de.snaggly.bossmodellerfx.view.factory.WindowFactory;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.Map;

/**
 * Builds a new Entity Editor Window.
 *
 * @author Omar Emshani
 */
public class EntityEditorWindowBuilder implements WindowFactory<Entity, EditEntityWindowController> {
    private final static EntityEditorWindowBuilder instance = new EntityEditorWindowBuilder(); //Singleton

    private EntityEditorWindowBuilder() { }

    @Override
    public Map.Entry<Scene, EditEntityWindowController> buildWindow(Entity model) throws IOException {
        var fxmlLoader = new FXMLLoader(Main.class.getResource("view/EditEntityWindow.fxml"), BOSS_Strings.resourceBundle);
        var scene = new Scene(fxmlLoader.load());
        var controller = (EditEntityWindowController)(fxmlLoader.getController());
        if (model != null) {
            controller.loadModel(model);
        }
        return new AbstractMap.SimpleEntry<>(scene, controller);
    }

    /**
     * Use this method to build a new window.
     * @param model Existing model to load on window. Can be null to create new model.
     * @return Returns the scene and controller.
     * @throws IOException When the file is not found.
     */
    public static Map.Entry<Scene, EditEntityWindowController> buildEntityEditor(Entity model) throws IOException {
        return instance.buildWindow(model);
    }
}
