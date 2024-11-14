package org.una.navigatetrack.roads;

import lombok.Getter;
import org.una.navigatetrack.list.ListConnections;
import org.una.navigatetrack.list.ListNodes;

import java.util.*;

//@SuppressWarnings("ALL")
@Getter
public class Graph {

    private Node initNode, endNode;
    private int initNodeID, endNodeID;

    private List<Integer> bestIdPath;
    private List<Edge> bestConectionPath;
    double[][] matrixPesos; // Matriz de pesos
    int[][] matrixDirecciones; // Matriz de direcciones

    public Graph(Node startNode, Node endNode) {
        this.initNode = startNode;
        this.endNode = endNode;
        this.initNodeID = startNode.getID();
        this.endNodeID = endNode.getID();
        createMatrix(); // Crea la matriz de pesos en el constructor


        bestIdPath = new ArrayList<>();
        bestConectionPath = new ArrayList<>();
    }

    private void createMatrix() {
        try {
            int size = ListNodes.getNodesList().size();

            if (size <= 0) {
                throw new IllegalArgumentException("El número de nodos debe ser mayor que cero.");
            }

            // Inicializar las matrices de pesos y direcciones con valores predeterminados
            matrixPesos = new double[size][size];
            matrixDirecciones = new int[size][size];

            for (int c = 0; c < size; c++) {  // columnas
                for (int f = 0; f < size; f++) {  // filas
                    matrixPesos[c][f] = Double.MAX_VALUE;  // Usamos Double.MAX_VALUE para indicar "sin conexión"
                    matrixDirecciones[c][f] = 0;  // Usamos 0 para indicar "sin dirección"
                }
            }

            // Llenar las matrices con la información de las conexiones
            for (Edge edge : ListConnections.getCONNECTIONS_LIST()) {
                int column = edge.getDestinationNodeID();  // Nodo de destino
                int fila = edge.getStartingNodeID();  // Nodo de inicio

                // Validación de los índices de fila y columna
                if (column < 0 || column >= size || fila < 0 || fila >= size) {
                    System.err.println("Error: Los índices de la conexión están fuera de rango. "
                            + "Destino: " + column + ", Inicio: " + fila);
                    continue;  // Salta esta conexión y sigue con la siguiente
                }

                // Verificar si el peso de la conexión es válido
                double weight = edge.getEffectiveWeight();
                if (Double.isNaN(weight) || weight < 0) {
                    System.err.println("Advertencia: Conexión con peso inválido o negativo. Nodo: "
                            + edge.getStartingNodeID() + " -> " + edge.getDestinationNodeID());
                    continue;  // Si el peso es inválido, no lo asignamos
                }

                // Asignar el peso y la dirección
                matrixPesos[column][fila] = weight;  // Asignar el peso de la conexión
                matrixDirecciones[column][fila] = column;  // Aquí asignamos la columna (o ajusta si es necesario)
            }

        } catch (Exception e) {
            System.err.println("Error al crear las matrices de conexiones: " + e.getMessage());
            e.printStackTrace();  // Imprime la traza completa del error
        }
    }

    public void setNodes(Node startNode, Node endNode) {
        this.initNode = startNode;
        this.endNode = endNode;

        this.initNodeID = startNode.getID();
        this.endNodeID = endNode.getID();
    }

    public boolean dijkstra() {
        bestIdPath.clear();
        bestConectionPath.clear();

        int n = matrixPesos.length;
        double[] dist = new double[n];

        int[] prev = new int[n];

        Arrays.fill(dist, Double.MAX_VALUE);
        Arrays.fill(prev, -1);
        dist[initNodeID] = 0;

        PriorityQueue<Integer> queue = new PriorityQueue<>(Comparator.comparingDouble(node -> dist[node]));
        queue.add(initNodeID);

        while (!queue.isEmpty()) {
            int u = queue.poll();

            if (u == endNodeID) break;

            for (int v = 0; v < n; v++) {
                if (matrixPesos[u][v] != Double.MAX_VALUE) {
                    double newDist = dist[u] + matrixPesos[u][v];
                    if (newDist < dist[v]) {
                        dist[v] = newDist;
                        prev[v] = u;
                        queue.add(v);
                    }
                }
            }
        }

        bestIdPath = reconstructPath(prev, endNodeID);

        if (bestIdPath == null || bestIdPath.isEmpty()) {
            return false;
        }

        bestConectionPath = new ArrayList<>();
        for (int i = 0; i < bestIdPath.size() - 1; i++) {
            bestConectionPath.add(ListNodes.getNodeByID(bestIdPath.get(i)).getConnectionInNode(bestIdPath.get(i + 1)));
        }
        return true;
    }

    private List<Integer> reconstructPath(int[] prev, int end) {
        List<Integer> path = new ArrayList<>();
        for (int at = end; at != -1; at = prev[at]) {
            path.add(at);
        }
        Collections.reverse(path);
        return path.size() > 1 ? path : null;
    }

    public double getPathDistance() {
        if (bestIdPath == null || bestIdPath.isEmpty()) {
            return 0;
        }
        double distance = 0;
        for (int i = 0; i < bestIdPath.size() - 1; i++) {
            distance += matrixPesos[bestIdPath.get(i)][bestIdPath.get(i + 1)];
        }
        return distance;
    }

    public boolean floydWarshall() {
        bestIdPath.clear();
        bestConectionPath.clear();

        int n = matrixPesos.length;

        // Matrices de distancias y direcciones
        double[][] dist = new double[n][n];
        int[][] next = new int[n][n];

        // Inicialización de las matrices
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i == j) {
                    dist[i][j] = 0; // La distancia de un nodo a sí mismo es 0
                    next[i][j] = -1; // No hay nodo siguiente si es el mismo nodo
                } else if (matrixPesos[i][j] != Double.MAX_VALUE) {
                    dist[i][j] = matrixPesos[i][j]; // Si hay una conexión, tomamos el peso
                    next[i][j] = j; // El siguiente nodo es el destino
                } else {
                    dist[i][j] = Double.MAX_VALUE; // Si no hay conexión, es infinito
                    next[i][j] = -1; // No hay camino
                }
            }
        }

        // Algoritmo de Floyd-Warshall
        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (dist[i][j] > dist[i][k] + dist[k][j]) {
                        dist[i][j] = dist[i][k] + dist[k][j]; // Actualizamos la distancia
                        next[i][j] = next[i][k]; // Actualizamos el siguiente nodo
                    }
                }
            }
        }

        // Si no se puede llegar al nodo final, retornamos false
        if (dist[initNodeID][endNodeID] == Double.MAX_VALUE) {
            return false;
        }

        // Reconstruir el camino más corto desde el nodo inicial hasta el nodo final
        bestIdPath = reconstructPathFromNext(next, initNodeID, endNodeID);

        if (bestIdPath == null) {
            return false; // No se pudo encontrar un camino
        }

        // Reconstruir las conexiones del camino más corto
        bestConectionPath = new ArrayList<>();
        for (int i = 0; i < bestIdPath.size() - 1; i++) {
            bestConectionPath.add(ListNodes.getNodeByID(bestIdPath.get(i)).getConnectionInNode(bestIdPath.get(i + 1)));
        }

        return true;
    }

    private List<Integer> reconstructPathFromNext(int[][] next, int start, int end) {
        List<Integer> path = new ArrayList<>();
        if (next[start][end] == -1) {
            return null; // No hay camino
        }
        for (int at = start; at != -1; at = next[at][end]) {
            path.add(at);
            if (at == end) break;
        }
        return path.size() > 1 ? path : null;
    }

}