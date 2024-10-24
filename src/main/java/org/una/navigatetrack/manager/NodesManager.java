package org.una.navigatetrack.manager;

import lombok.Getter;
import org.una.navigatetrack.manager.storage.StorageManager;
import org.una.navigatetrack.roads.Connection;
import org.una.navigatetrack.roads.Directions;
import org.una.navigatetrack.roads.Node;

import java.util.ArrayList;
import java.util.List;

public class NodesManager {
    private final StorageManager<List<Node>> nodesStorage = new StorageManager<>("src/main/resources/listNodes/", "listNodes.data");

    @Getter
    private final List<Node> listNodes = new ArrayList<>();

    public NodesManager() {
        readNodesFromFile();
    }

    //to all list
    public void readNodesFromFile() {
        List<Node> loadedNodes = nodesStorage.read();
        listNodes.addAll(loadedNodes);
    }

    public void updateNodesToFile() {
        nodesStorage.write(listNodes);
    }

    //element of list
    public void deleteNode(Node node) {
        if (node == null) return;
        listNodes.remove(node);
    }

    public void addNode(int[] location) {
        listNodes.add(new Node(location));
    }

    public Node getNodeAtLocation(int[] location) {
        return listNodes.stream()
                .filter(node -> node.getLocation()[0] == location[0] && node.getLocation()[1] == location[1])
                .findFirst()
                .orElse(null);
    }

    //element of node
    public void addConnection(Node currentNode, Node toNode, Directions direction) {
        if (currentNode != null)
            currentNode.addConnection(toNode, direction);
    }

    public void removeConnection(Node node, Directions direction) {
        node.deleteConnection(direction);
    }

    public Connection getConnectionInDirection(Node node, Directions direction) {
        return node != null ? node.getConnection(direction) : null;
    }

    //elements of Connection
    // block rute
    public void blockConnection(Node node, Directions direction) {
        Connection connection = getConnectionInDirection(node, direction);
        if (connection != null) {
            connection.blockRoute();
        }
    }

    // unlock una ruta
    public void unblockConnection(Node node, Directions direction) {
        Connection connection = getConnectionInDirection(node, direction);
        if (connection != null) {
            connection.unblockRoute();
        }
    }

}
