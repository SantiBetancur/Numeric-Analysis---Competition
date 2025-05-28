package JavaImplementation;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class PathPanel extends JPanel {
    private final List<List<Point>> routes;
    private final int padding = 40;

    public PathPanel(List<List<Point>> routes) {
        this.routes = routes;
        setPreferredSize(new Dimension(800, 800));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Find min/max for scaling
        double minX = Double.MAX_VALUE, maxX = Double.MIN_VALUE;
        double minY = Double.MAX_VALUE, maxY = Double.MIN_VALUE;
        Point depot = null;
        for (List<Point> route : routes) {
            for (Point p : route) {
                if (p.id == 0) depot = p;
                minX = Math.min(minX, p.x);
                maxX = Math.max(maxX, p.x);
                minY = Math.min(minY, p.y);
                maxY = Math.max(maxY, p.y);
            }
        }

        int w = getWidth() - 2 * padding;
        int h = getHeight() - 2 * padding;
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;

        // Color palette (expand if you have more vehicles)
        Color[] colors = {Color.RED, Color.BLUE, Color.GREEN, Color.MAGENTA, Color.ORANGE, Color.CYAN, Color.PINK, Color.YELLOW, Color.GRAY, Color.BLACK};

        // Draw the depot at the center
        if (depot != null) {
            g.setColor(Color.BLACK);
            g.fillOval(centerX - 10, centerY - 10, 20, 20);
            g.setColor(Color.WHITE);
            g.drawString("0", centerX + 12, centerY - 12);
        }

        // Draw all routes from the center depot
        for (int i = 0; i < routes.size(); i++) {
            List<Point> route = routes.get(i);
            if (route.isEmpty()) continue;

            g.setColor(colors[i % colors.length]);

            // Draw line from depot (center) to first node in route (not depot)
            for (int j = 1; j < route.size(); j++) {
                Point p1 = (j == 1) ? depot : route.get(j - 1);
                Point p2 = route.get(j);

                int x1, y1;
                if (j == 1) {
                    x1 = centerX;
                    y1 = centerY;
                } else {
                    x1 = (int) ((p1.x - minX) / (maxX - minX) * w) + padding;
                    y1 = (int) ((p1.y - minY) / (maxY - minY) * h) + padding;
                }
                int x2 = (int) ((p2.x - minX) / (maxX - minX) * w) + padding;
                int y2 = (int) ((p2.y - minY) / (maxY - minY) * h) + padding;

                g.drawLine(x1, y1, x2, y2);
                g.fillOval(x2 - 4, y2 - 4, 8, 8);
                g.setColor(Color.BLACK);
                g.drawString(String.valueOf(p2.id), x2 + 6, y2 - 6);
                g.setColor(colors[i % colors.length]);
            }
        }

        // Legend dimensions and position (top right corner)
        int boxSize = 16;
        int vehiclesPerColumn = 15; // Adjust for your window size
        int columnWidth = 120;
        g.setFont(new Font("Arial", Font.PLAIN, 14));
        int legendHeight = Math.min(routes.size(), vehiclesPerColumn) * (boxSize + 6) + 10;
        int legendWidth = ((routes.size() - 1) / vehiclesPerColumn + 1) * columnWidth;
        int legendX = getWidth() - legendWidth - padding;
        int legendY = padding;

        // Optional: Draw background for legend
        g.setColor(new Color(255, 255, 255, 220));
        g.fillRect(legendX - 8, legendY - 8, legendWidth, legendHeight);

        for (int i = 0; i < routes.size(); i++) {
            int col = i / vehiclesPerColumn;
            int row = i % vehiclesPerColumn;
            int x = legendX + col * columnWidth;
            int y = legendY + row * (boxSize + 6);

            g.setColor(colors[i % colors.length]);
            g.fillRect(x, y, boxSize, boxSize);
            g.setColor(Color.BLACK);
            g.drawRect(x, y, boxSize, boxSize);
            g.drawString("Vehículo " + (i + 1), x + boxSize + 8, y + boxSize - 3);
        }
    }
}
