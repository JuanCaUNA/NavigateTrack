package org.una.navigatetrack.manager;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import org.una.navigatetrack.roads.Node;
import org.una.navigatetrack.utils.Drawer;

public class Facade {
    private final NodesDrawerManager nodesDrawerManager;
    Node startNode, endNode;

    public Facade(Pane paintPane) {
        nodesDrawerManager = new NodesDrawerManager(new Drawer(paintPane));
    }

    //por definir
    void getApproximateLocation();
    void getRecorrido();
    void getLine();
    void getNode();
    void getTime();
    void drawLinesOfRecorrido();
    //separar un drawer para el movimiento -> el movieminto de star node


    public void setStartNode(int[] point) {
        removeStartNode(point);
        startNode = new Node(point);
        nodesDrawerManager.getDrawer().drawCircle(point[0], point[1], Color.BLUE);
    }

    public void setEndNode(int[] point) {
        removeEndNode(point);
        endNode = new Node(point);
        nodesDrawerManager.getDrawer().drawCircle(point[0], point[1], Color.BLUE);
    }

    public void removeStartNode(int[] point) {
        if (startNode != null) {
            nodesDrawerManager.getDrawer().removeCircle(point);
        }
    }

    public void removeEndNode(int[] point) {
        if (endNode != null) {
            nodesDrawerManager.getDrawer().removeCircle(point);
        }
    }

}
