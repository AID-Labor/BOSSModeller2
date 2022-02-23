package de.snaggly.bossmodellerfx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Here the application starts.
 *
 * @author Omar Emshani
 */
public class Main extends Application {

    public static void main(String[] args) {
        launch(args); //Delegates start to JavaFX Plattform.
    }

    /**
     * Loads the main window from the given fxml file and display it.
     * Further interactions are called in MainController by the View.
     */
    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("main-window.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        primaryStage.setTitle("BOSSModellerFX");
        primaryStage.setScene(scene);
        primaryStage.show();
        //To close all subwindows when main application closes.
        primaryStage.setOnCloseRequest(windowEvent -> ((MainController)fxmlLoader.getController()).closeApp());
    }
}