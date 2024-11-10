package org.una.navigatetrack.roads;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.una.navigatetrack.list.ListNodes;

import java.util.*;
import java.util.stream.Collectors;

@Setter
@Getter
public class Node {

    private int ID;
    private double[] location;
    private Map<Directions, Edge> connectionsMap;

    // Constructor vacío
    public Node() {
        connectionsMap = new EnumMap<>(Directions.class);
        location = new double[2];
    }

    // Constructor con ubicación
    public Node(double[] point) {
        connectionsMap = new EnumMap<>(Directions.class);
        location = point;
        //ID = ListNodes.getNextId(); TODO
    }

    // ===========================
    // Métodos de acceso
    // ===========================

    // Obtener todas las conexiones
    @JsonIgnore
    public List<Edge> getAllConnections() {
        return new ArrayList<>(connectionsMap.values());
    }

    // Obtener conexión en orden por peso, excluyendo un nodo específico
    @JsonIgnore
    public List<Edge> getConnectionsInOrderByWeight(Node entryNode) {
        return connectionsMap.values().stream()
                .filter(conn -> conn.getDestinationNodeID() != entryNode.getID() && !conn.isBlocked())
                .sorted(Comparator.comparingDouble(Edge::getEffectiveWeight)) // Ordenar por peso final
                .collect(Collectors.toList());
    }

    // Obtener todas las conexiones en orden por peso
    @JsonIgnore
    public List<Edge> getConnectionsInOrderByWeight() {
        return connectionsMap.values().stream()
                .filter(conn -> !conn.isBlocked())
                .sorted(Comparator.comparingDouble(Edge::getEffectiveWeight)) // Ordenar por peso final
                .collect(Collectors.toList());
    }

    // Obtener conexión en una dirección específica
    @JsonIgnore
    public Edge getConnection(Directions direction) {
        return connectionsMap.get(direction);
    }

    // Obtener la dirección conectada a un nodo específico
    @JsonIgnore
    public Directions getDirConnectedToNode(Node node) {
        return connectionsMap.entrySet().stream()  // Itera sobre las entradas (key-value) del mapa
                .filter(entry -> entry.getValue().getDestinationNodeID() == node.getID())  // Filtra por el ID del nodo
                .map(Map.Entry::getKey)  // Mapea a la clave (Directions)
                .findFirst()  // Devuelve el primer resultado encontrado
                .orElse(null);  // Si no se encuentra ninguna, devuelve null
    }

    // Buscar una conexión por ID de nodo
    @JsonIgnore
    public Edge getConnectionInNode(int nodeID) {
        return connectionsMap.values().stream()
                .filter(conn -> conn.getDestinationNodeID() == nodeID)
                .findFirst()
                .orElse(null);
    }

    // ===========================
    // Métodos de modificación de conexiones
    // ===========================

    // Agregar una nueva conexión
    public void addConnection(Node targetNode, Directions direction) {
        Edge edge = new Edge(ID, targetNode.getID(), calculateDistance(targetNode));
        edge.setDirection(direction);
        connectionsMap.put(direction, edge);
    }

    // Agregar una nueva conexión especificando el ID del nodo
    public void addConnection(int targetNodeId, Directions direction, double weight) {
        Edge edge = new Edge(ID, targetNodeId, (int) weight);
        edge.setDirection(direction);
        connectionsMap.put(direction, edge);
    }

    // Eliminar conexión por dirección
    public void deleteConnection(Directions direction) {
        connectionsMap.remove(direction);
    }

    public void deleteConnections() {
        connectionsMap.clear();
    }

    // Cambiar destino de una conexión
    public void changeConnectionIn(Node inNode, Node toNode) {
        Edge edge = getConnectionInNode(inNode.getID());
        if (edge != null) {
            edge.setDestinationNodeID(toNode.getID());
        }
    }

    // ===========================
    // Métodos de utilidad
    // ===========================

    // Calcular distancia a otro nodo
    public int calculateDistance(Node other) {
        return (int) Math.sqrt(Math.pow(location[0] - other.location[0], 2) + Math.pow(location[1] - other.location[1], 2));
    }

    // Verificar si está conectado a un nodo específico
    public boolean isConnectedToNode(Node node) {
        return connectionsMap.values().stream().noneMatch(conn -> conn.getDestinationNodeID() == node.getID());
    }

    // Buscar nodo por ID
    public Optional<Node> searchAndGetNode(int nodeID) {
        return ListNodes.findById(nodeID);
    }

    // Obtener nodo por ID
    public Node getIndexAt(int nodeID) {
        return ListNodes.getNodeByID(nodeID);
    }

    // ===========================
    // Métodos sobrecargados y otros
    // ===========================

    // Método toString para imprimir información sobre el nodo y sus conexiones
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Node{")
                .append("ID=").append(ID)
                .append(", location=").append(Arrays.toString(location))
                .append(", connectionsMap={");

        // Imprimir las conexiones
        if (connectionsMap != null && !connectionsMap.isEmpty()) {
            for (Map.Entry<Directions, Edge> entry : connectionsMap.entrySet()) {
                sb.append(entry.getKey()).append("->").append(entry.getValue().toString()).append(", ");
            }
            // Eliminar la coma y el espacio al final
            sb.setLength(sb.length() - 2);
        }

        sb.append("}}");

        return sb.toString();
    }
}
