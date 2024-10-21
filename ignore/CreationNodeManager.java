package org.una.navigatetrack.controller.fxml;

import lombok.Getter;
import lombok.Setter;
import org.una.navigatetrack.roads.Connection;
import org.una.navigatetrack.roads.Directions;
import org.una.navigatetrack.roads.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

public class CreationNodeManager {
    private final List<Node> listNodes;
    @Getter
    @Setter
    private Node currentNode;

    public CreationNodeManager() {
        listNodes = new ArrayList<>();
    }

    public void addNode(int[] point) {
        Node newNode = new Node();
        newNode.setLocation(point);
        listNodes.add(newNode);
    }

    public void createConnection(Node fromNode, Node targetNode, Directions direction) {
        if (fromNode != null && targetNode != null) {
            fromNode.addConnection(targetNode, direction);
        }
    }

    public void deleteNode(Node node) {
        removeConnectionsToNode(node);
        listNodes.remove(node);
    }

    public List<Node> getNodes() {
        return listNodes;
    }

    public String getNodeConnectionsInfo(Node node) {
        StringBuilder info = new StringBuilder("Conexiones:\n");
        for (Connection connection : node.getConnections()) {
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

    private void removeConnectionsToNode(Node node) {
        for (Node n : listNodes) {
            for (int i = 0; i < n.getConnections().length; i++) {
                Connection conn = n.getConnections()[i];
                if (conn != null && conn.getTargetNode().equals(node)) {
                    n.getConnections()[i] = null;
                }
            }
        }
    }
}

