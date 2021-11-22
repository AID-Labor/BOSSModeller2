package de.snaggly.bossmodeller2.view.controller;

import de.snaggly.bossmodeller2.model.Attribute;
import de.snaggly.bossmodeller2.model.UniqueCombination;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.util.ArrayList;

public class EditUniqueCombinationWindowController implements ViewController<UniqueCombination> {
    @FXML
    private VBox attributesListVBox;
    @FXML
    private VBox uniqueCombNamesListVBox;

    @FXML
    private void newUniqueComboClick(ActionEvent actionEvent) {
        var textField = new TextField();
        textField.setPromptText("Unique Name...");
        uniqueCombNamesListVBox.getChildren().add(textField);
    }

    @FXML
    private void deleteUniqueComboClick(ActionEvent actionEvent) {
    }

    @Override
    public void loadModel(UniqueCombination model) {

    }

    public void loadAttributes(ArrayList<Attribute> attributes) {
        for (var attribute : attributes) {
            attributesListVBox.getChildren().add(new CheckBox(attribute.getName()));
        }
    }
}
