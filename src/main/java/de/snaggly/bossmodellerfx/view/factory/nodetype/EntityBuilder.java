package de.snaggly.bossmodellerfx.view.factory.nodetype;

import de.snaggly.bossmodellerfx.guiLogic.GUIMethods;
import de.snaggly.bossmodellerfx.guiLogic.SelectionHandler;
import de.snaggly.bossmodellerfx.model.view.Entity;
import de.snaggly.bossmodellerfx.view.EntityView;
import de.snaggly.bossmodellerfx.view.factory.ViewFactory;
import javafx.scene.layout.Region;

import java.io.IOException;

/**
 * Builds the view to display an Entity on workbench.
 *
 * @author Omar Emshani
 */
public class EntityBuilder implements ViewFactory<Entity, EntityView> {

    private final Region parentRegion;
    private final SelectionHandler selectionHandler;

    private EntityBuilder(Region parentRegion, SelectionHandler selectionHandler) {
        this.selectionHandler = selectionHandler;
        this.parentRegion = parentRegion;
    }

    /**
     * Use this method to build a new view.
     * @param entityModel Existing model to load on view.
     * @param parentRegion Required to make the view movable across given region.
     * @param selectionHandler Required to make the view controllable.
     * @return Returns the new view class.
     * @throws IOException
     */
    public static EntityView buildEntity(Entity entityModel, Region parentRegion, SelectionHandler selectionHandler) throws IOException {
        var builder = new EntityBuilder(parentRegion, selectionHandler);

        return builder.buildView(entityModel);
    }

    @Override
    public EntityView buildView(Entity entityModel) throws IOException { //Realises EntityView
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
