package org.una.navigatetrack.roads;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

@Getter
@Setter
public class Connection implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Node targetNode; // Nodo de destino
    private int weight; // Peso de la ruta (longitud/costo)
    private boolean isBlocked; // Indica si la ruta está bloqueada
    private String trafficCondition; // Estado de tráfico ("normal", "moderado", "lento")
    private Directions direction; // Dirección de la conexión

    public Connection(Node targetNode, int weight, Directions direction) {
        this.targetNode = targetNode;
        this.weight = weight;
        this.isBlocked = false;
        this.trafficCondition = "normal";
        this.direction = direction;
    }

    public void blockRoute() {
        isBlocked = true;
    }

    public void unblockRoute() {
        isBlocked = false;
    }

    public boolean canAccess() {
        return !isBlocked; // Acceso permitido solo si no está bloqueada
    }

    private static final Map<String, Integer> TRAFFIC_MULTIPLIER = Map.of(
            "normal", 1,
            "moderado", 2,
            "lento", 3
    );

    public int getEffectiveWeight() {
        return weight * TRAFFIC_MULTIPLIER.getOrDefault(trafficCondition, 1);
    }

    public double calculateTravelTime() {
        return getEffectiveWeight() / 10.0; // Ajustar según sea necesario
    }
}