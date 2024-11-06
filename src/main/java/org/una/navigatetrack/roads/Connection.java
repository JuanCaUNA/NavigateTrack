package org.una.navigatetrack.roads;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import org.una.navigatetrack.list.ListConnections;
import org.una.navigatetrack.list.ListNodes;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;
import java.util.Optional;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)  // Ignora los campos que no estén en la clase
@JsonInclude(JsonInclude.Include.NON_NULL)  // Excluye los campos nulos del JSON
public class Connection {
    private int ID;

    // Referencias de nodos
    private int startingNodeID;
    private int destinationNodeID;

    private double weight; // Peso en base a distancia entre nodos

    private static final Map<String, Double> TRAFFIC_MULTIPLIER = Map.of("normal", 1.0, "moderado", 1.25, "lento", 1.65);

    // Estados de ruta
    private boolean isBlocked; // Indica si la ruta está bloqueada
    private String trafficCondition; // Estado de tráfico ("normal", "moderado", "lento")

    private Directions direction;
    private double accumulateWeight;  // Este campo se deserializa correctamente desde el JSON

    // Campos no presentes en el JSON, pero calculados
    private double effectiveWeight;  // Este campo no es necesario deserializarlo, ya que se calcula

    private double increment;  // Similar al campo `effectiveWeight`, este se calcula

    public Connection() {
        init();
    }

    public Connection(int startingNodeID, int destinationNodeID, int weight) {
        init();
        this.startingNodeID = startingNodeID;
        this.destinationNodeID = destinationNodeID;
        this.weight = weight;
    }

    public Connection(int id, int weight) {
        init();
        this.destinationNodeID = id;
        this.weight = weight;
    }

    public void init() {
        this.isBlocked = false;
        this.trafficCondition = "normal";
        this.ID = ListConnections.getID();

        this.startingNodeID = 0;
        this.destinationNodeID = 0;
        this.weight = 1.0;
        this.direction = null;
    }

    // Manejo de estados
    public void blockRoute() {
        isBlocked = true;
    }

    public void unblockRoute() {
        isBlocked = false;
    }

    private void refreshWeight() {
        weight -= getIncrement();
    }

    public void recalculateStartNode() {
        double[] init = getStartingNode().getLocation();
        double[] end = getDestinationNode().getLocation();

        calcularIncremento(init[0], init[1], end[0], end[1], getIncrement());

        refreshWeight();
    }

    // bool
    public boolean canAccess() {
        return !isBlocked;
    }

    // Gets
    public Node getDestinationNode() {
        return getIndexAt(destinationNodeID);
    }

    public Node getStartingNode() {
        return getIndexAt(startingNodeID);
    }

    public double getEffectiveWeight() {
        return (weight * TRAFFIC_MULTIPLIER.get(trafficCondition));
    }

    public double getIncrement() {
        return (2 - TRAFFIC_MULTIPLIER.get(trafficCondition));
    }

    // Sets
    public void setDestinationNode(Node targetNode) {
        destinationNodeID = targetNode.getID();
    }

    public void setStartingNode(Node startNode) {
        this.startingNodeID = startNode.getID();
    }

    // Gets for ID
    public Optional<Node> searchAndGetNode(int nodeID) {
        return ListNodes.findById(nodeID);
    }

    public Node getIndexAt(int ID) {
        return ListNodes.getNodeByID(ID);
    }

    public void calcularIncremento(double x1, double y1, double x2, double y2, double incremento) {
        double distanciaX = x2 - x1;
        double distanciaY = y2 - y1;
        double distanciaTotal = Math.sqrt(Math.pow(distanciaX, 2) + Math.pow(distanciaY, 2));

        double factorX = 0;
        double factorY = 0;

        if (weight > 0) {
            factorX = (distanciaX / weight) + incremento;
            factorY = (distanciaY / weight) + incremento;
        }

        // Calcular nuevas coordenadas
        double nuevaX = x1 + factorX;
        double nuevaY = y1 + factorY;

        getStartingNode().setLocation(new double[]{nuevaX, nuevaY});
    }
}
