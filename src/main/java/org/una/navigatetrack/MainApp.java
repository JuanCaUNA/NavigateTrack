package org.una.navigatetrack;

import javafx.application.Application;
import javafx.stage.Stage;
import org.una.navigatetrack.utils.AppContext;

public class MainApp extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        AppContext.getInstance().defineScreen(primaryStage, "/fxml/ImplementsLogic.fxml");

    }
}
