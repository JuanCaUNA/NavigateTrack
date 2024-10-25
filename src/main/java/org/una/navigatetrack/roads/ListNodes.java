package org.una.navigatetrack.roads;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ListNodes {
    @Getter
    private static List<Node> listNodes = new ArrayList<>();

    private ListNodes() {
    }

    public static void setListNodes(List<Node> list) {
        listNodes.addAll(list);
    }

    public static void addNode(Node node) {
        if (findById(node.getID()).isEmpty()) {
            listNodes.add(node);
        } else {
            throw new IllegalArgumentException("Ya existe un nodo con el mismo ID.");
        }
    }

    public static Optional<Node> findById(int ID) {
        return listNodes.stream()
                .filter(node -> node.getID() == ID)
                .findFirst();
    }

    public static boolean removeById(int ID) {
        return listNodes.removeIf(node -> node.getID() == ID);
    }
}
