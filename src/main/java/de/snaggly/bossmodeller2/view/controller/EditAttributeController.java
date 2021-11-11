package de.snaggly.bossmodeller2.view.controller;

import de.snaggly.bossmodeller2.model.Attribute;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

public class EditAttributeController implements ViewController<Attribute> {
    @FXML
    private Button upBtn;
    @FXML
    private Button downBtn;
    @FXML
    private TextField nameTF;
    @FXML
    private CheckBox isPrimaryCheck;
    @FXML
    private CheckBox isNonNullCheck;
    @FXML
    private TextField checkTF;
    @FXML
    private CheckBox isUniqueCheck;
    @FXML
    private TextField defaultTF;

    @FXML
    private void onPrimarySelected() {
        isNonNullCheck.setSelected(isPrimaryCheck.isSelected());
        isUniqueCheck.setSelected(isPrimaryCheck.isSelected());
        isNonNullCheck.setDisable(isPrimaryCheck.isSelected());
        isUniqueCheck.setDisable(isPrimaryCheck.isSelected());
    }

    public TextField getNameTF() {
        return nameTF;
    }

    public CheckBox getIsPrimaryCheck() {
        return isPrimaryCheck;
    }

    public CheckBox getIsNonNullCheck() {
        return isNonNullCheck;
    }

    public TextField getCheckTF() {
        return checkTF;
    }

    public CheckBox getIsUniqueCheck() {
        return isUniqueCheck;
    }

    public TextField getDefaultTF() {
        return defaultTF;
    }

    public void handleUpBtnClick(EventHandler<? super MouseEvent> eventHandler) {
        upBtn.setOnMouseClicked(eventHandler);
    }

    public void handleDownBtnClick(EventHandler<? super MouseEvent> eventHandler) {
        downBtn.setOnMouseClicked(eventHandler);
    }

    @Override
    public void loadModel(Attribute model) {
        this.nameTF.setText(model.getName());
        this.checkTF.setText(model.getCheckName());
        this.defaultTF.setText(model.getDefaultName());
        this.isPrimaryCheck.setSelected(model.isPrimary());
        this.isNonNullCheck.setSelected(model.isNonNull());
        this.isUniqueCheck.setSelected(model.isUnique());

        if (model.isPrimary()) {
            onPrimarySelected();
        }
    }
}
