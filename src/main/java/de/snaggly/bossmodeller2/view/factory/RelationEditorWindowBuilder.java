package de.snaggly.bossmodeller2.view.factory;

import de.snaggly.bossmodeller2.Main;
import de.snaggly.bossmodeller2.guiLogic.Project;
import de.snaggly.bossmodeller2.model.Relation;
import de.snaggly.bossmodeller2.view.controller.EditRelationWindowController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.Map;

public class RelationEditorWindowBuilder implements WindowFactory<Relation, EditRelationWindowController>{
    private static RelationEditorWindowBuilder instance;
    private final Project workspace;

    private RelationEditorWindowBuilder(Project workspace) {
        this.workspace = workspace;
    }

    @Override
    public Map.Entry<Scene, EditRelationWindowController> buildWindow(Relation model) throws IOException {
        var fxmlLoader = new FXMLLoader(Main.class.getResource("view/EditRelationWindow.fxml"));
        var scene = new Scene(fxmlLoader.load());
        var controller = (EditRelationWindowController)(fxmlLoader.getController());
        if (model != null) {
            controller.loadModel(model, workspace);
        } else {
            controller.loadModel(workspace);
        }
        return new AbstractMap.SimpleEntry<>(scene, controller);
    }

    public static Map.Entry<Scene, EditRelationWindowController> buildRelationEditor(Project workspace) throws IOException {
        instance = new RelationEditorWindowBuilder(workspace);
        return instance.buildWindow(null);
    }

    public static Map.Entry<Scene, EditRelationWindowController> buildRelationEditor(Relation model, Project workspace) throws IOException {
        instance = new RelationEditorWindowBuilder(workspace);
        return instance.buildWindow(model);
    }
}
