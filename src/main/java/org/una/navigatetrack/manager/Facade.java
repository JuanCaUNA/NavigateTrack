package org.una.navigatetrack.manager;

import javafx.scene.layout.Pane;
import lombok.Getter;
import lombok.Setter;
import org.una.navigatetrack.roads.Node;

@Getter
@Setter
public class Facade {
    private final NodesDrawerManagers nodesDrawerManagers;
    private Node startNode, endNode;
    private double[] startLine, endLine;
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

    public void locateNode(Node node, double[] location) {
        double[] array = nodesDrawerManagers.getDrawerManager().getLineAtWithCircle(location);
        startLine = new double[]{array[0], array[1]};
        endLine = new double[]{array[2], array[3]};
        comprobarDireccion(node);
    }

    private void comprobarDireccion(Node node) {
        Node init = nodesDrawerManagers.getNodesManager().getNodeAtLocation(startLine);
        Node end = nodesDrawerManagers.getNodesManager().getNodeAtLocation(endLine);

        if (init.isConnectedToNode(end)) {
            node.getConnections()[1].setTargetNode(end);
            init.changeConnectionIn(end, node);
        }
        if (end.isConnectedToNode(init)) {
            node.getConnections()[1].setTargetNode(init);
            end.changeConnectionIn(init, node);
        }

    }

    public void recalcularPosicion(){
    }

    public void setEndNode(double[] point) {
        if (endNode != null) {
            removeNodeVisual(endNode);
        }
        endNode = createNode(point);
    }

    private Node createNode(double[] point) {
        Node newNode = new Node(point);
        nodesDrawerManagers.createAndDrawNode(point);
        return newNode;
    }

    private void removeNodeVisual(Node node) {
        nodesDrawerManagers.getDrawerManager().removeCircle(node.getLocation());
    }

    public void deleteRegister() {
        // Implementar la lógica para eliminar el registro de nodos.
    }

    //todo
    public double[] getPointForStarTNode(double[] point) {
        startNode = nodesDrawerManagers.getNodesManager().getNodeAtLocation(startLine);
        return startNode.getLocation();
    }

    public double[] getPointForEndNode(double[] point) {
        startNode = nodesDrawerManagers.getNodesManager().getNodeAtLocation(startLine);
        return startNode.getLocation();
    }

    //otros
//    public Node getNodeAt(double[] point) {
//        return nodesDrawerManagers.getNodesManager().getNodeAtLocation(point);
//    }


    // Métodos a completar según la lógica necesaria.
    // void getApproximateLocation();
    // void getRecorrido();
    // void drawLinesOfRecorrido();
    // void getPrice();
    // void getTime();
    // void setTypeVoyage();
    // void startVoyage();
    // void endVoyage();
}

