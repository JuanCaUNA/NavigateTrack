package org.una.navigatetrack.list;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.Getter;
import org.una.navigatetrack.roads.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ListNodes {
    //private static final StorageManager<List<Node>> nodesStorage = new StorageManager<>("src/main/resources/listNodes/", "listNodes.data");
    @Getter
    private static List<Node> nodesList = new ArrayList<>();
    @Getter
    private static int maxId = 0; // ID máximo inicializado a -1, el primer ID será 0

    private ListNodes() {
        // Constructor privado para evitar instanciación
    }

    // Cargar nodos desde archivo
    public static void loadNodesList() {
        nodesList = Json.leer("ListNodes.json", new TypeReference<List<Node>>() {
        }).orElse(null);//nodesStorage.read(); // Lee desde el archivo y asigna la lista
        if (nodesList == null) {
            nodesList = new ArrayList<>();
        }
        if (nodesList.isEmpty()) {
            maxId = 0; // Si no hay nodos, maxId se restablece
        } else {
            maxId = nodesList.stream().mapToInt(Node::getID).max().orElse(-1);
        }
    }

    // Guardar nodos en archivo
    public static void saveNodesList() {
        //nodesStorage.write(nodesList);
        System.out.println();
        Json.save(nodesList, "ListNodes.json");
    }

    // Establecer una nueva lista de nodos (se hace un clon)
    public static void setListNodes(List<Node> nodes) {
        nodesList.clear();
        nodesList.addAll(nodes);  // Clonamos la lista pasada
        maxId = nodes.stream().mapToInt(Node::getID).max().orElse(-1);
    }

    // Buscar un nodo por su ID
    public static Optional<Node> findById(int id) {
        return nodesList.stream()
                .filter(node -> node.getID() == id)
                .findFirst();
    }

    // Obtener el nodo por su ID
    public static Node getNodeByID(int id) {
        return findById(id).orElse(null);  // Devuelve null si no lo encuentra
    }

    // Agregar un nodo
    public static void addNode(Node node) {
        if (!containsNodeWithID(node.getID())) {
            nodesList.add(node);
            maxId = Math.max(maxId, node.getID());
        } else {
            throw new IllegalArgumentException("Ya existe un nodo con el mismo ID: " + node.getID());
        }
    }

    // Eliminar un nodo por ID
    public static void removeById(int id) {
        nodesList.removeIf(node -> node.getID() == id);
    }

    // Obtener el siguiente ID disponible
    public static int getNextId() {
        return maxId + 1;
    }

    // Verificar si ya existe un nodo con un ID específico
    private static boolean containsNodeWithID(int id) {
        return nodesList.stream().anyMatch(node -> node.getID() == id);
    }
}
