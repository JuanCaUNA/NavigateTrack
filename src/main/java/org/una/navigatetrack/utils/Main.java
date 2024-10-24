package org.una.navigatetrack.utils;

import org.una.navigatetrack.manager.storage.StorageManager;
import org.una.navigatetrack.roads.Node;

import java.util.ArrayList;
import java.util.List;

public class Main {


    public static void main(String[] args) {
        StorageManager<List<Node>> nodesStorage = new StorageManager<>("src/main/resources/listNodes/", "listNodes.data");
        StorageManager<List<Locate>> nodesStorageDTO = new StorageManager<>("src/main/resources/listNodes/", "arraylist.data");


//        List<Node> listNodes = nodesStorage.read();
//        List<Locate> listNodesDTO = new ArrayList<>();
//        for (Node node : listNodes) {
//            listNodesDTO.add(new Locate(node.getLocatio()[0], node.getLocatio()[1]));
//        }
//        nodesStorageDTO.write(listNodesDTO);


        List<Node> listNodes = new ArrayList<>();
        List<Locate> listNodesDTO = nodesStorageDTO.read();

        for (Locate doubles : listNodesDTO) {
            listNodes.add(new Node(new double[]{doubles.x, doubles.y}));
        }

        nodesStorage.write(listNodes);

    }
}
