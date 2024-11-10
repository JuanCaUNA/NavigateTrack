package org.una.navigatetrack.list;

import org.una.navigatetrack.roads.Connection;

import java.util.ArrayList;
import java.util.List;

public class ListConnections {

    private static final List<Connection> connectionsList = new ArrayList<>(); // Lista de conexiones

    // Agregar una nueva conexión
    public static void addConnection(Connection connection) {
        if (!containsConnectionWithID(connection.getID())) {
            connectionsList.add(connection);  // Agrega la conexión a la lista
        } else {
            throw new IllegalArgumentException("Ya existe una conexión con el mismo ID: " + connection.getID());
        }
    }

    // Eliminar una conexión por su ID
    public static void removeConnection(int idConnection) {
        connectionsList.removeIf(connection -> connection.getID() == idConnection);
    }

    // Obtener una conexión por su ID
    public static Connection getConnection(int idConnection) {
        return connectionsList.stream()
                .filter(connection -> connection.getID() == idConnection)
                .findFirst()
                .orElse(null);  // Si no se encuentra, devuelve null
    }

    // Cargar conexiones desde una lista de nodos
//    public static void loadConnections(List<Node> nodeList) {
//        connectionsList.clear();  // Limpiar la lista actual
//        for (Node node : nodeList) {
//            // Recorrer las conexiones de cada nodo y agregar a la lista de conexiones
//            for (Map.Entry<Directions, Connection> entry : node.getConnectionsMap().entrySet()) {
//                Connection connection = entry.getValue();
//                if (!containsConnectionWithID(connection.getID())) {
//                    connectionsList.add(connection);
//                }
//            }
//        }
//    }

//    public static void loadConnections(List<Node> nodeList) {
//        connectionsList.clear();  // Limpiar la lista actual
//        for (Node node : nodeList) {
//            // Recorrer las conexiones de cada nodo y agregar a la lista de conexiones
//            for (Map.Entry<Directions, Connection> entry : node.getConnectionsMap().entrySet()) {
//                Connection connection = entry.getValue();
//                // Verificar si la conexión ya existe
//                if (!containsConnectionWithID(connection.getID())) {
//                    // Agregar conexión si no existe
//                    connectionsList.add(connection);
//                }
//            }
//        }
//    }

    // Obtener el siguiente ID disponible para una nueva conexión
    public static int nextConnectionId() {
        return connectionsList.stream()
                .mapToInt(Connection::getID)
                .max()
                .orElse(-1) + 1;  // Si no hay conexiones, el siguiente ID es 0
    }

    // Verificar si ya existe una conexión con un ID específico
    private static boolean containsConnectionWithID(int id) {
        return connectionsList.stream().anyMatch(connection -> connection.getID() == id);
    }

//    public static void resetID() {
//        int i = 0;
//        for (Connection connection : connectionsList) {
//            connection.setID(i);
//            i++;
//        }
//    }
}
