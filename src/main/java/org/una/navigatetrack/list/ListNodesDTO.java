package org.una.navigatetrack.list;

import lombok.Getter;
import lombok.Setter;
import org.una.navigatetrack.dto.NodeDTO;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


public class ListNodesDTO {
    @Getter
    @Setter
    private static Map<Integer, NodeDTO> nodeMap = new HashMap<>(); // Usar un Map para almacenar nodos

    private ListNodesDTO() {
    }

    public static void addChild(NodeDTO child, int id) {
        if (!nodeMap.containsKey(id)) {
            nodeMap.put(id, child); // Agregar el nodo al mapa
        } else {
            throw new IllegalArgumentException("Ya existe un nodo con el mismo ID.");
        }
    }

    public static Optional<NodeDTO> findById(int id) {
        return Optional.ofNullable(nodeMap.get(id)); // Obtener el nodo por ID
    }

    public static boolean removeById(int id) {
        return nodeMap.remove(id) != null; // Eliminar el nodo y retornar si fue exitoso
    }

    // Método para buscar la clave (ID) del mapa por un nodo
    public static Optional<Integer> findKeyByNode(NodeDTO node) {
        return nodeMap.entrySet()
                .stream()
                .filter(entry -> entry.getValue().equals(node))
                .map(Map.Entry::getKey)
                .findFirst();
    }

    // Método para obtener el nodo dado un objeto NodeDTO
    public static Optional<NodeDTO> findNode(NodeDTO node) {
        return nodeMap.values()
                .stream()
                .filter(n -> n.equals(node))
                .findFirst();
    }
    // Método para buscar un nodo por su ID (como un array de double)
//    public static Optional<Integer> findKeyByID(double[] id) {
//        return nodeMap.entrySet()
//                .stream()
//                .filter(entry -> {
//                    NodeDTO node = entry.getValue();
//                    double[] nodeId = node.getId(); // Suponiendo que hay un método getId() que devuelve un array de double
//                    return Arrays.equals(nodeId, id); // Comparar arrays
//                })
//                .map(Map.Entry::getKey)
//                .findFirst();
//    }
}
