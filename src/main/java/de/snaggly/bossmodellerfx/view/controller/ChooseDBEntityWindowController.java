package de.snaggly.bossmodellerfx.view.controller;

import de.snaggly.bossmodellerfx.guiLogic.GUIActionListener;
import de.snaggly.bossmodellerfx.guiLogic.GUIMethods;
import de.snaggly.bossmodellerfx.model.adapter.DBLAHolder;
import de.snaggly.bossmodellerfx.model.adapter.ProjectData;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.VBox;

import java.sql.SQLException;
import java.util.LinkedList;

public class ChooseDBEntityWindowController implements ModelController<DBLAHolder> {
    public GUIActionListener<ProjectData> parentObserver;

    private DBLAHolder localDBLA;
    private boolean selectionTrigger = false;

    @FXML
    public VBox entityListVBox;
    @FXML
    private CheckBox checkAllChkbox;
    @FXML
    private Button cancelBtn;
    @FXML
    private Button createBtn;

    @FXML
    private void onCheckAllChkBox(ActionEvent actionEvent) {
        selectionTrigger = true;
        for (var checkBox : entityListVBox.getChildren()) {
            if (checkBox instanceof CheckBox) {
                ((CheckBox) checkBox).setSelected(checkAllChkbox.isSelected());
            }
        }
        selectionTrigger = false;
    }

    @FXML
    private void onCancelClick(ActionEvent actionEvent) {
        GUIMethods.closeWindow(actionEvent);
    }

    @FXML
    private void onCreateClick(ActionEvent actionEvent) {
        var selectedEntities = new LinkedList<String>();
        for (var checkBox : entityListVBox.getChildren()) {
            if (checkBox instanceof CheckBox && ((CheckBox)checkBox).isSelected()) {
                selectedEntities.add(((CheckBox) checkBox).getText());
            }
        }

        try {
            localDBLA.getDbla().initializeSome(selectedEntities);
            localDBLA.getDbla().initializeRelations();

            parentObserver.notify(ProjectData.convertLegacyToFXModel(
                    localDBLA.getSchema(),
                    localDBLA.getDbla().getTables(),
                    localDBLA.getDbla().getRelations()));
        } catch (SQLException e) {
            GUIMethods.showError("DBConnector", "Fehler beim lesen der Tabellen", e.getLocalizedMessage());
        }
    }

    @Override
    public void loadModel(DBLAHolder model) {
        localDBLA = model;
        entityListVBox.getChildren().clear();
        try {
            for (var tableName : localDBLA.getDbla().getTableNames()) {
                var checkBox = new CheckBox(tableName);
                checkBox.selectedProperty().addListener((observableValue, aBoolean, newValue) -> {
                    if (selectionTrigger)
                        return;
                    boolean hasSelectedAll = true;
                    for (var vboxCheckBox : entityListVBox.getChildren()) {
                        if (vboxCheckBox instanceof CheckBox && !((CheckBox)vboxCheckBox).isSelected()) {
                            hasSelectedAll = false;
                            break;
                        }
                    }
                    checkAllChkbox.setSelected(hasSelectedAll);
                });
                entityListVBox.getChildren().add(checkBox);
            }
        } catch (SQLException e) {
            GUIMethods.showError("DBConnector", "Fehler beim lesen der Tabellen", e.getLocalizedMessage());
            GUIMethods.closeWindow(entityListVBox.getScene().getWindow());
        }
    }
}
