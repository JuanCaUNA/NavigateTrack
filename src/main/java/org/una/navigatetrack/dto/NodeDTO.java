package org.una.navigatetrack.dto;

import lombok.Getter;
import lombok.Setter;
import org.una.navigatetrack.roads.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.*;

@Getter
@Setter
public class NodeDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private static final int MAX_CONNECTIONS = 4;
    private List<ConnectionDTO> connectionsDTO = new ArrayList<>();
    private double[] location;
    private double[] id; // ID como un array

    public NodeDTO() {
    }

    public NodeDTO(Node node) {
        this.location = node.getLocation();
        this.id = location;
        //this.id = node.getID(); // Asignar el ID del nodo

        if (node.getAllConnections() != null) { // Obtener conexiones
            for (int i = 0; i < Math.min(node.getAllConnections().size(), MAX_CONNECTIONS); i++) {
                Connection connection = node.getAllConnections().get(i);
                if (connection != null) {
                    connectionsDTO.add(new ConnectionDTO(connection)); // Convertir y agregar la conexión
                }
            }
        }
    }

    public Node toNode(int ids) {
        Node node = new Node(location); // Crear nuevo nodo con la ubicación
        node.setID(ids); // Asignar el ID

        Map<Directions, Connection> connectionsx = new HashMap<>();

        for (ConnectionDTO connection : connectionsDTO) {
            if (connection != null) {
                connectionsx.put(connection.getDirection(),connection.toConnection(ids));
            }
        }
        node.setConnections( connectionsx);
        return node; // Retornar el nodo creado
    }

    public int getsID(){

        return ListConnections.getIDNodes();
    }

    @Override
    public String toString() {
        return "NodeDTO{" +
                "MAX_CONNECTIONS=" + MAX_CONNECTIONS +
                ", connectionsDTO=" + connectionsDTO +
                ", location=" + Arrays.toString(location) +
                ", id=" + Arrays.toString(id) +
                '}';
    }

}
