package de.snaggly.bossmodeller2.view.controller;

import de.snaggly.bossmodeller2.guiLogic.GUIMethods;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class AboutUsController {
    @FXML
    private void close(ActionEvent actionEvent) {
        GUIMethods.closeWindow(actionEvent);
    }
}
