package org.una.navigatetrack.manager;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("exports")
public class DrawerManager {
    private static final double CIRCLE_RADIUS = 5;
    private static final double LINE_STROKE_WIDTH = 5;
    private static final double DISTANCE_TOLERANCE = 2.0;

    private final List<Circle> circles;
    private final List<Line> lines;
    private final Pane paintPane;
    // private boolean flag;

    public DrawerManager(Pane paintPane) {
        this.paintPane = paintPane;
        circles = new ArrayList<>();
        lines = new ArrayList<>();
    }

    // drawing figures
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
    // drawing figures end

    // deleting figures
    public void removeCircle(double[] point) {
        Circle circle = getCircleAt(point);
        if (circle != null) {
            removeCircle(circle);
        } else {
            System.out.println("Circle not found at the specified location.");
        }
    }

    public void removeCircle(Circle circle) {
        if (circles.remove(circle)) {
            paintPane.getChildren().remove(circle);
        } else {
            System.out.println("Circle not found in the list.");
        }
    }

    public void removeLine(double[] startPoint, double[] endPoint) {
        boolean flag = lines.removeIf(line -> {
            boolean matches = line.getStartX() == startPoint[0] && line.getStartY() == startPoint[1] &&
                    line.getEndX() == endPoint[0] && line.getEndY() == endPoint[1];

            if (matches)
                paintPane.getChildren().remove(line);

            return matches;
        });

        if (!flag)
            System.out.println("Line not found");
    }

    public void removeLine(Line line) {
        if (lines.remove(line)) {
            paintPane.getChildren().remove(line);
        }
    }
    // deleting figures end

    // gets figure if exist
    public Circle getCircleAt(double[] point) {
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

    public Line getLineAt(double[] point) {
        double x = point[0];
        double y = point[1];
        for (Line line : lines) {
            double distance = pointToLineDistance(line.getStartX(), line.getStartY(), line.getEndX(), line.getEndY(), x,
                    y);
            if (distance <= DISTANCE_TOLERANCE) {
                return line;
            }
        }
        return null;
    }

    public Line getline(double[] start, double[] end) {
        return null; // TODO
    }
    // gets figure if exist end

    // logic operations
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

    // metodos nuevos para obtener ubicaciones
    public double[] getLineAtWithCircle(double x, double y) {
        return getLineAtWithCircle(new double[]{x, y});
    }

    public double[] getLineAtWithCircle(double[] circleCenter) {// cordenadas de x,y= inicio [0],[1], fin [2], [3], en
        // lineaPosition = [4], [5], null no se encontro
        double radius = CIRCLE_RADIUS + 2; // Aumenta el radio en 2
        for (Line line : lines) {
            double distance = pointToLineDistance(
                    line.getStartX(), line.getStartY(),
                    line.getEndX(), line.getEndY(),
                    circleCenter[0], circleCenter[1]);

            // Comprueba si la distancia al círculo es menor o igual al nuevo radio
            if (distance <= radius) {
                double[] intersection = calculateIntersection(line, circleCenter, radius);
                if (intersection != null) {
                    return new double[]{
                            line.getStartX(), line.getStartY(),
                            line.getEndX(), line.getEndY(),
                            intersection[0], intersection[1]
                    };
                }
            }
        }
        return null; // Si no hay contacto
    }

    private double[] calculateIntersection(Line line, double[] circleCenter, double radius) {
        // Implementación para calcular el punto de intersección entre la línea y el
        // círculo
        double x1 = line.getStartX();
        double y1 = line.getStartY();
        double x2 = line.getEndX();
        double y2 = line.getEndY();
        double cx = circleCenter[0];
        double cy = circleCenter[1];

        double dx = x2 - x1;
        double dy = y2 - y1;
        double A = dx * dx + dy * dy;
        double B = 2 * (dx * (x1 - cx) + dy * (y1 - cy));
        double C = (x1 - cx) * (x1 - cx) + (y1 - cy) * (y1 - cy) - radius * radius;

        double discriminant = B * B - 4 * A * C;

        if (discriminant < 0) {
            return null; // No hay intersección
        } else {
            // Calculamos las intersecciones
            double t1 = (-B - Math.sqrt(discriminant)) / (2 * A);
            double t2 = (-B + Math.sqrt(discriminant)) / (2 * A);

            // Verificamos si las intersecciones están en el segmento de la línea
            double[] intersection = new double[2];

            if (t1 >= 0 && t1 <= 1) {
                intersection[0] = x1 + t1 * dx;
                intersection[1] = y1 + t1 * dy;
                return intersection; // Retorna la primera intersección válida
            } else if (t2 >= 0 && t2 <= 1) {
                intersection[0] = x1 + t2 * dx;
                intersection[1] = y1 + t2 * dy;
                return intersection; // Retorna la segunda intersección válida
            }
        }

        return null; // Si no se encuentra una intersección válida
    }

    // logic operations end
}

// public void setPaintPane(Pane paint) {
//// paintPane.getChildren().clear();
//// paintPane.getChildren().addAll(paint);
// paintAll();
// paintPane = paint;
// }
//
// private void paintAll() {
//// for (Circle circle : circles) {
//// drawCircle(circle, Color.BLUE);
//// }
//// for (Line line : lines) {
//// drawLine(line, Color.BLUE);
//// }
// }