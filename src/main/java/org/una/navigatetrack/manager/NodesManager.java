package org.una.navigatetrack.manager;

import javafx.scene.paint.Color;
import org.una.navigatetrack.manager.storage.StorageManager;
import org.una.navigatetrack.roads.Connection;
import org.una.navigatetrack.roads.Directions;
import org.una.navigatetrack.roads.ListNodes;
import org.una.navigatetrack.roads.Node;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

public class NodesManager {
    private final StorageManager<Map<Integer, Node>> nodesStorage = new StorageManager<>("src/main/resources/listNodes/", "listNodes.data");

    public NodesManager() {
        readNodesFromFile();
    }

    // Cargar nodos desde el archivo y almacenarlos en ListNodes
    public void readNodesFromFile() {
        Map<Integer, Node> loadedNodes = nodesStorage.read();
        ListNodes.setListNodes(loadedNodes); // Agregar nodos directamente al Map
    }

    // Guardar los nodos en el archivo
    public void updateNodesToFile() {
        nodesStorage.write(ListNodes.getNodesMap()); // Guardar directamente desde ListNodes
    }

    // Eliminar un nodo
    public void deleteNode(int nodeID) {
        Optional<Node> nodeOpt = ListNodes.findById(nodeID);
        nodeOpt.ifPresentOrElse(
                node -> {
                    ListNodes.removeById(nodeID);
                    // No es necesario actualizar una lista local, ya no existe
                },
                () -> System.out.println("Node not found with ID: " + nodeID)
        );
    }

    // Añadir un nodo
    public void addNode(double[] location) {
        Node newNode = new Node(location);
        ListNodes.addNode(newNode); // Agregar nodo a ListNodes
    }

    // Obtener un nodo en una ubicación específica
    public Node getNodeAtLocation(double[] location) {
        return ListNodes.getNodesMap().values().stream()
                .filter(node -> Arrays.equals(node.getLocation(), location))
                .findFirst()
                .orElse(null);
    }

    // Añadir una conexión
    public void addConnection(int currentNodeID, int toNodeID, Directions direction) {
        Optional<Node> currentNodeOpt = ListNodes.findById(currentNodeID);
        Optional<Node> toNodeOpt = ListNodes.findById(toNodeID);

        if (currentNodeOpt.isPresent() && toNodeOpt.isPresent()) {
            currentNodeOpt.get().addConnection(toNodeOpt.get(), direction);
        }
    }

    // Eliminar una conexión
    public void removeConnection(int nodeID, Directions direction) {
        Optional<Node> nodeOpt = ListNodes.findById(nodeID);
        nodeOpt.ifPresent(node -> node.deleteConnection(direction));
    }

    // Obtener conexión en una dirección específica
    public Connection getConnectionInDirection(int nodeID, Directions direction) {
        return ListNodes.findById(nodeID)
                .map(node -> node.getConnection(direction))
                .orElse(null);
    }

    // Bloquear ruta
    public void blockConnection(int nodeID, Directions direction) {
        Connection connection = getConnectionInDirection(nodeID, direction);
        if (connection != null) {
            connection.blockRoute();
        }
    }

    // Desbloquear ruta
    public void unblockConnection(int nodeID, Directions direction) {
        Connection connection = getConnectionInDirection(nodeID, direction);
        if (connection != null) {
            connection.unblockRoute();
        }
    }

    // Buscar y obtener un nodo
    public Optional<Node> searchAndGetNode(int nodeID) {
        return ListNodes.findById(nodeID);
    }
    //
}
