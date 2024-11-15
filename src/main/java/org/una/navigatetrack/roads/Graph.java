package org.una.navigatetrack.roads;

import lombok.Getter;
import org.una.navigatetrack.list.ListConnections;
import org.una.navigatetrack.list.ListNodes;

import java.util.*;

//@SuppressWarnings("ALL")
@Getter
public class Graph {

    private Node startNode, endNode;
    private int startNodeID, endNodeID;

    private List<Integer> bestPathNodeIDs;
    private final List<Edge> bestPathEdges;
    private double[][] weightMatrix; // Matriz de pesos entre nodos
    private int[][] directionMatrix; // Matriz de direcciones (índices de los nodos)

    private static final double INFINITY = Double.MAX_VALUE; // Constante para representar "sin conexión"

    public Graph(Node startNode, Node endNode) {
        this.startNode = startNode;
        this.endNode = endNode;
        this.startNodeID = startNode.getID();
        this.endNodeID = endNode.getID();
        initializeMatrices(); // Crea la matriz de pesos y direcciones

        bestPathNodeIDs = new ArrayList<>();
        bestPathEdges = new ArrayList<>();
    }

    private void initializeMatrices() {
        try {
            List<Node> nodes = ListNodes.getNodesList();
            int size = nodes.size();

            if (size == 0) {
                throw new IllegalArgumentException("El número de nodos debe ser mayor que cero.");
            }

            // Inicializar matrices de pesos y direcciones
            weightMatrix = new double[size][size];
            directionMatrix = new int[size][size];

            for (int i = 0; i < size; i++) {
                Arrays.fill(weightMatrix[i], INFINITY);  // Usamos INFINITY para "sin conexión"
                Arrays.fill(directionMatrix[i], -1);  // Sin dirección inicial
            }

            // Llenar las matrices con las conexiones
            for (Edge edge : ListConnections.getCONNECTIONS_LIST()) {
                int startID = edge.getStartingNodeID();
                int endID = edge.getDestinationNodeID();

                // Validar si los índices están dentro del rango
                if (startID < 0 || startID >= size || endID < 0 || endID >= size) {
                    System.err.printf("Índices fuera de rango: %d -> %d%n", startID, endID);
                    continue;
                }

                // Verificar peso de la conexión
                double weight = edge.getEffectiveWeight();
                if (weight < 0 || Double.isNaN(weight)) {
                    System.err.printf("Peso inválido para la conexión: %d -> %d%n", startID, endID);
                    continue;
                }

                // Asignar peso y dirección
                weightMatrix[startID][endID] = weight;
                directionMatrix[startID][endID] = endID;
            }

        } catch (Exception e) {
            System.err.println("Error al inicializar las matrices: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void setNodes(Node startNode, Node endNode) {
        this.startNode = startNode;
        this.endNode = endNode;
        this.startNodeID = startNode.getID();
        this.endNodeID = endNode.getID();
    }

    public boolean runDijkstra() {
        int n = weightMatrix.length;
        double[] dist = new double[n];
        int[] prev = new int[n];

        Arrays.fill(dist, INFINITY);
        Arrays.fill(prev, -1);
        dist[startNodeID] = 0;

        PriorityQueue<Integer> queue = new PriorityQueue<>(Comparator.comparingDouble(node -> dist[node]));
        queue.add(startNodeID);

        while (!queue.isEmpty()) {
            int currentNodeID = queue.poll();

            if (currentNodeID == endNodeID) break;

            for (int neighborID = 0; neighborID < n; neighborID++) {
                if (weightMatrix[currentNodeID][neighborID] != INFINITY) {
                    double newDist = dist[currentNodeID] + weightMatrix[currentNodeID][neighborID];
                    if (newDist < dist[neighborID]) {
                        dist[neighborID] = newDist;
                        prev[neighborID] = currentNodeID;
                        queue.add(neighborID);
                    }
                }
            }
        }

        bestPathNodeIDs = reconstructPath(prev, endNodeID);
        if (bestPathNodeIDs == null || bestPathNodeIDs.isEmpty()) return false;

        reconstructEdgesFromPath();
        return true;
    }

    private List<Integer> reconstructPath(int[] prev, int endID) {
        List<Integer> path = new ArrayList<>();
        for (int at = endID; at != -1; at = prev[at]) {
            path.add(at);
        }
        Collections.reverse(path);
        return path.size() > 1 ? path : null; // Si el camino es válido (más de un nodo)
    }

    private void reconstructEdgesFromPath() {
        bestPathEdges.clear();
        for (int i = 0; i < bestPathNodeIDs.size() - 1; i++) {
            int startID = bestPathNodeIDs.get(i);
            int endID = bestPathNodeIDs.get(i + 1);
            bestPathEdges.add(ListNodes.getNodeByID(startID).getConnectionInNode(endID));
        }
    }

    public double getPathDistance() {
        if (bestPathNodeIDs == null || bestPathNodeIDs.isEmpty()) {
            return 0;
        }
        double distance = 0;
        for (int i = 0; i < bestPathNodeIDs.size() - 1; i++) {
            distance += weightMatrix[bestPathNodeIDs.get(i)][bestPathNodeIDs.get(i + 1)];
        }
        return distance;
    }

    public boolean runFloydWarshall() {
        int n = weightMatrix.length;

        double[][] dist = new double[n][n];
        int[][] next = new int[n][n];

        // Inicialización de matrices
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i == j) {
                    dist[i][j] = 0;
                    next[i][j] = -1;
                } else if (weightMatrix[i][j] != INFINITY) {
                    dist[i][j] = weightMatrix[i][j];
                    next[i][j] = j;
                } else {
                    dist[i][j] = INFINITY;
                    next[i][j] = -1;
                }
            }
        }

        // Algoritmo de Floyd-Warshall
        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (dist[i][j] > dist[i][k] + dist[k][j]) {
                        dist[i][j] = dist[i][k] + dist[k][j];
                        next[i][j] = next[i][k];
                    }
                }
            }
        }

        if (dist[startNodeID][endNodeID] == INFINITY) {
            return false;
        }

        bestPathNodeIDs = reconstructPathFromNext(next, startNodeID, endNodeID);
        if (bestPathNodeIDs == null) {
            return false; // No se pudo encontrar un camino
        }

        reconstructEdgesFromPath();
        return true;
    }

    private List<Integer> reconstructPathFromNext(int[][] next, int startID, int endID) {
        List<Integer> path = new ArrayList<>();
        if (next[startID][endID] == -1) return null;

        for (int at = startID; at != -1; at = next[at][endID]) {
            path.add(at);
            if (at == endID) break;
        }
        return path.size() > 1 ? path : null;
    }

//    // Método principal para ejecutar los algoritmos
//    public static void main(String[] args) {
//        // Cargar la lista de nodos y conexiones
//        ListNodes.loadNodesList();
//
//        // Ejemplo de uso: Camino entre nodos 3 y 4
//        System.out.println("Prueba: Camino de nodo 3 a nodo 4");
//        Graph graph = new Graph(ListNodes.getNodeByID(3), ListNodes.getNodeByID(4));
//        testGraphAlgorithms(graph);
//    }
//
//    private static void testGraphAlgorithms(Graph graph) {
//        System.out.println("Prueba de Dijkstra:");
//        if (graph.runDijkstra()) {
//            System.out.println("Camino encontrado: " + graph.bestPathNodeIDs);
//            System.out.println("Distancia total: " + graph.getPathDistance());
//        } else {
//            System.out.println("No se encontró un camino usando Dijkstra.");
//        }
//
//        for (Edge edge: graph.bestPathEdges){
//            System.out.println(edge.toString());
//        }
//
//        System.out.println("Prueba de Floyd-Warshall:");
//        if (graph.runFloydWarshall()) {
//            System.out.println("Camino encontrado: " + graph.bestPathNodeIDs);
//            System.out.println("Distancia total: " + graph.getPathDistance());
//        } else {
//            System.out.println("No se encontró un camino usando Floyd-Warshall.");
//        }
//    }
}
