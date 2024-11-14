package org.una.navigatetrack.roads;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javafx.scene.shape.Line;
import lombok.Getter;
import lombok.Setter;
import org.una.navigatetrack.list.ListConnections;
import org.una.navigatetrack.list.ListNodes;

import java.util.Map;
import java.util.Optional;

@Getter
@Setter
public class Edge {
    @JsonIgnore
    private static final Map<String, Double> TRAFFIC_MULTIPLIER = Map.of(
            "normal", 1.0,
            "moderado", 2.0,
            "lento", 3.0
    );

    @JsonIgnore
    private static final String DEFAULT_TRAFFIC_CONDITION = "normal";

    @JsonIgnore
    private static final double DEFAULT_WEIGHT = 1.0;

    @JsonIgnore
    private int ID;
    private int startingNodeID;
    private int destinationNodeID;
    private double weight; // Distancia entre nodos cambia al cambiar el punto de partida
    @JsonIgnore
    private boolean isBlocked;
    @JsonIgnore
    private String trafficCondition = DEFAULT_TRAFFIC_CONDITION;

    private Directions direction;
    @JsonIgnore
    private double accumulateWeight;

    // Nodos de inicio y destino
    private Node startingNode;
    private Node destinationNode;

    // Constructores
    public Edge() {
        this.isBlocked = false;
        this.trafficCondition = DEFAULT_TRAFFIC_CONDITION;
        this.ID = ListConnections.nextConnectionId();
        this.weight = DEFAULT_WEIGHT;
    }

    public Edge(int startingNodeID, int destinationNodeID, int weight) {
        this();
        this.startingNodeID = startingNodeID;
        this.destinationNodeID = destinationNodeID;
        this.weight = weight;
    }

    // Métodos de estado
    public void blockRoute() {
        isBlocked = true;
    }

    public void unblockRoute() {
        isBlocked = false;
    }

    public boolean canAccess() {
        return !isBlocked;
    }

    // Métodos de cálculo
    private void refreshWeight() {
        // Actualiza el peso de acuerdo con el incremento
        weight = Math.max(weight - getIncrement(), 0);  // Evita que el peso sea negativo
    }

    public void recalculateStartNode() {
        double[] init = getStartingNode().getLocation();
        double[] end = getDestinationNode().getLocation();
        calcularIncremento(init[0], init[1], end[0], end[1], getIncrement());
        refreshWeight();
    }

    public void calcularIncremento(double x1, double y1, double x2, double y2, double incremento) {
        // Calcular la distancia entre los puntos
        double distanciaX = x2 - x1;
        double distanciaY = y2 - y1;
        double distanciaTotal = Math.sqrt(Math.pow(distanciaX, 2) + Math.pow(distanciaY, 2));

        // Evitar división por cero
        if (distanciaTotal == 0) {
            return;  // Si los puntos son iguales, no hacer nada
        }

        // Normalizar la distancia para obtener el vector dirección
        double factorX = (distanciaX / distanciaTotal) * incremento;
        double factorY = (distanciaY / distanciaTotal) * incremento;

        // Si el incremento es mayor que la distancia restante, ajustarlo para no pasarse
        if (distanciaTotal < incremento) {
            factorX = distanciaX;  // Mover exactamente a la posición del destino
            factorY = distanciaY;
        }

        // Actualizar la nueva ubicación del nodo de inicio
        double nuevaX = x1 + factorX;
        double nuevaY = y1 + factorY;

        // Establecer las nuevas coordenadas del nodo de inicio
        Node node = getStartingNode();
        if (node != null) {
            node.setLocation(new double[]{nuevaX, nuevaY});
        }
    }

    // Métodos de obtención
    @JsonIgnore
    public Node getDestinationNode() {
        if (destinationNode == null) {
            destinationNode = getIndexAt(destinationNodeID);
        }
        return destinationNode;
    }

    @JsonIgnore
    public Node getStartingNode() {
        if (startingNode == null) {
            startingNode = getIndexAt(startingNodeID);
        }
        return startingNode;
    }

    // Métodos de establecimiento
    @JsonIgnore
    public double getEffectiveWeight() {
        if (isBlocked) {
            return Double.MAX_VALUE;
        }
        // El peso efectivo se ajusta según las condiciones de tráfico
        Double multiplier = TRAFFIC_MULTIPLIER.get(trafficCondition);
        return (weight * multiplier);
    }

    // Cálculo del incremento de acuerdo con el tráfico
    public double getIncrement() {
        double baseIncrement = 10.0;  // Valor base
        double trafficMultiplier = TRAFFIC_MULTIPLIER.get(trafficCondition);  // Obtén el multiplicador de tráfico
        return baseIncrement / trafficMultiplier;  // Ajusta el incremento según el tráfico
    }

    // Métodos de búsqueda
    public Optional<Node> searchAndGetNode(int nodeID) {
        return ListNodes.findById(nodeID);
    }

    public Node getIndexAt(int ID) {
        return ListNodes.getNodeByID(ID);
    }

    @SuppressWarnings("exports")
    public Line connectionline() {
        double[] start = ListNodes.getNodeByID(startingNodeID).getLocation();
        double[] end = ListNodes.getNodeByID(destinationNodeID).getLocation();
        return new Line(start[0], start[1], end[0], end[1]);
    }

    @Override
    public String toString() {
        return "Connection{" +
                "ID=" + ID +
                ", startingNodeID=" + startingNodeID +
                ", destinationNodeID=" + destinationNodeID +
                ", weight=" + weight +
                ", isBlocked=" + isBlocked +
                ", trafficCondition='" + trafficCondition + '\'' +
                ", direction=" + direction +
                '}';
    }
}


//@JsonIgnoreProperties(ignoreUnknown = true)  // Ignora los campos que no estén en la clase
//@JsonInclude(JsonInclude.Include.NON_NULL)  // Excluye los campos nulos del JSON