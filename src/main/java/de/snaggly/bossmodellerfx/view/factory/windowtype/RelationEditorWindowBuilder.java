package de.snaggly.bossmodellerfx.view.factory.windowtype;

import de.snaggly.bossmodellerfx.Main;
import de.snaggly.bossmodellerfx.model.subdata.Relation;
import de.snaggly.bossmodellerfx.view.controller.EditRelationWindowController;
import de.snaggly.bossmodellerfx.view.factory.WindowFactory;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.Map;

/**
 * Builds a new Relation Editor Window to edit relations between two entities.
 *
 * @author Omar Emshani
 */
public class RelationEditorWindowBuilder implements WindowFactory<Relation, EditRelationWindowController> {
    private final static RelationEditorWindowBuilder instance = new RelationEditorWindowBuilder(); //Singleton

    private RelationEditorWindowBuilder() { }

    @Override
    public Map.Entry<Scene, EditRelationWindowController> buildWindow(Relation model) throws IOException {
        var fxmlLoader = new FXMLLoader(Main.class.getResource("view/EditRelationWindow.fxml"));
        var scene = new Scene(fxmlLoader.load());
        var controller = (EditRelationWindowController)(fxmlLoader.getController());
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
    public static Map.Entry<Scene, EditRelationWindowController> buildRelationEditor(Relation model) throws IOException {
        return instance.buildWindow(model);
    }
}
