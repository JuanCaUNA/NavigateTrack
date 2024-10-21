package org.una.navigatetrack.manager;

import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import org.una.navigatetrack.roads.Connection;
import org.una.navigatetrack.roads.Node;
import org.una.navigatetrack.utils.Drawer;

import java.util.ArrayList;
import java.util.List;

public class Facade {
    private final NodesDrawerManager nodesDrawerManager;
    Node startsNode, endsNode;

    public Facade(Pane paintPane) {
        nodesDrawerManager = new NodesDrawerManager(new Drawer(paintPane));
    }

    //por definir
//    void getApproximateLocation();

    public List<Connection> getLineAt(int[] point) {
        Line line = nodesDrawerManager.getDrawer().getLineAt(point);

        int[] endLocation = {(int) line.getEndX(), (int) line.getEndY()};
        int[] startLocation = {(int) line.getStartX(), (int) line.getStartY()};

        Node endNode = nodesDrawerManager.getNodesManager().getNodeAtLocation(endLocation);
        Node startNode = nodesDrawerManager.getNodesManager().getNodeAtLocation(startLocation);
        List<Connection> connections = new ArrayList<>();
        connections.add(endNode.getConnection(startLocation));
        connections.add(startNode.getConnection(endLocation));
        return connections;
    }

    public Node getNodeAt(int[] point) {
        return nodesDrawerManager.getNodesManager().getNodeAtLocation(point);
    }

    //separar un drawer para el movimiento -> el movieminto de star node
    public void deleteRegister() {
        //TODO separar el resgistro?
        nodesDrawerManager.deleteAndRemoveNode(startsNode);
        nodesDrawerManager.deleteAndRemoveNode(endsNode);
    }


    public void setStartNode(int[] point) {
        removeStartNode(point);
        startsNode = new Node(point);
        //TODO
        nodesDrawerManager.createAndDrawNode(point);
        //nodesDrawerManager.getDrawer().drawCircle(point[0], point[1], Color.BLUE);
    }

    public void setEndsNode(int[] point) {
        removeEndNode(point);
        endsNode = new Node(point);
        //TODO
        nodesDrawerManager.createAndDrawNode(point);
        //nodesDrawerManager.getDrawer().drawCircle(point[0], point[1], Color.BLUE);
    }

    public void removeStartNode(int[] point) {
        if (startsNode != null) {
            nodesDrawerManager.getDrawer().removeCircle(point);
        }
    }

    public void removeEndNode(int[] point) {
        if (endsNode != null) {
            nodesDrawerManager.getDrawer().removeCircle(point);
        }
    }

    //sobre la lista de recorrido
//    void getRecorrido();
//    void drawLinesOfRecorrido();
//    void getPrice();
//    void getTime();
//
//    void setTipeVoyage();
//    void startVoyage();
//    void endVoyage();
}
