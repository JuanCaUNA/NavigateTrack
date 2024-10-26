package org.una.navigatetrack.roads;

import lombok.Getter;

import java.util.*;

public class ListNodes {
    @Getter
    private static Map<Integer, Node> nodesMap = new HashMap<>();

    private ListNodes() {
        // Constructor privado para evitar instanciación
    }

    public static void setListNodes(List<Node> nodes) {
        nodesMap.clear(); // Limpiar el mapa antes de agregar nuevos nodos
        for (Node node : nodes) {
            addNode(node); // Utiliza addNode para evitar duplicados
        }
    }

    public static List<Node> getListNodes() {
        return new ArrayList<>(nodesMap.values());
    }

    public static void setListNodes(Map<Integer, Node> nodes) {
        nodesMap.putAll(nodes);
    }

    public static void addNode(Node node) {
        if (!nodesMap.containsKey(node.getID())) {
            nodesMap.put(node.getID(), node);
        } else {
            throw new IllegalArgumentException("Ya existe un nodo con el mismo ID.");
        }
    }

    public static Optional<Node> findById(int ID) {
        return Optional.ofNullable(nodesMap.get(ID));
    }

    public static void removeById(int ID) {
        nodesMap.remove(ID);
    }
}