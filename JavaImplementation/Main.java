package JavaImplementation;
import java.util.List;


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
        Method1 method = new Method1(12345L, 6.0); // Semilla fija y alpha=2.5

        System.out.println("\n" + "=".repeat(60));
        System.out.println("COMPARACIÓN DE ALGORITMOS");
        System.out.println("=".repeat(60));

        // ===== MÉTODO 1: Algoritmo tradicional (mínimos vehículos) =====
        long startTime1 = System.currentTimeMillis();
        System.out.println("\n[MÉTODO 1] Ejecutando algoritmo probabilístico (mínimos vehículos)...");
        List<List<Point>> routesMinimal = method.probabilisticNearestNeighbor(points, start, matrix, 150000);

        System.out.println("Aplicando mejora local 2-opt...");
        routesMinimal = method.improve2Opt(routesMinimal, matrix, points);
        long endTime1 = System.currentTimeMillis();
        System.out.println("Tiempo de ejecución: " + (endTime1 - startTime1) + " ms");

        // ===== COMPARAR RESULTADOS =====
        System.out.println("\n" + "=".repeat(60));
        System.out.println("RESULTADOS COMPARATIVOS");
        System.out.println("=".repeat(60));

        // Método 1 - Mínimos vehículos
        System.out.println("\n MÉTODO 1 - MÍNIMOS VEHÍCULOS:");
        System.out.println("Rutas encontradas: " + routesMinimal.size());
        int totalClients1 = printRoutesSummary(routesMinimal);
        System.out.println("Total de clientes atendidos: " + totalClients1);
        System.out.println("Vehículos utilizados: " + routesMinimal.size() + "/20");
        System.out.println("Tiempo de ejecución: " + (endTime1 - startTime1) + " ms");
        double totalDistance1 = showRouteStatistics(routesMinimal, matrix, points, "MÉTODO 1");



    }

    private static int printRoutesSummary(List<List<Point>> routes) {
        int totalClients = 0;
        int routeCount = 0;

        for (int i = 0; i < routes.size(); i++) {
            List<Point> route = routes.get(i);
            if (!route.isEmpty()) {
                routeCount++;
                totalClients += route.size();
                if (routeCount <= 5) { // Mostrar solo las primeras 5 rutas para no saturar
                    System.out.println("  Ruta " + (i + 1) + ": " + route.size() + " clientes");
                } else if (routeCount == 6) {
                    System.out.println("  ... (mostrando solo primeras 5 rutas)");
                }
            }
        }
        return totalClients;
    }
    
    private static double showRouteStatistics(List<List<Point>> routes, double[][] matrix, List<Point> points, String methodName) {
        double totalDistance = 0.0;
        double maxRouteDistance = 0.0;
        double minRouteDistance = Double.MAX_VALUE;
        
        System.out.println("\n=== ESTADÍSTICAS DE RUTAS ===");
        
        for (int i = 0; i < routes.size(); i++) {
            List<Point> route = routes.get(i);
            double routeDistance = calculateRouteDistance(route, matrix, points);
            totalDistance += routeDistance;
            
            maxRouteDistance = Math.max(maxRouteDistance, routeDistance);
            minRouteDistance = Math.min(minRouteDistance, routeDistance);
            
            System.out.printf("Ruta %d: %.2f unidades de distancia%n", i + 1, routeDistance);
        }
        
        return totalDistance;
    }
    
    private static double calculateRouteDistance(List<Point> route, double[][] matrix, List<Point> points) {
        if (route.isEmpty()) return 0.0;
        
        double distance = 0.0;
        
        // Distancia del depósito al primer cliente
        int firstIndex = points.indexOf(route.get(0));
        distance += matrix[0][firstIndex];
        
        // Distancias entre clientes consecutivos
        for (int i = 0; i < route.size() - 1; i++) {
            int currentIndex = points.indexOf(route.get(i));
            int nextIndex = points.indexOf(route.get(i + 1));
            distance += matrix[currentIndex][nextIndex];
        }
        
        // Distancia del último cliente de vuelta al depósito
        int lastIndex = points.indexOf(route.get(route.size() - 1));
        distance += matrix[lastIndex][0];
        
        return distance;
    }
}