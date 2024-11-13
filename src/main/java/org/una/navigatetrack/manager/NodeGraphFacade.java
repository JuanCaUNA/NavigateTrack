package org.una.navigatetrack.manager;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import lombok.Getter;
import lombok.Setter;
import org.una.navigatetrack.list.ListNodes;
import org.una.navigatetrack.roads.Edge;
import org.una.navigatetrack.roads.Graph;
import org.una.navigatetrack.roads.Node;

import java.util.List;

@SuppressWarnings("exports")
public class NodeGraphFacade {
    private final NodesDrawerManagers nodesDrawerManagers;
    List<Edge> mejorRuta;
    private Node startNode, endNode;
    private double[] startPoint, endPoint;
    private double[] startConnection, endConnection;
    @Setter
    private boolean isDijkstra;
    @Getter
    private int time;

    public NodeGraphFacade(Pane paintPane) {
        nodesDrawerManagers = new NodesDrawerManagers(new DrawerManager(paintPane), false);
        startNode = new Node();
        endNode = new Node();
        startNode.setID(ListNodes.getNextId());
        endNode.setID(ListNodes.getNextId() + 1);

        ListNodes.addNode(startNode);
        ListNodes.addNode(endNode);
    }

    //metodos para definir el inicio y el final -----------------//
    private boolean setNode(double[] point, boolean isStartNode) {
        Node currentNode = isStartNode ? startNode : endNode;
        double[] currentPoint = isStartNode ? startPoint : endPoint;
        double[] currentConnection = isStartNode ? startConnection : endConnection;

        if (currentNode != null)
            resetNode(currentNode);

        currentPoint = point.clone();
        currentConnection = new double[4];

        // Intentamos localizar el nodo
        if (locateNode(currentPoint, currentConnection)) {
            Color nodeColor = isStartNode ? Color.GREEN : Color.RED;
            nodesDrawerManagers.drawCircle(currentPoint, nodeColor);
            if (isStartNode) startNode = new Node(currentPoint);
            else endNode = new Node(currentPoint);

            return true;
        }
        return false;
    }

    private void resetNode(Node node) {
        removeNodeVisual(node);
        node.setLocation(new double[]{0, 0});
        node.deleteConnections();
    }

    public boolean setStartNode(double[] point) {
        return setNode(point, true);
    }

    public boolean setEndNode(double[] point) {
        return setNode(point, false);
    }

    private void removeNodeVisual(Node node) {
        nodesDrawerManagers.removeCircle(node.getLocation());
    }

    private boolean locateNode(double[] location, double[] connection) {
        double[] relocated = nodesDrawerManagers.getLocationIfExistNodeAt(location);
        if (relocated != null) {
            System.arraycopy(relocated, 0, location, 0, 2);  // Actualizamos la ubicación
            connection[0] = Double.NaN;  // Desconectamos, no se usa
            return true;
        }

        double[] array = nodesDrawerManagers.getDrawerManager().getLineAtWithCircle(location);
        if (array != null) {
            System.arraycopy(array, 0, connection, 0, 4);  // Establecemos la conexión
            location[0] = array[4];
            location[1] = array[5];
            return true;
        }

        // Si no se encuentra el nodo
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

    // metosos para el recorrido ---------------------------//

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


    private void recalcularPosicion() {
        // Implementar la lógica para recalcular la posición de los nodos si es necesario.
    }

    private void changeConnction() {

    }

    private void deleteRegister() {
        // Implementar la lógica para eliminar el registro de nodos.
    }

    public void initTravel() {

    }

    public void endTravel() {

    }

    public void pauseTravel() {

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

    public Edge getConnection(double x, double y) {
        double[] locations = nodesDrawerManagers.getDrawerManager().getLineAtWithCircle(x, y);
        if (locations == null)
            return null;

        Node node1, node2;
        Edge edge;
        node1 = ListNodes.getNodeByLocation(locations[0], locations[1]);
        node2 = ListNodes.getNodeByLocation(locations[2], locations[3]);
        edge = node1.getConnectionInNode(node2.getID());

        return (edge == null) ? node2.getConnectionInNode(node1.getID()) : edge;
    }

    public void reDrawEdge(Edge edge, Color color) {
        if (edge != null) {
            nodesDrawerManagers.getDrawerManager().removeLine(edge.connectionline());
            nodesDrawerManagers.getDrawerManager().drawLine(edge.connectionline(), color);
        }
    }
}
