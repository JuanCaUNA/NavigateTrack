package org.una.navigatetrack.roads;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

public class Node implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Getter
    @Setter
    private int ID;

    @Getter
    @Setter
    private double[] location;

    private Map<Directions, Connection> connections;


    public Node() {
        connections = new EnumMap<>(Directions.class);
        location = new double[2];
    }

    public Node(double[] point) {
        connections = new EnumMap<>(Directions.class);
        location = point;
    }

    // Add a connection to another node
    public void addConnection(Node targetNode, Directions direction) {
        Connection connection = connections.get(direction);
        if (connection != null) {
            connection.setStartingNodeID(ID);
            connection.setDestinationNodeID(targetNode.getID());
            connection.setWeight(calculateDistance(targetNode));
        } else {
            double weight = calculateDistance(targetNode);
            connections.put(direction, new Connection(ID, targetNode.getID(), (int) weight, direction));
        }
    }

    // Calculate distance to another node
    public int calculateDistance(Node other) {
        return (int) Math.sqrt(Math.pow(location[0] - other.location[0], 2) + Math.pow(location[1] - other.location[1], 2));
    }

    // Delete connection based on direction
    public void deleteConnection(Directions direction) {
        connections.remove(direction);
    }

    // Get target node based on direction
    public Node getTargetNode(Directions direction) {
        Connection connection = connections.get(direction);
        return connection != null ? connection.getDestinationNode() : null;
    }

    // Get connections excluding a specific node
    public List<Connection> getAllConnections() {
        return new ArrayList<>(connections.values());
    }

    public List<Connection> getConnectionsInOrderByWeight(Node entryNode) {
        return connections.values().stream()
                .filter(conn -> conn.getDestinationNodeID() != entryNode.getID() && !conn.isBlocked())
                .sorted(Comparator.comparingDouble(Connection::getEffectiveWeight)) // Ordenar por peso final
                .collect(Collectors.toList());
    }

    public List<Connection> getConnectionsInOrderByWeight() {
        return connections.values().stream()
                .filter(conn -> !conn.isBlocked())
                .sorted(Comparator.comparingDouble(Connection::getEffectiveWeight)) // Ordenar por peso final
                .collect(Collectors.toList());
    }

    // Get connection based on direction
    public Connection getConnection(Directions direction) {
        return connections.get(direction);
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

    // Check if connected to a specific node
    public boolean isConnectedToNode(Node node) {
        return connections.values().stream().anyMatch(conn -> conn.getDestinationNodeID() == node.getID());
    }

    public Directions getDirConnectedToNode(Node node) {
        return getConnectionInNode(node.getID()).getDirection();
    }

    // Get connection in node
    public Connection getConnectionInNode(int nodeID) {
        return connections.values().stream()
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

    public void setConnections( Map<Directions, Connection> connections){
        this.connections = connections;
    }

    // Search for a node by ID
    public Optional<Node> searchAndGetNode(int nodeID) {
        return ListNodes.findById(nodeID);
    }

    // Get node by index
    public Node getIndexAt(int ID) {
        return ListNodes.getListNodes().get(ID);
    }
}


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