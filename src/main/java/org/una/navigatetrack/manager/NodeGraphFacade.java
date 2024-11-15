package org.una.navigatetrack.manager;

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import lombok.Getter;
import lombok.Setter;
import org.una.navigatetrack.dto.EdgeDTO;
import org.una.navigatetrack.list.ListNodes;
import org.una.navigatetrack.roads.Directions;
import org.una.navigatetrack.roads.Edge;
import org.una.navigatetrack.roads.Graph;
import org.una.navigatetrack.roads.Node;
import org.una.navigatetrack.utils.AppContext;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class NodeGraphFacade {

    private final NodesDrawerManagers nodesDrawerManagers;
    private final DrawerManager localDrawManager;
    private ScheduledExecutorService scheduler;

    private Graph graph;
    private List<Edge> bestPath;
    private EdgeDTO tempEdgeDTO;

    @Setter
    private Label timeL;
    @Setter
    private Button travelB, pauseB;

    @Setter
    private boolean isDijkstra;
    @Getter
    private Node startNode, endNode;
    private boolean pause, stop;

    private final double[] startPoint = new double[2], endPoint = new double[2];
    private final double[] startConnection = new double[4], endConnection = new double[4];

    private static final Color BLUE_COLOR = Color.rgb(0, 0, 255, 0.5);
    private static final Color START_NODE_COLOR = Color.YELLOW;
    private static final Color END_NODE_COLOR = Color.RED;
    private static final Color EDGE_COLOR = Color.GREEN;

    public NodeGraphFacade(Pane paintPane) {
        nodesDrawerManagers = new NodesDrawerManagers(new DrawerManager(paintPane), false);
        localDrawManager = new DrawerManager(paintPane);

        startNode = new Node();
        endNode = new Node();

        startNode.setID(ListNodes.getNextId());
        endNode.setID(ListNodes.getNextId() + 1);

        ListNodes.addNode(startNode);
        ListNodes.addNode(endNode);

        startNode.setEmptyValues(true);
        startNode.setStarNode(true);
        endNode.setEmptyValues(true);
    }

    // Métodos para configurar los nodos

    public boolean setStartNode(double[] point) {
        return setNode(point, startNode);
    }

    public boolean setEndNode(double[] point) {
        return setNode(point, endNode);
    }

    private boolean setNode(double[] point, Node currentNode) {
        double[] currentPoint = currentNode.isStarNode() ? startPoint : endPoint;
        double[] currentConnection = currentNode.isStarNode() ? startConnection : endConnection;

        if (!currentNode.isEmptyValues()) resetNode(currentNode, currentConnection);

        System.arraycopy(point, 0, currentPoint, 0, 2);
        Arrays.fill(currentConnection, Double.NaN);

        if (locateNode(currentPoint, currentConnection)) {

            int a = (int) currentPoint[0];
            int b = (int) currentPoint[1];
            currentPoint[0] = a;
            currentPoint[1] = b;


            if (currentNode.isStarNode()) {
                drawLocalCircle(currentPoint, START_NODE_COLOR);
            } else {
                nodesDrawerManagers.drawCircle(currentPoint, END_NODE_COLOR);
            }

            currentNode.setLocation(currentPoint);
            currentNode.setEmptyValues(false);
            return true;
        }
        return false;
    }

    private void resetNode(Node node, double[] currentConnection) {
        node.getAllConnections().forEach(edge -> nodesDrawerManagers.getDrawerManager().removeLine(edge.connectionline()));

        disconnectNode(node, currentConnection);

        removeNodeVisual(node);

        node.setLocation(new double[]{0, 0});
        node.setEmptyValues(true);
    }

    private void removeNodeVisual(Node node) {
        if (node.isStarNode()) {
            removeDrawLocalCircle(node.getLocation());
        } else {
            nodesDrawerManagers.removeCircle(node.getLocation());
        }
    }

    private boolean locateNode(double[] location, double[] connection) {
        double[] array = nodesDrawerManagers.getDrawerManager().getLineAtWithCircle(location);
        if (array != null) {
            System.arraycopy(array, 0, connection, 0, 4);
            location[0] = array[4];
            location[1] = array[5];
            return true;
        }

        Arrays.fill(connection, Double.NaN);
        Arrays.fill(location, Double.NaN);
        return false;
    }

    // Métodos de conexión y desconexión de nodos

    private void connectNode(Node node, double[] connectionXY) {
        Node init = getNodeAtLocation(connectionXY[0], connectionXY[1]);
        Node end = getNodeAtLocation(connectionXY[2], connectionXY[3]);


        if (init == null || end == null) {
            System.out.println("error de nulo inicio y fin");
            return;
        }

        System.out.println(init.toString());
        System.out.println(end.toString());
        System.out.println(node.toString());

        connectNodes(init, end, node);
        connectNodes(end, init, node);

        System.out.println(init.toString());
        System.out.println(end.toString());
        System.out.println(node.toString());

    }

    private Node getNodeAtLocation(double x, double y) {
        return nodesDrawerManagers.getNodesManager().getNodeAtLocation(new double[]{x, y});
    }

    private void connectNodes(Node node1, Node node2, Node node) {
        if (node1.isConnectedToNode(node2)) {
            Directions dir = node1.getDirConnectedToNode(node2);

            node.addConnection(node2, dir);

            node1.changeConnectionIn(node2, node);
        }
    }

    private void disconnectNode(Node node, double[] connectionXY) {
        Node init = getNodeAtLocation(connectionXY[0], connectionXY[1]);
        Node end = getNodeAtLocation(connectionXY[2], connectionXY[3]);

        connectNodes(node, end, init);//restablecer
        connectNodes(node, init, node);//retablecer

        if (node.isConnectedToNode(end)) node.removeConnection(end);
        if (node.isConnectedToNode(init)) node.removeConnection(init);
    }

    // para iniciar el ciclo de viaje

    public boolean initTravel() {
        if (startNode.isEmptyValues() || endNode.isEmptyValues()) {
            AppContext.getInstance().createNotification("No seleccionado", "Faltan los puntos de inicio y destino.");
            return false;
        }

        try {
            pause = stop = false;

            connectNode(endNode, endConnection);//conecta el nodo al grafo
            connectNode(startNode, startConnection);//conecta el nodo al grafo

            if (!loadBestPath())
                return false;

//             Dibujar las conexiones
            for (Edge edge : bestPath) {
                if (edge != null) drawEdgeLocal(edge, EDGE_COLOR);
                else {
                    System.err.println("conexion vacia");
                    return false;
                }
            }


            tempEdgeDTO = new EdgeDTO(bestPath.getFirst());

            startTravelCycle();

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private void startTravelCycle() {

        scheduler = Executors.newSingleThreadScheduledExecutor();
        System.out.println("Scheduler creado y listo para ejecutar.");
        try {

            scheduler.scheduleAtFixedRate(() -> {
                if (!stop && !pause) Platform.runLater(this::executeTravelCycleStep);
            }, 0, 1, TimeUnit.SECONDS);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void executeTravelCycleStep() {

        Edge currentE = bestPath.getFirst();
        Node currentN = currentE.getStartingNode();

        deleteDrawEdgeLocal(currentE);
        removeDrawLocalCircle(currentN.getLocation());

        currentE.recalculateStartNode();

        System.out.println("ciclo");
        System.out.println(currentE.toString());
        System.out.println(currentN.toString());

        double time = currentE.getEffectiveWeight();
        timeL.setText("Tiempo: " + time);

        System.out.println("fin");
        System.out.println(endNode.toString());
        if (time <= 0.0) {
            System.out.println(endNode.toString());
            updateTravelCycle(currentE);
            if (currentE.getDestinationNodeID() == endNode.getID()) {
                endTravel();
            } else {
                bestPath.removeFirst();
                tempEdgeDTO = new EdgeDTO(bestPath.getFirst());
            }
        } else {
            drawEdgeLocal(currentE, EDGE_COLOR);
            drawLocalCircle(currentN.getLocation(), START_NODE_COLOR);
            System.out.println(" detino" + currentE.getDestinationNode().toString());
        }

    }

    private void updateTravelCycle(Edge currentE) {
        currentE.setWeight(tempEdgeDTO.getWeight());
        currentE.getStartingNode().setLocation(tempEdgeDTO.getPointStart());
    }

    public void pauseTravel(Boolean pausar) {
        pause = pausar;  // Establecemos el estado de pausa
        System.out.println("Viaje pausado: " + pause);
    }

    public void endTravel() {
        stop = true;
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
        }


        disconnectNode(startNode, startConnection);
        disconnectNode(endNode, endConnection);
    }

    private boolean loadBestPath() {
        graph = new Graph(startNode, endNode);
        boolean exito = isDijkstra ? graph.runDijkstra() : graph.runFloydWarshall();

        if (!exito) {
            System.out.println("No se encontró ruta.");
            return false;
        }
        bestPath = graph.getBestPathEdges();
        return true;
    }

    private void drawLocalCircle(double[] point, Color color) {
        localDrawManager.drawCircle(point[0], point[1], color);
    }

    private void removeDrawLocalCircle(double[] point) {
        localDrawManager.removeCircle(point);
    }

    private void drawEdgeLocal(Edge edge, Color color) {
        if (edge == null) {
            System.err.println("edge null: deleteDrawEdgeLocal");
            return;
        }
        localDrawManager.drawLine(edge.connectionline(), color);
    }

    private void deleteDrawEdgeLocal(Edge edge) {
        if (edge == null) {
            System.err.println("edge null: deleteDrawEdgeLocal");
            return;
        }
        localDrawManager.removeLine(edge.connectionline());

    }

    public void reDrawEdge(Edge edge, Color color) {
        if (edge != null) {
            nodesDrawerManagers.getDrawerManager().removeLine(edge.connectionline());
            nodesDrawerManagers.getDrawerManager().drawLine(edge.connectionline(), color);
        }
    }

    public Edge getConnection(double x, double y) {
        double[] locations = nodesDrawerManagers.getDrawerManager().getLineAtWithCircle(x, y);
        if (locations == null) return null;

        Node node1 = ListNodes.getNodeByLocation(locations[0], locations[1]);
        Node node2 = ListNodes.getNodeByLocation(locations[2], locations[3]);
        return node1.getConnectionInNode(node2.getID()) != null ? node1.getConnectionInNode(node2.getID()) : node2.getConnectionInNode(node1.getID());
    }
}

