package org.una.navigatetrack.controller.fxml;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import org.una.navigatetrack.roads.Connection;
import org.una.navigatetrack.roads.Directions;
import org.una.navigatetrack.roads.Node;
import org.una.navigatetrack.storage.StorageManager;

import java.net.URL;
import java.util.*;

public class MapManageFXMLController implements Initializable {

    @FXML
    private Pane mapPane; // Donde se carga la imagen
    @FXML
    private Pane paintPane; // Donde se dibujan las líneas y puntos
    @FXML
    private Label nodoActualLabel; // Muestra el nodo actual
    @FXML
    private RadioButton izRadioB, derRadioB, adelanteRadioB, contrarioRadioB, seleccionarRadioB;
    @FXML
    private Button saveButton, deleteNodoButton, changeImageB; // Botones para guardar y eliminar nodos
    @FXML
    private RadioButton editRadioB, addRadioB; // Radio buttons para modo agregar/editar
    @FXML
    private TextArea nodoInfoTextArea; // Muestra información del nodo actual

    private static final int MAX_CONNECTIONS = 4;
    private static final int CIRCLE_RADIUS = 5;
    private static final String NODE_LABEL_NONE = "Nodo Actual: None";
    private static final Color DEFAULT_NODE_COLOR = Color.BLUE;
    private static final Color SELECTED_NODE_COLOR = Color.RED;

    private final List<Node> listNodos = new ArrayList<>();
    private Node currentNode;
    private Circle currentCircle;

    private final StorageManager<List<int[]>> puntosStorage = new StorageManager<>("src/main/resources/ListaNodos/", "listPointFijo.data");
    private final StorageManager<List<Node>> nodosStorage = new StorageManager<>("src/main/resources/ListaNodos/", "listNodos.data");

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupUI();
        loadInitialData();
        setupEventHandlers();
    }

    private void setupUI() {
        loadImageMap("/images/map2.png");
        paintPane.setStyle("-fx-background-color: rgba(255, 255, 255, 1);");
        setupToggleGroups();
    }

    private void setupEventHandlers() {
        paintPane.setOnMouseClicked(event -> handleMouseClick(event.getX(), event.getY()));
        saveButton.setOnAction(event -> saveNodes());
        deleteNodoButton.setOnAction(event -> deleteCurrentNode());
        changeImageB.setOnAction(event -> setChangeImageB());
    }

    Boolean change = false;

    void setChangeImageB() {
        loadImageMap((change) ? "/images/map2.png" : "/images/map0.png");
        change = !change;
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

    private void loadImageMap(String path) {
        Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream(path)));
        ImageView imageView = new ImageView(image);
        double ratio = Math.min(670 / image.getWidth(), 950 / image.getHeight());
        imageView.setFitWidth(image.getWidth() * ratio);
        imageView.setFitHeight(image.getHeight() * ratio);
        imageView.setPreserveRatio(true);
        mapPane.getChildren().add(imageView);
    }

    private void loadInitialData() {
        loadNodesFromFile();
    }

    private void loadNodesFromFile() {
        List<Node> nodosList = nodosStorage.read();
        nodosList.forEach(nodo -> {
            listNodos.add(nodo);
            drawNodeAt(nodo.getLocation(), DEFAULT_NODE_COLOR); // Dibuja cada nodo en su ubicación

            // Dibuja las conexiones si existen
            for (Connection connection : nodo.getConnections()) {
                if (connection != null && connection.getTargetNode() != null) {
                    drawConnection(nodo, connection.getTargetNode()); // Dibuja la conexión
                }
            }
        });
    }


    private void drawConnectionsForNode(Node nodo) {
        for (Connection connection : nodo.getConnections()) {
            if (connection != null && connection.getTargetNode() != null) {
                drawConnection(nodo, connection.getTargetNode());
            }
        }
    }

    private void loadPointsFromFile() {
        List<int[]> puntosList = puntosStorage.read();
        puntosList.forEach(this::addNodeAtPoint);
    }

    private void addNodeAtPoint(int[] punto) {
        Node nuevoNodo = new Node();
        nuevoNodo.setLocation(punto);
        listNodos.add(nuevoNodo);
        drawNodeAt(punto, DEFAULT_NODE_COLOR);
    }

    private void handleMouseClick(double x, double y) {
        int[] punto = {(int) x, (int) y};

        if (addRadioB.isSelected()) {
            addNode(punto);
        } else if (seleccionarRadioB.isSelected()) {
            selectNodeAtPoint(punto);
        } else {
            createConnection(punto);
        }
    }

    private void selectNodeAtPoint(int[] punto) {
        Node temp = findNodeAtPoint(punto);
        if (temp != null) {
            if (currentCircle != null) {
                // Restaurar el color del nodo anterior
                currentCircle.setFill(DEFAULT_NODE_COLOR);
            }
            currentNode = temp;
            currentCircle = drawNodeAt(temp.getLocation(), SELECTED_NODE_COLOR); // Dibuja el nodo seleccionado
            setNodeInfo();
        }
    }

    private void setNodeInfo() {
        if (currentNode == null) {
            nodoActualLabel.setText(NODE_LABEL_NONE);
            return;
        }

        nodoActualLabel.setText("Nodo Actual: " + Arrays.toString(currentNode.getLocation()));
        nodoInfoTextArea.setText(getNodeConnectionsInfo());
    }

    private String getNodeConnectionsInfo() {
        StringBuilder info = new StringBuilder("Conexiones\n");
        for (int i = 0; i < MAX_CONNECTIONS; i++) {
            info.append(getConnectionInfo(i)).append("\n");
        }
        return info.toString();
    }

    private String getConnectionInfo(int i) {
        Connection connection = currentNode.getConnections()[i];
        return connection != null ? connection.toString() : "Sin conexión";
    }

    private void addNode(int[] punto) {
        Node newNode = new Node();
        newNode.setLocation(punto);
        listNodos.add(newNode);
        drawNodeAt(punto, DEFAULT_NODE_COLOR);
        currentNode = newNode;
        setNodeInfo();
    }

    private void createConnection(int[] punto) {
        Node targetNode = findNodeAtPoint(punto);
        if (targetNode != null) {
            Directions direction = getDirection();
            if (direction != null) {
                if (currentNode.canAddConnection()) {
                    currentNode.addConnection(targetNode, direction, true);
                    drawConnection(currentNode, targetNode);
                    nodoInfoTextArea.setText("Conexión creada con el nodo en: " + Arrays.toString(targetNode.getLocation()));
                } else {
                    nodoInfoTextArea.setText("Máximo de conexiones alcanzado para este nodo.");
                }
            } else {
                nodoInfoTextArea.setText("Por favor selecciona una dirección.");
            }
        }
    }

    private Node findNodeAtPoint(int[] punto) {
        return listNodos.stream()
                .filter(nodo -> isNearPoint(punto, nodo.getLocation()))
                .findFirst()
                .orElse(null);
    }

    private boolean isNearPoint(int[] punto, int[] location) {
        int tolerance = 10; // Tolerancia en píxeles
        return Math.abs(punto[0] - location[0]) <= tolerance && Math.abs(punto[1] - location[1]) <= tolerance;
    }

    private Circle drawNodeAt(int[] location, Color color) {
        Circle circle = createCircle(location[0], location[1], color);
        paintPane.getChildren().add(circle);
        return circle;
    }

    private Circle createCircle(int x, int y, Color color) {
        return new Circle(x, y, CIRCLE_RADIUS, color);
    }

    private void drawConnection(Node fromNode, Node toNode) {
        if (fromNode != null && toNode != null) { // Verificar que ambos nodos no sean nulos
            Line line = new Line(fromNode.getLocation()[0], fromNode.getLocation()[1], toNode.getLocation()[0], toNode.getLocation()[1]);
            line.setStroke(DEFAULT_NODE_COLOR); // Puedes definir un color por defecto para las conexiones
            paintPane.getChildren().add(line);
        }
    }


    private Directions getDirection() {
        if (izRadioB.isSelected()) return Directions.IZQUIERDA;
        if (derRadioB.isSelected()) return Directions.DERECHA;
        if (adelanteRadioB.isSelected()) return Directions.ADELANTE;
        if (contrarioRadioB.isSelected()) return Directions.CONTRARIO;
        return null;
    }

    private Color getColorForDirection(Directions direction) {
        return switch (direction) {
            case IZQUIERDA -> Color.YELLOW;
            case DERECHA -> Color.GREEN;
            case ADELANTE -> Color.BLUE;
            case CONTRARIO -> Color.RED;
        };
    }

    private void saveNodes() {
        nodosStorage.save(listNodos);
    }

    private void deleteCurrentNode() {
        if (currentNode != null) {
            removeConnectionsToCurrentNode();
            listNodos.remove(currentNode);
            removeNodeFromDisplay();
            resetCurrentNode();
        } else {
            System.out.println("No hay un nodo seleccionado para eliminar.");
        }
    }

    private void removeConnectionsToCurrentNode() {
        listNodos.forEach(nodo -> {
            for (int i = 0; i < nodo.getConnections().length; i++) {
                Connection conn = nodo.getConnections()[i];
                if (conn != null && conn.getTargetNode().equals(currentNode)) {
                    nodo.getConnections()[i] = null;
                }
            }
        });
    }

    private void removeNodeFromDisplay() {
        paintPane.getChildren().removeIf(node -> {
            if (node instanceof Circle circle) {
                return isNearPoint(new int[]{(int) circle.getCenterX(), (int) circle.getCenterY()}, currentNode.getLocation());
            }
            return false;
        });
    }

    private void resetCurrentNode() {
        nodoActualLabel.setText(NODE_LABEL_NONE);
        currentNode = null;
        currentCircle = null; // Restablece la referencia al círculo actual
        System.out.println("Nodo eliminado correctamente.");
    }

    @FXML
    private void buscarCamino() {
        // Implementar lógica de búsqueda de caminos aquí.
    }
}