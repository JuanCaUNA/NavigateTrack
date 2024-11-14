package org.una.navigatetrack.manager;

import javafx.application.Platform;
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
    private ScheduledExecutorService scheduler;
    private Graph graph;

    @Getter
    private Node startNode;
    @Getter
    private Node endNode;
    private final double[] startPoint = new double[2];
    private final double[] endPoint = new double[2];
    private final double[] startConnection = new double[4];
    private final double[] endConnection = new double[4];

    @Setter
    private boolean isDijkstra;
    @Getter
    private int time;
    private boolean pause, stop;
    @Setter
    private Label timeL;
    private List<Edge> mejorRuta;
    private EdgeDTO temp;

    private static final Color BLUE_COLOR = Color.rgb(0, 0, 255, 0.5);
    private static final Color START_NODE_COLOR = Color.YELLOW;
    private static final Color END_NODE_COLOR = Color.RED;
    private static final Color EDGE_COLOR = Color.GREEN;

    public NodeGraphFacade(Pane paintPane) {
        nodesDrawerManagers = new NodesDrawerManagers(new DrawerManager(paintPane), false);

        startNode = new Node();
        endNode = new Node();

        startNode.setID(ListNodes.getNextId());
        endNode.setID(ListNodes.getNextId() + 1);

        ListNodes.addNode(startNode);
        ListNodes.addNode(endNode);

        startNode.setEmptyValues(true);
        endNode.setEmptyValues(true);
    }

    // Métodos para configurar los nodos
    public boolean setStartNode(double[] point) {
        return setNode(point, true);
    }

    public boolean setEndNode(double[] point) {
        return setNode(point, false);
    }

    private boolean setNode(double[] point, boolean isStartNode) {
        Node currentNode = isStartNode ? startNode : endNode;
        double[] currentPoint = isStartNode ? startPoint : endPoint;
        double[] currentConnection = isStartNode ? startConnection : endConnection;

        if (!currentNode.isEmptyValues()) resetNode(currentNode, currentConnection);

        // Establecer nuevas coordenadas
        System.arraycopy(point, 0, currentPoint, 0, 2);
        Arrays.fill(currentConnection, Double.NaN);

        if (locateNode(currentPoint, currentConnection)) {
            Color nodeColor = isStartNode ? START_NODE_COLOR : END_NODE_COLOR;
            nodesDrawerManagers.drawCircle(currentPoint, nodeColor);
            currentNode.setLocation(currentPoint);
            currentNode.setNodeType(!Double.isNaN(currentConnection[0]));  // Si la conexión está definida
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
        nodesDrawerManagers.removeCircle(node.getLocation());
    }

    private boolean locateNode(double[] location, double[] connection) {
        double[] relocated = nodesDrawerManagers.getLocationIfExistNodeAt(location);

        if (relocated != null) {
            System.arraycopy(relocated, 0, location, 0, 2);
            Arrays.fill(connection, Double.NaN);
            return true;
        }

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

        if (init == null || end == null) return;

        connectNodes(init, end, node);
        connectNodes(end, init, node);
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
            pause = stop = false;  // Inicializamos los estados

            connectNode(startNode, startConnection);
            connectNode(endNode, endConnection);

            loadBestPath();

            // Dibujar las conexiones
            for (Edge edge : mejorRuta) {
                if (edge != null) draEdgeDrawLocal(edge, EDGE_COLOR);
            }

            temp = new EdgeDTO(mejorRuta.getFirst());

            startTravelCycle();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private void startTravelCycle() {
        if (timeL == null) {
            System.out.println("timeL es null. No se puede comenzar el ciclo.");
            return;
        }

        scheduler = Executors.newSingleThreadScheduledExecutor();
        System.out.println("Scheduler creado y listo para ejecutar.");

        scheduler.scheduleAtFixedRate(() -> {
            try {
                if (!stop && !pause) {
                    Platform.runLater(this::executeTravelCycleStep);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 0, 1, TimeUnit.SECONDS);  // Ejecutamos cada 1 segundo
    }

    private void executeTravelCycleStep() {
        try {
            Edge currentE = mejorRuta.getFirst();
            Node currentN = currentE.getStartingNode();

            deleteEdgeDrawLocal(currentE);
            removeNodeVisual(currentN);

            currentE.recalculateStartNode();
            draEdgeDrawLocal(currentE, EDGE_COLOR);
            nodesDrawerManagers.drawCircle(currentN.getLocation(), START_NODE_COLOR);

            double time = currentE.getEffectiveWeight();
            timeL.setText("Tiempo: " + time);

            if (time <= 0.0) {
                if (currentE.getDestinationNodeID() == endNode.getID()) {
                    endTravel();
                } else {
                    updateTravelCycle(currentE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateTravelCycle(Edge currentE) {
        currentE.setWeight(temp.getWeight());
        currentE.getStartingNode().setLocation(temp.getPointStart());
        mejorRuta.removeFirst();
        temp = new EdgeDTO(mejorRuta.getFirst());
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

    private void loadBestPath() {
        graph = new Graph(startNode, endNode);
        boolean exito = isDijkstra ? graph.runDijkstra() : graph.runFloydWarshall();

        if (!exito) {
            System.out.println("No se encontró ruta.");
        }
        mejorRuta = graph.getBestPathEdges();
    }

    private void draEdgeDrawLocal(Edge edge, Color color) {
        if (edge != null) {
            nodesDrawerManagers.getDrawerManager().drawLine(edge.connectionline(), color);
        }
    }

    private void deleteEdgeDrawLocal(Edge edge) {
        if (edge != null) {
            nodesDrawerManagers.getDrawerManager().removeLine(edge.connectionline());
        }
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

