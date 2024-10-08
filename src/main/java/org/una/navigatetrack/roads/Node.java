package org.una.navigatetrack.roads;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.Arrays;

@Getter
@Setter
public class Node implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Connection[] connections;
    private int[] location;
    private static final int MAX_CONNECTIONS = 4;

    public Node() {
        connections = new Connection[MAX_CONNECTIONS];
        location = new int[2];
    }

    public void addConnection(Node targetNode, Directions direction) {
        for (Connection value : connections) {
            if (value != null && value.getDirection() == direction) {
                value.setTargetNode(targetNode);
                value.setWeight(calculateDistance(targetNode));
                return;
            }
        }

        for (int i = 0; i < connections.length; i++) {
            if (connections[i] == null) {
                double weight = calculateDistance(targetNode);
                Connection connection = new Connection(targetNode, (int) weight, direction);
                connections[i] = connection;
                return;
            }
        }
    }

    public int calculateDistance(Node other) {
        return (int) Math.sqrt(Math.pow(location[0] - other.location[0], 2) + Math.pow(location[1] - other.location[1], 2));
    }

    public void deleteConnection(Directions direction) {
        for (int i = 0; i < connections.length; i++) {
            if (connections[i] != null && connections[i].getDirection() == direction) {
                connections[i] = null;
            }
        }
    }

    public Node getTargetNode(Directions direction) {
        for (Connection connection : connections) {
            if (connection != null && connection.getDirection() == direction) {
                return connection.getTargetNode();
            }
        }
        return null;
    }

    public Connection[] getConnections(Node startNode) {
        return Arrays.stream(connections)
                .filter(conn -> conn != null && conn.getTargetNode() != startNode)
                .toArray(Connection[]::new);
    }

}