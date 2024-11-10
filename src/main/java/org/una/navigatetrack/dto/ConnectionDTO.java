package org.una.navigatetrack.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.una.navigatetrack.roads.Directions;

import java.util.Arrays;

@Getter
@Setter
public class ConnectionDTO {
    //datos nuevos
    private int sourceNodeID;// = ID del nodo donde esta contenido

    private int targetNodeIDNew;// = ID nodo del obejctivo se calculo al averiguar donde double[] targetNodeId  sea igual al location de un nodo

    //datos almacenados
    private Directions direction; // Dirección de la conexión
    private int weight; // Peso de la ruta (longitud/costo)

    @JsonProperty("targetNodeId")//targetNodeIdOld
    private double[] targetNodeId; // Identificador del nodo de destino
//    private double[] targetNodeIdOld;

    public ConnectionDTO() {
    }

    @Override
    public String toString() {
        return "ConnectionDTO{" +
                "targetNodeId=" + Arrays.toString(targetNodeId) +
                ", weight=" + weight +
                ", direction=" + direction +
                '}';
    }

    public double[] getTargetNodeIdOld() {
        return targetNodeId;
    }

}


// Constructor para convertir de Connection a ConnectionDTO
//    public ConnectionDTO(Connection connection) {
//        if (connection != null) {
//            this.targetNodeId = connection.getDestinationNode() != null ? connection.getDestinationNode().getLocation() : null;
//            this.weight = (int) connection.getWeight();
//            this.isBlocked = connection.isBlocked();
//            this.trafficCondition = connection.getTrafficCondition();
//            this.direction = connection.getDirection();
//        }
//    }
//
//    public Connection toConnection(int partida) {
//        Optional<Integer> i = ListNodesDTO.findKeyByID(targetNodeId);
//        return new Connection(partida, i.get(), this.weight, this.direction);
//    }
//
//    @Override
//    public String toString() {
//        return "ConnectionDTO{" +
//                "targetNodeId=" + Arrays.toString(targetNodeId) +
//                ", weight=" + weight +
//                ", isBlocked=" + isBlocked +
//                ", trafficCondition='" + trafficCondition + '\'' +
//                ", direction=" + direction + // Asegúrate de que la clase Directions también tenga un método toString
//                '}';
//    }