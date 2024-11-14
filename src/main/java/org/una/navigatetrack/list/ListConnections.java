package org.una.navigatetrack.list;

import lombok.Getter;
import org.una.navigatetrack.roads.Edge;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ListConnections {

    @Getter
    private static final List<Edge> CONNECTIONS_LIST = new ArrayList<>(); // Lista de conexiones


    public static void updateID(int oldId, int newId) {
        for (Edge edge : CONNECTIONS_LIST) {
            if (edge.getDestinationNodeID() == oldId) {
                edge.setDestinationNodeID(newId);
            }
        }
    }

    public static void randomizeConnections(double blockProbability, double normalTrafficProbability, double moderateTrafficProbability) {
        Random random = new Random();

        blockProbability = blockProbability > 1 ? 1 : (blockProbability < 0 ? random.nextDouble() : blockProbability);
        normalTrafficProbability = normalTrafficProbability > 1 ? 1 : (normalTrafficProbability < 0 ? random.nextDouble() : normalTrafficProbability);
        moderateTrafficProbability = moderateTrafficProbability > 1 ? 1 : (moderateTrafficProbability < 0 ? random.nextDouble() : moderateTrafficProbability);

        for (Edge edge : CONNECTIONS_LIST) {
            edge.setBlocked(random.nextDouble() < blockProbability);

            if (edge.isBlocked()) {
                edge.setTrafficCondition("lento");
            } else {
                double trafficRoll = random.nextDouble();

                if (trafficRoll < normalTrafficProbability) {
                    edge.setTrafficCondition("normal");
                } else if (trafficRoll < normalTrafficProbability + moderateTrafficProbability) {
                    edge.setTrafficCondition("moderado");
                } else {
                    edge.setTrafficCondition("lento");
                }
            }
        }
    }

    public static void addConnection(Edge edge) {
        if (!containsConnectionWithID(edge.getID())) {
            CONNECTIONS_LIST.add(edge);
        } else {
            throw new IllegalArgumentException("Ya existe una conexión con el mismo ID: " + edge.getID());
        }
    }

    public static void removeConnection(int idConnection) {
        CONNECTIONS_LIST.removeIf(connection -> connection.getID() == idConnection);
    }

    public static Edge getConnection(int idConnection) {
        return CONNECTIONS_LIST.stream()
                .filter(connection -> connection.getID() == idConnection)
                .findFirst()
                .orElse(null);  // Si no se encuentra, devuelve null
    }

    public static int nextConnectionId() {
        return CONNECTIONS_LIST.stream()
                .mapToInt(Edge::getID)
                .max()
                .orElse(-1) + 1;
    }

    private static boolean containsConnectionWithID(int id) {
        return CONNECTIONS_LIST.stream().anyMatch(connection -> connection.getID() == id);
    }
}