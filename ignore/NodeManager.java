package org.una.navigatetrack.controller.fxml;

import org.una.navigatetrack.roads.Connection;
import org.una.navigatetrack.roads.Node;

import java.util.ArrayList;
import java.util.List;

public class NodeManager {
    private final List<Node> listNodes = new ArrayList<>();

    public void addNode(Node node) {
        listNodes.add(node);
    }

    public void removeNode(Node node) {
        removeConnectionsToNode(node);
        listNodes.remove(node);
    }

    public void removeConnectionsToNode(Node targetNode) {
        listNodes.forEach(node -> {
            Connection[] connections = node.getConnections(null); // Obtener todas las conexiones
            for (Connection connection : connections) {
                if (connection != null && connection.getTargetNode().equals(targetNode)) {
                    node.deleteConnection(connection.getDirection());
                    break; // Salir después de eliminar
                }
            }
        });
    }

    public Node findNodeAtPoint(int[] point) {
        return listNodes.stream()
                .filter(node -> isNearPoint(point, node.getLocation()))
                .findFirst()
                .orElse(null);
    }

    private boolean isNearPoint(int[] point, int[] location) {
        int tolerance = 10; // Tolerancia en píxeles
        return Math.abs(point[0] - location[0]) <= tolerance && Math.abs(point[1] - location[1]) <= tolerance;
    }

    public List<Node> getNodes() {
        return listNodes;
    }
}

