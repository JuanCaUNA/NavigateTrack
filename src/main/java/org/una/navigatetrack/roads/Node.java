package org.una.navigatetrack.roads;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.Arrays;

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