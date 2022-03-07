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
import java.util.Arrays;

/**
 * Controller for DBConnector Window
 *
 * @author Omar Emshani
 */
public class ConnectToDBWindowController implements ModelController<DBLAHolder>{
    public GUIActionListener<DBLAHolder> parentObserver;
    private SQLInterface sqlInterface;

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
        sqlLangChoiceBox.getSelectionModel().selectedIndexProperty().addListener((observableValue, number, t1) -> {
            sqlInterface = SQLInterface.getSQLInterfaceDescriptor(SQLLanguage.values()[t1.intValue()]);
            portTf.setText(sqlInterface.getDefaultPort());
            usernameTf.setText(sqlInterface.getDefaultUsername());
        });
        sqlLangChoiceBox.getSelectionModel().selectFirst();
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
                                sqlInterface.getDefaultDBName(),
                                "",
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
                });
            }
        }).start();
    }

    @FXML
    private void onCancel(ActionEvent actionEvent) {
        GUIMethods.closeWindow(actionEvent);
    }

    private DBLogicalAdministration setDB() throws SQLException {
        var inter = SQLInterface.getDbDriverInterface(
                sqlInterface.getLanguage(),
                hostTf.getText(),
                portTf.getText(),
                sqlInterface.getDefaultDBName(),
                usernameTf.getText(),
                passwordTf.getText(),
                ""
        );

        if (inter == null)
            throw new SQLException(BOSS_Strings.COULD_NOT_CREATE_DBINTERFACE);
        return new DBLogicalAdministration(new DBInterfaceCommunication(inter));
    }

    @Override
    public void loadModel(DBLAHolder model) {
        sqlLangChoiceBox.getSelectionModel().select(Arrays.asList(SQLLanguage.values()).indexOf(model.getLanguage()));
        hostTf.setText(model.getHost());
        portTf.setText(model.getPort());
        usernameTf.setText(model.getUser());
        passwordTf.setText(model.getPass());
    }
}
