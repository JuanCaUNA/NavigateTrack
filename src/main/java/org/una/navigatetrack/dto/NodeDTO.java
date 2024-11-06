package org.una.navigatetrack.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
@Setter
public class NodeDTO {
    //dato nuevo
    private int ID;

    //datos almacenados en el json
    private List<ConnectionDTO> connectionsDTO = new ArrayList<>();
    private double[] location;

    public NodeDTO() {
    }

    @Override
    public String toString() {
        return "NodeDTO{" +
                ", ID=" + ID +
                ", location=" + Arrays.toString(location) +
                ", connectionsDTO=" + connectionsDTO +
                '}';
    }

}
//public NodeDTO(Node node) {
//        this.location = node.getLocation();
//        this.id = location;
//        //this.id = node.getID(); // Asignar el ID del nodo
//
//        if (node.getAllConnections() != null) { // Obtener conexiones
//            for (int i = 0; i < Math.min(node.getAllConnections().size(), MAX_CONNECTIONS); i++) {
//                Connection connection = node.getAllConnections().get(i);
//                if (connection != null) {
//                    connectionsDTO.add(new ConnectionDTO(connection)); // Convertir y agregar la conexión
//                }
//            }
//        }
//    }
//
//    public Node toNode(int ids) {
//        Node node = new Node(location); // Crear nuevo nodo con la ubicación
//        node.setID(ids); // Asignar el ID
//
//        Map<Directions, Connection> connectionsx = new HashMap<>();
//
//        for (ConnectionDTO connection : connectionsDTO) {
//            if (connection != null) {
//                connectionsx.put(connection.getDirection(), connection.toConnection(ids));
//            }
//        }
//        node.setConnections(connectionsx);
//        return node; // Retornar el nodo creado
//    }
//
//    public int getsID() {
//
//        return ListConnections.getIDNodes();
//    }
//
//    @Override
//    public String toString() {
//        return "NodeDTO{" +
//                "MAX_CONNECTIONS=" + MAX_CONNECTIONS +
//                ", connectionsDTO=" + connectionsDTO +
//                ", location=" + Arrays.toString(location) +
//                ", id=" + Arrays.toString(id) +
//                '}';
//    }
