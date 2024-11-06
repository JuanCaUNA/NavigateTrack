package org.una.navigatetrack.manager;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import org.una.navigatetrack.roads.Connection;
import org.una.navigatetrack.roads.Graph;
import org.una.navigatetrack.list.ListNodes;
import org.una.navigatetrack.roads.Node;

import java.util.List;

public class NodeGraphFacade {
    private final NodesDrawerManagers nodesDrawerManagers;
    private Node startNode, endNode;
    private double[] startPoint, endPoint;
    private double[] startConnection, endConnection;

    List<Connection> mejorRuta;

    public NodeGraphFacade(Pane paintPane) {
        nodesDrawerManagers = new NodesDrawerManagers(new DrawerManager(paintPane), false);
    }

    public boolean setStartNode(double[] point) {
        if (startNode != null) {
            removeNodeVisual(startNode);
            startNode = null;
        }
        startPoint = point.clone();
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
        double[] relocation = nodesDrawerManagers.getLocationIfExistNodeAt(location);
        if (relocation != null) {
            location[0] = relocation[0];
            location[1] = relocation[1];
            connection[0] = Double.NaN;
            return true;
        }

        double[] array = nodesDrawerManagers.getDrawerManager().getLineAtWithCircle(location);
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
            node.setID(ListNodes.getNextId());
            ListNodes.addNode(node);
            connectNode(node, connectionXY);
        }
    }

    /*
        if: init --> end
            init--> nodo --> end

        if: end--> init
            end--> nodo --> init
    */
    private void connectNode(Node node, double[] connectionXY) {
        double[] initP = {connectionXY[0], connectionXY[1]};
        double[] endP = {connectionXY[2], connectionXY[3]};

        Node init = nodesDrawerManagers.getNodesManager().getNodeAtLocation(initP);
        Node end = nodesDrawerManagers.getNodesManager().getNodeAtLocation(endP);

        if (init == null || end == null) {
            System.out.println("Init or end is null");
            return;
        }

        // Conectar nodos si no están conectados
        if (init.isConnectedToNode(end)) { //
            node.addConnection(end, init.getDirConnectedToNode(end)); //
            init.changeConnectionIn(end, node);// en esta end remplaza por esta node
        }
        // Conectar nodos si no están conectados
        if (end.isConnectedToNode(end)) { //
            node.addConnection(init, end.getDirConnectedToNode(init)); //
            end.changeConnectionIn(init, node);// en esta end remplaza por esta node
        }

    }

    /*
        if: nodo --> end
            init --> end
            nodo     end

        if: nodo --> init
            end  --> init
            nodo     init
    */
    private void disconnectNode(Node node, double[] connectionXY) {
        double[] initP = {connectionXY[0], connectionXY[1]};
        double[] endP = {connectionXY[2], connectionXY[3]};

        Node init = nodesDrawerManagers.getNodesManager().getNodeAtLocation(initP);
        Node end = nodesDrawerManagers.getNodesManager().getNodeAtLocation(endP);

        // Conectar nodos si no están conectados
        if (node.isConnectedToNode(end)) { //
            init.addConnection(end, init.getDirConnectedToNode(end)); //
            init.changeConnectionIn(end, node);// en esta end remplaza por esta node
        }
        // Conectar nodos si no están conectados
        if (end.isConnectedToNode(end)) { //
            node.addConnection(init, end.getDirConnectedToNode(init)); //
            end.changeConnectionIn(init, node);// en esta end remplaza por esta node
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


    public void calculateShortestPath() {//no modicar chatgpt
        if (startNode == null || endNode == null) {
            throw new IllegalStateException("Start and end nodes must be defined");
        }
        Graph graph = new Graph(startNode, endNode);
        graph.dijkstra(startNode, endNode);
        mejorRuta = graph.getBestPath();
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
