package org.una.navigatetrack.manager;

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import lombok.Getter;
import lombok.Setter;
import org.una.navigatetrack.dto.EdgeDTO;
import org.una.navigatetrack.list.ListConnections;
import org.una.navigatetrack.list.ListNodes;
import org.una.navigatetrack.roads.Directions;
import org.una.navigatetrack.roads.Edge;
import org.una.navigatetrack.roads.Graph;
import org.una.navigatetrack.roads.Node;
import org.una.navigatetrack.utils.AppContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class NodeGraphFacade {

    private final NodesDrawerManagers nodesDrawerManagers;
    private final DrawerManager localDrawManager;
    private ScheduledExecutorService scheduler;

    private Graph graph, graph2;
    private List<Edge> bestPath, usePath;
    private EdgeDTO tempEdgeDTO;
    private Double totalWeight;

    @Setter
    private Label timeL, precioL, preciofinalL;
    @Setter
    private Button algoritmoB, pauseB, startB;
    @Setter
    private TextArea infoTA;
    @Setter
    private boolean isDijkstra;
    @Getter
    private Node startNode, endNode;
    private Node[] nodesInit, nodesEnd;

    private Edge currentEdge;


    private boolean pause, stop, completedTravel = true;
    private int initWeigh, estimateTime, tiempoDetenido, timetranscurrido;

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
    }//Listo

    // Métodos para configurar los nodos

    public boolean setStartNode(double[] point) {
        return setNode(point, startNode);
    }//lisyo

    public boolean setEndNode(double[] point) {
        return setNode(point, endNode);
    }//listo

    private boolean setNode(double[] point, Node currentNode) {

        double[] currentPoint = currentNode.isStarNode() ? startPoint : endPoint;
        double[] currentConnection = currentNode.isStarNode() ? startConnection : endConnection;

        if (!currentNode.isEmptyValues()) resetNode(currentNode);

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

    private void resetNode(Node node) {
        removeNodeVisual(node);
        node.setLocation(new double[]{0, 0});
        node.setEmptyValues(true);
    }

    private void removeNodeVisual(Node node) {
        if (node.isStarNode()) {
            localDrawManager.removeCircles();
//            removeDrawLocalCircle(node.getLocation());
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

    /**
     * nodes != null
     * if nodes[0] is connect to nodes[1]: node connect to nodes[1]
     * or
     * if nodes[1] is connect to nodes[0]: node connect to nodes[0]
     *
     * @param node  (node startNode/endNode)
     * @param nodes (array 2 nodes nodesInit/nodesEnd)
     */
    private void connectNode(Node node, Node[] nodes) {
        if (nodes == null) {
            System.out.println("null nodes reference");
            return;
        }
        connectNodes(nodes[0], nodes[1], node);
        connectNodes(nodes[1], nodes[0], node);
    }//listo

    private void connectNodes(Node node1, Node node2, Node node) {
        if (node1.isConnectedToNode(node2)) {
            Directions dir = node1.getDirConnectedToNode(node2);

            node.addConnection(node2, dir);

            node1.changeConnectionIn(node2, node);
        }
    }//listo

    private void resetConnectNode(Node node, Node[] nodes) {
        Node init = nodes[0];
        Node end = nodes[1];

        connectNodes(node, end, init);//restablecer
        connectNodes(node, init, end);//retablecer

        if (node.isConnectedToNode(end)) node.removeConnection(end);
        if (node.isConnectedToNode(init)) node.removeConnection(init);
    }//listo

    //------------------------------
    //  gestion de viaje
    //------------------------------

    public void initTravel() {
        try {
            pause = stop = completedTravel = false;

            if (!validateNodes()) throw new IllegalStateException("No valido el partida y inicio");
            prepareTravel();
            if (!loadBestPath()) throw new IllegalStateException("No se encontro una Ruta");

            drawBestPath();
            initWeigh = (int) graph.getPathDistance();
            estimateTime = (int) graph.getPathDistance();
            tempEdgeDTO = new EdgeDTO(bestPath.getFirst());

//            usePath.clear();
            usePath = new ArrayList<>();
            startTravelCycle();
            updateUIForTravelStart();

        } catch (IllegalStateException e) {
            showInfoMessage("Error al iniciar el viaje: " + e.getMessage());
            handleEndTravel();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            showInfoMessage("Error inesperado al iniciar el viaje: ");
            handleEndTravel();
        }
    }//Listo

    private boolean validateNodes() {
        return !(startNode.isEmptyValues() || endNode.isEmptyValues());
    }// Listo

    private void prepareTravel() {
        Node init = getNodeAtLocation(startConnection[0], startConnection[1]);
        Node end = getNodeAtLocation(startConnection[2], startConnection[3]);
        nodesInit = new Node[]{end, init};
        connectNode(startNode, nodesInit);


        Node init2 = getNodeAtLocation(endConnection[0], endConnection[1]);
        Node end2 = getNodeAtLocation(endConnection[2], endConnection[3]);
        nodesEnd = new Node[]{init2, end2};
        connectNode(endNode, nodesEnd);

        //TODO
//        if (init.getID() == init2.getID() && end.getID() == end2.getID()) {
//            init.calculateDistance(startNode);
//            init.calculateDistance(endNode);
//        }

    }//TODO

    private boolean loadBestPath() {
        graph = new Graph(startNode, endNode);
        boolean completed = isDijkstra ? graph.runDijkstra() : graph.runFloydWarshall();
        bestPath = graph.getBestPathEdges();
        return completed;
    }//Listo

    private void drawBestPath() {
        for (Edge edge : bestPath) {
            if (edge != null) drawEdgeLocal(edge, EDGE_COLOR);
            else throw new IllegalStateException("Conexión vacía detectada.");
        }
    }//Listo

    private void startTravelCycle() {
        tiempoDetenido = timetranscurrido = 0;
        scheduler = Executors.newSingleThreadScheduledExecutor();


        currentEdge = bestPath.getFirst();
        try {
            scheduler.scheduleAtFixedRate(() -> {
                if (pause) {
                    tiempoDetenido++;
                    return;
                } else {
                    if (!stop) {
                        Platform.runLater(this::executeTravelCycleStep);
                    }
                }
                timetranscurrido++;
            }, 0, 1, TimeUnit.SECONDS);

        } catch (IllegalStateException e) {
            showInfoMessage("Error durante el ciclo: " + e.getMessage());
            handleEndTravel();
        } catch (Exception e) {
            e.printStackTrace();
            showInfoMessage("Error inesperado ciclo del viaje.");
        }
    }//Listo

    private void executeTravelCycleStep() {
        try {
            processCurrentEdge(currentEdge);

            estimateTime -= 10;
            timeL.setText("Tiempo: " + formatTime(estimateTime / 10));

            if (currentEdge.getEffectiveWeight() <= 0.0) {
                removeDrawLocalCircle(currentEdge.getStartingNode().getLocation());
                updateTravelCycle(currentEdge);

                usePath.add(currentEdge);

                if (hasReachedDestination(currentEdge)) {
                    removeDrawLocalCircle(currentEdge.getStartingNode().getLocation());
                    inFinalizeTravel();
                    return;
                }
                recalculateBestPath();
                currentEdge = bestPath.getFirst();
                tempEdgeDTO = new EdgeDTO(currentEdge);
                drawLocalCircle(currentEdge.getStartingNode().getLocation(), START_NODE_COLOR);
            }


        } catch (IllegalStateException e) {
            showInfoMessage("Error durante el ciclo: " + e.getMessage());
            handleEndTravel();
        } catch (Exception e) {
            e.printStackTrace();
            showInfoMessage("Error en el paso del ciclo de viaje.");
            handleEndTravel();
        }
    }

    private void inFinalizeTravel() {
        completedTravel = stop = true;
        if (scheduler != null && !scheduler.isShutdown()) scheduler.shutdown();

        bestPath = graph.getBestPathEdges();
        drawBestPath();

        for (Edge edge : usePath) {
            if (edge != null) drawEdgeLocal(edge, Color.ORANGE);
            else throw new IllegalStateException("Conexión vacía detectada.");
        }

        String tiempoTranscurridoFormat = formatTime(timetranscurrido);
        String tiempoDetenidoFormat = formatTime(tiempoDetenido);
        int costoTotal = (timetranscurrido + tiempoDetenido) * 10;

        infoTA.setText("Tiempo transcurrido: " +
                "\nPeso inicial: " + initWeigh +
                tiempoTranscurridoFormat +
                "\nTiempo detenido: " + tiempoDetenidoFormat +
                "\nCosto total: " + costoTotal);
        showInfoMessage("El viaje ha finalizado exitosamente.");

    }

    private String formatTime(int timeInSeconds) {
        int minutes = timeInSeconds / 60;
        int seconds = timeInSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }//listo

    private void processCurrentEdge(Edge currentEdge) {
        deleteDrawEdgeLocal(currentEdge);
        removeDrawLocalCircle(currentEdge.getStartingNode().getLocation());
        currentEdge.recalculateStartNode();
        drawEdgeLocal(currentEdge, EDGE_COLOR);
        drawLocalCircle(currentEdge.getStartingNode().getLocation(), START_NODE_COLOR);
    }//listo

    private boolean hasReachedDestination(Edge currentEdge) {
        return currentEdge.getDestinationNodeID() == endNode.getID();
    }//listo

    private void recalculateBestPath() {
        ListConnections.randomizeConnections(0.01, 0.45, 0.35);

        graph2 = new Graph(bestPath.getFirst().getDestinationNode(), endNode);
        boolean success = isDijkstra ? graph2.runDijkstra() : graph2.runFloydWarshall();

        if (!success) throw new IllegalArgumentException("No se puede llegar al destino.");

        bestPath = graph2.getBestPathEdges();

        localDrawManager.removeLines();
        nodesDrawerManagers.getDrawerManager().removeLines();
        nodesDrawerManagers.drawAllConnections();
        drawBestPath();

        estimateTime = (int) graph2.getPathDistance();
    }//Listo

    private void updateTravelCycle(Edge currentEdge) {
        currentEdge.setWeight(tempEdgeDTO.getWeight());
//        tempEdgeDTO.ge
        currentEdge.getStartingNode().setLocation(tempEdgeDTO.getPointStart());
    }//listo

    public void pauseTravel(Boolean pausar) {
        this.pause = pausar;
        showInfoMessage(pausar ? "Viaje pausado." : "Viaje reanudado.");
    }// Listo

    public void handleEndTravel() {
        stop = true;
        if (scheduler != null && !scheduler.isShutdown()) scheduler.shutdown();

        try {
            if (!completedTravel) updateTravelCycle(currentEdge);

            completedTravel = false;
            if (nodesInit != null)
                resetConnectNode(startNode, nodesInit);
            if (nodesEnd != null)
                resetConnectNode(endNode, nodesEnd);

            nodesInit = null;
            nodesEnd = null;

            startNode.getAllConnections().forEach(edge -> nodesDrawerManagers.getDrawerManager().removeLine(edge.connectionline()));
            endNode.getAllConnections().forEach(edge -> nodesDrawerManagers.getDrawerManager().removeLine(edge.connectionline()));

            endNode.setEmptyValues(true);
            startNode.setEmptyValues(true);
            endNode.setLocation(new double[]{0, 0});
            startNode.setLocation(new double[]{0, 0});


            localDrawManager.removeLines();
            localDrawManager.removeCircles();
            nodesDrawerManagers.getDrawerManager().removeLines();
            nodesDrawerManagers.getDrawerManager().removeCircles();
            nodesDrawerManagers.drawAllConnections();
        } catch (Exception e) {
            System.err.println("Error al finalizar (431): " + e);
        }

        updateUIForTravelEnd();
    }//listo

    //utilities
    private void updateUIForTravelStart() {
        startB.setText("Finalizar Viaje");
        startB.setStyle("-fx-background-color: #f44336;");
        pauseB.setDisable(false);
        showInfoMessage("Viaje iniciado.");
    }//listo --!

    private void updateUIForTravelEnd() {
        startB.setText("Iniciar Viaje");
        startB.setStyle("-fx-background-color: #66bb6a;");
        pauseB.setText("Pausar Viaje");
        pauseB.setDisable(true);
        startB.setDisable(true);
        pauseTravel(false);
        showInfoMessage("Viaje finalizado.");
    }//listo --!

    private void showInfoMessage(String message) {
        AppContext.getInstance().createNotification("Info", message);
    }//listo

    private void drawLocalCircle(double[] point, Color color) {
        localDrawManager.drawCircle(point[0], point[1], color);
    }//listo

    private void removeDrawLocalCircle(double[] point) {
        localDrawManager.removeCircle(point);
    }//listo

    private void drawEdgeLocal(Edge edge, Color color) {
        localDrawManager.drawLine(edge.connectionline(), color);
    }//listo

    private void deleteDrawEdgeLocal(Edge edge) {
        if (edge == null) {
            System.err.println("edge null: deleteDrawEdgeLocal");
            return;
        }
        localDrawManager.removeLine(edge.connectionline());

    }//lidto

    public void reDrawEdge(Edge edge, Color color) {
        localDrawManager.removeLine(edge.connectionline());
        localDrawManager.drawLine(edge.connectionline(), color);
    }//lito

    public Edge getConnection(double x, double y) {
        double[] locations = nodesDrawerManagers.getDrawerManager().getLineAtWithCircle(x, y);
        if (locations == null) return null;

        Node node1 = ListNodes.getNodeByLocation(locations[0], locations[1]);
        Node node2 = ListNodes.getNodeByLocation(locations[2], locations[3]);
        return node1.getConnectionInNode(node2.getID()) != null ? node1.getConnectionInNode(node2.getID()) : node2.getConnectionInNode(node1.getID());
    }//listo

    private Node getNodeAtLocation(double x, double y) {
        return nodesDrawerManagers.getNodesManager().getNodeAtLocation(new double[]{x, y});
    }//listo
}