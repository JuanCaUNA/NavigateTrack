package org.una.navigatetrack;

import javafx.application.Application;
import javafx.stage.Stage;
import org.una.navigatetrack.manager.ScreenManager;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        ScreenManager screenManager = new ScreenManager(primaryStage);
        screenManager.loadScreen("/fxml/MapManageFXML.fxml", "Initial View");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
