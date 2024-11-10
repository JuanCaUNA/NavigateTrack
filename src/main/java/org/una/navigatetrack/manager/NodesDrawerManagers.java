package org.una.navigatetrack.manager;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import lombok.Getter;
import org.una.navigatetrack.roads.Connection;
import org.una.navigatetrack.roads.Directions;
import org.una.navigatetrack.list.ListNodes;
import org.una.navigatetrack.roads.Node;
import org.una.navigatetrack.utils.AppContext;

import java.util.List;

@Getter
public class NodesDrawerManagers {
    private final DrawerManager drawerManager;
    private final NodesManager nodesManager;

    private Node currentNode;

    public NodesDrawerManagers(DrawerManager drawerManager) {
        AppContext appContext = AppContext.getInstance();
        this.nodesManager = appContext.getNodesManager();
        this.drawerManager = drawerManager;

        drawAllNodesAndConnections();
    }

    public NodesDrawerManagers(DrawerManager drawerManager, Boolean edicion) {
        AppContext appContext = AppContext.getInstance();
        this.nodesManager = appContext.getNodesManager();
        this.drawerManager = drawerManager;

        if (edicion)
            drawAllNodesAndConnections();
        else
            drawAllConnections();
    }

    private void drawAllConnections() {
        List<Node> list = ListNodes.getNodesList();
        for (Node node : list) {
            drawConnections(node, Color.BLUE);
        }
    }

    // Definition of nodes and connections
    public void createAndDrawNode(double[] location) {
        nodesManager.addNode(location);
        currentNode = nodesManager.getNodeAtLocation(location);
        drawNode(nodesManager.getNodeAtLocation(location));
    }

    public void createAndDrawConnection(double[] target, Directions direction) {
        target = getLocationIfExistNodeAt(target);
        if (target == null) return;

        Node fromNode = currentNode;
        Node toNode = nodesManager.getNodeAtLocation(target);
        if (toNode != null && fromNode != null) {
            nodesManager.addConnection(fromNode.getID(), toNode.getID(), direction);
            drawConnection(fromNode.getConnection(direction));
        }
    }
    // Definition of nodes and connections end

    // Drawings
    public void drawAllNodesAndConnections() {
        List<Node> list = ListNodes.getNodesList();
        for (Node node : list) {
            drawNode(node);
            drawConnections(node);
        }
    }

    private void drawNode(Node node) {
        drawNode(node, Color.BLUE); // By default, nodes are drawn in blue.
    }

    private void drawNode(Node node, Color color) {
        if (node == null) return;
        double[] location = node.getLocation();
        drawerManager.drawCircle(location[0], location[1], color);
    }

    private void drawConnections(Node node) {
        if (node == null) return;
        for (Connection connection : node.getAllConnections()) {
            if (connection != null) {
                drawConnection(connection);
            }
        }
    }

    private void drawConnections(Node node, Color color) {
        if (node == null) return;
        for (Connection connection : node.getAllConnections()) {
            if (connection != null) {
                drawConnection(connection, color);
            }
        }
    }

    private void drawConnection(Connection connection) {
        if (connection == null) return;
        Color color = getDirectionColor(connection.getDirection());
        drawConnection(connection, color);
    }

    private void drawConnection(Connection connection, Color color) {
        if (connection == null) return;
        double[] startLocation = connection.getStartingNode().getLocation();
        double[] endLocation = connection.getDestinationNode().getLocation();
        drawerManager.drawLine(startLocation[0], startLocation[1], endLocation[0], endLocation[1], color);
    }


    // Drawings end

    // Delete drawings
    public void deleteAndRemoveCurrentNode() {
        if (currentNode == null) return;

        // Remove visual connections to the current node before deletion
        for (Connection connection : currentNode.getAllConnections()) {//actualizar
            removeConnectionVisual(currentNode.getLocation(), connection);
        }

        drawerManager.removeCircle(currentNode.getLocation()); // Remove visual representation of the node
        nodesManager.deleteNode(currentNode.getID()); // Remove node from manager
        currentNode = null; // Clear the current node
    }

    public void removeConnectionAndVisual(Directions direction) {
        if (currentNode != null) {
            removeConnectionAndVisual(currentNode, direction);
            currentNode.deleteConnection(direction); // This should only delete the connection from the current node
        }
    }

    public void removeConnectionAndVisual(Node node, Directions direction) {
        if (node == null) return;

        Connection connection = nodesManager.getConnectionInDirection(node.getID(), direction);
        if (connection != null) {
            removeConnectionVisual(node.getLocation(), connection);
        }

        nodesManager.removeConnection(node.getID(), direction); // Remove connection from the manager
    }

    private void removeConnectionVisual(double[] startLocation, Connection connection) {
        if (connection == null) return;
        double[] endLocation = connection.getDestinationNode().getLocation();
        drawerManager.removeLine(startLocation, endLocation); // Remove the visual line connecting the nodes
    }
    // Delete drawings end

    // Current node management
    public void updateCurrentNode(double[] point) {
        point = getLocationIfExistNodeAt(point);

        if (point == null) return;

        updateCurrentNode(nodesManager.getNodeAtLocation(point));
    }

    private void updateCurrentNode(Node newCurrentNode) {
        if (newCurrentNode == null) return;

        Node previousNode = currentNode;
        currentNode = newCurrentNode;

        // Restore the visual representation of the previous node
        if (previousNode != null) {
            drawerManager.removeCircle(previousNode.getLocation());
            drawNode(previousNode, Color.BLUE); // Restore previous node in blue
        }

        drawerManager.removeCircle(newCurrentNode.getLocation());
        drawNode(newCurrentNode, Color.RED); // Highlight the current node in red
    }
    // Current node end

    // Others
    public double[] getLocationIfExistNodeAt(double[] point) {
        Circle circle = drawerManager.getCircleAt(point);
        if (circle != null) {
            return new double[]{(int) circle.getCenterX(), (int) circle.getCenterY()};
        }
        return null; // Return null if no node exists at the point
    }

    private Color getDirectionColor(Directions direction) {
        return switch (direction) {
            case IZQUIERDA -> Color.YELLOW;
            case DERECHA -> Color.ORANGE;
            case ADELANTE -> Color.GREEN;
            case CONTRARIO -> Color.RED;
        };
    }

    public void drawCircle(double[] point, Color color) {
        drawerManager.drawCircle(point[0], point[1], color);
    }

    public void removeCircle(double[] point) {
        drawerManager.removeCircle(point);
    }
    // Others end
}
