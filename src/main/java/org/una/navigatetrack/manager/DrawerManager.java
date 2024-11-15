package org.una.navigatetrack.manager;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

import java.util.ArrayList;
import java.util.List;

//@SuppressWarnings("exports")
public class DrawerManager {

    private static final double CIRCLE_RADIUS = 5;
    private static final double LINE_STROKE_WIDTH = 5;
    private static final double DISTANCE_TOLERANCE = 2.0;

    private final List<Circle> circles;
    private final List<Line> lines;
    private final Pane paintPane;

    public DrawerManager(Pane paintPane) {
        this.paintPane = paintPane;
        circles = new ArrayList<>();
        lines = new ArrayList<>();
    }

    // Drawing figures
    public void drawCircle(double centerX, double centerY, Color color) {
        Circle circle = new Circle(centerX, centerY, CIRCLE_RADIUS);
        drawCircle(circle, color);
    }

    public void drawCircle(Circle circle, Color color) {
        if (circle == null || color == null) {
            throw new IllegalArgumentException("Circle and color cannot be null");
        }

        circle.setFill(color);
        circle.setStroke(color);
        circle.setStrokeWidth(1);
        paintPane.getChildren().add(circle);
        circles.add(circle);
    }

    public void drawLine(double startX, double startY, double endX, double endY, Color color) {
        if (color == null) {
            throw new IllegalArgumentException("Color cannot be null");
        }

        Line line = new Line(startX, startY, endX, endY);
        drawLine(line, color);
    }

    public void drawLine(Line line, Color color) {
        if (line == null || color == null) {
            throw new IllegalArgumentException("Line and color cannot be null");
        }

        line.setStroke(color);
        line.setStrokeWidth(LINE_STROKE_WIDTH);
        paintPane.getChildren().add(line);
        lines.add(line);
    }

    // Removing figures
    public void removeCircle(double[] point) {
        try {
            Circle circle = getCircleAt(point);
            if (circle != null) {
                removeCircle(circle);
            } else {
                throw new ShapeNotFoundException("Circle not found at the specified location.");
            }
        } catch (ShapeNotFoundException e) {
            logError(e.getMessage());
        }
    }

    public void removeCircle(Circle circle) {
        if (circle == null) {
            logError("Attempted to remove a null circle.");
            return;
        }

        if (circles.remove(circle)) {
            paintPane.getChildren().remove(circle);
        } else {
            logError("Circle not found in the list.");
        }
    }

    public void removeLine(double[] startPoint, double[] endPoint) {
        if (startPoint == null || endPoint == null) {
            logError("Start or end point cannot be null.");
            return;
        }

        // Buscar y eliminar la línea usando los puntos proporcionados
//        Line line = findLineByCoordinates(startPoint, endPoint);
        Line line = new Line(startPoint[0], startPoint[1], endPoint[0], endPoint[1]);
        removeLine(line);  // Elimina la línea de manera eficiente
    }

    // Método para eliminar una línea directamente
    public void removeLine(Line line) {
        if (line == null) {
            logError("Attempted to remove a null line.");
            return;
        }

        double[] start = new double[]{line.getStartX(), line.getStartY()};
        double[] end = new double[]{line.getEndX(), line.getEndY()};

        line = findLineByCoordinates(start, end);

        // Buscar y eliminar la línea de manera eficiente
        if (lines.contains(line)) {
            paintPane.getChildren().remove(line);
            lines.remove(line);
        } else {
            logError("Line not found in the list.");
        }
    }

    // Función auxiliar para buscar una línea en base a sus coordenadas
    private Line findLineByCoordinates(double[] startPoint, double[] endPoint) {
        for (Line line : lines) {
            if ((line.getStartX() == startPoint[0] && line.getStartY() == startPoint[1] &&
                    line.getEndX() == endPoint[0] && line.getEndY() == endPoint[1]) ||
                    (line.getStartX() == endPoint[0] && line.getStartY() == endPoint[1] &&
                            line.getEndX() == startPoint[0] && line.getEndY() == startPoint[1])) {
                return line;
            }
        }
        return null;
    }


    // Get figures if exist
    public Circle getCircleAt(double[] point) {
        if (point == null) {
            logError("Point cannot be null.");
            return null;
        }

        double x = point[0];
        double y = point[1];
        for (Circle circle : circles) {
            if (distanceSquared(x, y, circle.getCenterX(), circle.getCenterY()) <= circle.getRadius() * circle.getRadius()) {
                return circle;
            }
        }
        return null;
    }

    public Line getLineAt(double[] point) {
        if (point == null) {
            logError("Point cannot be null.");
            return null;
        }

        double x = point[0];
        double y = point[1];
        for (Line line : lines) {
            if (pointToLineDistance(line, x, y) <= DISTANCE_TOLERANCE) {
                return line;
            }
        }
        return null;
    }

    // Logic operations
    private double pointToLineDistance(Line line, double px, double py) {
        double x1 = line.getStartX();
        double y1 = line.getStartY();
        double x2 = line.getEndX();
        double y2 = line.getEndY();

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

    public double[] getLineAtWithCircle(double x, double y) {
        return getLineAtWithCircle(new double[]{x, y});
    }

    // New methods for locating intersections
    public double[] getLineAtWithCircle(double[] circleCenter) {
        if (circleCenter == null) {
            logError("Circle center cannot be null.");
            return null;
        }

        double radius = CIRCLE_RADIUS + 2;  // Increased radius by 2
        for (Line line : lines) {
            double distance = pointToLineDistance(line, circleCenter[0], circleCenter[1]);
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
        return null;
    }

    private double[] calculateIntersection(Line line, double[] circleCenter, double radius) {
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
            return null; // No intersection
        } else {
            double t1 = (-B - Math.sqrt(discriminant)) / (2 * A);
            double t2 = (-B + Math.sqrt(discriminant)) / (2 * A);

            double[] intersection = new double[2];
            if (t1 >= 0 && t1 <= 1) {
                intersection[0] = x1 + t1 * dx;
                intersection[1] = y1 + t1 * dy;
                return intersection;  // First valid intersection
            } else if (t2 >= 0 && t2 <= 1) {
                intersection[0] = x1 + t2 * dx;
                intersection[1] = y1 + t2 * dy;
                return intersection;  // Second valid intersection
            }
        }
        return null;  // No valid intersection
    }

    public void removeLines() {
        if (lines == null || lines.isEmpty()) {
            System.out.println("No hay líneas para eliminar.");
            return;
        }
        List<Line> linesCopy = new ArrayList<>(lines);
        for (Line line : linesCopy) {
            if (line != null) {
                removeLine(line);  // Llama a la función para eliminar la línea
            } else {
                System.out.println("Advertencia: Línea nula encontrada, saltando.");
            }
        }
    }


    private void logError(String message) {
        System.err.println("[ERROR] " + message);
    }
}
