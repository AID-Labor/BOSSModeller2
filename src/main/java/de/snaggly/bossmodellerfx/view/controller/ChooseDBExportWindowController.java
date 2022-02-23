package de.snaggly.bossmodellerfx.view.controller;

import de.bossmodeler.logicalLayer.elements.DBLanguageNotFoundException;
import de.snaggly.bossmodellerfx.guiLogic.GUIMethods;
import de.snaggly.bossmodellerfx.guiLogic.Project;
import de.snaggly.bossmodellerfx.model.adapter.DBConnectorException;
import de.snaggly.bossmodellerfx.model.adapter.DBLAHolder;
import de.snaggly.bossmodellerfx.model.adapter.ProjectData;
import de.snaggly.bossmodellerfx.model.adapter.SQLLanguage;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.SQLException;
import java.util.HashMap;

/**
 * Controller for DB Export Window
 *
 * @author Omar Emshani
 */
public class ChooseDBExportWindowController implements ModelController<DBLAHolder> {
    private DBLAHolder dblaHolder;
    private final HashMap<String, ObservableList<String>> schemeMap = new HashMap<>();

    @FXML
    private ProgressIndicator progressIndicator;
    @FXML
    private ChoiceBox<String> existingDBChoiceBox;
    @FXML
    private CheckBox useExistingDBChkBox;
    @FXML
    private Label newDbNameLabel;
    @FXML
    private TextField newDBNameTf;
    @FXML
    private CheckBox useExistingSchemeCkBox;
    @FXML
    private ChoiceBox<String> existingSchemesChoiceBox;
    @FXML
    private Label createNewSchemeLabel;
    @FXML
    private TextField newSchemeTf;
    @FXML
    private CheckBox caseSensitive;
    @FXML
    private Button cancelBtn;
    @FXML
    private Button exportToDBBtn;

    @FXML
    private void useExistingDBChkBoxOnAction(ActionEvent actionEvent) {
    }

    @FXML
    private void onCancelClick(ActionEvent actionEvent) {
        GUIMethods.closeWindow(actionEvent);
    }

    @FXML
    private void onExportClick(ActionEvent actionEvent) {
        progressIndicator.setVisible(true);
        new Thread(() -> {
            try {
                var dBName = newDBNameTf.getText();
                var schemeName = newSchemeTf.getText();

                if (!useExistingSchemeCkBox.isSelected()) {
                    if (newSchemeTf.getText().equals("") && dblaHolder.getLanguage() != SQLLanguage.MySQL) {
                        throw new DBConnectorException("Kein Schema", "Es muss ein Schema angegeben werden!");
                    }

                    for (var existingSchemeName : existingSchemesChoiceBox.getItems()) {
                        if (existingSchemeName.equals(dBName)) {
                            throw new DBConnectorException("Existierender Name", "Es existiert bereits ein Schema mit dem selben Namen!");
                        }
                    }
                } else {
                    schemeName = existingSchemesChoiceBox.getSelectionModel().getSelectedItem();
                }

                if (!useExistingDBChkBox.isSelected()) {
                    for (var existingDBName : existingDBChoiceBox.getItems()) {
                        if (existingDBName.equals(schemeName)) {
                            throw new DBConnectorException("Existierender Name", "Es existiert bereits eine Datenbank mit dem selben Namen!");
                        }
                    }
                } else {
                    dBName = existingDBChoiceBox.getSelectionModel().getSelectedItem();
                }

                var dbExists = useExistingDBChkBox.isSelected();
                var newSchema = !useExistingSchemeCkBox.isSelected() && !schemeName.equals("public");
                var css = caseSensitive.isSelected();

                var dbla = dblaHolder.getDbla();
                var legacyProjectHolder = ProjectData.convertFXToLegacyModel(
                        Project.getCurrentProject().getEntities(), Project.getCurrentProject().getRelations()
                );
                dbla.getTables().clear();
                dbla.getTables().addAll(legacyProjectHolder.getDbTables());
                dbla.writeTablesToDB(dBName, schemeName, css, dbExists, newSchema);

                Platform.runLater(() -> GUIMethods.closeWindow(newDBNameTf.getScene().getWindow()));
            } catch (SQLException e) {
                Platform.runLater(() -> GUIMethods.showError("DBConnector", "SQL Error", e.getLocalizedMessage()));
            } catch (DBLanguageNotFoundException e) {
                Platform.runLater(() -> GUIMethods.showError("DBConnector", "SQL Language Error", e.getLocalizedMessage()));
            } catch (DBConnectorException e) {
                Platform.runLater(() -> GUIMethods.showError("DBConnector", e.head, e.reason));
            } finally {
                Platform.runLater(() -> progressIndicator.setVisible(false));
            }

        }).start();
    }

    @FXML
    private void initialize() {
        useExistingDBChkBox.selectedProperty().addListener((observableValue, aBoolean, newValue) -> {
            existingDBChoiceBox.setDisable(!newValue);
            newDBNameTf.setDisable(newValue);
            useExistingSchemeCkBox.setDisable(!newValue);
            if (!newValue) {
                useExistingSchemeCkBox.setSelected(false);
            }
        });
        useExistingSchemeCkBox.selectedProperty().addListener((observableValue, aBoolean, newValue) -> {
            existingSchemesChoiceBox.setDisable(!newValue);
            newSchemeTf.setDisable(newValue);
        });
        existingDBChoiceBox.getSelectionModel().selectedItemProperty().addListener((observableValue, s, newValue) ->  {
            existingSchemesChoiceBox.setItems(schemeMap.get(newValue));
            existingSchemesChoiceBox.getSelectionModel().selectFirst();
        });

        caseSensitive.setSelected(true);
        useExistingDBChkBox.setSelected(false);
        useExistingSchemeCkBox.setSelected(false);
        existingDBChoiceBox.setDisable(true);
        existingSchemesChoiceBox.setDisable(true);
    }

    @Override
    public void loadModel(DBLAHolder holder) {
        this.dblaHolder = holder;
        new Thread(() -> {
            Platform.runLater(() -> progressIndicator.setVisible(true));
            try {
                var database = holder.getDbla().getDatabase();
                if (holder.getLanguage() != SQLLanguage.MySQL) {
                    for (var dbName : database) {
                        schemeMap.put(dbName, FXCollections.observableArrayList(holder.getDbla().getDBSchemata(dbName)));
                    }
                }

                Platform.runLater(() -> {
                    existingDBChoiceBox.getItems().addAll(database);
                    existingDBChoiceBox.getSelectionModel().selectFirst();
                });
            } catch (SQLException e) {
                Platform.runLater(() -> {
                    GUIMethods.showError("DBConnector", "Verbindungsfehler", e.getLocalizedMessage());
                    GUIMethods.closeWindow(progressIndicator.getScene().getWindow());
                });
            } finally {
                Platform.runLater(() -> progressIndicator.setVisible(false));
            }

        }).start();
    }
}
