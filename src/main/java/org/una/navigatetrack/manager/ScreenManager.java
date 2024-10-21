package org.una.navigatetrack.manager;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class ScreenManager {
    private final Stage primaryStage;

    public ScreenManager(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void loadScreen(String fxmlFile, String title) {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(fxmlFile)));
            Scene scene = new Scene(root);

            String cssFile = "/styles/menu.css";
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource(cssFile)).toExternalForm());

            primaryStage.setScene(scene);
            primaryStage.setTitle(title);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
