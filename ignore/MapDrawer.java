package org.una.navigatetrack.controller.fxml;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Circle;
import org.una.navigatetrack.roads.Directions;
import org.una.navigatetrack.roads.Node;


public class MapDrawer implements Configs {
    private final Pane paintPane;

    public MapDrawer(Pane paintPane) {
        this.paintPane = paintPane;
    }

    public Circle drawNodeAt(int[] location, Color color) {
        Circle circle = new Circle(location[0], location[1], CIRCLE_RADIUS, color);
        paintPane.getChildren().add(circle);
        return circle;
    }

    public void drawConnection(Node fromNode, Node toNode, Directions direction) {
        Line line = new Line(fromNode.getLocation()[0], fromNode.getLocation()[1], toNode.getLocation()[0], toNode.getLocation()[1]);
        line.setStroke(getColorForDirection(direction));
        paintPane.getChildren().add(line);
    }

    public void removeConnectionLine(Node fromNode, Node toNode, Directions direction) {
        paintPane.getChildren().removeIf(node ->
                node instanceof Line line &&
                        (line.getStartX() == fromNode.getLocation()[0] &&
                                line.getStartY() == fromNode.getLocation()[1] &&
                                line.getEndX() == toNode.getLocation()[0] &&
                                line.getEndY() == toNode.getLocation()[1])
        );
    }

    private Color getColorForDirection(Directions direction) {
        return switch (direction) {
            case IZQUIERDA -> Color.YELLOW;
            case DERECHA -> Color.GREEN;
            case ADELANTE -> Color.BLUE;
            case CONTRARIO -> Color.RED;
        };
    }
}

