package org.una.navigatetrack.utils;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

import java.util.ArrayList;
import java.util.List;

public class Drawer {
    private static final double CIRCLE_RADIUS = 5;
    private static final double LINE_STROKE_WIDTH = 5;
    private static final double DISTANCE_TOLERANCE = 2.0;
    private final List<Circle> circles; // Lista para almacenar círculos
    private final List<Line> lines; // Lista para almacenar líneas
    private final Pane paintPane; // Panel donde se dibujarán las figuras

    public Drawer(Pane paintPane) {
        this.paintPane = paintPane; // Inicializamos el panel
        circles = new ArrayList<>(); // Inicializamos la lista de círculos
        lines = new ArrayList<>(); // Inicializamos la lista de líneas
    }

    // Dibuja un círculo
    public void drawCircle(double centerX, double centerY, Color color) {
        Circle circle = new Circle(centerX, centerY, CIRCLE_RADIUS);
        drawCircle(circle, color);
    }

    public void drawCircle(Circle circle, Color color) {
        circle.setFill(color);
        circle.setStroke(color);
        circle.setStrokeWidth(1);
        paintPane.getChildren().add(circle);
        circles.add(circle);
    }

    // Dibuja una línea
    public void drawLine(double startX, double startY, double endX, double endY, Color color) {
        Line line = new Line(startX, startY, endX, endY);
        drawLine(line, color);
    }

    public void drawLine(Line line, Color color) {
        line.setStroke(color);
        line.setStrokeWidth(LINE_STROKE_WIDTH);
        paintPane.getChildren().add(line);
        lines.add(line);
    }

    // Remueve un círculo basado en coordenadas
    public void removeCircle(int[] point) {
        Circle circle = getCircleAt(point);
        if (circle != null) {
            removeCircle(circle);
        }
    }

    // Remueve un círculo específico
    public void removeCircle(Circle circle) {
        if (circles.remove(circle)) {
            paintPane.getChildren().remove(circle);
        }
    }

    // Remueve una línea basada en coordenadas
    public void removeLine(int[] point) {
        Line line = getLineAt(point);
        if (line != null) {
            removeLine(line);
        }
    }

    // Remueve una línea específica
    public void removeLine(Line line) {
        if (lines.remove(line)) {
            paintPane.getChildren().remove(line);
        }
    }

    // Obtiene un círculo en las coordenadas dadas
    public Circle getCircleAt(int[] point) {
        double x = point[0];
        double y = point[1];
        for (Circle circle : circles) {
            double distanceSquared = distanceSquared(x, y, circle.getCenterX(), circle.getCenterY());
            if (distanceSquared <= circle.getRadius() * circle.getRadius()) {
                return circle;
            }
        }
        return null;
    }

    // Obtiene una línea en las coordenadas dadas
    public Line getLineAt(int[] point) {
        double x = point[0];
        double y = point[1];
        for (Line line : lines) {
            double distance = pointToLineDistance(line.getStartX(), line.getStartY(), line.getEndX(), line.getEndY(), x, y);
            if (distance <= DISTANCE_TOLERANCE) {
                return line;
            }
        }
        return null;
    }

    // Calcula la distancia de un punto a una línea
    private double pointToLineDistance(double x1, double y1, double x2, double y2, double px, double py) {
        double A = px - x1;
        double B = py - y1;
        double C = x2 - x1;
        double D = y2 - y1;

        double dot = A * C + B * D;
        double lenSq = C * C + D * D;
        double param = (lenSq != 0) ? dot / lenSq : -1;

        double xx, yy;

        if (param < 0) {
            xx = x1;
            yy = y1;
        } else if (param > 1) {
            xx = x2;
            yy = y2;
        } else {
            xx = x1 + param * C;
            yy = y1 + param * D;
        }

        double dx = px - xx;
        double dy = py - yy;
        return Math.sqrt(dx * dx + dy * dy);
    }


    private double distanceSquared(double x1, double y1, double x2, double y2) {
        double dx = x2 - x1;
        double dy = y2 - y1;
        return dx * dx + dy * dy;
    }
}

//public void setPaintPane(Pane paint) {
////        paintPane.getChildren().clear();
////        paintPane.getChildren().addAll(paint);
//    paintAll();
//    paintPane = paint;
//}
//
//private void paintAll() {
////        for (Circle circle : circles) {
////            drawCircle(circle, Color.BLUE);
////        }
////        for (Line line : lines) {
////            drawLine(line, Color.BLUE);
////        }
//}