package de.snaggly.bossmodellerfx.view.controller;

import de.snaggly.bossmodellerfx.BOSS_Strings;
import de.snaggly.bossmodellerfx.guiLogic.GUIMethods;
import de.snaggly.bossmodellerfx.model.subdata.Attribute;
import de.snaggly.bossmodellerfx.model.subdata.AttributeCombination;
import de.snaggly.bossmodellerfx.model.subdata.UniqueCombination;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Controller for EntityView
 *
 * @author Omar Emshani
 */
public class EditUniqueCombinationWindowController implements ModelController<UniqueCombination> {
    private boolean tabSwitchMutex = false;
    private UniqueCombination model;
    private UniqueCombination refModel;
    private final HashMap<Attribute, CheckBox> checkBoxOverview = new HashMap<>();
    private final HashMap<TextField, AttributeCombination> attributeCombinationOverview = new HashMap<>();
    private AttributeCombination currentActiveCombination;

    @FXML
    private VBox attributesListVBox;
    @FXML
    private VBox uniqueCombNamesListVBox;
    @FXML
    private Button newBtn;
    @FXML
    private Button deleteBtn;

    @FXML
    private void initialize() {
        attributesListVBox.setDisable(true);
    }

    @FXML
    private void newUniqueComboClick(ActionEvent actionEvent) {
        var newAttributeCombination = new AttributeCombination();
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
        this.model = new UniqueCombination();
        this.model.setCombinations(new ArrayList<>());
        for (var combination : model.getCombinations()) {
            var attriCombinationL = new AttributeCombination();
            attriCombinationL.setCombinationName(combination.getCombinationName());
            attriCombinationL.setPrimaryCombination(combination.isPrimaryCombination());
            for (var attribute : combination.getAttributes()){
                attriCombinationL.addAttribute(attribute);
            }
            this.model.getCombinations().add(attriCombinationL);
        }
        this.refModel = model;
        for (var combination : this.model.getCombinations()) {
            uniqueCombNamesListVBox.getChildren().add(generateUniqueCombinationKeyTextField(combination.getCombinationName(), combination));
        }
    }

    private TextField generateUniqueCombinationKeyTextField(String name, AttributeCombination attributeCombination) {
        var textFieldCombination = new TextField();
        if (name == null) {
            textFieldCombination.setPromptText(BOSS_Strings.NEW_UNIQUE_NAME_PROMPT);
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
            attributesListVBox.setDisable(attributeCombination.isPrimaryCombination());
            deleteBtn.setDisable(attributeCombination.isPrimaryCombination());
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

    @FXML
    private void onSaveAction(ActionEvent actionEvent) {
        refModel.setCombinations(model.getCombinations());
        GUIMethods.closeWindow(actionEvent);
    }
}
