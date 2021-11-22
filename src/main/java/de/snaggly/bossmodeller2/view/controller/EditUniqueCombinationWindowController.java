package de.snaggly.bossmodeller2.view.controller;

import de.snaggly.bossmodeller2.model.Attribute;
import de.snaggly.bossmodeller2.model.UniqueCombination;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;

public class EditUniqueCombinationWindowController implements ViewController<UniqueCombination> {
    @FXML
    private ScrollPane attributesListVBox;
    @FXML
    private VBox uniqueCombNamesListVBox;

    @FXML
    private void newUniqueComboClick(ActionEvent actionEvent) {

    }

    @FXML
    private void deleteUniqueComboClick(ActionEvent actionEvent) {
    }

    @Override
    public void loadModel(UniqueCombination model) {

    }

    public void loadAttributes(ArrayList<Attribute> attributes) {

    }
}
