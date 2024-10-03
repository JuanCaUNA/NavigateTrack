package org.una.navigatetrack.roads;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
public class Node implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Connection[] connections; // Cambiado a un array fijo de conexiones
    private int[] location;
    private static final int MAX_CONNECTIONS = 4; // Máximo de conexiones

    public Node() {
        connections = new Connection[MAX_CONNECTIONS];
        location = new int[2];
    }

    public void addConnection(Node targetNode, Directions direction, boolean isTwoWay) {
        for (int i = 0; i < connections.length; i++) {
            if (connections[i] == null) {
                double weight = calculateDistance(targetNode);
                Connection connection = new Connection(targetNode, (int) weight, direction);
                connections[i] = connection;

                if (isTwoWay) {
                    targetNode.addConnection(this, direction.getOpposite(), false); // Agrega conexión inversa
                }
                return; // Salir una vez que se ha añadido la conexión
            }
        }
        System.out.println("No se puede añadir más conexiones a este nodo.");
    }

    public boolean canAddConnection() {
        for (Connection conn : connections) {
            if (conn == null) return true;
        }
        return false;
    }

    public double calculateDistance(Node other) {
        return Math.sqrt(Math.pow(location[0] - other.location[0], 2) + Math.pow(location[1] - other.location[1], 2));
    }
}