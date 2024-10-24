package org.una.navigatetrack.manager;

import lombok.Getter;
import org.una.navigatetrack.manager.storage.StorageManager;
import org.una.navigatetrack.roads.Connection;
import org.una.navigatetrack.roads.Directions;
import org.una.navigatetrack.roads.Node;

import java.util.ArrayList;
import java.util.Arrays;
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
    public void deleteNode(org.una.navigatetrack.roads.Node node) {
        if (node == null) return;
        if (!listNodes.remove(node)) {
            System.out.println("Node not found: " + Arrays.toString(node.getLocation()));
        }
    }

    public void addNode(double[] location) {
        listNodes.add(new org.una.navigatetrack.roads.Node(location));
    }

    public org.una.navigatetrack.roads.Node getNodeAtLocation(double[] location) {
        return listNodes.stream()
                .filter(node -> node.getLocation()[0] == location[0] && node.getLocation()[1] == location[1])
                .findFirst()
                .orElse(null);
    }

    //element of node
    public void addConnection(org.una.navigatetrack.roads.Node currentNode, org.una.navigatetrack.roads.Node toNode, Directions direction) {
        if (currentNode != null)
            currentNode.addConnection(toNode, direction);
    }

    public void removeConnection(org.una.navigatetrack.roads.Node node, Directions direction) {
        node.deleteConnection(direction);
    }

    public Connection getConnectionInDirection(org.una.navigatetrack.roads.Node node, Directions direction) {
        return node != null ? node.getConnection(direction) : null;
    }

    //elements of Connection
    // block rute
    public void blockConnection(org.una.navigatetrack.roads.Node node, Directions direction) {
        Connection connection = getConnectionInDirection(node, direction);
        if (connection != null) {
            connection.blockRoute();
        }
    }

    // unlock una ruta
    public void unblockConnection(org.una.navigatetrack.roads.Node node, Directions direction) {
        Connection connection = getConnectionInDirection(node, direction);
        if (connection != null) {
            connection.unblockRoute();
        }
    }

}
