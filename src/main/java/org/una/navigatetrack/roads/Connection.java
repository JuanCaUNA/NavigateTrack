package org.una.navigatetrack.roads;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;
import java.util.Optional;

@Getter
@Setter
public class Connection implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Directions direction; //identification

    private static final Map<String, Double> TRAFFIC_MULTIPLIER = Map.of(
            "normal", 1.0,
            "moderado", 0.75,
            "lento", 0.50
    );

    //referencias de nodos
    private int startNodeID;
    private int targetNodeID;

    private int weight; // Peso en base a distancia entre nodos

    //estados de ruta
    private boolean isBlocked; // Indica si la ruta está bloqueada
    private String trafficCondition; // Estado de tráfico ("normal", "moderado", "lento")


    public Connection(Node targetNode, int weight, Directions direction) {
        this.targetNodeID = targetNode.getID();
        this.weight = weight;
        this.isBlocked = false;
        this.trafficCondition = "normal";
        this.direction = direction;
    }

    public Connection() {

    }

    // manejo de estados
    public void blockRoute() {        isBlocked = true;    }

    public void unblockRoute() {        isBlocked = false;    }

    public void refreshWeight() {
        weight -= getEffectiveWeight();
    }

    //bool
    public boolean canAccess() {        return !isBlocked;    }

    //Gets
    public Node getTargetNode() {        return getIndexAt(targetNodeID);    }

    public Node getStartNode() {        return getIndexAt(startNodeID);    }

    public int getEffectiveWeight() {        return weight * TRAFFIC_MULTIPLIER.getOrDefault(trafficCondition, 1);    }

    public double calculateTravelTime() {        return getEffectiveWeight() / 10.0;    }


    //sets
    public void setTargetNode(Node targetNode) {        targetNodeID = targetNode.getID();    }

    public void setStartNode(Node startNode) {        this.startNodeID = startNode.getID();    }

    //gets for ID
    public Optional<Node> searchAndGetNode(int nodeID) {        return ListNodes.findById(nodeID);    }

    public Node getIndexAt(int ID) {        return ListNodes.getListNodes().get(ID);    }
}
