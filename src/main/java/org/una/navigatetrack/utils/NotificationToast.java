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
    private static final int MAX_CHARACTERS = 150;
    // Variable estática para el último Stage mostrado
    private static Stage lastNotificationStage = null;
    private final int minHeight = 40, maxHeight = 200;
    private final int minWidth = 150, maxWidth = 900;
    private final int pixelesCaracter = 8, maxlines = 3;
    @Setter
    private String message;
    @Setter
    private String title;
    @Setter
    private int duration = 8;
    private int lines;

    public NotificationToast(String message, String title) {
        this.message = message;
        this.title = title;
    }

    public NotificationToast() {
        this("Default message goes here.", "Notification");
    }

    public static void main(String[] args) {
        launch(args);
    }

    @SuppressWarnings("exports")
    @Override
    public void start(Stage primaryStage) {
        // Cerrar la notificación anterior, si existe
        if (lastNotificationStage != null) {
            lastNotificationStage.close();
        }

        // Crear y mostrar la nueva notificación
        Label messageLabel = createNotificationLabel();
        StackPane stackPane = createNotificationStackPane(messageLabel);

        int width = countMaxCharsPerLine(message) * pixelesCaracter;
        width = Math.min(width, maxWidth);
        width = Math.max(width, minWidth);

        Stage notificationStage = createNotificationStage(primaryStage, stackPane, width, minHeight * lines);
        setAppIcon(notificationStage);
        positionNotificationStage(notificationStage, primaryStage, width);
        addNotificationAnimations(notificationStage, stackPane);

        // Mostrar la nueva notificación
        notificationStage.show();

        // Actualizar el último Stage mostrado
        lastNotificationStage = notificationStage;
    }

    private Label createNotificationLabel() {
        Label label = new Label(message);
        label.setFont(new Font("Arial", 16));
        label.setWrapText(true);
        label.setStyle("-fx-background-color: transparent;" +
                "-fx-padding: 10px;" +
                "-fx-text-fill: #8fffee;" +
                "-fx-font-weight: bold;");
        label.setMaxHeight(maxHeight);
        label.setMinHeight(minHeight);
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
        notificationStage.setMinWidth(minWidth + 10);
        notificationStage.setMinHeight(minHeight + 10);
        notificationStage.setMaxHeight(maxHeight + 10);
        notificationStage.setMaxWidth(maxWidth + 10);
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
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.1), stackPane);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.1), stackPane);
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

    public int countMaxCharsPerLine(String text) {
        int maxChars = 0;
        int currentCount = 0;
        lines = 1;

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);

            if (c == '\n') {
                maxChars = Math.max(maxChars, currentCount);
                lines++;
                currentCount = 0;
            } else {
                currentCount++;
                if (currentCount >= MAX_CHARACTERS) {
                    maxChars = MAX_CHARACTERS;
                    lines++;
                    currentCount = 0;
                }
            }
        }

        maxChars = Math.max(maxChars, currentCount);
        lines = Math.min(lines, maxlines);

        return maxChars;
    }
}