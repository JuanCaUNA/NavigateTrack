package org.una.navigatetrack.utils;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import lombok.Setter;

import java.util.Objects;

public class NotificationToast extends Application {
    private static final int MAX_CHARACTERS = 150; // Max characters per line
    private static final int PIXELS_PER_CHARACTER = 10; // Pixels per character to estimate width
    private static final int MAX_LINES = 3; // Max number of lines
    private static final int MIN_WIDTH = 150;
    private static final int MAX_WIDTH = 900;
    private static final int MIN_HEIGHT = 40;
    private static final int MAX_HEIGHT = 200;

    @Setter
    private String message;
    @Setter
    private String title;
    @Setter
    private int duration = 8; // Duration in seconds

    private int lines;

    // Variable estática para el último Stage mostrado
    private static Stage lastNotificationStage = null;

    public NotificationToast(String message, String title) {
        this.message = message;
        this.title = title;
    }

    public NotificationToast() {
        this("Default message goes here. (4454.848,4848.484) h", "Notification");
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        // Cerrar la notificación anterior, si existe
        if (lastNotificationStage != null) {
            lastNotificationStage.close();
        }

//        System.out.println("Original Message: " + message);
        message = trimMessage(message);  // Asegurarse de que el mensaje no exceda el límite

        Label messageLabel = createNotificationLabel();
        StackPane stackPane = createNotificationStackPane(messageLabel);

        // Calcular el ancho del stage basado en el texto
        int width = countMaxCharsPerLine(message) * PIXELS_PER_CHARACTER;
        width = Math.min(width, MAX_WIDTH);
        width = Math.max(width, MIN_WIDTH);

        // Crear y configurar la ventana de la notificación
        Stage notificationStage = createNotificationStage(primaryStage, stackPane, width, MIN_HEIGHT * lines);
        setAppIcon(notificationStage);
        positionNotificationStage(notificationStage, primaryStage, width);
        addNotificationAnimations(notificationStage, stackPane);

//        System.out.println("Trimmed Message: " + message);

        // Mostrar la nueva notificación
        notificationStage.show();

        // Actualizar el último Stage mostrado
        lastNotificationStage = notificationStage;
    }

    private Label createNotificationLabel() {
        Label label = new Label(message);  // Asegúrate de que message es el texto final
        label.setFont(new Font("Arial", 16));
        label.setWrapText(true);
        label.setStyle("-fx-background-color: transparent;" +
                "-fx-padding: 10px;" +
                "-fx-text-fill: #8fffee;" +
                "-fx-font-weight: bold;");
        label.setMaxHeight(MAX_HEIGHT);
        label.setMinHeight(MIN_HEIGHT);
        label.setAlignment(Pos.CENTER);
        return label;
    }

    private StackPane createNotificationStackPane(Label label) {
        StackPane stackPane = new StackPane(label);
        stackPane.setStyle("-fx-background-color: linear-gradient(to top left, #000000, #203935, #000000);" +
                "-fx-background-radius: 15px;" +
                "-fx-border-color: #8fffee;" +
                "-fx-border-width: 1px;" +
                "-fx-border-radius: 15px;");
        return stackPane;
    }

    private Stage createNotificationStage(Stage primaryStage, StackPane stackPane, double width, double height) {
        Stage notificationStage = new Stage();
        notificationStage.initOwner(primaryStage);
        notificationStage.setOpacity(0.85);
        notificationStage.setMinWidth(MIN_WIDTH + 10);
        notificationStage.setMinHeight(MIN_HEIGHT + 10);
        notificationStage.setMaxHeight(MAX_HEIGHT + 10);
        notificationStage.setMaxWidth(MAX_WIDTH + 10);
        notificationStage.setScene(new Scene(stackPane, width, height));
        notificationStage.setTitle(title);
        notificationStage.initStyle(StageStyle.DECORATED);
        notificationStage.alwaysOnTopProperty();
        return notificationStage;
    }

    private void positionNotificationStage(Stage notificationStage, Stage primaryStage, double width) {
        notificationStage.setX(primaryStage.getX() + ((primaryStage.getWidth() / 2) - (width / 2)));
        notificationStage.setY(primaryStage.getY() + 10);
        notificationStage.setResizable(false);
    }

    private void addNotificationAnimations(Stage notificationStage, StackPane stackPane) {
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.2), stackPane);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.2), stackPane);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(e -> notificationStage.close());

        fadeIn.play();
        PauseTransition pause = new PauseTransition(Duration.seconds(duration));
        pause.setOnFinished(e -> fadeOut.play());
        pause.play();

        stackPane.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> notificationStage.close());
    }

    private void setAppIcon(Stage notificationStage) {
        String iconPath = "/icons/notification-icon.png";
        Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream(iconPath)));
        notificationStage.getIcons().add(icon);
    }

    /**
     * Counts the maximum number of characters that can fit in one line of the notification
     * and calculates the number of lines needed to display the full message.
     *
     * @param text The notification message text.
     * @return The maximum number of characters per line.
     */
    public int countMaxCharsPerLine(String text) {
        int maxChars = 0;
        int currentCount = 0;
        lines = 1; // Inicializar las líneas en 1

        // Iterar sobre cada carácter del mensaje
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);

            // Si es un salto de línea, aumentar el número de líneas
            if (c == '\n') {
                maxChars = Math.max(maxChars, currentCount); // Guardar el máximo de caracteres por línea
                lines++;
                currentCount = 0;
            } else {
                currentCount++;
                // Si llegamos al límite de caracteres por línea, aumentamos el número de líneas
                if (currentCount >= MAX_CHARACTERS) {
                    maxChars = MAX_CHARACTERS;
                    lines++;
                    currentCount = 0;
                }
            }
        }

        // Asegurarse de que maxChars tenga el valor correcto para la última línea
        maxChars = Math.max(maxChars, currentCount);

        // Limitar las líneas a un máximo de MAX_LINES
        lines = Math.min(lines, MAX_LINES);

        return maxChars;
    }

    /**
     * Ajusta el mensaje a un tamaño máximo para evitar que sea demasiado largo.
     * Si el mensaje es demasiado largo, se recorta y se agrega "..." al final.
     */
    public String trimMessage(String message) {
        int maxLength = MAX_CHARACTERS * MAX_LINES; // El límite de caracteres total
        if (message.length() > maxLength) {
            return message.substring(0, maxLength) + "..."; // Recorta y agrega "..."
        }
        return message;
    }
}
