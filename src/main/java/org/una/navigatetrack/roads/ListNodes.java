package org.una.navigatetrack.roads;

import lombok.Getter;

import java.util.*;

public class ListNodes {
    @Getter
    private static final Map<Integer, Node> nodesMap = new HashMap<>();
    @Getter
    private static int maxId = 0; // ID máximo inicializado a 0

    private ListNodes() {
        // Constructor privado para evitar instanciación
    }

    public static void setListNodes(List<Node> nodes) {
        nodesMap.clear();
        for (Node node : nodes) {
            addNode(node);
        }
    }

    public static List<Node> getListNodes() {
        return new ArrayList<>(nodesMap.values());
    }

    public static void setListNodes(Map<Integer, Node> nodes) {
        nodesMap.clear();
        nodesMap.putAll(nodes);
        for (Node node : nodes.values()) {
            maxId = Math.max(maxId, node.getID());
        }
    }

    public static void addNode(Node node) {
        if (!nodesMap.containsKey(node.getID())) {
            nodesMap.put(node.getID(), node);
            maxId = Math.max(maxId, node.getID());
        } else {
            throw new IllegalArgumentException("Ya existe un nodo con el mismo ID: " + node.getID());
        }
    }

    public static Optional<Node> findById(int ID) {
        return Optional.ofNullable(nodesMap.get(ID));
    }

    public static void removeById(int ID) {
        if (nodesMap.remove(ID) != null) {
            if (ID == maxId) {
                maxId = nodesMap.keySet().stream().mapToInt(Integer::intValue).max().orElse(0);
            }
        }
    }

    public static int getNextId() {
        return maxId + 1;
    }
}