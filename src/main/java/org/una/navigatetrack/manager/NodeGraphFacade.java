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
    private List<Edge> bestPath;
    private EdgeDTO tempEdgeDTO;
    private Double totalWeight;

    @Setter
    private Label timeL, precioL, preciofinalL;
    @Setter
    private Button algoritmoB, pauseB, startB;
    @Setter
    private TextArea infoTA;

    int estimateTime, tiempoDetenido, timetranscurrido;

    @Setter
    private boolean isDijkstra;
    @Getter
    private Node startNode, endNode;
    private boolean pause, stop, finalizadoE = true;

    Node[] nodesInit, nodesEnd;

    boolean initTrave = false;
    int initWeigh;

    private final double[] startPoint = new double[2], endPoint = new double[2];
    private final double[] startConnection = new double[4], endConnection = new double[4];

    Node currentNode;
    Edge currentEdge;

    Node init, end;


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
        if (!finalizadoE) {
            finalizar();
        }

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

    private void connectNode(Node node, Node[] nodes) {
        Node init = nodes[0];
        Node end = nodes[1];

        if (init == null || end == null) {
            System.out.println("error de nulo inicio y fin");
            return;
        }

        connectNodes(init, end, node);
        connectNodes(end, init, node);

    }//listo

    private Node getNodeAtLocation(double x, double y) {
        return nodesDrawerManagers.getNodesManager().getNodeAtLocation(new double[]{x, y});
    }//listo

    private void connectNodes(Node node1, Node node2, Node node) {
        if (node1.isConnectedToNode(node2)) {
            Directions dir = node1.getDirConnectedToNode(node2);

            node.addConnection(node2, dir);

            node1.changeConnectionIn(node2, node);
        }
    }//listo

    private void disconnectNode(Node node, Node[] nodes) {
        Node init = nodes[0];
        Node end = nodes[1];

        connectNodes(node, end, init);//restablecer
        connectNodes(node, init, node);//retablecer

        if (node.isConnectedToNode(end)) node.removeConnection(end);
        if (node.isConnectedToNode(init)) node.removeConnection(init);
    }//listo

    //metodos de vieje

    public boolean initTravel() {
        if (!validateNodes()) {
            return false;
        }
        finalizadoE = false;

        try {
            prepareTravel();
            if (!loadBestPath()) {
                return false;
            }

            drawBestPath();
            initWeigh = (int) graph.getPathDistance();

            estimateTime = (int) graph.getPathDistance();
            tempEdgeDTO = new EdgeDTO(bestPath.getFirst());

            startTravelCycle();
            updateUIForTravelStart();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            showInfoMessage("Error al iniciar el viaje.");
            return false;
        }
    }//Listo

    private boolean validateNodes() {
        if (startNode.isEmptyValues() || endNode.isEmptyValues()) {
            showInfoMessage("Faltan los puntos de inicio y destino.");
            return false;
        }
        return true;
    }// Listo

    private void prepareTravel() {
        pause = stop = false;

        init = getNodeAtLocation(startConnection[0], startConnection[1]);
        end = getNodeAtLocation(startConnection[2], startConnection[3]);
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
        boolean exito = isDijkstra ? graph.runDijkstra() : graph.runFloydWarshall();

        if (!exito) {
            System.out.println("No se encontró ruta.");
            return false;
        }
        bestPath = graph.getBestPathEdges();
        return true;
    }//Listo

    private void drawBestPath() {
        for (Edge edge : bestPath) {
            if (edge != null) {
                drawEdgeLocal(edge, EDGE_COLOR);
            } else {
                System.err.println("Conexión vacía detectada.");
                throw new IllegalStateException("Conexión vacía en el mejor camino.");
            }
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
                }
                timetranscurrido++;
                if (!pause || !stop) {
                    Platform.runLater(this::executeTravelCycleStep);
                }
            }, 0, 1, TimeUnit.SECONDS);

        } catch (Exception e) {
            e.printStackTrace();
            showInfoMessage("Error al iniciar el ciclo del viaje.");
        }
    }//Listo

    private void executeTravelCycleStep() {
        try {
            processCurrentEdge(currentEdge);

            estimateTime -= 10;
            timeL.setText("Tiempo: " + formatTime(estimateTime / 10));

            if (currentEdge.getEffectiveWeight() <= 0.0) {
                updateTravelCycle(currentEdge);
                if (hasReachedDestination(currentEdge)) {
                    removeDrawLocalCircle(currentEdge.getStartingNode().getLocation());
                    finalizeTravel();
                    return;
                }
                recalculateBestPath();
                currentEdge = bestPath.getFirst();
                tempEdgeDTO = new EdgeDTO(currentEdge);
            }


        } catch (Exception e) {
            e.printStackTrace();
            showInfoMessage("Error en el paso del ciclo de viaje.");
            endTravel();
        }
    }

    private void finalizeTravel() {
        stop = true;

        bestPath = graph.getBestPathEdges();
        drawBestPath();

        String tiempoTranscurridoFormat = formatTime(timetranscurrido);
        String tiempoDetenidoFormat = formatTime(tiempoDetenido);
        int costoTotal = (timetranscurrido + tiempoDetenido) * 10;

        infoTA.setText("Tiempo transcurrido: " +
                "\nPeso inicial: " + initWeigh +
                tiempoTranscurridoFormat +
                "\nTiempo detenido: " + tiempoDetenidoFormat +
                "\nCosto total: " + costoTotal);
        showInfoMessage("El viaje ha finalizado exitosamente.");

        endTravel();
    }

    private String formatTime(int tiempoEnSegundos) {
        int minutos = tiempoEnSegundos / 60;
        int segundos = tiempoEnSegundos % 60;
        return String.format("%02d:%02d", minutos, segundos);
    }//listo

    private void processCurrentEdge(Edge currentEdge) {
        deleteDrawEdgeLocal(currentEdge);
        removeDrawLocalCircle(currentEdge.getStartingNode().getLocation());
        currentEdge.recalculateStartNode();
        drawEdgeLocal(currentEdge, EDGE_COLOR);
        currentNode = currentEdge.getStartingNode();
        drawLocalCircle(currentEdge.getStartingNode().getLocation(), START_NODE_COLOR);
    }//listo

    private boolean hasReachedDestination(Edge currentEdge) {
        return currentEdge.getDestinationNodeID() == endNode.getID();
    }//listo

    private void recalculateBestPath() {
        ListConnections.randomizeConnections(0.01, 0.45, 0.35);

        graph2 = new Graph(bestPath.getFirst().getDestinationNode(), endNode);
        boolean success = isDijkstra ? graph2.runDijkstra() : graph2.runFloydWarshall();

        if (!success) {
            showInfoMessage("No se puede llegar al destino.");
            endTravel();
            return;
        }
        bestPath = graph2.getBestPathEdges();

        localDrawManager.removeLines();
        nodesDrawerManagers.getDrawerManager().removeLines();
        nodesDrawerManagers.drawAllConnections();
        drawBestPath();

        estimateTime = (int) graph2.getPathDistance();
    }//Listo

    private void updateTravelCycle(Edge currentEdge) {
        currentEdge.setWeight(tempEdgeDTO.getWeight());
        currentEdge.getStartingNode().setLocation(tempEdgeDTO.getPointStart());
    }//listo

    public void pauseTravel(Boolean pausar) {
        pause = pausar;
        showInfoMessage(pausar ? "Viaje pausado." : "Viaje reanudado.");
    }// Listo

    public void endTravel() {
        stop = true;
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
        }

        try {
            if (!finalizadoE)
                finalizar();

            disconnectNode(startNode, nodesInit);
            disconnectNode(endNode, nodesEnd);


            startNode.getAllConnections().forEach(edge -> nodesDrawerManagers.getDrawerManager().removeLine(edge.connectionline()));
//            removeNodeVisual(startNode);
            startNode.setLocation(new double[]{0, 0});
            startNode.setEmptyValues(true);

            startNode.setEmptyValues(true);
            localDrawManager.removeCircles();

        } catch (Exception e) {
            System.out.println("Error: " + e);
        }

        updateUIForTravelEnd();
    }//listo

    private void finalizar() {
        updateTravelCycle(currentEdge);
        localDrawManager.removeLines();
        finalizadoE = true;
    }//listo

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
        pauseTravel(false);
        showInfoMessage("Viaje finalizado.");
    }//listo --!

    //utilities
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
        if (edge == null) {
            System.err.println("edge null: deleteDrawEdgeLocal");
            return;
        }
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
        if (edge != null) {
            nodesDrawerManagers.getDrawerManager().removeLine(edge.connectionline());
            nodesDrawerManagers.getDrawerManager().drawLine(edge.connectionline(), color);
        }
    }//lito

    public Edge getConnection(double x, double y) {
        double[] locations = nodesDrawerManagers.getDrawerManager().getLineAtWithCircle(x, y);
        if (locations == null) return null;

        Node node1 = ListNodes.getNodeByLocation(locations[0], locations[1]);
        Node node2 = ListNodes.getNodeByLocation(locations[2], locations[3]);
        return node1.getConnectionInNode(node2.getID()) != null ? node1.getConnectionInNode(node2.getID()) : node2.getConnectionInNode(node1.getID());
    }//listo
}