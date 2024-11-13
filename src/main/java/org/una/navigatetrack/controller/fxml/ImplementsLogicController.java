package org.una.navigatetrack.controller.fxml;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import org.una.navigatetrack.configs.Config;
import org.una.navigatetrack.manager.NodeGraphFacade;
import org.una.navigatetrack.roads.Edge;
import org.una.navigatetrack.utils.AppContext;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * FXML Controller class
 * Controlador que maneja la lógica para la interfaz gráfica.
 *
 * @author juanc
 */
@SuppressWarnings("All")
public class ImplementsLogicController implements Initializable {

    @FXML
    private CheckBox blockCBox;
    @FXML
    private TextArea textArea;
    @FXML
    private Pane mapPane, paintPane;
    @FXML
    private Button startB, finishB, changeImageB, infoB, pauseB;
    @FXML
    private Label labelDestino, labelTitle, labelPartida, labelTime;
    @FXML
    private RadioButton initRadioB, endingRadioB, radioBNode, radioBConnection, radioBDijkstra, radioBFloydWarshall;

    private boolean change = false;
    private NodeGraphFacade nodeGraphFacade;
    private Edge edge;
    private String message;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        nodeGraphFacade = new NodeGraphFacade(paintPane);
        setupUI();

        setupEventHandlers();
        blockCBox.setDisable(true);
        radioBNode.setDisable(true);
        radioBNode.setVisible(false);
    }

    private void setupUI() {
        paintPane.setStyle("-fx-background-color: rgba(255, 255, 255, 0.0);");
        loadImageMap("/images/map2.png");
    }


    private void changeImage() {
        loadImageMap(change ? "/images/map2.png" : "/images/map0.png");
        change = !change;
    }

    private void loadImageMap(String path) {
        var image = new Image(Objects.requireNonNull(getClass().getResourceAsStream(path)));
        var imageView = new ImageView(image);
        var ratio = Math.min(670 / image.getWidth(), 950 / image.getHeight());
        imageView.setFitWidth(image.getWidth() * ratio);
        imageView.setFitHeight(image.getHeight() * ratio);
        imageView.setPreserveRatio(true);
        mapPane.getChildren().add(imageView);
    }

    private void setupEventHandlers() {
        paintPane.setOnMouseClicked(event -> select(new double[]{event.getX(), event.getY()}));//todo *******

        infoB.setOnAction(actionEvent -> showInfoMessage(Config.instructions));
        pauseB.setOnAction(actionEvent -> nodeGraphFacade.pauseTravel()); // TO-DO: Implementar la pausa
        changeImageB.setOnAction(event -> changeImage());
        startB.setOnAction(event -> startTravel());
        finishB.setOnAction(event -> finishTravel());

        blockCBox.setOnAction(event -> handleBlockAction()); // TO-DO: Implementar acción de bloquear
    }

    private void startTravel() {
        System.out.println("Iniciando viaje...");
        nodeGraphFacade.setDijkstra(radioBDijkstra.isSelected());

        Thread travelThread = new Thread(new Runnable() {
            @Override
            public void run() {
                nodeGraphFacade.initTravel();
            }
        });
        travelThread.start(); // Inicia el hilo

        showInfoMessage("Viaje iniciado.");
    }

    private void finishTravel() {
        System.out.println("Finalizando viaje...");
        nodeGraphFacade.endTravel();
        showInfoMessage("Viaje finalizado.");
    }

    private void handleBlockAction() {
        edge.setBlocked(blockCBox.isSelected());
        message = blockCBox.isSelected() ? "Camino bloqueado." : "Camino desbloqueado.";

        Color color = edge.isBlocked() ? Color.RED : Color.LIGHTGREEN;
        nodeGraphFacade.reDrawEdge(edge, color);
        showInfoMessage(message);
    }

    private void select(double[] location) {
        blockCBox.setDisable(true);
        if (initRadioB.isSelected()) {
            message = nodeGraphFacade.setStartNode(location) ? "Punto de partida: " : "No marcó una ruta válida: ";
        } else if (endingRadioB.isSelected()) {
            message = nodeGraphFacade.setEndNode(location) ? "Punto de destino: " : "No marcó una ruta válida: ";
        } else if (radioBNode.isSelected()) {
            message = "Seleccionaste un nodo en: ";
        } else if (radioBConnection.isSelected()) {
            handleConnectionSelection(location);
        }
        // Mostrar notificación en lugar de texto
        showInfoMessage(message + location[0] + ", " + location[1]);
    }

    private void handleConnectionSelection(double[] location) {
        if (edge != null && !edge.isBlocked()) {
            nodeGraphFacade.reDrawEdge(edge, Color.BLUE);
        }

        edge = nodeGraphFacade.getConnection(location[0], location[1]);
        if (edge != null) {
            message = "Marcó un camino en: ";
            textArea.setText(edge.toString());
            Color color = edge.isBlocked() ? Color.RED : Color.LIGHTGREEN;
            nodeGraphFacade.reDrawEdge(edge, color);
            blockCBox.setDisable(false);
            blockCBox.setSelected(edge.isBlocked());
        } else {
            message = "No hay ningún camino en: ";
        }
    }

    private void showInfoMessage(String message) {
        // Usamos la clase AppContext para mostrar notificaciones
        AppContext.getInstance().createNotification("Info", message);
    }
}

// private void setupToggleGroups() {
//     ToggleGroup selectionGroup = new ToggleGroup();
//     initRadioB.setToggleGroup(selectionGroup);
//     endingRadioB.setToggleGroup(selectionGroup);
//     radioBNode.setToggleGroup(selectionGroup);
//     radioBConnection.setToggleGroup(selectionGroup);
//     endingRadioB.setSelected(true);

//     ToggleGroup modeGroup = new ToggleGroup();
//     radioBDijkstra.setToggleGroup(modeGroup);
//     radioBFloydWarshall.setToggleGroup(modeGroup);
//     radioBFloydWarshall.setSelected(true);
// }
//        setupToggleGroups();