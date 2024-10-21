package org.una.navigatetrack.controller;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

import java.util.ArrayList;
import java.util.List;

public class Drawer {
    private final Pane paintPane; // Panel donde se dibujarán las figuras
    private final List<Circle> circles; // Lista para almacenar círculos
    private final List<Line> lines; // Lista para almacenar líneas

    public Drawer(Pane paintPane) {
        this.paintPane = paintPane; // Inicializamos el panel
        circles = new ArrayList<>(); // Inicializamos la lista de círculos
        lines = new ArrayList<>(); // Inicializamos la lista de líneas
    }

    public void drawCircle(double centerX, double centerY, Color color) {
        double radius = 5;
        Circle circle = new Circle(centerX, centerY, radius);
        circle.setFill(color); // Relleno del círculo (opcional)
        circle.setStroke(color); // Color del borde del círculo
        circle.setStrokeWidth(1); // Grosor del borde del círculo
        paintPane.getChildren().add(circle); // Añadir el círculo al panel
        circles.add(circle); // Añadir el círculo a la lista
    }

    public void drawLine(double startX, double startY, double endX, double endY, Color color) {
        Line line = new Line(startX, startY, endX, endY);
        line.setStroke(color); // Color de la línea
        double strokeWidth = 2;
        line.setStrokeWidth(strokeWidth); // Grosor de la línea
        paintPane.getChildren().add(line); // Añadir la línea al panel
        lines.add(line); // Añadir la línea a la lista
    }

    //removes
    public void removeCircle(Circle circle) {
        if (circles.contains(circle)) {
            circles.remove(circle); // Remover el círculo de la lista
            paintPane.getChildren().remove(circle); // Borrar el círculo del panel
        }
    }

    public void removeLine(Line line) {
        if (lines.contains(line)) {
            lines.remove(line); // Remover la línea de la lista
            paintPane.getChildren().remove(line); // Borrar la línea del panel
        }
    }

    public void removeCircle(int[] point) {
        Circle circle = getCircleAt(point);
        if (circle == null) {
            return;
        }
        if (circles.contains(circle)) {
            circles.remove(circle); // Remover el círculo de la lista
            paintPane.getChildren().remove(circle); // Borrar el círculo del panel
        }
    }

    public void removeLine(int[] point) {
        Line line = getLineAt(point);
        if (line == null) {
            return;
        }
        if (lines.contains(line)) {
            lines.remove(line); // Remover la línea de la lista
            paintPane.getChildren().remove(line); // Borrar la línea del panel
        }
    }
    //removes end

    public Circle getCircleAt(int[] point) {
        double x = point[0];
        double y = point[1];
        for (Circle circle : circles) {
            // Verifica si el punto (x, y) está dentro del radio del círculo
            double dx = x - circle.getCenterX();
            double dy = y - circle.getCenterY();
            double distanceSquared = dx * dx + dy * dy;
            if (distanceSquared <= circle.getRadius() * circle.getRadius()) {
                return circle; // Devuelve el círculo si está en esa coordenada
            }
        }
        return null; // No se encontró ningún círculo en esa coordenada
    }

    public Line getLineAt(int[] point) {
        double x = point[0];
        double y = point[1];
        for (Line line : lines) {
            // Verificar si el punto está cerca de la línea
            double tolerance = 2.0; // Ajusta esto para definir la "cercanía" a la línea
            double distance = pointToLineDistance(line.getStartX(), line.getStartY(), line.getEndX(), line.getEndY(), x, y);
            if (distance <= tolerance) {
                return line; // Devuelve la línea si está en esa coordenada
            }
        }
        return null; // No se encontró ninguna línea en esa coordenada
    }

    private double pointToLineDistance(double x1, double y1, double x2, double y2, double px, double py) {
        double A = px - x1;
        double B = py - y1;
        double C = x2 - x1;
        double D = y2 - y1;

        double dot = A * C + B * D;
        double len_sq = C * C + D * D;
        double param = (len_sq != 0) ? dot / len_sq : -1;

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
}


