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
    private Button startB, changeImageB, infoB, pauseB, editionB, algoritmoB;
    @FXML
    private Label labelTime, labelPrecioEstimado, labelPrecioTotal;
    @FXML
    private RadioButton initRadioB, endingRadioB, radioBConnection, radioBFnormal, radioBFmoderado, radioBFlento, sentido1RadioB, sentido2RadioB;

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
        nodeGraphFacade.setPrecioL(labelPrecioEstimado);
        nodeGraphFacade.setPreciofinalL(labelPrecioTotal);
        nodeGraphFacade.setInfoTA(textArea);
        nodeGraphFacade.setStartB(startB);

        nodeGraphFacade.setAlgoritmoB(algoritmoB);
        nodeGraphFacade.setPauseB(pauseB);

        setupUI();
        setupEventHandlers();
    }

    // Configura el estilo de UI y carga la imagen inicial del mapa
    private void setupUI() {
        paintPane.setStyle("-fx-background-color: rgba(255, 255, 255, 0.0);");
        loadImageMap("/images/map2.png");
        blockCBox.setDisable(true);
        pauseB.setDisable(true);
        algoritmoB.setDisable(true);
        startB.setDisable(true);
        sentido1RadioB.setDisable(true);
        sentido2RadioB.setDisable(true);
    }

    // Configura los controladores de eventos para botones y otros elementos de UI
    private void setupEventHandlers() {
        paintPane.setOnMouseClicked(event -> select(new double[]{event.getX(), event.getY()}));
        infoB.setOnAction(event -> showInfoMessage(Config.instructions));
        pauseB.setOnAction(event -> togglePause());
        changeImageB.setOnAction(event -> changeImage());
        startB.setOnAction(event -> toggleInitFinally());
        algoritmoB.setOnAction(event -> toggleAlgorithms());
        editionB.setOnAction(event -> changeMode());
        blockCBox.setOnAction(event -> handleBlockAction());
        //paintPane.setOnMouseClicked(event -> traffic());
    }

    // Alterna el estado de viaje (iniciar o finalizar)
    private void toggleInitFinally() {
        if (startB.getText().equals("Iniciar Viaje")) {
            nodeGraphFacade.initTravel();
//            nodeGraphFacade.iniTravelB();
        } else {
            nodeGraphFacade.endTravel();
//            nodeGraphFacade.endTravel();
//            nodeGraphFacade.endTravelB();
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

    // Alterna el algoritmo del viaje
    private void toggleAlgorithms() {
        if (algoritmoB.getText().equals("Floyd Warshall is currently selected")) {
            algoritmoB.setText("Dijkstra is currently selected");
            nodeGraphFacade.setDijkstra(true);
        } else {
            algoritmoB.setText("Floyd Warshall is currently selected");
            nodeGraphFacade.setDijkstra(false);
        }
        startB.setDisable(false);
    }

    // Cambiar el modo de la vista
    private void changeMode() {
        AppContext.getInstance().loadScreen("/fxml/MapManager.fxml");
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
        } else if (radioBConnection.isSelected()) {
            handleConnectionSelection(location);
        }
        showInfoMessage(message + "(" + location[0] + "," + location[1] + ")");
        System.out.println(message + "(" + location[0] + "," + location[1] + ")");
        textArea.setText(message + "(" + nodeGraphFacade.getStartNode().toString() + "," + nodeGraphFacade.getEndNode().toString() + ")");

        if (!nodeGraphFacade.getStartNode().isEmptyValues() && !nodeGraphFacade.getEndNode().isEmptyValues()) {
            algoritmoB.setDisable(false);
        } else {
            pauseB.setDisable(true);
            algoritmoB.setDisable(true);
            startB.setDisable(true);
        }
    }

    // Selecciona un nodo o conexión en el mapa según la ubicación
    private void traffic() {
        blockCBox.setDisable(true);
        if (radioBFnormal.isSelected()) {
            edge.setTrafficCondition(radioBFnormal.getText());
            message = "El estado del trafico es " + radioBFnormal.getText();
        } else if (radioBFmoderado.isSelected()) {
            edge.setTrafficCondition(radioBFmoderado.getText());
            message = "El estado del trafico es " + radioBFmoderado.getText();
        } else if (radioBFlento.isSelected()) {
            edge.setTrafficCondition(radioBFlento.getText());
            message = "El estado del trafico es " + radioBFlento.getText();
        }
        showInfoMessage(message);
        System.out.println(message);
        textArea.setText(message);
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
            sentido1RadioB.setDisable(false);
            sentido2RadioB.setDisable(false);
            blockCBox.setSelected(edge.isBlocked());
            traffic();//TODO
        } else {
            message = "No hay ningún camino en: ";
        }
    }

    // Muestra un mensaje de información en la interfaz de usuario
    private void showInfoMessage(String message) {
        AppContext.getInstance().createNotification("Info", message);
    }
}