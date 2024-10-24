package org.una.navigatetrack.controller.fxml;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import org.una.navigatetrack.manager.DrawerManager;
import org.una.navigatetrack.manager.NodesDrawerManagers;
import org.una.navigatetrack.roads.Connection;
import org.una.navigatetrack.roads.Directions;
import org.una.navigatetrack.roads.Node;

import java.net.URL;
import java.util.Arrays;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.StringJoiner;

public class MapManageController implements Initializable {

    @FXML    private Pane mapPane, paintPane, menuFlowPane, rootStackPane;
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

        rootStackPane.setFocusTraversable(true);
        rootStackPane.requestFocus();
    }

    private void setupUI() {
        paintPane.setStyle("-fx-background-color: rgba(255, 255, 255, 0.0);");
        loadImageMap("/images/map2.png"); // Carga la imagen del mapa por defecto
    }

    private void loadImageMap(String path) {
        Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream(path)));
        ImageView imageView = new ImageView(image);
        double ratio = Math.min(670 / image.getWidth(), 950 / image.getHeight());
        imageView.setFitWidth(image.getWidth() * ratio);
        imageView.setFitHeight(image.getHeight() * ratio);
        imageView.setPreserveRatio(true);
        mapPane.getChildren().add(imageView); // Añade la imagen al panel del mapa
        change = !change; // Cambia el estado de cambio para alternar la imagen
    }

    private void setupEventHandlers() {
        // Maneja clics en el panel de dibujo
        paintPane.setOnMouseClicked(event -> {handleMouseClick(event.getX(), event.getY()); rootStackPane.requestFocus(); });

        // Maneja el cambio de imagen del mapa
        changeImageB.setOnAction(event -> loadImageMap(change ? "/images/map2.png" : "/images/map0.png"));

        // Maneja la eliminación de conexiones
        deleteConectionButton.setOnAction(event -> manager.removeConnectionAndVisual(getDirection()));

        // Maneja la actualización del archivo de nodos
        saveButton.setOnAction(event -> manager.getNodesManager().updateNodesToFile());

        // Maneja la eliminación del nodo actual
        deleteNodoButton.setOnAction(event -> {
            manager.deleteAndRemoveCurrentNode();
            setNodeInfo();
        });
    }

    private void setupToggleGroups() {
        // Agrupación de botones de modo
        ToggleGroup modoToggleGroup = new ToggleGroup();
        addRadioB.setToggleGroup(modoToggleGroup); // si este es verdad seleccionarRadioB simpre activo
        editRadioB.setToggleGroup(modoToggleGroup);// si este es verdad se desactiva el directionToggleGroup
        editRadioB.setSelected(true); // Establece el modo de edición por defecto

        // Agrupación de botones de dirección
        ToggleGroup directionToggleGroup = new ToggleGroup();
        izRadioB.setToggleGroup(directionToggleGroup);
        derRadioB.setToggleGroup(directionToggleGroup);
        adelanteRadioB.setToggleGroup(directionToggleGroup);
        contrarioRadioB.setToggleGroup(directionToggleGroup);
        seleccionarRadioB.setToggleGroup(directionToggleGroup);
        seleccionarRadioB.setSelected(true); // Establece la opción de selección por defecto
    }

    private void setupKeyBindings() {
        // Asigna teclas numéricas a direcciones
        rootStackPane.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case A -> izRadioB.setSelected(true); // A para izquierda
                case D -> derRadioB.setSelected(true); // D para derecha
                case W -> adelanteRadioB.setSelected(true); // W para adelante
                case S -> contrarioRadioB.setSelected(true); // S para contrario
                case E -> seleccionarRadioB.setSelected(true); // E para seleccionar
            }
        });
    }

    private Directions getDirection() {
        // Devuelve la dirección seleccionada
        if (izRadioB.isSelected()) return Directions.IZQUIERDA;
        if (derRadioB.isSelected()) return Directions.DERECHA;
        if (adelanteRadioB.isSelected()) return Directions.ADELANTE;
        if (contrarioRadioB.isSelected()) return Directions.CONTRARIO;
        return null; // Retorna null si no hay dirección seleccionada
    }

    private void handleMouseClick(double x, double y) {
        double[] point = {x, y};
        // Crea un nodo o conexión dependiendo del modo seleccionado
        if (addRadioB.isSelected()) {
            manager.createAndDrawNode(point);
            setNodeInfo();
        } else if (seleccionarRadioB.isSelected()) {
            manager.updateCurrentNode(point);
            setNodeInfo(); // Muestra información del nodo seleccionado
        } else {
            manager.createAndDrawConnection(point, getDirection());
        }
    }

    private void setNodeInfo() {
        Node currentNode = manager.getCurrentNode();
        if (currentNode == null) {
            nodoActualLabel.setText("Nodo Actual: None");
            nodoInfoTextArea.setText("");
        } else {
            nodoActualLabel.setText("Nodo Actual: " + Arrays.toString(currentNode.getLocation()));
            nodoInfoTextArea.setText(getNodeConnectionsInfo(currentNode)); // Muestra información sobre las conexiones del nodo
        }
    }

    private String getNodeConnectionsInfo(Node node) {
        if (node.getConnections() == null || node.isConnectionsEmpty()) {
            return "No hay conexiones disponibles."; // Mensaje si no hay conexiones
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
        return info.toString(); // Devuelve la información formateada sobre las conexiones
    }
}