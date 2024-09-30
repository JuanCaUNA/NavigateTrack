package org.una.navigatetrack;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import org.una.navigatetrack.storage.StorageManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ClickLocationApp extends Application {

    private Pane pane;
    private final StorageManager<List<int[]>> puntos = new StorageManager<>("src/main/resources/ListaPuntos/", "listPointFijo.data");
    private final StorageManager<List<Nodo>> nodos = new StorageManager<>("src/main/resources/ListaPuntos/", "listNodos.data");
    private final List<Nodo> listNodos = new ArrayList<>();
    private final List<Circle> circles = new ArrayList<>();
    private final List<int[]> clickPoints = new ArrayList<>();
    private Nodo nodo = new Nodo();

    @Override
    public void start(Stage primaryStage) {
        Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/map2.png")));
        ImageView imageView = new ImageView(image);

        // Ajustar el tamaño del ImageView
        double ratio = Math.min(670 / image.getWidth(), 950 / image.getHeight());
        imageView.setFitWidth(image.getWidth() * ratio);
        imageView.setFitHeight(image.getHeight() * ratio);
        imageView.setPreserveRatio(true);

        pane = new Pane(imageView);
        Scene scene = new Scene(pane, 670, 950);
        pane.setOnMouseClicked(this::handleMouseClick);
//        pane.setOnMouseClicked(this::handleMouseClickSelec);

        primaryStage.setTitle("Click Location Example");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();

        loadInitialData();
    }

    private void handleMouseClickSelec(MouseEvent event) {
        int[] punto = {(int) event.getX(), (int) event.getY()};

        switch (event.getButton()) {
            case PRIMARY -> {
                clickPoints.add(punto);
                createCircle(punto[0], punto[1], Color.BLUE);
            }
            case SECONDARY -> clickPoints.remove(findPoint(punto));
            default -> puntos.save(clickPoints);
        }
        //clickPoints.clear();
    }

    private void handleMouseClick(MouseEvent event) {
        int[] punto = {(int) event.getX(), (int) event.getY()};

        switch (event.getButton()) {
            case PRIMARY -> handlePrimaryClick(punto);
            case SECONDARY -> handleSecondaryClick(punto);
            case BACK -> handleBackClick(punto);
            case FORWARD -> handleForwardClick(punto);
            default -> nodos.save(listNodos);
        }
    }

    private void handlePrimaryClick(int[] punto) {
        Nodo nodoIzquierdo = findNodeAtPoint(punto);
        if (nodoIzquierdo != null) {
            nodo.setLeft(nodoIzquierdo);
            createCircle(punto[0], punto[1], Color.RED);
            drawLineBetweenNode(nodoIzquierdo, Color.RED);
        }
    }

    private void handleSecondaryClick(int[] punto) {
        Nodo nodoDerecho = findNodeAtPoint(punto);
        if (nodoDerecho != null) {
            nodo.setRight(nodoDerecho);
            createCircle(punto[0], punto[1], Color.PURPLE);
            drawLineBetweenNode(nodoDerecho, Color.PURPLE);
        }
    }

    private void handleBackClick(int[] punto) {
        Nodo nodoSeleccionado = findNodeAtPoint(punto);
        if (nodoSeleccionado != null) {
            nodo = nodoSeleccionado;
            createCircle(punto[0], punto[1], Color.GREEN);
        }
    }

    private void handleForwardClick(int[] punto) {
        Nodo nodoFrom = findNodeAtPoint(punto);
        if (nodoFrom != null) {
            nodo.setFrom(nodoFrom);
            createCircle(punto[0], punto[1], Color.YELLOW);
            drawLineBetweenNode(nodoFrom, Color.YELLOW);
        }
    }

    private Nodo findNodeAtPoint(int[] punto) {
        return listNodos.stream()
                .filter(nodoExistente -> isNearPoint(punto, nodoExistente.getLocation()))
                .findFirst()
                .orElse(null);
    }

    private int[] findPoint(int[] punto) {
        return clickPoints.stream()
                .filter(nodoExistente -> isNearPoint(punto, nodoExistente))
                .findFirst()
                .orElse(null);
    }

    private boolean isNearPoint(int[] punto, int[] location) {
        int tolerance = 10; // Tolerancia en píxeles
        return Math.abs(punto[0] - location[0]) <= tolerance && Math.abs(punto[1] - location[1]) <= tolerance;
    }

    private void createCircle(int x, int y, Color color) {
        Circle circle = new Circle(x, y, 5, color);
        circles.add(circle);
        pane.getChildren().add(circle);
    }

    private void drawLineBetweenNode(Nodo nodo, Color color) {
        Line line = new Line(this.nodo.getLocation()[0], this.nodo.getLocation()[1], nodo.getLocation()[0], nodo.getLocation()[1]);
        line.setStroke(color);
        pane.getChildren().add(line);
    }

    private void loadInitialData() {
        try {
            List<int[]> puntosList = puntos.read();
            //clickPoints.addAll(puntosList);
            for (int[] punto : puntosList) {
                createCircle(punto[0], punto[1], Color.BLUE);
                Nodo nuevoNodo = new Nodo();
                nuevoNodo.setLocation(punto);
                listNodos.add(nuevoNodo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}

