package org.una.navigatetrack.controller;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import lombok.Getter;
import org.una.navigatetrack.roads.Connection;
import org.una.navigatetrack.roads.Directions;
import org.una.navigatetrack.roads.Node;

import java.util.List;

public class NodeDrawerManager {
    @Getter
    private final NodeManager nodeManager;
    private final Drawer drawer;

    public NodeDrawerManager(NodeManager nodeManager, Drawer drawer) {
        this.nodeManager = nodeManager;
        this.drawer = drawer;

        // Cargar y dibujar nodos al inicializar
        drawAllNodesAndConnections();
    }

    // Dibuja todos los nodos y sus conexiones al cargar desde archivo
    public void drawAllNodesAndConnections() {
        List<Node> nodes = nodeManager.getListNodes();
        for (Node node : nodes) {
            drawNode(node); // Dibujar el nodo
            drawConnections(node); // Dibujar las conexiones
        }
    }

    // Crear y dibujar un nodo en una ubicación dada
    public void createAndDrawNode(int[] location) {
        nodeManager.createNode(location); // Crear nodo en el NodeManager
        Node node = nodeManager.getNodeAtLocation(location);
        drawNode(node); // Dibujar el nodo
    }

    // Eliminar un nodo y su representación gráfica (círculo y conexiones)
    public void deleteAndRemoveNode(Node node) {
        // Eliminar conexiones visuales
        Connection[] connections = node.getConnections(node);
        for (Connection connection : connections) {
            removeConnectionVisual(node.getLocation(), connection); // Eliminar visualmente la línea
        }

        // Eliminar el nodo visualmente (círculo)
        drawer.removeCircle(node.getLocation());

        // Eliminar nodo de la lista
        nodeManager.deleteNode(node);
    }

    public void deleteAndRemoveCurrentNode() {
        // Eliminar conexiones visuales
        Node node = nodeManager.getCurrentNode();
        if (node == null) {
            return;
        }
        Connection[] connections = node.getConnections(node);
        if (connections != null) {
            for (Connection connection : connections) {
                removeConnectionVisual(node.getLocation(), connection); // Eliminar visualmente la línea
            }
        }

        // Eliminar el nodo visualmente (círculo)
        drawer.removeCircle(node.getLocation());

        // Eliminar nodo de la lista
        nodeManager.deleteNode(node);
    }

    // Dibujar un nodo
    private void drawNode(Node node) {
        int[] location = node.getLocation();
        Color color = node == nodeManager.getCurrentNode() ? Color.RED : Color.BLUE; // Nodo actual en rojo, otros en azul
        drawer.drawCircle(location[0], location[1], color);
    }

    private void drawNode(Node node, Color color) {
        int[] location = node.getLocation();
        drawer.drawCircle(location[0], location[1], color);
    }

    // Dibujar todas las conexiones de un nodo
    private void drawConnections(Node node) {
        Connection[] connections = node.getConnections(node);
        for (Connection connection : connections) {
            if (connection != null) {
                drawConnection(node.getLocation(), connection);
            }
        }
    }

    // Dibujar una conexión
    private void drawConnection(int[] startLocation, Connection connection) {
        // Nodo de inicio
        int[] endLocation = connection.getTargetNode().getLocation(); // Nodo de destino

        Color color = getDirectionColor(connection.getDirection()); // Color según la dirección
        drawer.drawLine(startLocation[0], startLocation[1], endLocation[0], endLocation[1], color);
    }

    private void drawConnection(int[] startLocation, int[] endLocation, Directions direction) {
        // Nodo de inicio
        // Nodo de destino
        Color color = getDirectionColor(direction); // Color según la dirección
        drawer.drawLine(startLocation[0], startLocation[1], endLocation[0], endLocation[1], color);
    }

    // Eliminar visualmente una conexión (línea)
    private void removeConnectionVisual(int[] startLocation, Connection connection) {
        // Nodo de inicio
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
        if (circle == null)
            return;
        point[0] = (int) circle.getCenterX();
        point[1] = (int) circle.getCenterY();
        updateCurrentNode(new Node(point));
    }

    // Actualizar el nodo actual y redibujarlo en rojo
    public void updateCurrentNode(Node newCurrentNode) {
        // Redibujar el nodo actual anterior en azul
        Node previousNode = nodeManager.getCurrentNode();
        if (previousNode != null) {
            drawer.removeCircle(previousNode.getLocation()); // Remover la representación anterior
            drawNode(previousNode, Color.BLUE); // Redibujar en azul
        }

        // Actualizar el nodo actual
        nodeManager.setCurrentNode(newCurrentNode);

        // Dibujar el nuevo nodo actual en rojo
        drawNode(newCurrentNode, Color.RED);
    }

    // Eliminar una conexión de un nodo
    public void removeConnectionAndVisual(Node node, Directions direction) {
        nodeManager.removeConnection(node, direction); // Eliminar la conexión lógicamente
        Connection connection = nodeManager.getConnectionInDirection(node, direction);
        if (connection != null) {
            removeConnectionVisual(node.getLocation(), connection); // Eliminar visualmente la conexión
        }
    }

    // Eliminar una conexión de un nodo
    public void removeConnectionAndVisual(Directions direction) {
        Node node = nodeManager.getCurrentNode();
        if (node == null) {
            return;
        }
        nodeManager.removeConnection(node, direction); // Eliminar la conexión lógicamente
        Connection connection = nodeManager.getConnectionInDirection(node, direction);
        if (connection != null) {
            removeConnectionVisual(node.getLocation(), connection); // Eliminar visualmente la conexión
        }
    }

    public void createAndDrawConnection(int[] target, Directions direction) {
        target = existNodeAt(target);
        if (target == null)
            return;

        Node fromNode = nodeManager.getCurrentNode();
        Node toNode = nodeManager.getNodeAtLocation(target);
        nodeManager.addConnection(fromNode, toNode, direction);
        int[] startLocation = fromNode.getLocation();

        drawConnection(startLocation, target, direction);
    }

    private int[] existNodeAt(int[] point) {
        Circle circle = drawer.getCircleAt(point);
        if (circle == null)
            return null;
        point[0] = (int) circle.getCenterX();
        point[1] = (int) circle.getCenterY();
        return point;
    }
}

