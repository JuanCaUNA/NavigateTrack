package org.una.navigatetrack.manager;

import org.una.navigatetrack.roads.Connection;
import org.una.navigatetrack.roads.Directions;
import org.una.navigatetrack.list.ListNodes;
import org.una.navigatetrack.roads.Node;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class NodesManager {
    private final List<Node> nodes;

    // Constructor
    public NodesManager() {
        readNodesFromFile();
        nodes = ListNodes.getNodesList();  // Usamos la lista actualizada de nodos
    }

    // Métodos de ciclo de vida

    // Cargar nodos desde el archivo y almacenarlos en ListNodes
    public void readNodesFromFile() {
        ListNodes.loadNodesList();
    }

    // Guardar los nodos en el archivo
    public void updateNodesToFile() {
        ListNodes.saveNodesList();
    }

    // Métodos relacionados con nodos

    // Añadir un nodo
    public void addNode(double[] location) {
        Node newNode = new Node(location);
        newNode.setID(ListNodes.getNextId());
        ListNodes.addNode(newNode);  // Agregar el nodo a ListNodes
    }

    // Eliminar un nodo
    public void deleteNode(int nodeID) {
        Node node = ListNodes.getNodeByID(nodeID);
        if (node != null) {
            ListNodes.removeById(nodeID);
        } else {
            System.out.println("Node not found with ID: " + nodeID);
        }
    }

    // Obtener un nodo en una ubicación específica
    public Node getNodeAtLocation(double[] location) {
        return nodes.stream()
                .filter(node -> Arrays.equals(node.getLocation(), location))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Node not found at the given location"));
    }

    // Buscar y obtener un nodo de manera segura (sin Optional)
    public Node searchAndGetNode(int nodeID) {
        Node node = ListNodes.getNodeByID(nodeID);
        if (node != null) {
            return node;
        }
        throw new IllegalArgumentException("Node not found with ID: " + nodeID);
    }

    // Métodos relacionados con conexiones

    // Añadir una conexión entre nodos
    public void addConnection(int currentNodeID, int toNodeID, Directions direction) {
        Node currentNode = ListNodes.getNodeByID(currentNodeID);
        Node toNode = ListNodes.getNodeByID(toNodeID);

        if (currentNode != null && toNode != null) {
            currentNode.addConnection(toNode, direction);
        } else {
            System.out.println("One or both nodes not found.");
        }
    }

    // Eliminar una conexión
    public void removeConnection(int nodeID, Directions direction) {
        Node node = ListNodes.getNodeByID(nodeID);
        if (node != null) {
            node.deleteConnection(direction);
        } else {
            System.out.println("Node not found with ID: " + nodeID);
        }
    }

    // Obtener conexión en una dirección específica
    public Connection getConnectionInDirection(int nodeID, Directions direction) {
        Node node = ListNodes.getNodeByID(nodeID);
        if (node != null) {
            return node.getConnection(direction);
        }
        return null;  // Si el nodo no existe, devolvemos null
    }

    // Bloquear ruta en una dirección específica
    public void blockConnection(int nodeID, Directions direction) {
        Connection connection = getConnectionInDirection(nodeID, direction);
        if (connection != null) {
            connection.setBlocked(true);
        }
    }

    // Desbloquear ruta en una dirección específica
    public void unblockConnection(int nodeID, Directions direction) {
        Connection connection = getConnectionInDirection(nodeID, direction);
        if (connection != null) {
            connection.setBlocked(false);
        }
    }

    // Métodos adicionales

    // Método genérico para búsqueda que devuelve Optional<Node>
    public Optional<Node> find(int nodeID) {
        return ListNodes.findById(nodeID);
    }
}
