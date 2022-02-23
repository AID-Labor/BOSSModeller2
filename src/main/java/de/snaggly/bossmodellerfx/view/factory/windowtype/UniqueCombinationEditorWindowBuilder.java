package de.snaggly.bossmodellerfx.view.factory.windowtype;

import de.snaggly.bossmodellerfx.Main;
import de.snaggly.bossmodellerfx.model.subdata.Attribute;
import de.snaggly.bossmodellerfx.model.subdata.UniqueCombination;
import de.snaggly.bossmodellerfx.view.controller.EditUniqueCombinationWindowController;
import de.snaggly.bossmodellerfx.view.factory.WindowFactory;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Map;

/**
 * Builds a new UniqueCombination Editor Window. Used in Entity Editor Window
 *
 * @author Omar Emshani
 */
public class UniqueCombinationEditorWindowBuilder implements WindowFactory<UniqueCombination, EditUniqueCombinationWindowController> {
    private final static UniqueCombinationEditorWindowBuilder instance = new UniqueCombinationEditorWindowBuilder(); //Singleton

    private UniqueCombinationEditorWindowBuilder() { }

    @Override
    public Map.Entry<Scene, EditUniqueCombinationWindowController> buildWindow(UniqueCombination model) throws IOException {
        return buildWindow(model, null);
    }

    public Map.Entry<Scene, EditUniqueCombinationWindowController> buildWindow(ArrayList<Attribute> attributes) throws IOException {
        return buildWindow(null, attributes);
    }

    public Map.Entry<Scene, EditUniqueCombinationWindowController> buildWindow(UniqueCombination model, ArrayList<Attribute> attributes) throws IOException {
        var fxmlLoader = new FXMLLoader(Main.class.getResource("view/EditUniqueCombinationWindow.fxml"));
        var scene = new Scene(fxmlLoader.load());
        var controller = (EditUniqueCombinationWindowController)(fxmlLoader.getController());
        if (model != null) {
            controller.loadModel(model);
        }
        if (attributes != null) {
            controller.loadAttributes(attributes);
        }
        return new AbstractMap.SimpleEntry<>(scene, controller);
    }

    /**
     * Use this method to build a new window.
     * @param model Model of existing UniqueCombination to load on window. Can be null to create new model.
     * @param attributes Pass the attributes list to select the combinations from.
     * @return Returns the scene and controller.
     * @throws IOException When the file is not found.
     */
    public static Map.Entry<Scene, EditUniqueCombinationWindowController> buildEntityEditor(UniqueCombination model, ArrayList<Attribute> attributes) throws IOException {
        return instance.buildWindow(model, attributes);
    }
}
