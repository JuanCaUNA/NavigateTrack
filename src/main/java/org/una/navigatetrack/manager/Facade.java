package org.una.navigatetrack.manager;

import javafx.scene.layout.Pane;
import lombok.Getter;
import lombok.Setter;
import org.una.navigatetrack.roads.Connection;
import org.una.navigatetrack.roads.Graph;
import org.una.navigatetrack.roads.Node;

import java.util.Map;

@Getter
@Setter
public class Facade {
    private final NodesDrawerManagers nodesDrawerManagers; // Gestor de nodos
    private Node startNode, endNode; // Nodos de inicio y fin
    private double[] startPoint, endPoint; // Líneas para la representación gráfica
    private boolean flag;

    public Facade(Pane paintPane) {
        nodesDrawerManagers = new NodesDrawerManagers(new DrawerManager(paintPane));
    }

    public void locateStartNode(double x, double y) {
        locateNode(startNode, new double[]{x, y});
    }

    public void locateEndNode(double x, double y) {
        locateNode(endNode, new double[]{x, y});
    }

    private void locateNode(Node node, double[] location) {
        if (node == null) {
            throw new IllegalArgumentException("Node cannot be null");
        }
        double[] array = nodesDrawerManagers.getDrawerManager().getLineAtWithCircle(location);
        startPoint = new double[]{array[0], array[1]};
        endPoint = new double[]{array[2], array[3]};
        checkDirection(node);
    }

    public double[] getPointForNode(boolean isStartNode) {
        Node targetNode = isStartNode ? startNode : endNode;
        return targetNode != null ? targetNode.getLocation() : null;
    }

    private void checkDirection(Node node) {
        Node init = nodesDrawerManagers.getNodesManager().getNodeAtLocation(startPoint);
        Node end = nodesDrawerManagers.getNodesManager().getNodeAtLocation(endPoint);

        if (init.isConnectedToNode(end)) {
            node.addConnection(end, init.getDirConnectedToNode(end));
            init.changeConnectionIn(end, node);
        }
        if (end.isConnectedToNode(init)) {
            node.addConnection(init, end.getDirConnectedToNode(init));
            end.changeConnectionIn(init, node);
        }
    }

    public void recalcularPosicion() {
        // Implementar la lógica para recalcular la posición de los nodos si es necesario.
    }

    public void setEndNode(double[] point) {
        if (endNode != null) {
            removeNodeVisual(endNode);
        }
        endNode = createNode(point); // Crear un nuevo nodo de fin
    }

    private Node createNode(double[] point) {
        Node newNode = new Node(point);
        nodesDrawerManagers.createAndDrawNode(point); // Dibuja el nodo en el panel
        return newNode;
    }

    private void removeNodeVisual(Node node) {
        nodesDrawerManagers.getDrawerManager().removeCircle(node.getLocation());
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
}
