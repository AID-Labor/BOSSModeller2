package de.snaggly.bossmodellerfx.view.viewtypes;

import de.snaggly.bossmodellerfx.model.BOSSModel;
import de.snaggly.bossmodellerfx.view.controller.ModelController;
import javafx.scene.layout.AnchorPane;

/**
 * Parent view class which contains a model of BOSSModel to display.
 * @param <T> The model type which the extending view shows or handles.
 *
 * @author Omar Emshani
 */
public abstract class CustomNode<T extends BOSSModel> extends AnchorPane implements Selectable {
    public abstract T getModel();
    public abstract ModelController<T> getController();
}
