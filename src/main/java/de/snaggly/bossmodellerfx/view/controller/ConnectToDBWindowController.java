package de.snaggly.bossmodellerfx.view.controller;

import de.bossmodeler.logicalLayer.elements.DBInterfaceCommunication;
import de.bossmodeler.logicalLayer.elements.DBLogicalAdministration;
import de.snaggly.bossmodellerfx.BOSS_Strings;
import de.snaggly.bossmodellerfx.guiLogic.GUIActionListener;
import de.snaggly.bossmodellerfx.guiLogic.GUIMethods;
import de.snaggly.bossmodellerfx.model.adapter.DBLAHolder;
import de.snaggly.bossmodellerfx.model.adapter.SQLInterface;
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

    private String dbCustomName = "";

    @FXML
    private ChoiceBox<String> sqlLangChoiceBox;
    @FXML
    private TextField usernameTf;
    @FXML
    private TextField passwordTf;
    @FXML
    private TextField hostTf;
    @FXML
    private TextField portTf;
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
                                usernameTf.getText(),
                                passwordTf.getText())
                        );
                    }
                });
            } catch (SQLException e) {
                Platform.runLater(() -> {
                    progressIndicator.setVisible(false);
                    GUIMethods.showError(BOSS_Strings.DB_CONNECTOR, BOSS_Strings.CONNECTION_ERROR, e.getLocalizedMessage());

                    var textInputDialog = new TextInputDialog();
                    textInputDialog.setResizable(true);
                    textInputDialog.setContentText(BOSS_Strings.TRY_DIFFERENT_DBNAME_PROMPT);
                    textInputDialog.setHeaderText(BOSS_Strings.TRY_DIFFERENT_DBNAME_HEADER);
                    textInputDialog.setTitle(BOSS_Strings.DB_CONNECTOR);
                    textInputDialog.showAndWait().ifPresent(this::setDbCustomName);
                });
            }
        }).start();
    }

    @FXML
    private void onCancel(ActionEvent actionEvent) {
        GUIMethods.closeWindow(actionEvent);
    }

    private DBLogicalAdministration setDB() throws SQLException {
        var sqlInterface = SQLInterface.getSQLInterfaceDescriptor(SQLLanguage.values()[sqlLangChoiceBox.getSelectionModel().getSelectedIndex()]);
        var inter = SQLInterface.getDbDriverInterface(
                sqlInterface.getLanguage(),
                hostTf.getText(),
                portTf.getText(),
                dbCustomName.equals("") ? sqlInterface.getDefaultDBName() : dbCustomName,
                usernameTf.getText(),
                passwordTf.getText(),
                ""
        );

        if (inter == null)
            throw new SQLException(BOSS_Strings.COULD_NOT_CREATE_DBINTERFACE);
        var interCom = new DBInterfaceCommunication(inter);
        var result = new DBLogicalAdministration(interCom);
        result.initializeTables();
        return result;
    }

    @Override
    public void loadModel(DBLAHolder model) {
        hostTf.setText(model.getHost());
        portTf.setText(model.getPort());
        usernameTf.setText(model.getUser());
        passwordTf.setText(model.getPass());
    }

    public String getDbCustomName() {
        return dbCustomName;
    }

    public void setDbCustomName(String dbCustomName) {
        this.dbCustomName = dbCustomName;
    }
}
