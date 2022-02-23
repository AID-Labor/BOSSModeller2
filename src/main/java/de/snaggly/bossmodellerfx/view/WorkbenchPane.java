package de.snaggly.bossmodellerfx.view;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

/**
 * Custom Pane to be used to display a project on the main window.
 *
 * @author Omar Emshani
 */
public class WorkbenchPane extends Pane {
    public WorkbenchPane(EventHandler<MouseEvent> onMouseClick) {
        AnchorPane.setTopAnchor(this, 0.0);
        AnchorPane.setBottomAnchor(this, 0.0);
        AnchorPane.setLeftAnchor(this, 0.0);
        AnchorPane.setRightAnchor(this, 0.0);
        this.setStyle("-fx-border-color: grey;");
        this.setOnMouseClicked(onMouseClick);
    }
}
