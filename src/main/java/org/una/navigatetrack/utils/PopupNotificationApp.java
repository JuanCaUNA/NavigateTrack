package org.una.navigatetrack.utils;

import com.jfoenix.controls.JFXPopup;
import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

public class PopupNotificationApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Crear el StackPane principal de la aplicación
        StackPane root = new StackPane();
        root.getChildren().add(new Label("Haz clic para mostrar la notificación personalizada"));

        // Crear el JFXPopup
        JFXPopup popup = new JFXPopup();

        // Crear el contenido del popup (en este caso, un Label con estilo)
        StackPane popupContent = createPopupContent();
        popup.setPopupContent(popupContent);

        // Animación de Fade In cuando se muestra el popup
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.5), popupContent);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        // Animación de Fade Out cuando el popup desaparece
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.5), popupContent);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);

        // Mostrar el popup al hacer clic en el StackPane principal
        root.setOnMouseClicked(event -> {
            // Mostrar el popup con animación
            popup.show(primaryStage);
            fadeIn.play();

            // Cerrar el popup automáticamente después de 3 segundos
            fadeOut.setOnFinished(e -> popup.hide());
            fadeOut.play();
        });

        // Configurar la escena y la ventana principal
        Scene scene = new Scene(root, 400, 250);
        primaryStage.setTitle("Notificación Emergente Personalizada");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Crea el contenido del popup con personalización de estilo
    private StackPane createPopupContent() {
        StackPane content = new StackPane();

        // Crear un Label con texto personalizado
        Label popupLabel = new Label("¡Notificación emergente!");
        popupLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #fd0000; -fx-padding: 10px;");

        // Estilo de fondo, bordes redondeados y sombra
        content.setStyle("-fx-background-color: linear-gradient(to bottom right, #2e3b3b, #1e2a2a);" +
                "-fx-background-radius: 10px; " +
                "-fx-padding: 20px; " +
                "-fx-border-radius: 10px; " +
                "-fx-border-color: #8fffee; " +
                "-fx-border-width: 2px;");

        // Añadir el Label al StackPane
        content.getChildren().add(popupLabel);

        // Personalizar sombras y bordes
        content.setEffect(new javafx.scene.effect.DropShadow(10, Color.BLACK));

        return content;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
