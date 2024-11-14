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
 * Controlador de la interfaz gráfica para gestionar rutas de viaje.
 */
//@SuppressWarnings("All")
public class ImplementsLogicController implements Initializable {

    // FXML UI Elements
    @FXML
    private CheckBox blockCBox;
    @FXML
    private TextArea textArea;
    @FXML
    private Pane mapPane, paintPane;
    @FXML
    private Button startB, changeImageB, infoB, pauseB;
    @FXML
    private Label labelDestino, labelTitle, labelPartida, labelTime;
    @FXML
    private RadioButton initRadioB, endingRadioB, radioBNode, radioBConnection, radioBDijkstra, radioBFloydWarshall;

    // Controller properties
    private boolean change = false;
    private NodeGraphFacade nodeGraphFacade;
    private Edge edge;
    private String message;

    // Color constants for UI feedback
    private static final Color BLUE_COLOR = Color.rgb(0, 0, 255, 0.5);
    private static final Color RED_COLOR = Color.RED;
    private static final Color LIGHTGREEN_COLOR = Color.LIGHTGREEN;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        nodeGraphFacade = new NodeGraphFacade(paintPane);
        nodeGraphFacade.setTimeL(labelTime);
        setupUI();
        setupEventHandlers();
        initializeRadioButtonSettings();
    }

    // Configura el estilo de UI y carga la imagen inicial del mapa
    private void setupUI() {
        paintPane.setStyle("-fx-background-color: rgba(255, 255, 255, 0.0);");
        loadImageMap("/images/map2.png");
        blockCBox.setDisable(true);
        radioBNode.setDisable(true);
        radioBNode.setVisible(false);
    }

    // Configura los controladores de eventos para botones y otros elementos de UI
    private void setupEventHandlers() {
        paintPane.setOnMouseClicked(event -> select(new double[]{event.getX(), event.getY()}));
        infoB.setOnAction(event -> showInfoMessage(Config.instructions));
        pauseB.setOnAction(event -> togglePause());
        changeImageB.setOnAction(event -> changeImage());
        startB.setOnAction(event -> toggleInitFinally());
        blockCBox.setOnAction(event -> handleBlockAction());
    }

    // Configura los botones de radio con su grupo de selección inicial
    private void initializeRadioButtonSettings() {
        ToggleGroup selectionGroup = new ToggleGroup();
        initRadioB.setToggleGroup(selectionGroup);
        endingRadioB.setToggleGroup(selectionGroup);
        radioBNode.setToggleGroup(selectionGroup);
        radioBConnection.setToggleGroup(selectionGroup);
        endingRadioB.setSelected(true);

        ToggleGroup modeGroup = new ToggleGroup();
        radioBDijkstra.setToggleGroup(modeGroup);
        radioBFloydWarshall.setToggleGroup(modeGroup);
        radioBFloydWarshall.setSelected(true);
    }

    // Alterna el estado de viaje (iniciar o finalizar)
    private void toggleInitFinally() {
        if (startB.getText().equals("Iniciar Viaje")) {
            if (nodeGraphFacade.initTravel()) {
                startB.setText("Finalizar Viaje");
                startB.setStyle("-fx-background-color: #f44336;");

                System.out.println("Iniciando viaje...");
                nodeGraphFacade.setDijkstra(radioBDijkstra.isSelected());

                System.out.println("Iniciando viaje...");
                showInfoMessage("Viaje iniciado.");
            }
        } else {
            startB.setText("Iniciar Viaje");
            startB.setStyle("-fx-background-color: #66bb6a;");

            System.out.println("Finalizando viaje...");
            nodeGraphFacade.endTravel();
            System.out.println("Finalizando viaje...");
            showInfoMessage("Viaje finalizado.");
        }
    }


    // Alterna el estado de pausa en el viaje
    private void togglePause() {
        if (pauseB.getText().equals("Pausar Viaje")) {
            pauseB.setText("Continuar Viaje");
            nodeGraphFacade.pauseTravel(true);
        } else {
            pauseB.setText("Pausar Viaje");
            nodeGraphFacade.pauseTravel(false);
        }
    }

    // Cambia la imagen del mapa
    private void changeImage() {
        loadImageMap(change ? "/images/map2.png" : "/images/map0.png");
        change = !change;
    }

    // Carga la imagen del mapa en el panel
    private void loadImageMap(String path) {
        mapPane.getChildren().clear();
        var image = new Image(Objects.requireNonNull(getClass().getResourceAsStream(path)));
        var imageView = new ImageView(image);
        var ratio = Math.min(670 / image.getWidth(), 950 / image.getHeight());
        imageView.setFitWidth(image.getWidth() * ratio);
        imageView.setFitHeight(image.getHeight() * ratio);
        imageView.setPreserveRatio(true);
        mapPane.getChildren().add(imageView);
    }

    // Maneja la acción del checkbox de bloqueo de caminos
    private void handleBlockAction() {
        edge.setBlocked(blockCBox.isSelected());
        message = blockCBox.isSelected() ? "Camino bloqueado." : "Camino desbloqueado.";

        Color color = edge.isBlocked() ? RED_COLOR : LIGHTGREEN_COLOR;
        nodeGraphFacade.reDrawEdge(edge, color);
        showInfoMessage(message);
    }

    // Selecciona un nodo o conexión en el mapa según la ubicación
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
        showInfoMessage(message + "(" + location[0] + "," + location[1] + ")");
        System.out.println(message + "(" + location[0] + "," + location[1] + ")");
        textArea.setText(message + "(" + nodeGraphFacade.getStartNode().toString() + "," + nodeGraphFacade.getEndNode().toString() + ")");

    }

    // Maneja la selección de una conexión en el mapa
    private void handleConnectionSelection(double[] location) {
        if (edge != null && !edge.isBlocked()) {
            nodeGraphFacade.reDrawEdge(edge, BLUE_COLOR);
        }

        edge = nodeGraphFacade.getConnection(location[0], location[1]);
        if (edge != null) {
            message = "Marcó un camino en: ";
            textArea.setText(edge.toString());
            Color color = edge.isBlocked() ? RED_COLOR : LIGHTGREEN_COLOR;
            nodeGraphFacade.reDrawEdge(edge, color);
            blockCBox.setDisable(false);
            blockCBox.setSelected(edge.isBlocked());
        } else {
            message = "No hay ningún camino en: ";
        }
    }

    // Muestra un mensaje de información en la interfaz de usuario
    private void showInfoMessage(String message) {
        AppContext.getInstance().createNotification("Info", message);
    }
}
