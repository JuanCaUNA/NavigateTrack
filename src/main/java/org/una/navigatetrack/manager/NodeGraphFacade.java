package org.una.navigatetrack.manager;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import org.una.navigatetrack.roads.Connection;
import org.una.navigatetrack.roads.Graph;
import org.una.navigatetrack.roads.Node;

import java.util.Map;

public class NodeGraphFacade {
    private final NodesDrawerManagers nodesDrawerManagers;
    private Node startNode, endNode;
    private double[] startPoint, endPoint;
    private double[] startConnection, endConnection;

    public NodeGraphFacade(Pane paintPane) {
        nodesDrawerManagers = new NodesDrawerManagers(new DrawerManager(paintPane));
    }

    public boolean setStartNode(double[] point) {
        if (startNode != null) {
            removeNodeVisual(startNode);
            startNode = null;
        }
        startPoint = point.clone(); // Clonamos para evitar referencias externas
        startConnection = new double[4];

        if (locateNode(startPoint, startConnection)) {
            nodesDrawerManagers.drawCircle(startPoint, Color.GREEN);
            return true;
        }
        return false;
    }

    public boolean setEndNode(double[] point) {
        if (endNode != null) {
            removeNodeVisual(endNode);
            endNode = null;
        }
        endPoint = point.clone();
        endConnection = new double[4];

        if (locateNode(endPoint, endConnection)) {
            nodesDrawerManagers.drawCircle(endPoint, Color.RED);
            return true;
        }
        return false;
    }

    private void removeNodeVisual(Node node) {
        nodesDrawerManagers.removeCircle(node.getLocation());
    }

    private boolean locateNode(double[] location, double[] connection) {
        DrawerManager drawerManager = nodesDrawerManagers.getDrawerManager();

        double[] relocation = nodesDrawerManagers.getLocationIfExistNodeAt(location);
        if (relocation != null) {
            location[0] = relocation[0];
            location[1] = relocation[1];
            connection[0] = Double.NaN;
            return true;
        }

        double[] array = drawerManager.getLineAtWithCircle(location);
        if (array != null) {
            System.arraycopy(array, 0, connection, 0, 4);
            location[0] = array[4];
            location[1] = array[5];
            return true;
        }

        connection[0] = Double.NaN;
        location[0] = Double.NaN;
        return false;
    }

    private void locateNodes() {
        if (Double.isNaN(startPoint[0]) || Double.isNaN(endPoint[0])) return;

        startNode = new Node(startPoint);
        endNode = new Node(endPoint);

        connectNodeIfValid(startNode, startConnection);
        connectNodeIfValid(endNode, endConnection);
    }

    private void connectNodeIfValid(Node node, double[] connectionXY) {
        if (!Double.isNaN(connectionXY[0])) {
            connectNode(node, connectionXY);
        }
    }

    private void connectNode(Node node, double[] connectionXY) {
        double[] initP = {connectionXY[0], connectionXY[1]};
        double[] endP = {connectionXY[2], connectionXY[3]};

        Node init = nodesDrawerManagers.getNodesManager().getNodeAtLocation(initP);
        Node end = nodesDrawerManagers.getNodesManager().getNodeAtLocation(endP);

        if (init == null || end == null) {
            System.out.println("Init or end is null");
            return;
        }

        // Conectar nodos
        if (!init.isConnectedToNode(end)) {
            node.addConnection(end, init.getDirConnectedToNode(end));
            init.changeConnectionIn(end, node);
        }
    }

    public double[] getPointForNode(boolean isStartNode) {
        Node targetNode = isStartNode ? startNode : endNode;
        return targetNode != null ? targetNode.getLocation() : null;
    }


    public void recalcularPosicion() {
        // Implementar la lógica para recalcular la posición de los nodos si es necesario.
    }

    public void changeConnction() {

    }

    public void deleteRegister() {
        // Implementar la lógica para eliminar el registro de nodos.
    }

    public Graph createGraph() {
        if (startNode == null || endNode == null) {
            throw new IllegalStateException("Start and end nodes must be defined");
        }
        return new Graph(startNode, endNode); // Crear y retornar un nuevo grafo
    }

    public Map<Connection, Integer> calculateShortestPath() {
        Graph graph = createGraph();
        return graph.dijkstra(startNode, endNode);
    }

    public double[] getPointForStartNode(double[] point) {
        startNode = nodesDrawerManagers.getNodesManager().getNodeAtLocation(startPoint);
        return startNode.getLocation();
    }

    public double[] getPointForEndNode(double[] point) {
        endNode = nodesDrawerManagers.getNodesManager().getNodeAtLocation(endPoint); // Cambiar a endNode
        return endNode != null ? endNode.getLocation() : null;
    }

    // Otros métodos que faltan implementar según la lógica necesaria
    // void getApproximateLocation();
    // void getRecorrido();
    // void drawLinesOfRecorrido();
    // void getPrice();
    // void getTime();
    // void setTypeVoyage();
    // void startVoyage();
    // void endVoyage();

    private Node createNode(double[] point) {
        return new Node(point);
    }
}
