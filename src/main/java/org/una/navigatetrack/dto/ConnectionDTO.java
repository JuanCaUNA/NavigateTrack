package org.una.navigatetrack.dto;

import lombok.Getter;
import lombok.Setter;
import org.una.navigatetrack.roads.Connection;
import org.una.navigatetrack.roads.Directions;
import org.una.navigatetrack.roads.ListNodes;
import org.una.navigatetrack.roads.Node;

import java.io.Serial;
import java.io.Serializable;
import java.util.Optional;

@Getter
@Setter
public class ConnectionDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private double[] targetNodeId; // Identificador del nodo de destino
    private int weight; // Peso de la ruta (longitud/costo)
    private boolean isBlocked; // Indica si la ruta está bloqueada
    private String trafficCondition; // Estado de tráfico
    private Directions direction; // Dirección de la conexión

    // Constructor para convertir de Connection a ConnectionDTO
    public ConnectionDTO(Connection connection) {
        if (connection != null) {
            this.targetNodeId = connection.getDestinationNode() != null ? connection.getDestinationNode().getLocation() : null;
            this.weight = (int) connection.getWeight();
            this.isBlocked = connection.isBlocked();
            this.trafficCondition = connection.getTrafficCondition();
            this.direction = connection.getDirection();
        }
    }

    public Connection toConnection(int partida) {
        Optional<Integer> i = ListNodesDTO.findKeyByID(targetNodeId);
        Connection connection = new Connection(partida,i.get(), this.weight, this.direction);
        return connection;
    }

}

//@Override
//public String toString() {
//    return "ConnectionDTO{" +
//            "targetNodeId=" + Arrays.toString(targetNodeId) +
//            ", weight=" + weight +
//            ", isBlocked=" + isBlocked +
//            ", trafficCondition='" + trafficCondition + '\'' +
//            ", direction=" + direction + // Asegúrate de que la clase Directions también tenga un método toString
//            '}';
//}
