package org.una.navigatetrack.controller.fxml;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import org.una.navigatetrack.controller.Drawer;
import org.una.navigatetrack.controller.NodeDrawerManager;
import org.una.navigatetrack.roads.Connection;
import org.una.navigatetrack.roads.Directions;
import org.una.navigatetrack.roads.Node;

import java.net.URL;
import java.util.Arrays;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.StringJoiner;

public class MapManageFXMLController implements Initializable {

    // UI Components
    @FXML    private Label nodoActualLabel;
    @FXML    private Pane mapPane, paintPane;
    @FXML    private TextArea nodoInfoTextArea;
    @FXML    private Button saveButton, deleteNodoButton, deleteConectionButton, changeImageB;
    @FXML    private RadioButton izRadioB, derRadioB, adelanteRadioB, contrarioRadioB, seleccionarRadioB;
    @FXML    private RadioButton editRadioB, addRadioB;

    NodeDrawerManager manager;

    // Initialization
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupUI();
        manager = new NodeDrawerManager(new org.una.navigatetrack.controller.NodeManager(), new Drawer(paintPane));
        setupEventHandlers();
    }

    // UI Setup
    private void setupUI() {
        paintPane.setStyle("-fx-background-color: rgba(255, 255, 255, 0.0);");
        loadImageMap("/images/map2.png");
        setupToggleGroups();
    }

    private boolean change = false;

    private void loadImageMap(String path) {
        Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream(path)));
        ImageView imageView = new ImageView(image);
        double ratio = Math.min(670 / image.getWidth(), 950 / image.getHeight());
        imageView.setFitWidth(image.getWidth() * ratio);
        imageView.setFitHeight(image.getHeight() * ratio);
        imageView.setPreserveRatio(true);
        mapPane.getChildren().add(imageView);
        change = !change;
    }

    // Toggle Groups Setup
    private void setupToggleGroups() {
        ToggleGroup modoToggleGroup = new ToggleGroup();
        addRadioB.setToggleGroup(modoToggleGroup);
        editRadioB.setToggleGroup(modoToggleGroup);
        editRadioB.setSelected(true);

        ToggleGroup directionToggleGroup = new ToggleGroup();
        izRadioB.setToggleGroup(directionToggleGroup);
        derRadioB.setToggleGroup(directionToggleGroup);
        adelanteRadioB.setToggleGroup(directionToggleGroup);
        contrarioRadioB.setToggleGroup(directionToggleGroup);
        seleccionarRadioB.setToggleGroup(directionToggleGroup);
        seleccionarRadioB.setSelected(true);
    }

    // Get Directions
    private Directions getDirection() {
        if (izRadioB.isSelected()) return Directions.IZQUIERDA;
        if (derRadioB.isSelected()) return Directions.DERECHA;
        if (adelanteRadioB.isSelected()) return Directions.ADELANTE;
        if (contrarioRadioB.isSelected()) return Directions.CONTRARIO;
        return null;
    }

    // Event Handlers
    private void setupEventHandlers() {
        saveButton.setOnAction(event -> manager.getNodeManager().saveNodesToFile());//*
        changeImageB.setOnAction(event -> loadImageMap(change ? "/images/map2.png" : "/images/map0.png"));
        deleteNodoButton.setOnAction(event -> {
            manager.deleteAndRemoveCurrentNode();
            resetCurrentNode();
        });
        deleteConectionButton.setOnAction(event -> manager.removeConnectionAndVisual(getDirection()));
        paintPane.setOnMouseClicked(event -> handleMouseClick(event.getX(), event.getY()));
    }

    private void handleMouseClick(double x, double y) {
        int[] point = {(int) x, (int) y};
        if (addRadioB.isSelected()) {
            manager.createAndDrawNode(point);
        } else if (seleccionarRadioB.isSelected()) {
            manager.updateCurrentNode(point);
            setNodeInfo();
        } else {
            manager.createAndDrawConnection(point, getDirection());
        }
    }

    private void setNodeInfo() {
        Node currentNode = manager.getNodeManager().getCurrentNode();  // Variable intermedia para mejorar legibilidad
        if (currentNode == null) {
            nodoActualLabel.setText("Nodo Actual: None");
            nodoInfoTextArea.setText("");
        } else {
            nodoActualLabel.setText("Nodo Actual: " + Arrays.toString(currentNode.getLocation()));
            nodoInfoTextArea.setText(getNodeConnectionsInfo(currentNode));
        }
    }

    private String getNodeConnectionsInfo(Node node) {
        if (node.getConnections() == null) {
            return "No hay conexiones disponibles.";
        }

        StringJoiner info = new StringJoiner("\n", "Conexiones:\n", "");
        for (Connection connection : node.getConnections()) {
            if (connection != null) {
                info.add(String.format(
                        "Destino: %s, Peso: %d, Bloqueada: %s, Estado de Tráfico: %s, Dirección: %s",
                        Arrays.toString(connection.getTargetNode().getLocation()),
                        connection.getWeight(),  // Usa %d en lugar de %.2f
                        connection.isBlocked() ? "Sí" : "No",
                        connection.getTrafficCondition(),
                        connection.getDirection()
                ));
            }
        }
        return info.toString();
    }

    private void resetCurrentNode() {
        nodoActualLabel.setText("Nodo Actual: None");
        nodoInfoTextArea.setText("");
    }
}
