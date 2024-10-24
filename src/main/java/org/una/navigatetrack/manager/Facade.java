package org.una.navigatetrack.manager;

import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import org.una.navigatetrack.roads.Connection;
import org.una.navigatetrack.roads.Node;

import java.util.ArrayList;
import java.util.List;

public class Facade {
    private final NodesDrawerManagers nodesDrawerManagers;
    Node startsNode, endsNode;

    public Facade(Pane paintPane) {
        nodesDrawerManagers = new NodesDrawerManagers(new DrawerManager(paintPane));
    }

    //por definir
//    void getApproximateLocation();

    public List<Connection> getLineAt(int[] point) {
        Line line = nodesDrawerManagers.getDrawerManager().getLineAt(point);

        int[] endLocation = {(int) line.getEndX(), (int) line.getEndY()};
        int[] startLocation = {(int) line.getStartX(), (int) line.getStartY()};

        Node endNode = nodesDrawerManagers.getNodesManager().getNodeAtLocation(endLocation);
        Node startNode = nodesDrawerManagers.getNodesManager().getNodeAtLocation(startLocation);
        List<Connection> connections = new ArrayList<>();
        connections.add(endNode.getConnection(startLocation));
        connections.add(startNode.getConnection(endLocation));
        return connections;
    }

    public Node getNodeAt(int[] point) {
        return nodesDrawerManagers.getNodesManager().getNodeAtLocation(point);
    }

    //separar un drawer para el movimiento -> el movieminto de star node
    public void deleteRegister() {
        //TODO separar el resgistro?
        //end and start node.
    }


    public void setStartNode(int[] point) {
        removeStartNode(point);
        startsNode = new Node(point);
        //TODO
        nodesDrawerManagers.createAndDrawNode(point);
        //nodesDrawerManager.getDrawer().drawCircle(point[0], point[1], Color.BLUE);
    }

    public void setEndsNode(int[] point) {
        removeEndNode(point);
        endsNode = new Node(point);
        //TODO
        nodesDrawerManagers.createAndDrawNode(point);
        //nodesDrawerManager.getDrawer().drawCircle(point[0], point[1], Color.BLUE);
    }

    public void removeStartNode(int[] point) {
        if (startsNode != null) {
            nodesDrawerManagers.getDrawerManager().removeCircle(point);
        }
    }

    public void removeEndNode(int[] point) {
        if (endsNode != null) {
            nodesDrawerManagers.getDrawerManager().removeCircle(point);
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
