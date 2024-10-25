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

    private static final Map<String, Integer> TRAFFIC_MULTIPLIER = Map.of(
            "normal", 1,
            "moderado", 2,
            "lento", 3
    );

    private int startNodeID;
    private int targetNodeID; // Nodo de destino

    private int weight; // Peso de la ruta (longitud/costo)
    private boolean isBlocked; // Indica si la ruta está bloqueada
    private String trafficCondition; // Estado de tráfico ("normal", "moderado", "lento")
    private Directions direction; // Dirección de la conexión

    public Connection(Node targetNode, int weight, Directions direction) {
        this.targetNodeID = targetNode.getID();
        this.weight = weight;
        this.isBlocked = false;
        this.trafficCondition = "normal";
        this.direction = direction;
    }

    public Connection() {

    }

    public void blockRoute() {
        isBlocked = true;
    }

    public void unblockRoute() {
        isBlocked = false;
    }

    public boolean canAccess() {
        return !isBlocked;
    }

    public int getEffectiveWeight() {
        return weight * TRAFFIC_MULTIPLIER.getOrDefault(trafficCondition, 1);
    }

    public double calculateTravelTime() {
        return getEffectiveWeight() / 10.0;
    }

    public void setTargetNode(Node targetNode) {
        targetNodeID = targetNode.getID();
    }

    public void setStartNode(Node startNode) {
        this.startNodeID = startNode.getID();
    }

    public Node getStartNode() {
        return getIndexAt(startNodeID);
    }

    public Node getTargetNode() {
        return getIndexAt(targetNodeID);
    }

    public Optional<Node> searchAndGetNode(int nodeID) {
        return ListNodes.findById(nodeID);
    }

    public Node getIndexAt(int ID) {
        return ListNodes.getListNodes().get(ID);
    }
}
