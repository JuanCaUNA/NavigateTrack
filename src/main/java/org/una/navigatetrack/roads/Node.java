package org.una.navigatetrack.roads;

import lombok.Getter;
import lombok.Setter;
import org.una.navigatetrack.list.ListNodes;

import java.io.Serial;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

@Setter
@Getter
public class Node {

    private int ID;

    private double[] location;
    private Map<Directions, Connection> connectionsMap;

    public Node() {
        connectionsMap = new EnumMap<>(Directions.class);
        location = new double[2];
    }

    public Node(double[] point) {
        connectionsMap = new EnumMap<>(Directions.class);
        location = point;
        //ID = ListNodes.getNextId(); TODO
    }

    // Add a connection to another node
    public void addConnection(Node targetNode, Directions direction) {
        // Si no existe una conexión en la dirección dada, crea una nueva
        Connection connection = new Connection(ID, targetNode.getID(), calculateDistance(targetNode));
        connection.setDirection(direction);
        // Se agrega o reemplaza la conexión en el mapa
        connectionsMap.put(direction, connection);
    }

    // Calculate distance to another node
    public int calculateDistance(Node other) {
        return (int) Math.sqrt(Math.pow(location[0] - other.location[0], 2) + Math.pow(location[1] - other.location[1], 2));
    }

    // Delete connection based on direction
    public void deleteConnection(Directions direction) {
        connectionsMap.remove(direction);
    }

    // Get connections excluding a specific node
    public List<Connection> getAllConnections() {
        return new ArrayList<>(connectionsMap.values());
    }

    public List<Connection> getConnectionsInOrderByWeight(Node entryNode) {
        return connectionsMap.values().stream()
                .filter(conn -> conn.getDestinationNodeID() != entryNode.getID() && !conn.isBlocked())
                .sorted(Comparator.comparingDouble(Connection::getEffectiveWeight)) // Ordenar por peso final
                .collect(Collectors.toList());
    }

    public List<Connection> getConnectionsInOrderByWeight() {
        return connectionsMap.values().stream()
                .filter(conn -> !conn.isBlocked())
                .sorted(Comparator.comparingDouble(Connection::getEffectiveWeight)) // Ordenar por peso final
                .collect(Collectors.toList());
    }

    // Get connection based on direction
    public Connection getConnection(Directions direction) {
        return connectionsMap.get(direction);
    }

    // Check if connected to a specific node
    public boolean isConnectedToNode(Node node) {
        return connectionsMap.values().stream().noneMatch(conn -> conn.getDestinationNodeID() == node.getID());
    }

    public Directions getDirConnectedToNode(Node node) {
        return connectionsMap.entrySet().stream()  // Itera sobre las entradas (key-value) del mapa
                .filter(entry -> entry.getValue().getDestinationNodeID() == node.getID())  // Filtra por el ID del nodo
                .map(Map.Entry::getKey)  // Mapea a la clave (Directions)
                .findFirst()  // Devuelve el primer resultado encontrado
                .orElse(null);  // Si no se encuentra ninguna, devuelve null
    }


    // Get connection in node
    public Connection getConnectionInNode(int nodeID) {
        return connectionsMap.values().stream()
                .filter(conn -> conn.getDestinationNodeID() == nodeID)
                .findFirst()
                .orElse(null);
    }

    // Change connection target
    public void changeConnectionIn(Node inNode, Node toNode) {
        Connection connection = getConnectionInNode(inNode.getID());
        if (connection != null) {
            connection.setDestinationNodeID(toNode.getID());
        }
    }

    // Search for a node by ID
    public Optional<Node> searchAndGetNode(int nodeID) {
        return ListNodes.findById(nodeID);
    }

    // Get node by index
    public Node getIndexAt(int nodeID) {
        return ListNodes.getNodeByID(nodeID);
    }

    public void addConnection(int targetNodeId, Directions direction, double weight) {
        Connection connection = new Connection(ID, targetNodeId, (int) weight);
        connection.setDirection(direction);
        connectionsMap.put(direction, connection);
    }
}
/*
    // Get target node based on direction
    public Node getTargetNode(Directions direction) {
        Connection connection = connections.get(direction);
        return connection != null ? connection.getDestinationNode() : null;
    }

        // Get connection based on target position
    public Connection getConnection(double[] position) {
        return connections.values().stream()
                .filter(conn -> Arrays.equals(conn.getDestinationNode().location, position))
                .findFirst()
                .orElse(null);
    }

        // Check if there are no connections
    public boolean isConnectionsEmpty() {
        return connections.isEmpty();
    }

 */

//    private double[] getLocation( int[] point){
//        return new double[]{point[0], point[1]};
//    }
//    private int[] getLocation( double[] point){
//        return new int[]{(int) point[0], (int) point[1]};
//    }
//    public double[] getLocation(){
//        return new double[]{location[0], location[1]};
//    }
//
//    public double[] getLocatio(){
//        return new double[]{location[0], location[1]};
//    }