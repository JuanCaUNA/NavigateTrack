package org.una.navigatetrack.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.una.navigatetrack.dto.ConnectionDTO;
import org.una.navigatetrack.dto.NodeDTO;
import org.una.navigatetrack.roads.Node;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class Main {

    static ObjectMapper mapper = new ObjectMapper();

    static <T> Optional<List<T>> leer(String path, TypeReference<List<T>> reference) {
        try {
            File nodeListFile = new File(path);
            if (nodeListFile.exists()) {
                List<T> deserializedNodeList = mapper.readValue(nodeListFile, reference);
                System.out.println("Lista de nodos leída desde: " + path);
                return Optional.of(deserializedNodeList);
            } else {
                System.err.println("El archivo " + path + " no existe.");
            }
        } catch (IOException e) {
            System.err.println("Error al leer el archivo JSON: " + e.getMessage());
            e.printStackTrace();
        }
        return Optional.empty();  // Mejor que devolver null
    }


    static void save(List<?> list, String path) {
        try {
            File file = new File(path);
            //file.getParentFile().mkdirs();  // Asegura que los directorios existan, pero por el momento dejar comentado
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, list);
            System.out.println("Lista de nodos guardada en: " + path);
        } catch (IOException e) {
            System.err.println("Error al guardar el archivo JSON: " + e.getMessage());
            e.printStackTrace();
        }
    }

    static void imprimir(List<NodeDTO> list) {
        for (NodeDTO node : list) {
            System.out.println(node);
        }
    }

    //NewNodeList   nodeList
    public static void main(String[] args) {
        List<NodeDTO> listaDTO = leer("NewNodeList.json", new TypeReference<List<NodeDTO>>() {
        }).orElse(null);

        if (listaDTO != null) {
            List<Node> listNodes = new ArrayList<>();
            for (NodeDTO n : listaDTO) {
                Node node = new Node();
                node.setID(n.getID());
                node.setLocation(n.getLocation());
                List<ConnectionDTO> connectionDTOS = n.getConnectionsDTO();
                for (ConnectionDTO c : connectionDTOS) {
                    listaDTO.stream()
                            .filter(element -> Arrays.equals(element.getLocation(), c.getTargetNodeId()))
                            .findFirst().ifPresent(nodDTO -> node.addConnection(nodDTO.getID(), c.getDirection(), c.getWeight()));
                }
                listNodes.add(node);
            }


            imprimir(listaDTO);
            save(listNodes, "ListNodes.json");//"output/ListNodes.json"
        }
    }

    void identificador(List<NodeDTO> listaDTO) {
        // Asignar un ID único a cada nodo
        for (int i = 0; i < listaDTO.size(); i++) {
            listaDTO.get(i).setID(i + 1); // Asignar ID secuencial
        }

        // Ahora, asignamos el targetNodeID de las conexiones de cada nodo
        for (NodeDTO node : listaDTO) {
            if (node.getConnectionsDTO() != null) {
                for (ConnectionDTO connection : node.getConnectionsDTO()) {
                    Optional<NodeDTO> targetNode = listaDTO.stream()
                            .filter(n -> Arrays.equals(n.getLocation(), connection.getTargetNodeId()))
                            .findFirst();

                    targetNode.ifPresent(nodeDTO -> connection.setTargetNodeIDNew(nodeDTO.getID()));
                    connection.setSourceNodeID(node.getID());
                }
            }
        }

    }
}

//    static int idis = 0;
//
//    public static int getID() {
//        idis++;
//        return idis;
//    }
// Cargar nodos desde el archivo y almacenarlos en ListNodes
//    public void readNodesFromFile() {
//        Map<Integer, Node> loadedNodes = nodesStorage.read();
//        ListNodes.setListNodes(loadedNodes); // Agregar nodos directamente al Map
//    }
// StorageManager<List<NodeDTO>> nodesStorage = new StorageManager<>("src/main/resources/listNodes/", "listNodes.data");

//        NodesManager nodesManager = new NodesManager();
//    // Instanciar el StorageManager para manejar la lista de NodeDTO
//    StorageManager<List<NodeDTO>> storageManager = new StorageManager<>("src/main/resources/listNodes/", "listNodesDTO.data");
//
//    // Leer la lista de NodeDTO desde el almacenamiento

// De Nodo a Nodo DTO
//        List<Node> listNodes = nodesStorage.read();
//        List<NodeDTO> listNodesDTO = ListNodesDTO.getListNodesDTO();
//
//        for (Node node : listNodes) {
//            listNodesDTO.add(new NodeDTO(node));
//        }
//        nodesStorageDTO.write(listNodesDTO);


//------------------------------------------------------------------------------//
//        System.out.println("Cargando Nodo DTO");
//        List<NodeDTO> listNodesDTO = nodesStorageDTO.read();
//        Map<Integer, NodeDTO> nodeMap = listNodesDTO.stream()
//                .collect(Collectors.toMap(
//                        nodeDTO -> getID(), // Usar el primer elemento del ID como clave
//                        nodeDTO -> nodeDTO,
//                        (existing, replacement) -> existing));
//        ListNodesDTO.setNodeMap(nodeMap);
//
//        List<Node> listNodes = new ArrayList<>();


//        List<NodeDTO> nodeList = new ArrayList<>(nodeMap.values());


//        System.out.println("Convertiendo de Nodo dto a nodo");
//        for (int i = 1; i < nodeMap.size(); i++) {// ayudame con la conversion
//            //Node nodeTemp = nodeMap.get(i).toNode(i);
//            //System.out.println(nodeTemp.toString());
//            //ListNodes.addNode(nodeTemp);
//              System.out.println(nodeMap.get(i).toString());
//        }
//System.out.println(ListNodes.findById(1).get().getAllConnections().get(0).getDestinationNodeID());

//        if (ListNodes.findById(0).isEmpty())
//            System.out.println(" inicia en 1");
//
//        for (Node node : listNodes) {
//            List<Connection> listaConeciones = node.getAllConnections();
//            for (Connection connection: listaConeciones){
//                //connection.g
//            }
//
//        }

//        ListNodes.saveNodesList();