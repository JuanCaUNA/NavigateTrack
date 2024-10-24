package org.una.navigatetrack.controller.fxml;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import org.una.navigatetrack.manager.NodesDrawerManagers;
import org.una.navigatetrack.roads.Connection;
import org.una.navigatetrack.roads.Directions;
import org.una.navigatetrack.roads.Node;
import org.una.navigatetrack.manager.DrawerManager;

import java.net.URL;
import java.util.Arrays;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.StringJoiner;

public class MapManageController implements Initializable {

    @FXML    private Pane mapPane, paintPane;

    @FXML    private Label nodoActualLabel;

    @FXML    private TextArea nodoInfoTextArea;

    @FXML    private Button saveButton, deleteNodoButton, deleteConectionButton, changeImageB;

    @FXML    private RadioButton izRadioB, derRadioB, adelanteRadioB, contrarioRadioB, seleccionarRadioB;
    @FXML    private RadioButton editRadioB, addRadioB;

    private boolean change = false;
    private NodesDrawerManagers manager;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupUI();
        manager = new NodesDrawerManagers(new DrawerManager(paintPane));
        setupEventHandlers();
        setupToggleGroups();
        setupKeyBindings();
    }

    private void setupUI() {
        paintPane.setStyle("-fx-background-color: rgba(255, 255, 255, 0.0);");
        loadImageMap("/images/map2.png");
    }

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

    private void setupEventHandlers() {
        paintPane.setOnMouseClicked(event -> handleMouseClick(event.getX(), event.getY()));

        changeImageB.setOnAction(event -> loadImageMap(change ? "/images/map2.png" : "/images/map0.png"));
        deleteConectionButton.setOnAction(event -> manager.removeConnectionAndVisual(getDirection()));
        saveButton.setOnAction(event -> manager.getNodesManager().updateNodesToFile());
        deleteNodoButton.setOnAction(event -> {
            manager.deleteAndRemoveCurrentNode();
            resetCurrentNode();
        });
    }

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

    private void setupKeyBindings() {
        paintPane.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case DIGIT4 -> izRadioB.setSelected(true);
                case DIGIT6 -> derRadioB.setSelected(true);
                case DIGIT8 -> adelanteRadioB.setSelected(true);
                case DIGIT2 -> contrarioRadioB.setSelected(true);
                case DIGIT0 -> seleccionarRadioB.setSelected(true);
            }
        });
    }

    private Directions getDirection() {
        if (izRadioB.isSelected()) return Directions.IZQUIERDA;
        if (derRadioB.isSelected()) return Directions.DERECHA;
        if (adelanteRadioB.isSelected()) return Directions.ADELANTE;
        if (contrarioRadioB.isSelected()) return Directions.CONTRARIO;
        return null;
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
        Node currentNode = manager.getNodesManager().getCurrentNode();
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
                        connection.getWeight(),
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
