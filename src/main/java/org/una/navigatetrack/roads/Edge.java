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
    private static final Map<String, Double> TRAFFIC_MULTIPLIER = Map.of("normal", 1.0, "moderado", 1.25, "lento", 1.65);
    @JsonIgnore
    private int ID;
    // Referencias de nodos
    private int startingNodeID;
    private int destinationNodeID;
    private double weight; // Peso en base a distancia entre nodos
    // Estados de ruta
    @JsonIgnore
    private boolean isBlocked; // Indica si la ruta está bloqueada
    @JsonIgnore
    private String trafficCondition; // Estado de tráfico ("normal", "moderado", "lento")

    private Directions direction;
    @JsonIgnore
    private double accumulateWeight;  // Este campo se deserializa correctamente desde el JSON

    // Campos no presentes en el JSON, pero calculados
    @JsonIgnore
    private double effectiveWeight;  // Este campo no es necesario deserializarlo, ya que se calcula
    @JsonIgnore
    private double increment;  // Similar al campo `effectiveWeight`, este se calcula

    // ===========================
    // Constructores
    // ===========================

    public Edge() {
        init();
    }

    public Edge(int startingNodeID, int destinationNodeID, int weight) {
        init();
        this.startingNodeID = startingNodeID;
        this.destinationNodeID = destinationNodeID;
        this.weight = weight;
    }

    public Edge(int id, int weight) {
        init();
        this.destinationNodeID = id;
        this.weight = weight;
    }

    // ===========================
    // Inicialización
    // ===========================

    private void init() {
        this.isBlocked = false;
        this.trafficCondition = "normal";
        this.ID = ListConnections.nextConnectionId();

        this.startingNodeID = 0;
        this.destinationNodeID = 0;

        this.weight = 1.0;
        this.direction = null;

        this.effectiveWeight = 0.0;

        this.accumulateWeight = 0.0;

        this.increment = 1.0;

        ListConnections.addConnection(this);
    }

    // ===========================
    // Métodos de estado
    // ===========================

    // Bloquear la ruta
    public void blockRoute() {
        isBlocked = true;
    }

    // Desbloquear la ruta
    public void unblockRoute() {
        isBlocked = false;
    }

    // Verificar si se puede acceder a la ruta
    public boolean canAccess() {
        return !isBlocked;
    }

    // ===========================
    // Métodos de cálculo
    // ===========================

    // Calcular la distancia y actualizar el peso
    private void refreshWeight() {
        weight -= getIncrement();
    }

    // Recalcular el nodo de inicio basándose en las coordenadas de los nodos
    public void recalculateStartNode() {
        double[] init = getStartingNode().getLocation();
        double[] end = getDestinationNode().getLocation();

        calcularIncremento(init[0], init[1], end[0], end[1], getIncrement());
        refreshWeight();
    }

    // Calcular el incremento para actualizar las coordenadas del nodo
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

    // ===========================
    // Métodos de obtención
    // ===========================

    // Obtener el nodo de destino
    @JsonIgnore
    public Node getDestinationNode() {
        return getIndexAt(destinationNodeID);
    }

    // Establecer el nodo de destino
    public void setDestinationNode(Node targetNode) {
        destinationNodeID = targetNode.getID();
    }

    // Obtener el nodo de inicio
    @JsonIgnore
    public Node getStartingNode() {
        return getIndexAt(startingNodeID);
    }

    // Establecer el nodo de inicio
    public void setStartingNode(Node startNode) {
        this.startingNodeID = startNode.getID();
    }

    // ===========================
    // Métodos de establecimiento
    // ===========================

    // Obtener el peso efectivo ajustado por el tráfico
    @JsonIgnore
    public double getEffectiveWeight() {
        if(isBlocked){
            return Double.MAX_VALUE;
        }

        return (weight * TRAFFIC_MULTIPLIER.get(trafficCondition));
    }

    // Obtener el incremento basado en el estado del tráfico
    public double getIncrement() {
        return (2 - TRAFFIC_MULTIPLIER.get(trafficCondition));
    }

    // ===========================
    // Métodos de búsqueda
    // ===========================

    // Buscar un nodo por ID
    public Optional<Node> searchAndGetNode(int nodeID) {
        return ListNodes.findById(nodeID);
    }

    // Obtener el nodo por su ID
    public Node getIndexAt(int ID) {
        return ListNodes.getNodeByID(ID);
    }

    @SuppressWarnings("exports")
    public Line connectionline() {
        double[] start = ListNodes.getNodeByID(startingNodeID).getLocation();
        double[] end = ListNodes.getNodeByID(destinationNodeID).getLocation();
        return new Line(start[0], start[1], end[0], end[1]);
    }

    // ===========================
    // Método toString
    // ===========================
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