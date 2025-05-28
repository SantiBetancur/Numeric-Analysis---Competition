package JavaImplementation;
import java.util.List;
import javax.swing.JFrame;


public class Main {
    public static void main(String[] args) {
        // Leer las coordenadas del archivo
        List<Point> points = Reader.readCoord("JavaImplementation/../VRP_example/Coord.txt");

        

        System.out.println("Número de puntos: " + (points.size() - 1)); // Excluir el punto depósito
        Point start = points.get(0); // Depósito, donde comienza el recorrido

        // Generar la matriz de distancias
        DistanceMatrix distanceMatrix = new DistanceMatrix(points.size());
        distanceMatrix.generateDistances(points.toArray(new Point[0]));
        double[][] matrix = distanceMatrix.getMatrix();
        System.out.println("Matriz de distancias generada.");

        // Resolver usando el algoritmo probabilístico del vecino más cercano
        Method1 method = new Method1(12345L, 50); // Semilla fija y alpha=2.5

        System.out.println("\n" + "=".repeat(60));
        System.out.println("COMPARACIÓN DE ALGORITMOS");
        System.out.println("=".repeat(60));

        // ===== MÉTODO 1: Algoritmo tradicional (mínimos vehículos) =====
        long startTime1 = System.currentTimeMillis();
        System.out.println("\n[MÉTODO 1] Ejecutando algoritmo probabilístico (mínimos vehículos)...");
        List<List<Point>> routesMinimal = method.probabilisticNearestNeighbor(points, start, matrix, 49000, distanceMatrix.getMatrix());

        long endTime1 = System.currentTimeMillis();
        System.out.println("Tiempo de ejecución: " + (endTime1 - startTime1) + " ms");
        printRoutes(routesMinimal);

        // Show graphical routes
        javax.swing.SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Rutas de Vehículos");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(new PathPanel(routesMinimal));
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    // Imprimir las rutas de cáda vehículo
    private static void printRoutes(List<List<Point>> routes) {
        for (int i = 0; i < routes.size(); i++) {
            System.out.println("Ruta del vehículo " + (i + 1) + ": " + routes.get(i));
        }
    }

}