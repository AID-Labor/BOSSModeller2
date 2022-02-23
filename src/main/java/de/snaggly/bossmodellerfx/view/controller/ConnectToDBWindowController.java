package de.snaggly.bossmodellerfx.view.controller;

import de.bossmodeler.dbInterface.MSSQLServerSchnittstelle;
import de.bossmodeler.dbInterface.MySQLSchnittstelle;
import de.bossmodeler.dbInterface.PostgreSQLSchnittstelle;
import de.bossmodeler.dbInterface.Schnittstelle;
import de.bossmodeler.logicalLayer.elements.DBInterfaceCommunication;
import de.bossmodeler.logicalLayer.elements.DBLogicalAdministration;
import de.snaggly.bossmodellerfx.guiLogic.GUIActionListener;
import de.snaggly.bossmodellerfx.guiLogic.GUIMethods;
import de.snaggly.bossmodellerfx.model.adapter.DBLAHolder;
import de.snaggly.bossmodellerfx.model.adapter.SQLLanguage;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.SQLException;

/**
 * Controller for DBConnector Window
 *
 * @author Omar Emshani
 */
public class ConnectToDBWindowController implements ModelController<DBLAHolder>{
    public GUIActionListener<DBLAHolder> parentObserver;

    @FXML
    private ChoiceBox<String> sqlLangChoiceBox;
    @FXML
    private Label usernameLabel;
    @FXML
    private TextField usernameTf;
    @FXML
    private Label passwordLabel;
    @FXML
    private TextField passwordTf;
    @FXML
    private Label hostLabel;
    @FXML
    private TextField hostTf;
    @FXML
    private Label portLabel;
    @FXML
    private TextField portTf;
    @FXML
    private Label dbNameLabel;
    @FXML
    private TextField dbNameTf;
    @FXML
    private Label schemeNameLabel;
    @FXML
    private TextField schemeNameTf;
    @FXML
    private Button connectBtn;
    @FXML
    private Button cancelBtn;
    @FXML
    private ProgressIndicator progressIndicator;

    @FXML
    private void initialize(){
        progressIndicator.setVisible(false);

        for (var sqlLang : SQLLanguage.values()) {
            sqlLangChoiceBox.getItems().add(sqlLang.name());
        }
        sqlLangChoiceBox.getSelectionModel().selectFirst();

        /*usernameTf.setText("postgres");
        hostTf.setText("localhost");
        portTf.setText("5432");
        dbNameTf.setText("postgres");
        schemeNameTf.setText("public");*/

        usernameTf.setText("admin");
        passwordTf.setText("admin123");
        hostTf.setText("192.168.0.37");
        portTf.setText("5432");
        dbNameTf.setText("aid");
        schemeNameTf.setText("boss");
    }

    @FXML
    private void connectClick(ActionEvent actionEvent) {
        progressIndicator.setVisible(true);
        new Thread(() -> {
            try {
                var db = setDB();
                Platform.runLater(() -> {
                    progressIndicator.setVisible(false);
                    if (parentObserver != null) {
                        parentObserver.notify(new DBLAHolder(
                                db,
                                SQLLanguage.values()[sqlLangChoiceBox.getSelectionModel().getSelectedIndex()],
                                hostTf.getText(),
                                portTf.getText(),
                                dbNameTf.getText(),
                                usernameTf.getText(),
                                passwordTf.getText(),
                                schemeNameTf.getText())
                        );
                    }
                });
            } catch (SQLException e) {
                Platform.runLater(() -> {
                    progressIndicator.setVisible(false);
                    GUIMethods.showError("DBConnector", "Verbindungsfehler", e.getLocalizedMessage());
                });
            }
        }).start();
    }

    @FXML
    private void onCancel(ActionEvent actionEvent) {
        GUIMethods.closeWindow(actionEvent);
    }

    private DBLogicalAdministration setDB() throws SQLException {
        Schnittstelle inter = null;
        switch (sqlLangChoiceBox.getSelectionModel().getSelectedItem()) {
            case "PostgreSQL":
                inter = new PostgreSQLSchnittstelle(
                        hostTf.getText(),
                        portTf.getText(),
                        dbNameTf.getText(),
                        usernameTf.getText(),
                        passwordTf.getText(),
                        schemeNameTf.getText());
                break;
            case "MSSQL":
                inter = new MSSQLServerSchnittstelle(
                        hostTf.getText(),
                        portTf.getText(),
                        dbNameTf.getText(),
                        usernameTf.getText(),
                        passwordTf.getText(),
                        schemeNameTf.getText());
                break;
            case "MySQL":
                inter = new MySQLSchnittstelle(
                        hostTf.getText(),
                        portTf.getText(),
                        dbNameTf.getText(),
                        usernameTf.getText(),
                        passwordTf.getText(),
                        "");
                break;
        }

        if (inter == null)
            throw new SQLException("Datenbank Schnittstelle konnte nicht erstellt werden");
        var interCom = new DBInterfaceCommunication(inter);
        var result = new DBLogicalAdministration(interCom);
        result.initializeTables();
        return result;
    }

    @Override
    public void loadModel(DBLAHolder model) {
        hostTf.setText(model.getHost());
        portTf.setText(model.getPort());
        dbNameTf.setText(model.getDb());
        usernameTf.setText(model.getUser());
        passwordTf.setText(model.getPass());
        schemeNameTf.setText(model.getSchema());
    }

    public void prepForExport(boolean willBeUsedForExport) {
        schemeNameTf.setText("");
        schemeNameTf.setVisible(!willBeUsedForExport);
        schemeNameLabel.setVisible(!willBeUsedForExport);
    }
}
