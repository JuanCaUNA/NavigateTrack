package org.una.navigatetrack.manager;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import lombok.Getter;
import org.una.navigatetrack.roads.Connection;
import org.una.navigatetrack.roads.Directions;
import org.una.navigatetrack.roads.Node;
import org.una.navigatetrack.utils.Singleton;

@Getter
public class NodesDrawerManagers {
    private final DrawerManager drawerManager;
    private final NodesManager nodesManager;

    private Node currentNode;

    public NodesDrawerManagers(DrawerManager drawerManager) {
        Singleton singleton = Singleton.getInstance();
        this.nodesManager = singleton.getNodesManager();
        this.drawerManager = drawerManager;

        drawAllNodesAndConnections();
    }

    //definition of nodes and connections
    public void createAndDrawNode(int[] location) {
        nodesManager.addNode(location);
        drawNode(nodesManager.getNodeAtLocation(location));
    }

    public void createAndDrawConnection(int[] target, Directions direction) {
        target = getLocationIfExistNodeAt(target);
        if (target == null) return;
        Node fromNode = currentNode;
        Node toNode = nodesManager.getNodeAtLocation(target);
        if (toNode != null && fromNode != null) {
            nodesManager.addConnection(currentNode, toNode, direction);
            drawConnection(fromNode.getLocation(), fromNode.getConnection(direction));
        }
    }
    //definition of nodes and connections end

    //drawings
    public void drawAllNodesAndConnections() {
        for (Node node : nodesManager.getListNodes()) {
            drawNode(node);
            drawConnections(node);
        }
    }

    private void drawNode(Node node) {
        drawNode(node, Color.BLUE);
    }//by default

    private void drawNode(Node node, Color color) {
        if (node == null) return;
        int[] location = node.getLocation();
        drawerManager.drawCircle(location[0], location[1], color);
    }

    private void drawConnections(Node node) {
        if (node == null) return;

        for (Connection connection : node.getConnections(node)) {
            if (connection != null) drawConnection(node.getLocation(), connection);
        }
    }

    private void drawConnection(int[] startLocation, Connection connection) {
        if (connection == null) return;

        int[] endLocation = connection.getTargetNode().getLocation();
        Color color = getDirectionColor(connection.getDirection());
        drawerManager.drawLine(startLocation[0], startLocation[1], endLocation[0], endLocation[1], color);
    }
    //drawings end

    //delete drawings
    public void deleteAndRemoveCurrentNode() {
        for (Connection connection : currentNode.getConnections()) {
            removeConnectionVisual(currentNode.getLocation(), connection);
        }

        drawerManager.removeCircle(currentNode.getLocation());
        nodesManager.deleteNode(currentNode);
        currentNode = null;
    }

    public void removeConnectionAndVisual(Directions direction) {
        if (currentNode != null) {
            removeConnectionAndVisual(currentNode, direction);
            currentNode.deleteConnection(direction);
        }
    }

    public void removeConnectionAndVisual(Node node, Directions direction) {
        if (node == null) return;

        nodesManager.removeConnection(node, direction);
        Connection connection = nodesManager.getConnectionInDirection(node, direction);
        if (connection != null) {
            removeConnectionVisual(node.getLocation(), connection);
        }
    }

    private void removeConnectionVisual(int[] startLocation, Connection connection) {
        if (connection == null) return;
        int[] endLocation = connection.getTargetNode().getLocation();
        drawerManager.removeLine(startLocation, endLocation);
    }
    //delete drawings end

    //current node
    public void updateCurrentNode(int[] point) {
        point = getLocationIfExistNodeAt(point);

        if (point == null) return;

        updateCurrentNode(nodesManager.getNodeAtLocation(point));
    }

    private void updateCurrentNode(Node newCurrentNode) {
        if (newCurrentNode == null) return;

        Node previousNode = currentNode;
        currentNode = newCurrentNode;

        if (previousNode != null) {
            drawerManager.removeCircle(previousNode.getLocation());
            drawNode(previousNode, Color.BLUE);
        }

        drawNode(newCurrentNode, Color.RED);
    }
    //current node end

    //others
    private int[] getLocationIfExistNodeAt(int[] point) {
        Circle circle = drawerManager.getCircleAt(point);
        if (circle != null) {
            point[0] = (int) circle.getCenterX();
            point[1] = (int) circle.getCenterY();
            return point;
        }
        return null;
    }

    private Color getDirectionColor(Directions direction) {
        return switch (direction) {
            case IZQUIERDA -> Color.YELLOW;
            case DERECHA -> Color.ORANGE;
            case ADELANTE -> Color.GREEN;
            case CONTRARIO -> Color.RED;
        };
    }
    //others end
}