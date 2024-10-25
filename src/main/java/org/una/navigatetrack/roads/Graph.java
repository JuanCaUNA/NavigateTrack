//package org.una.navigatetrack.roads;
//
//import java.util.*;
//
//// introduccion esta clase se encarga de los tipos de recorridos y devolver la lista de resultado. ayudaria tener un indide para saber por cual nodo esta durante el recorrido
//// se va a basar en el peso de los nodos
//public class ?? { que nombre uso
//    private final List<Node> nodes;
//
//    public Graph() {
//        nodes = new ArrayList<>();
//    }
//
//    public List<Node> getNodes() {
//        return nodes;
//    }
//
//    public Map<Node, Double> dijkstra(Node start) {    }
//    floydWarshall() {    }
//}
////clase con la que trabaja:
///*
//@Getter
//@Setter
//public class Node implements Serializable {
//    @Serial
//    private static final long serialVersionUID = 1L;
//
//    private static final int MAX_CONNECTIONS = 4;
//    private Connection[] connections;
//    private double[] location;
//
//    public Node() {
//        connections = new Connection[MAX_CONNECTIONS];
//        location = new double[2];
//    }
//
//    public Node(double[] point) {
//        connections = new Connection[MAX_CONNECTIONS];
//        location = point;
//    }
//
//    public void addConnection(Node targetNode, Directions direction) {
//        for (Connection value : connections) {
//            if (value != null && value.getDirection() == direction) {
//                value.setTargetNode(targetNode);
//                value.setWeight(calculateDistance(targetNode));
//                return;
//            }
//        }
//
//        for (int i = 0; i < connections.length; i++) {
//            if (connections[i] == null) {
//                double weight = calculateDistance(targetNode);
//                Connection connection = new Connection(targetNode, (int) weight, direction);
//                connections[i] = connection;
//                return;
//            }
//        }
//    }
//
//    public int calculateDistance(Node other) {
//        return (int) Math.sqrt(Math.pow(location[0] - other.location[0], 2) + Math.pow(location[1] - other.location[1], 2));
//    }
//
//    public void deleteConnection(Directions direction) {
//        for (int i = 0; i < connections.length; i++) {
//            if (connections[i] != null && connections[i].getDirection() == direction) {
//                connections[i] = null;
//                return; // Agregué un return para salir del método después de borrar
//            }
//        }
//    }
//
//    public Node getTargetNode(Directions direction) {
//        for (Connection connection : connections) {
//            if (connection != null && connection.getDirection() == direction) {
//                return connection.getTargetNode();
//            }
//        }
//        return null;
//    }
//
//    public Connection[] getConnections(Node startNode) {
//        return Arrays.stream(connections)
//                .filter(conn -> conn != null && conn.getTargetNode() != startNode)
//                .toArray(Connection[]::new);
//    }
//
//    public Connection getConnection(Directions direction) {
//        for (Connection connection : connections) {
//            if (connection != null && connection.getDirection() == direction) {
//                return connection;
//            }
//        }
//        return null;
//    }
//
//    public Connection getConnection(double[] position) {
//        for (Connection connection : connections) {
//            if (connection != null && Arrays.equals(connection.getTargetNode().location, position)) {
//                return connection;
//            }
//        }
//        return null;
//    }
//
//    //TODO
//    public void ordenar() {
//        Connection[] ordenado = new Connection[MAX_CONNECTIONS];
//
//        for (Connection connection : connections) {
//            if (connection != null) {
//                switch (connection.getDirection()) {
//                    case IZQUIERDA -> ordenado[0] = connection;
//                    case ADELANTE -> ordenado[1] = connection;
//                    case DERECHA -> ordenado[2] = connection;
//                    case CONTRARIO -> ordenado[3] = connection;
//                }
//            }
//        }
//        connections = ordenado;
//    }
//
//    public boolean isConnectionsEmpty(){
//        for (Connection connection : connections) {
//            if (connection != null) {
//                return false;
//            }
//        }
//        return true;
//    }
//}
//
// */
///*
//
//@Getter
//@Setter
//public class Connection implements Serializable {
//    @Serial
//    private static final long serialVersionUID = 1L;
//
//    private static final Map<String, Integer> TRAFFIC_MULTIPLIER = Map.of(
//            "normal", 1,
//            "moderado", 2,
//            "lento", 3
//    );
//
//    private Node targetNode; // Nodo de destino
//    private int weight; // Peso de la ruta (longitud/costo)
//    private boolean isBlocked; // Indica si la ruta está bloqueada
//    private String trafficCondition; // Estado de tráfico ("normal", "moderado", "lento")
//    private Directions direction; // Dirección de la conexión
//
//    public Connection(Node targetNode, int weight, Directions direction) {
//        this.targetNode = targetNode;
//        this.weight = weight;
//        this.isBlocked = false;
//        this.trafficCondition = "normal";
//        this.direction = direction;
//    }
//
//    public void blockRoute() {
//        isBlocked = true;
//    }
//
//    public void unblockRoute() {
//        isBlocked = false;
//    }
//
//    public boolean canAccess() {
//        return !isBlocked; // Acceso permitido solo si no está bloqueada
//    }
//
//    public int getEffectiveWeight() {
//        return weight * TRAFFIC_MULTIPLIER.getOrDefault(trafficCondition, 1);
//    }
//
//    public double calculateTravelTime() {
//        return getEffectiveWeight() / 10.0; // Ajustar según sea necesario
//    }
//}
//
//
//*/