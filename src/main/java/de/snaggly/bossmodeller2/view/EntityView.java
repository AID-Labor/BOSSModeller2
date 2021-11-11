package de.snaggly.bossmodeller2.view;

import de.snaggly.bossmodeller2.model.Entity;
import de.snaggly.bossmodeller2.view.controller.ViewController;
import de.snaggly.bossmodeller2.view.viewtypes.Controllable;
import de.snaggly.bossmodeller2.view.viewtypes.CustomNode;
import de.snaggly.bossmodeller2.view.viewtypes.Draggable;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;

import java.io.IOException;

public abstract class EntityView extends CustomNode<Entity> implements Draggable, Controllable {
    private final Entity model;
    private final FXMLLoader fxmlLoader;

    public EntityView(Entity entity) throws IOException {
        this.model = entity;
        fxmlLoader = new FXMLLoader(EntityView.class.getResource("Entity.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.load();
        loadModel(entity);

        setOnClick();
        setDraggable();

        this.setLayoutX(entity.getXCoordinate());
        this.setLayoutY(entity.getYCoordinate());
    }

    @Override
    public Entity getModel(){
        return model;
    }

    @Override
    public ViewController<Entity> getController() {
        return this::loadModel;
    }

    @Override
    public void setFocusStyle() {
        this.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(3,158,211,0.8), 17, 0, 0, 0);");
    }

    @Override
    public void setDeFocusStyle() {
        this.setStyle("");
    }

    private void loadModel(Entity entity) {
        var headLabel = (Label) fxmlLoader.getNamespace().get("entityHeadTitle");
        headLabel.setText(entity.getName());

        var attributesList = (VBox) fxmlLoader.getNamespace().get("entityAttributesVBox");
        for (var attribute : entity.getAttributes()) {
            var attributeLabel = new Label(attribute.getName());
            attributeLabel.setUnderline(attribute.isPrimary());
            if (!attribute.getFkTableName().equals(""))
                attributeLabel.setStyle("-fx-font-style: italic; ");
            attributeLabel.setPadding(new Insets(2, 15, 2, 15));
            attributesList.getChildren().add(attributeLabel);
        }

        if (!entity.isWeakType()) {
            var line = (Line)fxmlLoader.getNamespace().get("weakLineNW");
            line.setVisible(false);
            line = (Line)fxmlLoader.getNamespace().get("weakLineNE");
            line.setVisible(false);
            line = (Line)fxmlLoader.getNamespace().get("weakLineSW");
            line.setVisible(false);
            line = (Line)fxmlLoader.getNamespace().get("weakLineSE");
            line.setVisible(false);
        }
    }
}
