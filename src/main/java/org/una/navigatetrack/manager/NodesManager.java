package org.una.navigatetrack.manager;

import lombok.Getter;
import org.una.navigatetrack.manager.storage.StorageManager;
import org.una.navigatetrack.roads.Connection;
import org.una.navigatetrack.roads.Directions;
import org.una.navigatetrack.roads.ListNodes;
import org.una.navigatetrack.roads.Node;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class NodesManager {
    private final StorageManager<List<Node>> nodesStorage = new StorageManager<>("src/main/resources/listNodes/", "listNodes.data");

    @Getter
    private List<Node> listNodes = new ArrayList<>();

    public NodesManager() {
        readNodesFromFile();
    }

    //to all list
    public void readNodesFromFile() {
        List<Node> loadedNodes = nodesStorage.read();
        ListNodes.setListNodes(loadedNodes);

        listNodes = ListNodes.getListNodes();
    }

    public void updateNodesToFile() {
        nodesStorage.write(listNodes);
    }
    //to all list end

    //element of list
    public void deleteNode(Node node) {
        if (node == null) return;
        if (!listNodes.remove(node)) {
            System.out.println("Node not found: " + Arrays.toString(node.getLocation()));
        }
    }

    public void addNode(double[] location) {
        listNodes.add(new Node(location));
    }

    public Node getNodeAtLocation(double[] location) {
        return listNodes.stream()
                .filter(node -> Arrays.equals(node.getLocation(), location))
                .findFirst()
                .orElse(null);
    }
    //element of list end

    //element of node
    public void addConnection(Node currentNode, Node toNode, Directions direction) {
        if (currentNode != null) {
            currentNode.addConnection(toNode, direction);
        }
    }

    public void removeConnection(Node node, Directions direction) {
        if (node != null) {
            node.deleteConnection(direction);
        }
    }
    //element of node end

    //elements of Connection
    public Connection getConnectionInDirection(Node node, Directions direction) {
        return node != null ? node.getConnection(direction) : null;
    }

    // block route
    public void blockConnection(Node node, Directions direction) {
        Connection connection = getConnectionInDirection(node, direction);
        if (connection != null) {
            connection.blockRoute();
        }
    }

    // unlock a route
    public void unblockConnection(Node node, Directions direction) {
        Connection connection = getConnectionInDirection(node, direction);
        if (connection != null) {
            connection.unblockRoute();
        }
    }
    //elements of Connection end

    public Optional<Node> searchAndGetNode(int nodeID) {
        return ListNodes.findById(nodeID);
    }

    public Node getIndexAt(int nodeID) {
        return ListNodes.getListNodes().get(nodeID);
    }
}