package de.snaggly.bossmodellerfx.view.controller;

import de.bossmodeler.dbInterface.Schnittstelle;
import de.snaggly.bossmodellerfx.BOSS_Strings;
import de.snaggly.bossmodellerfx.MainController;
import de.snaggly.bossmodellerfx.guiLogic.GUIMethods;
import de.snaggly.bossmodellerfx.model.adapter.DBProjectHolder;
import de.snaggly.bossmodellerfx.model.adapter.SQLInterface;
import de.snaggly.bossmodellerfx.model.adapter.SQLLanguage;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.SQLException;
import java.util.LinkedList;

/**
 * Controls the SQLViewer window
 * @author Omar Emshani
 */
public class SQLViewerController implements ModelController<DBProjectHolder> {
    private DBProjectHolder dbProject;
    @FXML
    private TextArea sqlTextArea;
    @FXML
    private ChoiceBox<String> dbmsChoiceBox;
    @FXML
    private CheckBox createNewSchemaChkBox;
    @FXML
    private TextField schemaNameTf;
    @FXML
    private CheckBox cssChkBox;

    @FXML
    private void initialize() {
        var sqlLanguages = SQLLanguage.values();
        var sqlLanguageStrings = new LinkedList<String>();
        for (var sqlLanguage : sqlLanguages) {
            sqlLanguageStrings.add(sqlLanguage.name());
        }
        dbmsChoiceBox.getItems().addAll(sqlLanguageStrings);
        dbmsChoiceBox.getSelectionModel().selectedItemProperty().addListener((observableValue, s, t1) -> {
            if (t1.equals("MySQL")) {
                schemaNameTf.setText("");
                schemaNameTf.setVisible(false);
                createNewSchemaChkBox.setSelected(false);
                createNewSchemaChkBox.setVisible(false);
            } else {
                schemaNameTf.setVisible(true);
                createNewSchemaChkBox.setSelected(true);
                createNewSchemaChkBox.setVisible(true);
            }
        });
    }

    @FXML
    private void onCloseClick(ActionEvent actionEvent) {
        GUIMethods.closeWindow(actionEvent);
    }

    @FXML
    private void onSaveClick(ActionEvent actionEvent) {
        var file = GUIMethods.showSQLFileSaveDialog(BOSS_Strings.SAVE_SQL, sqlTextArea.getScene().getWindow());
        if (file == null)
            return;
        saveFile(file);
        GUIMethods.closeWindow(sqlTextArea.getScene().getWindow());
    }

    private void saveFile(File file) {
        try {
            var fileWriter = new BufferedWriter(new FileWriter(file));
            fileWriter.write(sqlTextArea.getText());
            fileWriter.close();
        } catch (Exception e) {
            GUIMethods.showError(MainController.class.getSimpleName(), BOSS_Strings.PRODUCT_NAME, e.getLocalizedMessage());
        }
    }

    @Override
    public void loadModel(DBProjectHolder model) {
        dbProject = model;
        dbmsChoiceBox.getSelectionModel().selectedItemProperty().addListener((observableValue, stringSingleSelectionModel, t1) -> updateText());
        createNewSchemaChkBox.selectedProperty().addListener((observableValue, s, t1) -> updateText());
        schemaNameTf.textProperty().addListener((observableValue, s, t1) -> updateText());
        cssChkBox.selectedProperty().addListener((observableValue, aBoolean, t1) -> updateText());

        dbmsChoiceBox.getSelectionModel().selectFirst();
    }

    @FXML
    private void updateText() {
        try {
            var dbInterface = getDBInterface();
            sqlTextArea.setText(dbInterface.generateSQLCodeFromTables(
                    dbProject.getDbTables(),
                    cssChkBox.isSelected(),
                    createNewSchemaChkBox.isSelected(),
                    schemaNameTf.getText()));
        } catch (SQLException e) {
            GUIMethods.showError(BOSS_Strings.DB_CONNECTOR, BOSS_Strings.CONNECTION_ERROR, e.getLocalizedMessage());
        }
    }

    private Schnittstelle getDBInterface() throws SQLException {
        var inter = SQLInterface.getDbDriverInterface(SQLLanguage.values()[dbmsChoiceBox.getSelectionModel().getSelectedIndex()]);
        if (inter == null)
            throw new SQLException(BOSS_Strings.COULD_NOT_CREATE_DBINTERFACE);

        return inter;
    }
}
