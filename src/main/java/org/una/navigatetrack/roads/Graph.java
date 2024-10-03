package org.una.navigatetrack.roads;

import java.util.*;

public class Graph {
    private final List<Node> nodes;

    public Graph() {
        nodes = new ArrayList<>();
    }

    public void addNode(Node node) {
        nodes.add(node);
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public List<Connection> getConnections(Node node) {
        List<Connection> connections = new ArrayList<>();
        for (Connection connection : node.getConnections()) {
            if (connection != null) {
                connections.add(connection);
            }
        }
        return connections;
    }

    public Map<Node, Double> dijkstra(Node start) {
        Map<Node, Double> distances = new HashMap<>();
        PriorityQueue<Node> priorityQueue = new PriorityQueue<>(Comparator.comparing(distances::get));
        Set<Node> visited = new HashSet<>();

        for (Node node : nodes) {
            distances.put(node, Double.MAX_VALUE);
        }
        distances.put(start, 0.0);
        priorityQueue.add(start);

        while (!priorityQueue.isEmpty()) {
            Node currentNode = priorityQueue.poll();
            if (!visited.add(currentNode)) continue;

            for (Connection connection : getConnections(currentNode)) {
                if (connection.canAccess()) {
                    Node targetNode = connection.getTargetNode();
                    double newDist = distances.get(currentNode) + connection.getEffectiveWeight();
                    if (newDist < distances.get(targetNode)) {
                        distances.put(targetNode, newDist);
                        priorityQueue.add(targetNode);
                    }
                }
            }
        }
        return distances;
    }

    public void floydWarshall() {
        int size = nodes.size();
        double[][] distanceMatrix = new double[size][size];

        // Inicializa la matriz
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (i == j) {
                    distanceMatrix[i][j] = 0;
                } else {
                    distanceMatrix[i][j] = Double.MAX_VALUE; // o un valor grande
                }
            }
        }

        // Llena la matriz con los pesos de las conexiones
        for (int i = 0; i < size; i++) {
            Node node = nodes.get(i);
            for (Connection connection : getConnections(node)) {
                int targetIndex = nodes.indexOf(connection.getTargetNode());
                distanceMatrix[i][targetIndex] = connection.getEffectiveWeight();
            }
        }

        // Aplica Floyd-Warshall
        for (int k = 0; k < size; k++) {
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    if (distanceMatrix[i][k] + distanceMatrix[k][j] < distanceMatrix[i][j]) {
                        distanceMatrix[i][j] = distanceMatrix[i][k] + distanceMatrix[k][j];
                    }
                }
            }
        }
    }
}
