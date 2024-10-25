package org.una.navigatetrack.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class ListNodesDTO {
    @Getter
    @Setter
    private static List<NodeDTO> listNodesDTO = new ArrayList<>();

    private ListNodesDTO() {
    }

    public static void addChild(NodeDTO child) {
        if (findById(child.getId()).isEmpty()) {
            listNodesDTO.add(child);
        } else {
            throw new IllegalArgumentException("Ya existe un nodo con el mismo ID.");
        }
    }

    public static Optional<NodeDTO> findById(double[] id) {
        return listNodesDTO.stream()
                .filter(node -> Arrays.equals(node.getId(), id))
                .findFirst();
    }

    public static boolean removeById(double[] id) {
        return listNodesDTO.removeIf(node -> Arrays.equals(node.getId(), id));
    }
}
