package de.snaggly.bossmodeller2.view.controller;

import de.snaggly.bossmodeller2.model.Attribute;
import de.snaggly.bossmodeller2.model.UniqueCombination;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.HashMap;

public class EditUniqueCombinationWindowController implements ViewController<UniqueCombination> {
    private boolean tabSwitchMutex = false;
    private UniqueCombination model;
    private HashMap<Attribute, CheckBox> checkBoxOverview = new HashMap<>();
    private HashMap<TextField, UniqueCombination.AttributeCombination> attributeCombinationOverview = new HashMap<>();
    private UniqueCombination.AttributeCombination currentActiveCombination;

    @FXML
    private VBox attributesListVBox;
    @FXML
    private VBox uniqueCombNamesListVBox;

    @FXML
    private void initialize() {
        attributesListVBox.setDisable(true);
    }

    @FXML
    private void newUniqueComboClick(ActionEvent actionEvent) {
        var newAttributeCombination = new UniqueCombination.AttributeCombination();
        model.getCombinations().add(newAttributeCombination);
        uniqueCombNamesListVBox.getChildren().add(generateUniqueCombinationKeyTextField(null, newAttributeCombination));
    }

    @FXML
    private void deleteUniqueComboClick(ActionEvent actionEvent) {
        var node = uniqueCombNamesListVBox.getScene().getFocusOwner();

        if (node instanceof TextField) {
            var attributeCombination = attributeCombinationOverview.get((TextField) node);
            model.getCombinations().remove(attributeCombination);
            tabSwitchMutex = true;
            uniqueCombNamesListVBox.getChildren().remove(node);
            for (var checkBoxNode : attributesListVBox.getChildren()) {
                if (checkBoxNode instanceof CheckBox) {
                    ((CheckBox)checkBoxNode).setSelected(false);
                }
            }
            tabSwitchMutex = true;

            attributesListVBox.setDisable(true);
        }
    }

    @Override
    public void loadModel(UniqueCombination model) {
        this.model = model;
        for (var combination : this.model.getCombinations()) {
            uniqueCombNamesListVBox.getChildren().add(generateUniqueCombinationKeyTextField(combination.getCombinationName(), combination));
        }
    }

    private TextField generateUniqueCombinationKeyTextField(String name, UniqueCombination.AttributeCombination attributeCombination) {
        var textFieldCombination = new TextField();
        if (name == null) {
            textFieldCombination.setPromptText("Unique Name...");
        }
        else {
            textFieldCombination.setText(name);
        }

        textFieldCombination.textProperty().addListener((observable, oldValue, newValue) -> {
            attributeCombination.setCombinationName(newValue);
        });

        textFieldCombination.setOnMouseClicked(mouseEvent -> {
            tabSwitchMutex = true;
            for (var node : attributesListVBox.getChildren()) {
                if (node instanceof CheckBox) {
                    ((CheckBox)node).setSelected(false);
                }
            }
            for (var attribute : attributeCombination.getAttributes()) {
                var checkBox = checkBoxOverview.get(attribute);
                if (checkBox != null) {
                    checkBox.setSelected(true);
                }
            }
            tabSwitchMutex = false;

            currentActiveCombination = attributeCombination;
            attributesListVBox.setDisable(false);
        });

        attributeCombinationOverview.put(textFieldCombination, attributeCombination);
        return  textFieldCombination;
    }

    public void loadAttributes(ArrayList<Attribute> attributes) {
        for (var attribute : attributes) {
            var checkBox = new CheckBox(attribute.getName());
            checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
                if (!tabSwitchMutex) {
                    if (newValue) {
                        currentActiveCombination.addAttribute(attribute);
                    }
                    else {
                        currentActiveCombination.removeAttribute(attribute);
                    }
                }
            });

            attributesListVBox.getChildren().add(checkBox);
            checkBoxOverview.put(attribute, checkBox);
        }
    }
}
