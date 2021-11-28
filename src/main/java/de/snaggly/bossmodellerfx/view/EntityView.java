package de.snaggly.bossmodellerfx.view;

import de.snaggly.bossmodellerfx.model.view.Entity;
import de.snaggly.bossmodellerfx.view.controller.EntityViewController;
import de.snaggly.bossmodellerfx.view.viewtypes.Controllable;
import de.snaggly.bossmodellerfx.view.viewtypes.CustomNode;
import de.snaggly.bossmodellerfx.view.viewtypes.Draggable;
import javafx.fxml.FXMLLoader;

import java.io.IOException;

public abstract class EntityView extends CustomNode<Entity> implements Draggable, Controllable {
    private final Entity model;
    private final EntityViewController controller;

    public EntityView(Entity entity) throws IOException {
        this.model = entity;
        FXMLLoader fxmlLoader = new FXMLLoader(EntityView.class.getResource("Entity.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.load();
        controller = fxmlLoader.getController();
        controller.loadModel(entity);

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
    public EntityViewController getController() {
        return controller;
    }

    @Override
    public void setFocusStyle() {
        this.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(3,158,211,0.8), 17, 0, 0, 0);");
    }

    @Override
    public void setDeFocusStyle() {
        this.setStyle("");
    }
}
