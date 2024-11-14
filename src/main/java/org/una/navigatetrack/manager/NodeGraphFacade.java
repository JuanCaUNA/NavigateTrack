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
import org.una.navigatetrack.roads.Node;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

// Clase principal para gestionar el grafo de nodos y sus conexiones
public class NodeGraphFacade {

    // Gestión de nodos y conexiones
    private final NodesDrawerManagers nodesDrawerManagers;
//    private final DrawerManager drawerManager;
    private ScheduledExecutorService scheduler;

    // Variables de estado y configuración
    @Getter
    private Node startNode, endNode;
    private double[] startPoint, endPoint;
    private double[] startConnection, endConnection;
    @Setter
    private boolean isDijkstra;
    @Getter
    private int time;
    private boolean pause, stop;
    @Setter
    private Label timeL;
    private List<Edge> mejorRuta;
    private final Color BlueColor = Color.rgb(0, 0, 255, 0.5);

    // ----------------- Constructor ----------------- //
    public NodeGraphFacade(Pane paintPane) {

        nodesDrawerManagers = new NodesDrawerManagers(new DrawerManager(paintPane), false);
//        drawerManager = new DrawerManager(paintPane);


        startPoint = new double[2];
        endPoint = new double[2];
        startConnection = new double[4];
        endConnection = new double[4];

        startNode = new Node();
        endNode = new Node();
        startNode.setID(ListNodes.getNextId());
        endNode.setID(ListNodes.getNextId() + 1);

        ListNodes.addNode(startNode);
        ListNodes.addNode(endNode);
    }

    // -------- Métodos para Configuración de Nodos de Inicio y Fin -------- //

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

        if (currentNode != null) resetNode(currentNode);

        currentPoint = point.clone();
        Arrays.fill(currentConnection, Double.NaN);

        // Localizar nodo en la posición dada
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

    // -------- Métodos de Conexión y Desconexión de Nodos -------- //

    private void connectNode(Node node, double[] connectionXY) {
        double[] initP = {connectionXY[0], connectionXY[1]};
        double[] endP = {connectionXY[2], connectionXY[3]};

        Node init = nodesDrawerManagers.getNodesManager().getNodeAtLocation(initP);
        Node end = nodesDrawerManagers.getNodesManager().getNodeAtLocation(endP);

        if (init == null || end == null) return;

        if (init.isConnectedToNode(end)) {
            node.addConnection(end, init.getDirConnectedToNode(end));
            init.changeConnectionIn(end, node);
        }

        if (end.isConnectedToNode(init)) {
            node.addConnection(init, end.getDirConnectedToNode(init));
            end.changeConnectionIn(init, node);
        }
    }

    private void disconnectNode(Node node, double[] connectionXY) {
        double[] initP = {connectionXY[0], connectionXY[1]};
        double[] endP = {connectionXY[2], connectionXY[3]};

        Node init = nodesDrawerManagers.getNodesManager().getNodeAtLocation(initP);
        Node end = nodesDrawerManagers.getNodesManager().getNodeAtLocation(endP);

        if (node.isConnectedToNode(end)) node.removeConnection(end);
        if (node.isConnectedToNode(init)) node.removeConnection(init);
    }

    private void connectNodeIfValid(Node node, double[] connectionXY) {
        if (!Double.isNaN(connectionXY[0])) {
            node.setID(ListNodes.getNextId());
            ListNodes.addNode(node);
            connectNode(node, connectionXY);
        }
    }

// -------- Métodos de Viaje y Gestión de Scheduler -------- //

    private EdgeDTO temp;
    public void initTravel() {
        try {
            pause = stop = false;  // Inicializamos los estados
            mejorRuta = new ArrayList<>();  // Inicializamos la ruta
            startNode.addConnection(endNode, Directions.ADELANTE);  // Conexión de inicio y fin
            mejorRuta.add(startNode.getConnection(Directions.ADELANTE));  // Añadimos la primera conexión a la ruta

            // Verificar que el primer edge esté correctamente agregado
            for (Edge edge : mejorRuta) {
                System.out.println("Recorriendo: " + edge);  // Añadido para verificar
                draEdgeDrawLocal(edge, Color.GREEN);
            }

            temp = new EdgeDTO(mejorRuta.getFirst());  // Aseguramos que estamos usando el primer elemento

            // Inicializamos el ciclo de ejecución directamente con un scheduler
            startTravelCycle();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startTravelCycle() {
        try {
            if (timeL == null) {
                System.out.println("timeL es null. No se puede comenzar el ciclo.");
                return;  // Asegurarnos de que `timeL` está inicializado antes de continuar
            }

            scheduler = Executors.newSingleThreadScheduledExecutor();  // Creamos el scheduler
            System.out.println("Scheduler creado y listo para ejecutar.");  // Mensaje de depuración

            scheduler.scheduleAtFixedRate(() -> {
                try {
                    if (!stop && !pause) {
                        Platform.runLater(() -> {
                            System.out.println("Ejecutando en el hilo de JavaFX.");

                            Edge currentE = mejorRuta.getFirst();
                            Node currentN = currentE.getStartingNode();

                            // Eliminar visualización del nodo anterior
                            deleteEdgeDrawLocal(currentE);
                            removeNodeVisual(currentN);

                            currentE.recalculateStartNode();

                            // Dibujar el círculo del nodo
                            draEdgeDrawLocal(currentE, Color.GREEN);
                            nodesDrawerManagers.drawCircle(currentN.getLocation(), Color.YELLOW);

                            double time = currentE.getEffectiveWeight();
                            timeL.setText("tiempo: " + time);

                            // Condición para finalizar el viaje o continuar
                            if (time <= 0.0) {
                                if (currentE.getDestinationNodeID() == endNode.getID()) {
                                    endTravel();
                                } else {
                                    currentE.setWeight(temp.getWeight());
                                    currentE.getStartingNode().setLocation(temp.getPointStart());
                                    mejorRuta.removeFirst();  // Remover el primer elemento de la lista
                                    temp = new EdgeDTO(mejorRuta.getFirst());  // Actualizar el objeto EdgeDTO
                                }
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }, 0, 1, TimeUnit.SECONDS);  // Ejecutamos cada 1 segundo

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void pauseTravel(Boolean pausar) {
        pause = pausar;  // Establecemos el estado de pausa
        System.out.println("Viaje pausado: " + pause);
    }

    public void endTravel() {
        stop = true;  // Marcamos el viaje como finalizado
        System.out.println("Viaje finalizado.");

        // Detenemos el scheduler si no está apagado
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
        }
    }

    // ------------- Métodos de Utilidad para Conexiones y Dibujos --------------- //

    public Edge getConnection(double x, double y) {
        double[] locations = nodesDrawerManagers.getDrawerManager().getLineAtWithCircle(x, y);
        if (locations == null) return null;

        Node node1 = ListNodes.getNodeByLocation(locations[0], locations[1]);
        Node node2 = ListNodes.getNodeByLocation(locations[2], locations[3]);
        Edge edge = node1.getConnectionInNode(node2.getID());

        return (edge == null) ? node2.getConnectionInNode(node1.getID()) : edge;
    }

    private void draEdgeDrawLocal(Edge edge, Color color) {
//        drawerManager.drawLine(edge.connectionline(), color);
        nodesDrawerManagers.getDrawerManager().drawLine(edge.connectionline(), color);
    }

    private void deleteEdgeDrawLocal(Edge edge) {
//        drawerManager.removeLine(edge.connectionline());
        nodesDrawerManagers.getDrawerManager().removeLine(edge.connectionline());
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
