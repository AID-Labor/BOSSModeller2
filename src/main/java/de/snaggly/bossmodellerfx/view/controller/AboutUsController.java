package de.snaggly.bossmodellerfx.view.controller;

import de.snaggly.bossmodellerfx.guiLogic.GUIMethods;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

/**
 * Controller for AboutUs Window
 *
 * @author Omar Emshani
 */
public class AboutUsController {
    @FXML
    private void close(ActionEvent actionEvent) {
        GUIMethods.closeWindow(actionEvent);
    }
}
