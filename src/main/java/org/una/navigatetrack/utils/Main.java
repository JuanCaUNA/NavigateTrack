package org.una.navigatetrack.utils;

import org.una.navigatetrack.dto.ListNodesDTO;
import org.una.navigatetrack.dto.NodeDTO;
import org.una.navigatetrack.manager.NodesManager;
import org.una.navigatetrack.manager.storage.StorageManager;
import org.una.navigatetrack.roads.ListNodes;
import org.una.navigatetrack.roads.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Main {


    // Cargar nodos desde el archivo y almacenarlos en ListNodes
//    public void readNodesFromFile() {
//        Map<Integer, Node> loadedNodes = nodesStorage.read();
//        ListNodes.setListNodes(loadedNodes); // Agregar nodos directamente al Map
//    }


    public static void main(String[] args) {
        NodesManager nodesManager = new NodesManager();


        // De Nodo a Nodo DTO
//        List<Node> listNodes = nodesStorage.read();
//        List<NodeDTO> listNodesDTO = ListNodesDTO.getListNodesDTO();
//
//        for (Node node : listNodes) {
//            listNodesDTO.add(new NodeDTO(node));
//        }
//        nodesStorageDTO.write(listNodesDTO);

        StorageManager<List<NodeDTO>> nodesStorageDTO = new StorageManager<>("src/main/resources/listNodes/", "listNodesDTO.data");

        //Nodo DTO a NODO
        List<Node> listNodes = new ArrayList<>();
        List<NodeDTO> listNodesDTO = nodesStorageDTO.read();


        Map<Integer, NodeDTO> nodeMap = listNodesDTO.stream()
                .collect(Collectors.toMap(
                        nodeDTO -> getID(), // Usar el primer elemento del ID como clave
                        nodeDTO -> nodeDTO,
                        (existing, replacement) -> existing));
        ListNodesDTO.setNodeMap(nodeMap);

        for (int i = 0; i < nodeMap.size(); i++) {// ayudame con la conversion
            ListNodes.addNode(nodeMap.get(i).toNode(i));
        }

        nodesManager.updateNodesToFile();
    }

    static int idis = -1;

    public static int getID() {
        idis++;
        return idis;
    }
}
