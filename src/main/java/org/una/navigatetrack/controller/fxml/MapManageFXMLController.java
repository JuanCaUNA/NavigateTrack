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
    private Button saveButton, deleteNodoButton, deleteConectionButton, changeImageB; // Botones para guardar y eliminar nodos
    @FXML
    private RadioButton editRadioB, addRadioB; // Radio buttons para modo agregar/editar
    @FXML
    private TextArea nodoInfoTextArea; // Muestra información del nodo actual

    private static final int MAX_CONNECTIONS = 4;
    private static final int CIRCLE_RADIUS = 5;
    private static final String NODE_LABEL_NONE = "Nodo Actual: None";
    private static final Color DEFAULT_NODE_COLOR = Color.BLUE;
    private static final Color SELECTED_NODE_COLOR = Color.RED;

    private final List<Node> listNodes = new ArrayList<>();
    private Node currentNode;
    private Circle currentCircle;

    private final StorageManager<List<int[]>> pointsStorage = new StorageManager<>("src/main/resources/ListaNodos/", "listPointFijo.data");
    private final StorageManager<List<Node>> nodesStorage = new StorageManager<>("src/main/resources/ListaNodos/", "listNodos.data");

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupUI();
        loadInitialData();
        setupEventHandlers();
    }

    private void setupUI() {
        loadImageMap("/images/map2.png");
        paintPane.setStyle("-fx-background-color: rgba(255, 255, 255, 0.2);");
        setupToggleGroups();
    }

    private void setupEventHandlers() {
        paintPane.setOnMouseClicked(event -> handleMouseClick(event.getX(), event.getY()));
        saveButton.setOnAction(event -> saveNodes());
        deleteNodoButton.setOnAction(event -> deleteCurrentNode());
        deleteConectionButton.setOnAction(event -> deleteCurrentConnection());
        changeImageB.setOnAction(event -> setChangeImageB());
    }

    private void deleteCurrentConnection() {
        if (currentNode != null) {
            Directions direction = getDirection(); // Obtén la dirección seleccionada
            if (direction != null) {
                // Busca el nodo de destino asociado a la dirección
                Node targetNode = currentNode.getTargetNode(direction);
                if (targetNode != null) {
                    // Eliminar la conexión del nodo actual
                    currentNode.deleteConnection(direction);
                    // Eliminar la línea visual de la conexión
                    removeConnectionLine(currentNode, targetNode, direction);
                    nodoInfoTextArea.setText("Conexión eliminada con el nodo en: " + Arrays.toString(targetNode.getLocation()));
                } else {
                    nodoInfoTextArea.setText("No hay conexión en esa dirección.");
                }
            } else {
                nodoInfoTextArea.setText("Por favor selecciona una dirección.");
            }
        } else {
            nodoInfoTextArea.setText("No hay un nodo seleccionado.");
        }
    }

    private void removeConnectionLine(Node fromNode, Node toNode, Directions direction) {
        paintPane.getChildren().removeIf(node -> {
            if (node instanceof Line line) {
                // Compara las coordenadas de la línea con las de los nodos
                return (line.getStartX() == fromNode.getLocation()[0] &&
                        line.getStartY() == fromNode.getLocation()[1] &&
                        line.getEndX() == toNode.getLocation()[0] &&
                        line.getEndY() == toNode.getLocation()[1]);
            }
            return false;
        });
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
        List<Node> nodosList = nodesStorage.read();
        nodosList.forEach(node -> {
            listNodes.add(node);
            drawNodeAt(node.getLocation(), DEFAULT_NODE_COLOR);

            for (Connection connection : node.getConnections()) {
                if (connection != null && connection.getTargetNode() != null) {
                    drawConnection(node, connection.getTargetNode(), connection.getDirection());
                }
            }
        });
    }

    private void loadPointsFromFile() {
        List<int[]> puntosList = pointsStorage.read();
        puntosList.forEach(this::addNodeAtPoint);
    }

    private void addNodeAtPoint(int[] point) {
        Node newNode = new Node();
        newNode.setLocation(point);
        listNodes.add(newNode);
        drawNodeAt(point, DEFAULT_NODE_COLOR);
    }

    private void handleMouseClick(double x, double y) {
        int[] point = {(int) x, (int) y};

        if (addRadioB.isSelected()) {
            addNode(point);
        } else if (seleccionarRadioB.isSelected()) {
            selectNodeAtPoint(point);
        } else {
            createConnection(point);
        }
    }

    private void selectNodeAtPoint(int[] point) {
        Node temp = findNodeAtPoint(point);
        if (temp != null) {
            if (currentCircle != null) {
                currentCircle.setFill(DEFAULT_NODE_COLOR);
            }
            currentNode = temp;
            currentCircle = drawNodeAt(temp.getLocation(), SELECTED_NODE_COLOR);
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
        StringBuilder info = new StringBuilder("Conexiones:\n");
        for (Connection connection : currentNode.getConnections()) {
            if (connection != null) {
                info.append("Destino: ")
                        .append(Arrays.toString(connection.getTargetNode().getLocation()))
                        .append(", Peso: ")
                        .append(connection.getWeight())
                        .append(", Bloqueada: ")
                        .append(connection.isBlocked() ? "Sí" : "No")
                        .append(", Estado de Tráfico: ")
                        .append(connection.getTrafficCondition())
                        .append(", Dirección: ")
                        .append(connection.getDirection())
                        .append("\n");
            }
        }
        return info.toString();
    }

    private String getConnectionInfo(int i) {
        Connection connection = currentNode.getConnections()[i];
        return connection != null ? connection.toString() : "Sin conexión";
    }

    private void addNode(int[] point) {
        Node newNode = new Node();
        newNode.setLocation(point);
        listNodes.add(newNode);
        drawNodeAt(point, DEFAULT_NODE_COLOR);
        currentNode = newNode;
        setNodeInfo();
    }

    private void createConnection(int[] point) {
        Node targetNode = findNodeAtPoint(point);
        if (targetNode != null) {
            Directions direction = getDirection();
            if (direction != null) {
                currentNode.addConnection(targetNode, direction);
                drawConnection(currentNode, targetNode, direction);
                nodoInfoTextArea.setText("Conexión definida con el nodo en: " + Arrays.toString(targetNode.getLocation()));
            } else {
                nodoInfoTextArea.setText("Por favor selecciona una dirección.");
            }
        }
    }

    private Node findNodeAtPoint(int[] point) {
        return listNodes.stream()
                .filter(node -> isNearPoint(point, node.getLocation()))
                .findFirst()
                .orElse(null);
    }

    private boolean isNearPoint(int[] point, int[] location) {
        int tolerance = 10; // Tolerancia en píxeles
        return Math.abs(point[0] - location[0]) <= tolerance && Math.abs(point[1] - location[1]) <= tolerance;
    }

    private Circle drawNodeAt(int[] location, Color color) {
        Circle circle = createCircle(location[0], location[1], color);
        paintPane.getChildren().add(circle);
        return circle;
    }

    private Circle createCircle(int x, int y, Color color) {
        return new Circle(x, y, CIRCLE_RADIUS, color);
    }

    private void drawConnection(Node fromNode, Node toNode, Directions direction) {
        if (fromNode != null && toNode != null) {
            Line line = new Line(fromNode.getLocation()[0], fromNode.getLocation()[1],
                    toNode.getLocation()[0], toNode.getLocation()[1]);
            line.setStroke(getColorForDirection(direction != null ? direction : Directions.ADELANTE)); // valor por defecto
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
        nodesStorage.save(listNodes);
    }

    private void deleteCurrentNode() {
        if (currentNode != null) {
            removeConnectionsToCurrentNode();
            listNodes.remove(currentNode);
            removeNodeFromDisplay();
            resetCurrentNode();
        } else {
            System.out.println("No hay un nodo seleccionado para eliminar.");
        }
    }

    private void removeConnectionsToCurrentNode() {
        listNodes.forEach(node -> {
            for (int i = 0; i < node.getConnections().length; i++) {
                Connection conn = node.getConnections()[i];
                if (conn != null && conn.getTargetNode().equals(currentNode)) {
                    node.getConnections()[i] = null;
                }
            }
        });
    }

    private void removeNodeFromDisplay() {
        paintPane.getChildren().removeIf(node ->
                node instanceof Circle circle &&
                        isNearPoint(new int[]{(int) circle.getCenterX(), (int) circle.getCenterY()}, currentNode.getLocation())
        );
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