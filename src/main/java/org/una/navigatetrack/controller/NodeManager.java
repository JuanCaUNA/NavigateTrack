package org.una.navigatetrack.controller;

import lombok.Getter;
import lombok.Setter;
import org.una.navigatetrack.roads.Connection;
import org.una.navigatetrack.roads.Directions;
import org.una.navigatetrack.roads.Node;
import org.una.navigatetrack.storage.StorageManager;

import java.util.ArrayList;
import java.util.List;

public class NodeManager {
    private final StorageManager<List<Node>> nodesStorage = new StorageManager<>("src/main/resources/ListaNodos/", "listNodos.data");
    @Getter
    private final List<Node> listNodes = new ArrayList<>();
    @Getter
    @Setter
    private Node currentNode;

    // Constructor para cargar los nodos desde archivo al inicializar
    public NodeManager() {
        loadNodesFromFile();
    }

    // Cargar nodos desde archivo
    private void loadNodesFromFile() {
        List<Node> loadedNodes = nodesStorage.read();
        if (loadedNodes != null) {
            listNodes.addAll(loadedNodes);
        }
    }

    // Guardar nodos en archivo
    public void saveNodesToFile() {
        nodesStorage.write(listNodes);
    }

    // Crear un nuevo nodo y añadirlo a la lista
    public void createNode(int[] location) {
        Node newNode = new Node();
        newNode.setLocation(location);
        listNodes.add(newNode);
        saveNodesToFile();
    }

    // Eliminar un nodo
    public void deleteNode(Node node) {
        listNodes.remove(node);
        saveNodesToFile();
    }

    // Buscar un nodo en una ubicación específica
    public Node getNodeAtLocation(int[] location) {
        for (Node node : listNodes) {
            if (node.getLocation()[0] == location[0] && node.getLocation()[1] == location[1]) {
                return node;
            }
        }
        return null;
    }

    // Añadir una conexión entre dos nodos
    public void addConnection(Node fromNode, Node toNode, Directions direction) {
        fromNode.addConnection(toNode, direction);
        saveNodesToFile();
    }

    // Eliminar una conexión de un nodo
    public void removeConnection(Node node, Directions direction) {
        node.deleteConnection(direction);
        saveNodesToFile();
    }

    // Obtener todas las conexiones de un nodo
    public Connection[] getConnections(Node node) {
        return node.getConnections(node);
    }

    // Bloquear una ruta
    public void blockConnection(Node node, Directions direction) {
        Connection connection = getConnectionInDirection(node, direction);
        if (connection != null) {
            connection.blockRoute();
            saveNodesToFile();
        }
    }

    // Desbloquear una ruta
    public void unblockConnection(Node node, Directions direction) {
        Connection connection = getConnectionInDirection(node, direction);
        if (connection != null) {
            connection.unblockRoute();
            saveNodesToFile();
        }
    }

    // Obtener una conexión en una dirección específica
    public Connection getConnectionInDirection(Node node, Directions direction) {
        return node.getTargetNode(direction) != null ? node.getTargetNode(direction).getConnection(direction) : null;
    }
}
