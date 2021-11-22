package de.snaggly.bossmodeller2.view.factory.nodetype;

import de.snaggly.bossmodeller2.guiLogic.GUIMethods;
import de.snaggly.bossmodeller2.guiLogic.SelectionHandler;
import de.snaggly.bossmodeller2.model.Entity;
import de.snaggly.bossmodeller2.view.EntityView;
import de.snaggly.bossmodeller2.view.factory.ViewFactory;
import javafx.scene.layout.Region;

import java.io.IOException;

public class EntityBuilder implements ViewFactory<Entity, EntityView> {

    private final Region parentRegion;
    private final SelectionHandler selectionHandler;

    private EntityBuilder(Region parentRegion, SelectionHandler selectionHandler) {
        this.selectionHandler = selectionHandler;
        this.parentRegion = parentRegion;
    }

    public static EntityView buildEntity(Entity entityModel, Region parentRegion, SelectionHandler selectionHandler) throws IOException {
        var builder = new EntityBuilder(parentRegion, selectionHandler);

        return builder.buildView(entityModel);
    }

    @Override
    public EntityView buildView(Entity entityModel) throws IOException {
        return new EntityView(entityModel) {
            @Override
            public void setOnClick() {
                GUIMethods.enableClick(this, selectionHandler);
            }

            @Override
            public void setDraggable() {
                GUIMethods.enableDrag(this, parentRegion, entityModel);
            }
        };
    }
}
