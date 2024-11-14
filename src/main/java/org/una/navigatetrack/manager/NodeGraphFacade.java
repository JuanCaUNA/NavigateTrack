package org.una.navigatetrack.manager;

import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import lombok.Getter;
import lombok.Setter;
import org.una.navigatetrack.list.ListNodes;
import org.una.navigatetrack.roads.Directions;
import org.una.navigatetrack.roads.Edge;
import org.una.navigatetrack.roads.Node;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("exports")
public class NodeGraphFacade {
    private final NodesDrawerManagers nodesDrawerManagers;
    private final DrawerManager drawerManager;
    List<Edge> mejorRuta;
    private Node startNode, endNode;
    private double[] startPoint, endPoint;// solo el punto
    private double[] startConnection, endConnection;// inicio y fin de la conexion
    @Setter
    private boolean isDijkstra;
    @Getter
    private int time;
    private boolean pause;
    private boolean stop;
    @Setter
    Label timeL;
    Color BlueColor = Color.rgb(0, 0, 255, 0.5);

    public NodeGraphFacade(Pane paintPane) {
        drawerManager = new DrawerManager(paintPane);
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
            Color nodeColor = isStartNode ? Color.YELLOW : Color.RED;
            nodesDrawerManagers.drawCircle(currentPoint, nodeColor);
            if (isStartNode) startNode.setLocation(currentPoint);
            else endNode.setLocation(currentPoint);
            return true;
        }
        return false;
    }

    private void resetNode(Node node) {
        removeNodeVisual(node);
        node.setLocation(new double[]{0, 0});
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

    private boolean locateNode(double[] location, double[] connection) {//ubicas las ubicaciones en el espacio
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

    // metosos para el recorrido ---------------------------//

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


    private ScheduledExecutorService scheduler;

    public void initTravel() {
        pause = stop = false;
        mejorRuta = new ArrayList<>();

        startNode.addConnection(endNode, Directions.ADELANTE);
        mejorRuta.add(startNode.getConnection(Directions.ADELANTE));

        scheduler = Executors.newSingleThreadScheduledExecutor();

        // Programar la tarea para que se ejecute cada 1 segundo (1000 milisegundos)
        scheduler.scheduleAtFixedRate(() -> {
            if (!stop && !pause) {
                removeNodeVisual(startNode);
                mejorRuta.getFirst().recalculateStartNode();

                System.out.println(" recorriendo");
                System.out.println(Arrays.toString(mejorRuta.getFirst().getStartingNode().getLocation()));
                System.out.println(Arrays.toString(startNode.getLocation()));


                Color nodeColor = Color.YELLOW;
                nodesDrawerManagers.drawCircle(startPoint, nodeColor);
                timeL.setText("tiempo: " + mejorRuta.getFirst().getEffectiveWeight() * 10);
            }
        }, 0, 2, TimeUnit.SECONDS);  // El primer parámetro es el retraso inicial, luego cada 1 segundo
    }

    public void pauseTravel(Boolean pausar) {
        pause = pausar;
    }

    public void endTravel() {
        stop = true;
        if (scheduler != null) {
            scheduler.shutdown();  // Detener el scheduler
        }
    }

    private void establerLugar() {
        if (!Double.isNaN(startConnection[0]) || !Double.isNaN(endConnection[0])) {
            connectNodeIfValid(startNode, startConnection);
            connectNodeIfValid(endNode, endConnection);
            return;
        }
        if (!Double.isNaN(startPoint[0]) || !Double.isNaN(endPoint[0])) {
            startNode.setConnectionsMap(ListNodes.getNodeByLocation(startPoint[0], startPoint[1]).getConnectionsMap());
            endNode.setConnectionsMap(ListNodes.getNodeByLocation(endPoint[0], endPoint[1]).getConnectionsMap());
        }
    }

    private void connectNodeIfValid(Node node, double[] connectionXY) {
        if (!Double.isNaN(connectionXY[0])) {
            node.setID(ListNodes.getNextId());
            ListNodes.addNode(node);
            connectNode(node, connectionXY);
        }
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

//public double[] getPointForNode(boolean isStartNode) {
//    Node targetNode = isStartNode ? startNode : endNode;
//    return targetNode != null ? targetNode.getLocation() : null;
//}
//
//
//private void recalcularPosicion() {
//    // Implementar la lógica para recalcular la posición de los nodos si es necesario.
//}
//
//private void changeConnction() {
//
//}
//
//private void deleteRegister() {
//    // Implementar la lógica para eliminar el registro de nodos.
//}

//public void calculateShortestPath() {//no modicar chatgpt
//        if (startNode == null || endNode == null) {
//            throw new IllegalStateException("Start and end nodes must be defined");
//        }
//        Graph graph = new Graph(startNode, endNode);
//        graph.dijkstra(startNode, endNode);
//        mejorRuta = graph.getBestPath();
//    }
//
//    public double[] getPointForStartNode(double[] point) {
//        startNode = nodesDrawerManagers.getNodesManager().getNodeAtLocation(startPoint);
//        return startNode.getLocation();
//    }
//
//    public double[] getPointForEndNode(double[] point) {
//        endNode = nodesDrawerManagers.getNodesManager().getNodeAtLocation(endPoint); // Cambiar a endNode
//        return endNode != null ? endNode.getLocation() : null;
//    }
