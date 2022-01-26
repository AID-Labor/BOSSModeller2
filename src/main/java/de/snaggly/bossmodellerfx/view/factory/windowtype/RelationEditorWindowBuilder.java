package de.snaggly.bossmodellerfx.view.factory.windowtype;

import de.snaggly.bossmodellerfx.Main;
import de.snaggly.bossmodellerfx.guiLogic.Project;
import de.snaggly.bossmodellerfx.model.subdata.Relation;
import de.snaggly.bossmodellerfx.view.controller.EditRelationWindowController;
import de.snaggly.bossmodellerfx.view.factory.WindowFactory;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.Map;

public class RelationEditorWindowBuilder implements WindowFactory<Relation, EditRelationWindowController> {
    private static RelationEditorWindowBuilder instance;

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

    public static Map.Entry<Scene, EditRelationWindowController> buildRelationEditor() throws IOException {
        instance = new RelationEditorWindowBuilder();
        return instance.buildWindow(null);
    }

    public static Map.Entry<Scene, EditRelationWindowController> buildRelationEditor(Relation model) throws IOException {
        instance = new RelationEditorWindowBuilder();
        return instance.buildWindow(model);
    }
}
