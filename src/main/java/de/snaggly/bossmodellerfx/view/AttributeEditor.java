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
    public void setFocusStyle() {
        this.setStyle("-fx-border-color: rgb(165,185,205); -fx-border-width: 3; -fx-border-radius: 15; -fx-background-color: rgb(211,218,224); -fx-background-radius: 15;");
    }

    @Override
    public void setDeFocusStyle() {
        this.setStyle("");
    }
}
