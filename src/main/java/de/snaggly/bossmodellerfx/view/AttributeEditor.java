package de.snaggly.bossmodellerfx.view;

import de.snaggly.bossmodellerfx.BOSS_Strings;
import de.snaggly.bossmodellerfx.model.subdata.Attribute;
import de.snaggly.bossmodellerfx.view.controller.EditAttributeController;
import de.snaggly.bossmodellerfx.view.viewtypes.CustomNode;
import javafx.fxml.FXMLLoader;

import java.io.IOException;

/**
 * AttributeEditor View-Class used in an Entity Editor Window
 * This class is realized by a Factory.
 *
 * @author Omar Emshani
 */
public abstract class AttributeEditor extends CustomNode<Attribute> {
    private final EditAttributeController controller;
    private final Attribute model;

    public AttributeEditor(Attribute model) throws IOException {
        this.model = model;
        var fxmlLoader = new FXMLLoader(AttributeEditor.class.getResource("EditAttribute.fxml"), BOSS_Strings.resourceBundle);
        fxmlLoader.setRoot(this);
        fxmlLoader.load();
        controller = fxmlLoader.getController();

        if (model != null) {
            controller.loadModel(model);
        }
    }

    @Override
    public Attribute getModel() {
        return model;
    }

    @Override
    public EditAttributeController getController() {
        return controller;
    }

    @Override
    public void setFocusStyle() { }

    @Override
    public void setDeFocusStyle() { }
}
