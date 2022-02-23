package de.snaggly.bossmodellerfx.view.controller;

import de.snaggly.bossmodellerfx.model.subdata.Attribute;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

/**
 * Controller for Attribute Editor View
 *
 * @author Omar Emshani
 */
public class EditAttributeController implements ModelController<Attribute> {
    @FXML
    private ComboBox<String> dataTypeComboBox;
    @FXML
    private VBox nameVBox;
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

    public ComboBox<String> getDataTypeComboBox() {
        return dataTypeComboBox;
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
        this.dataTypeComboBox.getEditor().setText(model.getType());
        this.checkTF.setText(model.getCheckName());
        this.defaultTF.setText(model.getDefaultName());
        this.isPrimaryCheck.setSelected(model.isPrimary());
        this.isNonNullCheck.setSelected(model.isNonNull());
        this.isUniqueCheck.setSelected(model.isUnique());

        if (model.isPrimary()) {
            onPrimarySelected();
        }

        if (model.getFkTableColumn() != null) {
            this.dataTypeComboBox.setDisable(true);
            this.checkTF.setDisable(true);
            this.defaultTF.setDisable(true);
            this.isPrimaryCheck.setDisable(true);
            this.isNonNullCheck.setDisable(true);
            this.isUniqueCheck.setDisable(true);
            nameVBox.getChildren().add(new Label("*Fremdschlüssel zu: " + model.getFkTableColumn().getName()));
        }
    }

    @FXML
    private void initialize() {
        dataTypeComboBox.getItems().addAll(testDT);
    }

    //TODO
    private final String[] testDT = {
            "bigint" ,
            "bigserial" ,
            "bit" ,
            "varbit" ,
            "boolean" ,
            "box" ,
            "bytea" ,
            "varchar" ,
            "varchar(25)" ,
            "varchar(125)" ,
            "varchar(1024)" ,
            "char" ,
            "cidr" ,
            "circle" ,
            "date" ,
            "double precision" ,
            "inet" ,
            "integer" ,
            "interval" ,
            "line" ,
            "lseg" ,
            "macaddr" ,
            "money" ,
            "numeric" ,
            "path" ,
            "point" ,
            "polygon" ,
            "real" ,
            "smallint" ,
            "serial" ,
            "text" ,
            "time" ,
            "timetz" ,
            "timestamp" ,
            "timestamptz" ,
            "tsquery" ,
            "tsvector" ,
            "txid_snapshot" ,
            "uuid" ,
            "xml" };
}
