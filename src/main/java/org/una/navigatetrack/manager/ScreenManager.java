package org.una.navigatetrack.manager;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.una.navigatetrack.utils.NotificationToast;

import java.io.IOException;
import java.util.Objects;

public class ScreenManager {

    private final Stage primaryStage;
    private final NotificationToast notificationToast;

    @SuppressWarnings("exports")
    public ScreenManager(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.notificationToast = new NotificationToast();
        setAppIcon();
    }

    private void setAppIcon() {
        // Cargar el ícono de la aplicación
        String iconPath = "/icons/app-icon.png"; // Ruta al archivo de imagen dentro de los recursos
        Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream(iconPath)));
        primaryStage.getIcons().add(icon); // Establecer el ícono
    }

    public void loadScreen(String fxmlFile, String title) {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(fxmlFile)));
            Scene scene = new Scene(root);

            // Cargar el archivo CSS
            String cssFile = "/styles/menu.css";
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource(cssFile)).toExternalForm());

            // Configurar la escena
            primaryStage.setScene(scene);
            primaryStage.setTitle(title);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createNotification(String title, String message) {
        notificationToast.setMessage(message);
        notificationToast.setTitle(title);
        notificationToast.start(primaryStage);
    }
}

