package org.una.navigatetrack.utils;

import org.una.navigatetrack.dto.ListNodesDTO;
import org.una.navigatetrack.dto.NodeDTO;
import org.una.navigatetrack.manager.storage.StorageManager;
import org.una.navigatetrack.roads.Node;

import java.util.List;

public class Main {


    public static void main(String[] args) {
        StorageManager<List<Node>> nodesStorage = new StorageManager<>("src/main/resources/listNodes/", "listNodes.data");
        StorageManager<List<NodeDTO>> nodesStorageDTO = new StorageManager<>("src/main/resources/listNodes/", "listNodesDTO.data");


        List<Node> listNodes = nodesStorage.read();
        List<NodeDTO> listNodesDTO = ListNodesDTO.getListNodesDTO();

        for (Node node : listNodes) {
            listNodesDTO.add(new NodeDTO(node));
        }
        nodesStorageDTO.write(listNodesDTO);


//        List<Node> listNodes = new ArrayList<>();
//        List<NodeDTO> listNodesDTO = nodesStorageDTO.read();
//
//        for (NodeDTO nodeDTO : listNodesDTO) {
////            listNodes.add(nodeDTO.toNode());
//            System.out.println(nodeDTO.toString());
//        }
////        nodesStorage.write(listNodes);

    }
}
