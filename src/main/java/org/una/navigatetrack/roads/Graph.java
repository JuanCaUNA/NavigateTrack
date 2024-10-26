package org.una.navigatetrack.roads;

import lombok.Getter;
import lombok.Setter;

import java.util.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

@Getter
public class Graph {

    @Setter
    private Node start, end;
    private final List<Connection> connections; // Lista de conexiones

    public Graph() {
        connections = new ArrayList<>();
    }

    public void addConnection(Connection connection) {
        connections.add(connection);
    }

    // Método para obtener la lista de conexiones a partir del nodo de inicio
    public List<Connection> getConnectionsFromStart() {
        List<Connection> path = new ArrayList<>();
        Map<Node, Integer> distances = dijkstra(start);

        Node currentNode = start;

        // Continuar hasta que no haya más conexiones
        while (currentNode != null) {
            List<Connection> currentConnections = currentNode.getConnections();
            currentConnections.sort(Comparator.comparingInt(Connection::getEffectiveWeight)); // Ordenar por peso

            if (currentConnections.isEmpty()) {
                break; // Si no hay más conexiones, salimos del bucle
            }

            for (Connection connection : currentConnections) {
                Node nextNode = connection.getTargetNode();
                if (distances.get(nextNode) < Integer.MAX_VALUE) { // Solo considerar nodos accesibles
                    path.add(connection); // Añadir la conexión al camino
                    currentNode = nextNode; // Cambiar al siguiente nodo
                    break; // Romper el bucle para procesar el siguiente nodo
                }
            }
        }

        return path; // Retornar el camino encontrado
    }

    public Map<Node, Integer> dijkstra(Node source) {
        Map<Node, Integer> distances = new HashMap<>();
        PriorityQueue<Node> priorityQueue = new PriorityQueue<>(Comparator.comparingInt(distances::get));

        for (Connection connection : connections) {
            distances.put(connection.getStartNode(), Integer.MAX_VALUE);
        }
        distances.put(source, 0);
        priorityQueue.add(source);

        while (!priorityQueue.isEmpty()) {
            Node currentNode = priorityQueue.poll();

            for (Connection connection : currentNode.getAllConnections()) {
                Node neighbor = connection.getTargetNode();
                int newDist = distances.get(currentNode) + (int) connection.getWeight();

                if (newDist < distances.get(neighbor)) {
                    distances.put(neighbor, newDist);
                    priorityQueue.add(neighbor);
                }
            }
        }
        return distances;
    }

    // Implementación del algoritmo de Floyd-Warshall
    public void floydWarshall() {
        int size = connections.size();
        int[][] dist = new int[size][size];

        // Inicializar la matriz de distancias
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (i == j) {
                    dist[i][j] = 0;
                } else {
                    dist[i][j] = Integer.MAX_VALUE; // Representa la ausencia de conexión
                }
            }
        }

        // Rellenar la matriz con los pesos de las conexiones
        for (Connection connection : connections) {
            dist[connection.getStartNodeID()][connection.getTargetNodeID()] = (int) connection.getWeight();
        }

        // Aplicar el algoritmo de Floyd-Warshall
        for (int k = 0; k < size; k++) {
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    if (dist[i][j] > dist[i][k] + dist[k][j]) {
                        dist[i][j] = dist[i][k] + dist[k][j];
                    }
                }
            }
        }

        // Aquí puedes hacer algo con la matriz dist, como almacenarla o imprimir los resultados.
    }
}


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