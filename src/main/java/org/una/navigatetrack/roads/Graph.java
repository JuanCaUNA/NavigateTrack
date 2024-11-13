package org.una.navigatetrack.roads;

import lombok.Getter;
import org.una.navigatetrack.list.ListConnections;
import org.una.navigatetrack.list.ListNodes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("ALL")
@Getter
public class Graph {
    private final Node initNode, endNode; // Nodos de inicio y fin del grafo
    private final int initNodeID, endNodeID; // IDs de los nodos de inicio y fin
    private List<Edge> bestPath; // Lista para almacenar la mejor ruta encontrada


    private double[][] matrixPesos; // Matriz de pesos
    private int[][] matrixDirecciones;

    // Constructor que inicializa el grafo con nodos de inicio y fin
    public Graph(Node startNode, Node endNode) {
        this.initNode = startNode;
        this.endNode = endNode;
        this.initNodeID = startNode.getID();
        this.endNodeID = endNode.getID();
    }

    /*
     * Méto-do principal que ejecuta el algoritmo de Dijkstra para encontrar el camino más corto
     * entre el nodo de inicio y el nodo de destino.
     * Inicializa las distancias desde el nodo de inicio, explora todas las conexiones
     * y utiliza un méto-do recursivo para encontrar la mejor ruta, actualizando el peso acumulado.
     *
     * @param start El nodo de inicio para la búsqueda (no se utiliza directamente en este méto-do).
     * @param end El nodo de fin para la búsqueda (no se utiliza directamente en este méto-do).
     * @return Un mapa de conexiones con sus respectivas distancias desde el nodo de inicio.
     */
    public Map<Edge, Integer> dijkstra(Node start, Node end) {
        bestPath = new ArrayList<>(); // Inicializa la mejor ruta
        List<Edge> currentPath = new ArrayList<>(); // Ruta actual en exploración
        Map<Edge, Integer> distances = new HashMap<>(); // Mapa para almacenar distancias

        // Inicializa las distancias desde el nodo de inicio
        initializeDistances(start, distances);

        // Itera sobre las conexiones del nodo de inicio
        for (Edge edge : initNode.getConnectionsInOrderByWeight()) {
            int newWeight = (int) edge.getEffectiveWeight(); // Peso de la conexión
            currentPath.add(edge); // Agregar la conexión al camino actual
            findBestPath(edge, newWeight, distances, currentPath); // Buscar el mejor camino
            currentPath.remove(edge); // Retirar la conexión para explorar otras rutas
        }

        return distances; // Retornar el mapa de distancias
    }

    // Méto-do que inicializa el mapa de distancias desde el nodo de inicio
    private void initializeDistances(Node start, Map<Edge, Integer> distances) {
        for (Edge edge : start.getConnectionsInOrderByWeight()) {
            distances.put(edge, (int) edge.getEffectiveWeight()); // Establecer distancias
        }
    }

    // Méto-do recursivo que encuentra el mejor camino a partir de una conexión previa
    private void findBestPath(Edge previousEdge, int currentWeight,
                              Map<Edge, Integer> distances, List<Edge> currentPath) {
        Node entryNode = previousEdge.getStartingNode(); // Nodo de inicio de la conexión previa
        Node currentNode = previousEdge.getDestinationNode(); // Nodo de destino de la conexión previa

        // Verifica si se ha llegado al nodo de destino
        if (currentNode.getID() == endNodeID) {
            // Actualiza la mejor ruta si es necesario
            if (bestPath.isEmpty() || currentWeight < calculateTotalWeight(bestPath)) {
                bestPath = new ArrayList<>(currentPath); // Almacena la mejor ruta
            }
            return; // Termina la búsqueda en esta ruta
        }

        // Impide regresar al nodo inicial
        if (currentNode.getID() == initNodeID) {
            return; // No regresar al nodo inicial
        }

        // Itera sobre las conexiones del nodo actual
        for (Edge edge : currentNode.getConnectionsInOrderByWeight(entryNode)) {
            int newWeight = currentWeight + (int) edge.getEffectiveWeight(); // Peso acumulado

            // Si la conexión no ha sido visitada
            if (!currentPath.contains(edge)) {
                currentPath.add(edge); // Agregar la conexión al camino
                // Llamada recursiva para seguir buscando el mejor camino
                findBestPath(edge, newWeight, distances, currentPath);
                currentPath.remove(edge); // Retirar la conexión al volver
            }
        }
    }

    // Méto-do que calcula el peso total de un camino dado
    private int calculateTotalWeight(List<Edge> path) {
        return path.stream().mapToInt(connection -> (int) connection.getEffectiveWeight()).sum(); // Sumar pesos
    }


    public void createMatrix() {
        int size = ListNodes.getNextId() - 1;  // Asumiendo que el número de nodos es ListNodes.getNextId() - 1

        // Usando matrices tradicionales (double[][] y int[][])
        matrixPesos = new double[size][size]; // Matriz de pesos
        matrixDirecciones = new int[size][size]; // Matriz de direcciones

        for (int c = 0; c < size; c++) {  // columnas
            for (int f = 0; f < size; f++) {  // filas
                matrixPesos[c][f] = Double.MAX_VALUE;  // Usamos Double.MAX_VALUE para indicar "sin conexión"
                matrixDirecciones[c][f] = -1;
            }
        }

        // Llenar las matrices con la información de las conexiones
        for (Edge edge : ListConnections.getCONNECTIONS_LIST()) {
            int column = edge.getDestinationNodeID();  // Nodo de destino
            int fila = edge.getStartingNodeID();  // Nodo de inicio

            // Asignar el peso y la dirección
            matrixPesos[column][fila] = edge.getEffectiveWeight();  // Asignar el peso de la conexión
            matrixDirecciones[column][fila] = column;
        }
    }

}

//ya las conexiones tienen la disatanciadefinida
    /*
        connection.getEffectiveWeight();// peso total
        connection.getStartingNode();//node de partida
        connection.getDestinationNode();//nodo

        otros a user
         connection.getAccumulateWeight();
         connection.setAccumulateWeight(acomulado);

    */
    /*
        se inicia desde start y obtiene la lista de coneciones

        start.getConnectionsInOrderByWeight();
        //para las proximas va definicendo el nodo anterior
        Node entryNode;
        start.getConnectionsInOrderByWeight(entryNode);
        start.getID();
     */


//    // Implementación del algoritmo de Floyd-Warshall
//    public void floydWarshall() {
//        int size = connections.size();
//        int[][] dist = new int[size][size];
//
//        // Inicializar la matriz de distancias
//        for (int i = 0; i < size; i++) {
//            for (int j = 0; j < size; j++) {
//                if (i == j) {
//                    dist[i][j] = 0;
//                } else {
//                    dist[i][j] = Integer.MAX_VALUE; // Representa la ausencia de conexión
//                }
//            }
//        }
//
//        // Rellenar la matriz con los pesos de las conexiones
//        for (Connection connection : connections) {
//            dist[connection.getStartingNodeID()][connection.getDestinationNodeID()] = (int) connection.getWeight();
//        }
//
//        // Aplicar el algoritmo de Floyd-Warshall
//        for (int k = 0; k < size; k++) {
//            for (int i = 0; i < size; i++) {
//                for (int j = 0; j < size; j++) {
//                    if (dist[i][j] > dist[i][k] + dist[k][j]) {
//                        dist[i][j] = dist[i][k] + dist[k][j];
//                    }
//                }
//            }
//        }
//
//        // Aquí puedes hacer algo con la matriz dist, como almacenarla o imprimir los resultados.
//    }
//}


//clase con la que trabaja:
/*
@Getter
@Setter
public class Node implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private static final int MAX_CONNECTIONS = 4;
    private Connection[] connections;
    private double[] location;

    public Node() {
        connections = new Connection[MAX_CONNECTIONS];
        location = new double[2];
    }

    public Node(double[] point) {
        connections = new Connection[MAX_CONNECTIONS];
        location = point;
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
                return; // Agregué un return para salir del método después de borrar
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

    public Connection getConnection(Directions direction) {
        for (Connection connection : connections) {
            if (connection != null && connection.getDirection() == direction) {
                return connection;
            }
        }
        return null;
    }

    public Connection getConnection(double[] position) {
        for (Connection connection : connections) {
            if (connection != null && Arrays.equals(connection.getTargetNode().location, position)) {
                return connection;
            }
        }
        return null;
    }

    //TODO
    public void ordenar() {
        Connection[] ordenado = new Connection[MAX_CONNECTIONS];

        for (Connection connection : connections) {
            if (connection != null) {
                switch (connection.getDirection()) {
                    case IZQUIERDA -> ordenado[0] = connection;
                    case ADELANTE -> ordenado[1] = connection;
                    case DERECHA -> ordenado[2] = connection;
                    case CONTRARIO -> ordenado[3] = connection;
                }
            }
        }
        connections = ordenado;
    }

    public boolean isConnectionsEmpty(){
        for (Connection connection : connections) {
            if (connection != null) {
                return false;
            }
        }
        return true;
    }
}

 */
/*

@Getter
@Setter
public class Connection implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private static final Map<String, Integer> TRAFFIC_MULTIPLIER = Map.of(
            "normal", 1,
            "moderado", 2,
            "lento", 3
    );

    private Node targetNode; // Nodo de destino
    private int weight; // Peso de la ruta (longitud/costo)
    private boolean isBlocked; // Indica si la ruta está bloqueada
    private String trafficCondition; // Estado de tráfico ("normal", "moderado", "lento")
    private Directions direction; // Dirección de la conexión

    public Connection(Node targetNode, int weight, Directions direction) {
        this.targetNode = targetNode;
        this.weight = weight;
        this.isBlocked = false;
        this.trafficCondition = "normal";
        this.direction = direction;
    }

    public void blockRoute() {
        isBlocked = true;
    }

    public void unblockRoute() {
        isBlocked = false;
    }

    public boolean canAccess() {
        return !isBlocked; // Acceso permitido solo si no está bloqueada
    }

    public int getEffectiveWeight() {
        return weight * TRAFFIC_MULTIPLIER.getOrDefault(trafficCondition, 1);
    }

    public double calculateTravelTime() {
        return getEffectiveWeight() / 10.0; // Ajustar según sea necesario
    }
}


*/