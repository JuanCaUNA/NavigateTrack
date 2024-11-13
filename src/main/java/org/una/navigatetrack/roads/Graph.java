package org.una.navigatetrack.roads;

import lombok.Getter;
import org.una.navigatetrack.list.ListConnections;
import org.una.navigatetrack.list.ListNodes;

import java.util.*;

@SuppressWarnings("ALL")
@Getter
public class Graph {
    private final Node initNode, endNode;
    private final int initNodeID, endNodeID;
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
    }

    public void createMatrix() {
        int size = ListNodes.getNextId() - 1;  // Asumiendo que el número de nodos es ListNodes.getNextId() - 1

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

            // Asignar el peso y la dirección
            matrixPesos[column][fila] = edge.getEffectiveWeight();  // Asignar el peso de la conexión
            matrixDirecciones[column][fila] = column;  // Aquí asignamos la columna, puede necesitar ajustarse según la lógica
        }
    }

    public boolean dijkstra() {
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

        if (bestIdPath == null) {
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
            dijkstra();
        }
        double distance = 0;
        for (int i = 0; i < bestIdPath.size() - 1; i++) {
            distance += matrixPesos[bestIdPath.get(i)][bestIdPath.get(i + 1)];
        }
        return distance;
    }
}