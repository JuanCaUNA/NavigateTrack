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
    private double weight;  // Distancia entre nodos
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

    // Constructor vacío
    public Edge() {
        this.isBlocked = false;
        this.trafficCondition = DEFAULT_TRAFFIC_CONDITION;
        this.weight = DEFAULT_WEIGHT;
        this.ID = ListConnections.nextConnectionId();
        ListConnections.addConnection(this);
    }

    // Constructor con parámetros de nodos e ID
    public Edge(int startingNodeID, int destinationNodeID, double weight) {
        this(); // Llama al constructor vacío para inicializar los valores por defecto
        if (startingNodeID <= -1 || destinationNodeID <= -1) {
            throw new IllegalArgumentException("Los IDs de los nodos deben ser mayores a -1.");
        }
        this.startingNodeID = startingNodeID;
        this.destinationNodeID = destinationNodeID;
        this.weight = weight > 0 ? weight : DEFAULT_WEIGHT;  // Asegura que el peso sea positivo
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
        weight = Math.max(weight - getIncrement(), 0);  // Evita que el peso sea negativo

//        if (weight == 0) {
//            startingNode = ListNodes.getNodeByID(startingNodeID);
//            startingNode.setLocation(getDestinationNode().getLocation());
//        }
    }

    public void recalculateStartNode() {
        Node startNode = getStartingNode();
        Node endNode = getDestinationNode();

        if (startNode == null || endNode == null) {
            throw new IllegalStateException("No se pueden calcular las ubicaciones: los nodos no son válidos.");
        }

        double[] init = startNode.getLocation();
        double[] end = endNode.getLocation();

        calcularIncremento(init[0], init[1], end[0], end[1], getIncrement());

        refreshWeight();
    }

    public void calcularIncremento(double x1, double y1, double x2, double y2, double incremento) {
        double distanciaX = x2 - x1;
        double distanciaY = y2 - y1;
        double distanciaTotal = Math.sqrt(Math.pow(distanciaX, 2) + Math.pow(distanciaY, 2));

        // Evitar división por cero
        if (distanciaTotal == 0) return;

        // Normalizar la distancia para obtener el vector dirección
        double factorX = (int) ((distanciaX / distanciaTotal) * incremento);
        double factorY = (int) ((distanciaY / distanciaTotal) * incremento);

        // Ajustar si el incremento excede la distancia
        if (distanciaTotal < incremento) {
            factorX =(int) distanciaX;
            factorY =(int) distanciaY;
        }

        // Actualizar la nueva ubicación del nodo de inicio
        Node node = getStartingNode();
        if (node != null) {
            node.setLocation(new double[]{x1 + factorX, y1 + factorY});
        }
    }

    // Métodos de obtención de nodos
    @JsonIgnore
    public Node getDestinationNode() {
//        if (destinationNode == null) {
//            destinationNode = getIndexAt(destinationNodeID);
//        }
        return ListNodes.getNodeByID(destinationNodeID);
    }

    @JsonIgnore
    public Node getStartingNode() {
//        if (startingNode == null) {
//            startingNode = getIndexAt(startingNodeID);
//        }
//        return startingNode;
        return ListNodes.getNodeByID(startingNodeID);
    }

    // Método para obtener el peso efectivo (ajustado por condiciones de tráfico)
    @JsonIgnore
    public double getEffectiveWeight() {
        if (isBlocked) {
            return Double.MAX_VALUE;  // Representa un valor no accesible
        }
        Double multiplier = TRAFFIC_MULTIPLIER.get(trafficCondition);
        return weight * multiplier;
    }

    // Cálculo del incremento ajustado por el tráfico
    @JsonIgnore
    public double getIncrement() {
        double baseIncrement = 10.0;
        double trafficMultiplier = TRAFFIC_MULTIPLIER.get(trafficCondition);
        return baseIncrement / trafficMultiplier;
    }

    // Métodos de búsqueda
    public Optional<Node> searchAndGetNode(int nodeID) {
        return ListNodes.findById(nodeID);
    }

    public Node getIndexAt(int ID) {
        return ListNodes.getNodeByID(ID);
    }

    // Método para obtener la línea de conexión (para visualización, por ejemplo)
    @SuppressWarnings("exports")
    public Line connectionline() {
        Node startNode = getStartingNode();
        Node endNode = getDestinationNode();
        if (startNode == null || endNode == null) {
            throw new IllegalStateException("No se pueden obtener las ubicaciones de los nodos para la línea.");
        }
        double[] start = startNode.getLocation();
        double[] end = endNode.getLocation();
        return new Line(start[0], start[1], end[0], end[1]);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Connection{\n")
                .append("  ID=").append(ID).append("\n")
                .append("  startingNodeID=").append(startingNodeID).append("\n")
                .append("  destinationNodeID=").append(destinationNodeID).append("\n")
                .append("  weight=").append(weight).append("\n")
                .append("  isBlocked=").append(isBlocked).append("\n")
                .append("  trafficCondition='").append(trafficCondition).append("'\n")
//                .append("  direction=").append(direction).append("\n")
                .append("}");

        return sb.toString();
    }

}


//@JsonIgnoreProperties(ignoreUnknown = true)  // Ignora los campos que no estén en la clase
//@JsonInclude(JsonInclude.Include.NON_NULL)  // Excluye los campos nulos del JSON