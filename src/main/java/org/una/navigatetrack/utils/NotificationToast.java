package org.una.navigatetrack.utils;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import lombok.Setter;

import java.util.Objects;

@Setter
public class NotificationToast extends Application {
    private String message;
    private String title;
    private int duration;

    // Constructor para establecer mensaje y título de la notificación
    public NotificationToast(String message, String title) {
        init(message, title);
    }

    public NotificationToast() {
        init("message", "title");
    }

    public void init(String message, String title) {
        this.message = message;
        this.title = title;
        this.duration = 8;
    }

    @Override
    public void start(Stage primaryStage) {
        Label messageLabel = createNotificationLabel();

        StackPane stackPane = createNotificationStackPane(messageLabel);//Para contener el label y darle estilo


        Stage notificationStage = createNotificationStage(primaryStage, stackPane);// Crear el Stage para la notificación

        // Establecer el ícono de la notificación
        setAppIcon(notificationStage);

        // Posicionar la notificación en la parte inferior central de la pantalla
        positionNotificationStage(notificationStage, primaryStage);

        // Agregar animaciones de aparición y desaparición
        addNotificationAnimations(notificationStage, stackPane);

        // Mostrar la notificación
        notificationStage.show();
    }

    // Crea el Label de la notificación
    private Label createNotificationLabel() {
        Label label = new Label(message);
        label.setFont(new Font("Arial", 16));
        label.setTextFill(Color.WHITE);
        label.setStyle(
                "-fx-background-color: transparent;" +
                        " -fx-padding: 15px;" +
                        " -fx-text-fill: #8fffee;" +
                        " -fx-font-weight: bold;");
        label.setTextAlignment(TextAlignment.CENTER);  // Centrar el texto dentro del Label
        return label;
    }

    // Crea el StackPane con el estilo de la notificación
    private StackPane createNotificationStackPane(Label label) {
        StackPane stackPane = new StackPane(label);
        stackPane.setStyle(
                "-fx-background-color: linear-gradient(to top left,#000000, #203935, #000000); " // Fondo de gradiente
                        + "-fx-background-radius: 15px; "  // Esquinas redondeadas del fondo
                        + "-fx-border-color: #8fffee; "    // Color del borde (puedes personalizarlo)
                        + "-fx-border-width: 1px; "        // Grosor del borde
                        + "-fx-border-radius: 15px; ");    // Esquinas redondeadas del borde
        return stackPane;
    }

    // Crea el Stage para la notificación
    private Stage createNotificationStage(Stage primaryStage, StackPane stackPane) {
        Stage notificationStage = new Stage();
        notificationStage.initOwner(primaryStage);
        notificationStage.setOpacity(0.85);  // Un poco más opaco
        notificationStage.setScene(new Scene(stackPane, 350, 70)); // Establecer un tamaño flexible y mayor
        notificationStage.setTitle(title);

        // Hacer la barra de título invisible (simulando que no tiene barra de título)
        notificationStage.initStyle(StageStyle.DECORATED);  // Eliminar la barra de título
        notificationStage.alwaysOnTopProperty();
        return notificationStage;
    }

    // Posiciona la notificación en la pantalla
    private void positionNotificationStage(Stage notificationStage, Stage primaryStage) {
        // Posicionar la notificación en la parte inferior central de la pantalla
        notificationStage.setX(primaryStage.getX() + (primaryStage.getWidth() - 350) / 2);  // Centrado horizontal
        notificationStage.setY(primaryStage.getY() + 10); // Centrado vertical abajo + primaryStage.getHeight()
    }

    // Añade animaciones de aparición y desaparición a la notificación
    private void addNotificationAnimations(Stage notificationStage, StackPane stackPane) {
        // Efecto de aparición (Fade In)
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.5), stackPane);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        // Efecto de desaparición (Fade Out) después de un tiempo
        PauseTransition pause = new PauseTransition(Duration.seconds(duration)); // duración visible
        pause.setOnFinished(e -> {
            // Efecto de desaparición
            FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.1), stackPane);
            fadeOut.setFromValue(1);
            fadeOut.setToValue(0);
            fadeOut.setOnFinished(event -> notificationStage.close());  // Cerrar el Stage después del efecto
            fadeOut.play();
        });

        // Mostrar la notificación con la animación de entrada
        fadeIn.play();

        // Iniciar la pausa para la desaparición automática
        pause.play();

        // Cerrar la notificación si el usuario hace clic sobre ella
        stackPane.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> notificationStage.close());
    }

    // Establece el ícono de la notificación
    private void setAppIcon(Stage notificationStage) {
        // Cargar el ícono de la notificación
        String iconPath = "/icons/notification-icon.png";  // Ruta del ícono (asegurarse de que esté en la carpeta de recursos)
        Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream(iconPath)));
        notificationStage.getIcons().add(icon);  // Establecer el ícono
    }

    // Méto-do para mostrar la notificación
    public void showNotification(Stage primaryStage) {
        start(primaryStage);
    }

    // Méto-do principal
    public static void main(String[] args) {
        launch(args);
    }
}

//"-fx-background-color: rgba(0, 0, 0, 0.75);"