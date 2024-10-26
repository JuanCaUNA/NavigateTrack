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
    private Directions direction;
    private int ID;

    private double accumulateWeight;

    private static final Map<String, Double> TRAFFIC_MULTIPLIER = Map.of(
            "normal", 1.0,
            "moderado", 1.25,
            "lento", 1.65
    );

    //referencias de nodos
    private int startingNodeID;
    private int destinationNodeID;

    private double weight; // Peso en base a distancia entre nodos

    //estados de ruta
    private boolean isBlocked; // Indica si la ruta está bloqueada
    private String trafficCondition; // Estado de tráfico ("normal", "moderado", "lento")


    public Connection(int startingNodeID, int destinationNodeID, int weight, Directions direction) {
        this.startingNodeID = startingNodeID;
        this.destinationNodeID = destinationNodeID;
        this.weight = weight;
        this.isBlocked = false;
        this.trafficCondition = "normal";
        this.direction = direction;

        accumulateWeight = 0;

        ID = ListConnections.getID();
        ListConnections.incrementID();
    }


    public Connection(int id, int weight, Directions direction) {
        this.destinationNodeID = id;
        this.weight = weight;
        this.isBlocked = false;
        this.trafficCondition = "normal";
        this.direction = direction;

        accumulateWeight = 0;

        ID = ListConnections.getID();
        ListConnections.incrementID();
    }

    public Connection() {    }

    // manejo de estados
    public void blockRoute() {        isBlocked = true;    }

    public void unblockRoute() {        isBlocked = false;    }

    private void refreshWeight() {       weight -= getIncrement();    }

    public void recalculateStartNode(){
        double[]  init = getStartingNode().getLocation();
        double[]  end = getDestinationNode().getLocation();

        calcularIncremento(init[0], init[1], end[0], end[1], getIncrement() );

        refreshWeight();
    }

    //bool
    public boolean canAccess() {        return !isBlocked;    }

    //Gets
    public Node getDestinationNode() {        return getIndexAt(destinationNodeID);    }

    public Node getStartingNode() {        return getIndexAt(startingNodeID);    }

    public double getEffectiveWeight() {        return (weight * TRAFFIC_MULTIPLIER.get(trafficCondition));  }

    public double getIncrement(){        return  (2-TRAFFIC_MULTIPLIER.get(trafficCondition));    }

    //sets
    public void setDestinationNode(Node targetNode) {        destinationNodeID = targetNode.getID();    }

    public void setStartingNode(Node startNode) {        this.startingNodeID = startNode.getID();    }

    //gets for ID
    public Optional<Node> searchAndGetNode(int nodeID) {        return ListNodes.findById(nodeID);    }

    public Node getIndexAt(int ID) {        return ListNodes.getListNodes().get(ID);    }

    public void calcularIncremento(double x1, double y1, double x2, double y2, double incremento) {
        double distanciaX = x2 - x1;
        double distanciaY = y2 - y1;
//        double distanciaTotal = Math.sqrt(distanciaX * distanciaX + distanciaY * distanciaY);

        double factorX = 0;
        double factorY = 0;

        if (weight > 0) {
            factorX = (distanciaX / weight) + incremento;
            factorY = (distanciaY / weight) + incremento;
        }

        // Calcular nuevas coordenadas
        double nuevaX = x1 + factorX;
        double nuevaY = y1 + factorY;

        getStartingNode().setLocation( new double[]{nuevaX, nuevaY});
    }
}
