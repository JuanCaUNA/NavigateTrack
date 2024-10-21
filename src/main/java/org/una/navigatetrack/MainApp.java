package org.una.navigatetrack;

import javafx.application.Application;
import javafx.stage.Stage;
import org.una.navigatetrack.manager.ScreenManager;
import org.una.navigatetrack.utils.Singleton;

public class MainApp extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        ScreenManager screenManager = new ScreenManager(primaryStage);
        Singleton singleton = Singleton.getInstance();
        singleton.setScreenManager(screenManager);

        screenManager.loadScreen("/fxml/MapManager.fxml", "Initial View");
//        screenManager.loadScreen("/fxml/ImplementsLogic.fxml", "Initial View");
    }
}
