package org.una.navigatetrack.manager;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import lombok.Getter;
import org.una.navigatetrack.roads.Connection;
import org.una.navigatetrack.roads.Directions;
import org.una.navigatetrack.roads.Node;
import org.una.navigatetrack.utils.Drawer;

import java.util.List;

public class NodesDrawerManager {
    @Getter
    private final NodesManager nodesManager;
    private final Drawer drawer;

    public NodesDrawerManager(NodesManager nodesManager, Drawer drawer) {
        this.nodesManager = nodesManager;
        this.drawer = drawer;

        // Cargar y dibujar nodos al inicializar
        drawAllNodesAndConnections();
    }

    // Dibuja todos los nodos y sus conexiones al cargar desde archivo
    public void drawAllNodesAndConnections() {
        List<Node> nodes = nodesManager.getListNodes();
        for (Node node : nodes) {
            drawNode(node); // Dibujar el nodo
            drawConnections(node); // Dibujar las conexiones
        }
    }

    // Crear y dibujar un nodo en una ubicación dada
    public void createAndDrawNode(int[] location) {
        nodesManager.createNode(location); // Crear nodo en el NodesManager
        Node node = nodesManager.getNodeAtLocation(location);
        drawNode(node); // Dibujar el nodo
    }

    public void deleteAndRemoveCurrentNode() {
        deleteAndRemoveNode(nodesManager.getCurrentNode());
    }

    // Eliminar un nodo y su representación gráfica (círculo y conexiones)
    public void deleteAndRemoveNode(Node node) {
        if (node == null) return;

        // Eliminar conexiones visuales
        for (Connection connection : node.getConnections(node)) {
            removeConnectionVisual(node.getLocation(), connection); // Eliminar visualmente la línea
        }

        // Eliminar el nodo visualmente (círculo)
        drawer.removeCircle(node.getLocation());

        // Eliminar nodo de la lista
        nodesManager.deleteNode(node);
    }

    // Dibujar un nodo
    private void drawNode(Node node) {
        if (node == null) return;

        int[] location = node.getLocation();
        Color color = (node == nodesManager.getCurrentNode()) ? Color.RED : Color.BLUE; // Nodo actual en rojo, otros en azul
        drawer.drawCircle(location[0], location[1], color);
    }

    // Dibujar todas las conexiones de un nodo
    private void drawConnections(Node node) {
        if (node == null) return;

        for (Connection connection : node.getConnections(node)) {
            if (connection != null) {
                drawConnection(node.getLocation(), connection);
            }
        }
    }

    // Dibujar una conexión
    private void drawConnection(int[] startLocation, Connection connection) {
        if (connection == null) return;

        int[] endLocation = connection.getTargetNode().getLocation(); // Nodo de destino
        Color color = getDirectionColor(connection.getDirection()); // Color según la dirección
        drawer.drawLine(startLocation[0], startLocation[1], endLocation[0], endLocation[1], color);
    }

    // Eliminar visualmente una conexión (línea)
    private void removeConnectionVisual(int[] startLocation, Connection connection) {
        if (connection == null) return;

        int[] endLocation = connection.getTargetNode().getLocation(); // Nodo de destino
        drawer.removeLine(startLocation);
        drawer.removeLine(endLocation);
    }

    // Obtener el color según la dirección
    private Color getDirectionColor(Directions direction) {
        return switch (direction) {
            case IZQUIERDA -> Color.YELLOW;
            case DERECHA -> Color.ORANGE;
            case ADELANTE -> Color.GREEN;
            case CONTRARIO -> Color.RED;
        };
    }

    // Actualizar el nodo actual y redibujarlo en rojo
    public void updateCurrentNode(int[] point) {
        Circle circle = drawer.getCircleAt(point);
        if (circle != null) {
            point[0] = (int) circle.getCenterX();
            point[1] = (int) circle.getCenterY();
            updateCurrentNode(new Node(point));
        }
    }

    // Actualizar el nodo actual y redibujarlo en rojo
    public void updateCurrentNode(Node newCurrentNode) {
        Node previousNode = nodesManager.getCurrentNode();
        if (previousNode != null) {
            drawer.removeCircle(previousNode.getLocation()); // Remover la representación anterior
            drawNode(previousNode); // Redibujar en azul
        }

        // Actualizar el nodo actual
        nodesManager.setCurrentNode(newCurrentNode);

        // Dibujar el nuevo nodo actual en rojo
        drawNode(newCurrentNode);
    }

    // Eliminar una conexión de un nodo
    public void removeConnectionAndVisual(Directions direction) {
        Node node = nodesManager.getCurrentNode();
        if (node != null) {
            removeConnectionAndVisual(node, direction);
        }
    }

    // Eliminar una conexión de un nodo
    public void removeConnectionAndVisual(Node node, Directions direction) {
        if (node == null) return;

        nodesManager.removeConnection(node, direction); // Eliminar la conexión lógicamente
        Connection connection = nodesManager.getConnectionInDirection(node, direction);
        if (connection != null) {
            removeConnectionVisual(node.getLocation(), connection); // Eliminar visualmente la conexión
        }
    }

    public void createAndDrawConnection(int[] target, Directions direction) {
        target = existNodeAt(target);
        if (target == null) return;

        Node fromNode = nodesManager.getCurrentNode();
        Node toNode = nodesManager.getNodeAtLocation(target);
        if (toNode != null) {
            nodesManager.addConnection(fromNode, toNode, direction);
            drawConnection(fromNode.getLocation(), toNode.getConnection(direction));
        }
    }

    private int[] existNodeAt(int[] point) {
        Circle circle = drawer.getCircleAt(point);
        if (circle != null) {
            point[0] = (int) circle.getCenterX();
            point[1] = (int) circle.getCenterY();
            return point;
        }
        return null;
    }
}