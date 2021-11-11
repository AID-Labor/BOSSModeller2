package de.snaggly.bossmodeller2.view;

import de.snaggly.bossmodeller2.model.Attribute;
import de.snaggly.bossmodeller2.view.controller.EditAttributeController;
import de.snaggly.bossmodeller2.view.viewtypes.CustomNode;
import javafx.fxml.FXMLLoader;

import java.io.IOException;

public abstract class AttributeEditor extends CustomNode<Attribute> {
    private final EditAttributeController controller;
    private final Attribute model;

    public AttributeEditor(Attribute model) throws IOException {
        this.model = model;
        var fxmlLoader = new FXMLLoader(AttributeEditor.class.getResource("EditAttribute.fxml"));
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
