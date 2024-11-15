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

    @JsonIgnore
    boolean nodeType, emptyValues, starNode;

    public void removeConnection(Node end) {
        // Comprobar si el mapa de conexiones es nulo o vacío
        if (connectionsMap == null || connectionsMap.isEmpty()) {
            System.err.println("Error: El mapa de conexiones está vacío o es nulo.");
            return;
        }

        if (end == null) {
            throw new IllegalArgumentException("El nodo de destino no puede ser nulo.");
        }

        // Buscar y eliminar la conexión del nodo 'end'
        Iterator<Map.Entry<Directions, Edge>> iterator = connectionsMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Directions, Edge> entry = iterator.next();
            Edge edge = entry.getValue();

            if (edge.getDestinationNodeID() == end.getID()) {
                iterator.remove(); // Eliminar la conexión del mapa
                System.out.println("Conexión eliminada con destino al nodo: " + end);
                return; // Salir después de eliminar la conexión
            }
        }

        System.err.println("Error: No se encontró una conexión con el nodo de destino: " + end);
    }

    // Constructor vacío
    public Node() {
        connectionsMap = new EnumMap<>(Directions.class);
        location = new double[2];  // Suponiendo que la ubicación es en 2D (x, y)
        nodeType = emptyValues = starNode = false;
    }

    // Constructor con ubicación
    public Node(double[] point) {
        if (point == null || point.length != 2) {
            throw new IllegalArgumentException("La ubicación debe ser un arreglo de dos valores (x, y).");
        }
        connectionsMap = new EnumMap<>(Directions.class);
        location = point;

        nodeType = starNode = false;
        emptyValues = true;
    }

    // ===========================
    // Métodos de acceso
    // ===========================

    @JsonIgnore
    public List<Edge> getAllConnections() {
        return new ArrayList<>(connectionsMap.values());
    }

    @JsonIgnore
    public List<Edge> getConnectionsInOrderByWeight(Node entryNode) {
        if (entryNode == null) {
            throw new IllegalArgumentException("El nodo de entrada no puede ser nulo.");
        }

        return connectionsMap.values().stream()
                .filter(conn -> conn.getDestinationNodeID() != entryNode.getID() && !conn.isBlocked())
                .sorted(Comparator.comparingDouble(Edge::getEffectiveWeight))
                .collect(Collectors.toList());
    }

    @JsonIgnore
    public List<Edge> getConnectionsInOrderByWeight() {
        return connectionsMap.values().stream()
                .filter(conn -> !conn.isBlocked())
                .sorted(Comparator.comparingDouble(Edge::getEffectiveWeight))
                .collect(Collectors.toList());
    }

    @JsonIgnore
    public Edge getConnection(Directions direction) {
        if (direction == null) {
            throw new IllegalArgumentException("La dirección no puede ser nula.");
        }
        return connectionsMap.get(direction);
    }

    @JsonIgnore
    public Directions getDirConnectedToNode(Node node) {
        if (node == null) {
            throw new IllegalArgumentException("El nodo no puede ser nulo.");
        }

        return connectionsMap.entrySet().stream()
                .filter(entry -> entry.getValue().getDestinationNodeID() == node.getID())
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
    }

    @JsonIgnore
    public Edge getConnectionInNode(int nodeID) {
        if (nodeID <= -1) {
            throw new IllegalArgumentException("El ID del nodo debe ser mayor que -1.");
        }

        return connectionsMap.values().stream()
                .filter(conn -> conn.getDestinationNodeID() == nodeID)
                .findFirst()
                .orElse(null);
    }

    // ===========================
    // Métodos de modificación de conexiones
    // ===========================

    public void addConnection(Node targetNode, Directions direction) {
        if (targetNode == null) {
            throw new IllegalArgumentException("El nodo de destino no puede ser nulo.");
        }

        if (direction == null) {
            throw new IllegalArgumentException("La dirección no puede ser nula.");
        }

        Edge edge = new Edge(ID, targetNode.getID(), calculateDistance(targetNode));
        edge.setDirection(direction);
        connectionsMap.put(direction, edge);
    }

    public void addConnection(int targetNodeId, Directions direction, double weight) {
        if (targetNodeId <= 0) {
            throw new IllegalArgumentException("El ID del nodo de destino debe ser mayor que cero.");
        }

        if (direction == null) {
            throw new IllegalArgumentException("La dirección no puede ser nula.");
        }

        Edge edge = new Edge(ID, targetNodeId, (int) weight);
        edge.setDirection(direction);
        connectionsMap.put(direction, edge);
    }

    public void deleteConnection(Directions direction) {
        if (direction == null) {
            throw new IllegalArgumentException("La dirección no puede ser nula.");
        }
        connectionsMap.remove(direction);
    }

    public void deleteConnections() {
        connectionsMap.clear();
    }

    public void changeConnectionIn(Node inNode, Node toNode) {
        if (inNode == null || toNode == null) {
            throw new IllegalArgumentException("Los nodos no pueden ser nulos.");
        }

        Edge edge = getConnectionInNode(inNode.getID());

        if (edge != null) {
            edge.setDestinationNodeID(toNode.getID());
            edge.setWeight(calculateDistance(toNode));
        } else {
            System.err.println("Error: No se encontró una conexión con el nodo de entrada.");
        }
    }

    // ===========================
    // Métodos de utilidad
    // ===========================

    @JsonIgnore
    public boolean isConnectionsMapEmpty() {
        return connectionsMap.isEmpty();
    }

    public int calculateDistance(Node other) {
        if (other == null) {
            throw new IllegalArgumentException("El nodo de referencia no puede ser nulo.");
        }
        return (int) Math.sqrt(Math.pow(location[0] - other.location[0], 2) + Math.pow(location[1] - other.location[1], 2));
    }

    public boolean isConnectedToNode(Node node) {
        if (node == null) {
            throw new IllegalArgumentException("El nodo no puede ser nulo.");
        }

        // Usar anyMatch para devolver true si se encuentra alguna conexión
        return connectionsMap.values().stream()
                .anyMatch(conn -> conn.getDestinationNodeID() == node.getID());
    }

    public Optional<Node> searchAndGetNode(int nodeID) {
        return ListNodes.findById(nodeID);
    }

    public Node getIndexAt(int nodeID) {
        return ListNodes.getNodeByID(nodeID);
    }

    // ===========================
    // Métodos sobrecargados y otros
    // ===========================

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        // Agregar ID y ubicación
        sb.append("Node{\n")
                .append("  ID=").append(ID).append("\n")
                .append("  location=").append(Arrays.toString(location)).append("\n")
                .append("  connectionsMap={\n");

        // Si hay conexiones, iterar sobre ellas y agregarlas en líneas separadas
        if (connectionsMap != null && !connectionsMap.isEmpty()) {
            for (Map.Entry<Directions, Edge> entry : connectionsMap.entrySet()) {
                sb.append("    ").append(entry.getKey()).append(" -> ").append(entry.getValue()).append("\n");
            }
        }

        // Cerrar el mapa de conexiones y la clase Node
        sb.append("  }\n")
                .append("}");

        return sb.toString();
    }

}
