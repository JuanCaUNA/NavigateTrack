package org.una.navigatetrack.list;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.Getter;
import org.una.navigatetrack.roads.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@SuppressWarnings("All")
public class ListNodes {

    @Getter
    private static List<Node> nodesList = new ArrayList<>();
    @Getter
    private static int baseIndex;

    private ListNodes() {
        // Constructor privado para evitar instanciación
    }

    // Cargar nodos desde archivo
    public static void loadNodesList() {
        nodesList = JSON.leer("ListNodes.json", new TypeReference<List<Node>>() {
        }).orElse(null);
        if (nodesList == null) {
            nodesList = new ArrayList<>();
        }
        baseIndex = nodesList.size();
//        ListConnections.loadConnections(nodesList);
//        ListConnections.resetID();
//        saveNodesList();
    }

    // Guardar nodos en archivo
    public static void saveNodesList() {
        JSON.save(nodesList, "ListNodes.json");
    }

    // Establecer una nueva lista de nodos (se hace un clon)
    public static void setListNodes(List<Node> nodes) {
        nodesList.clear();
        nodesList.addAll(nodes);  // Clonamos la lista pasada
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
        // Genera el siguiente ID basado en los nodos existentes
        return nodesList.size() + 1;
//                nodesList.stream()
//                .mapToInt(Node::getID)
//                .max()
//                .orElse(-1) + 1; // Si no hay nodos, devuelve 0
    }

    // Verificar si ya existe un nodo con un ID específico
    private static boolean containsNodeWithID(int id) {
        return nodesList.stream().anyMatch(node -> node.getID() == id);
    }

    public static Node getNodeByLocation(double x, double y) {//List<Node> nodesList
        return nodesList.stream()
                .filter(node -> node.getLocation()[0] == x && node.getLocation()[1] == y) // Filtrar por las coordenadas
                .findFirst() // Obtener el primer nodo que coincida
                .orElse(null); // Si no se encuentra, devolver null
    }


//    public void resetID() {
//        int i = 0;
//        for (Node node : nodesList) {
//            node.setID(i);
//            i++;
//        }
//    }
}

