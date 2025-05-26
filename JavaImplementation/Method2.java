package JavaImplementation;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Method2 {
    private Random random;
    private double alpha; // Parámetro que controla la intensidad de la probabilidad
    
    public Method2() {
        this.random = new Random();
        this.alpha = 2.0; // Valor por defecto, puede ajustarse
    }
    
    public Method2(long seed, double alpha) {
        this.random = new Random(seed);
        this.alpha = alpha;
    }
    
    /**
     * Algoritmo probabilístico del vecino más cercano para VRP
     * @param points Lista de puntos (incluyendo depósito en índice 0)
     * @param startPoint Punto de inicio (depósito)
     * @param distanceMatrix Matriz de distancias
     * @param iterations Número de iteraciones para encontrar la mejor solución
     * @return Lista de rutas optimizadas
     */
    public List<List<Point>> probabilisticNearestNeighbor(List<Point> points, Point startPoint, 
                                                         double[][] distanceMatrix, int iterations) {
        List<List<Point>> bestRoutes = null;
        double bestTotalDistance = Double.MAX_VALUE;
        
        // Ejecutar múltiples iteraciones para encontrar la mejor solución
        for (int iter = 0; iter < iterations; iter++) {
            List<List<Point>> currentRoutes = solveSingleIteration(points, distanceMatrix);
            double totalDistance = calculateTotalDistance(currentRoutes, distanceMatrix, points);
            
            if (totalDistance < bestTotalDistance) {
                bestTotalDistance = totalDistance;
                bestRoutes = new ArrayList<>();
                for (List<Point> route : currentRoutes) {
                    bestRoutes.add(new ArrayList<>(route));
                }
            }
        }
        
        System.out.println("Mejor distancia total encontrada: " + bestTotalDistance);
        return bestRoutes;
    }
    
    /**
     * Algoritmo que garantiza el uso de todos los 20 vehículos disponibles
     * Distribuye los clientes de manera más equilibrada
     * @param points Lista de puntos (incluyendo depósito en índice 0)
     * @param startPoint Punto de inicio (depósito)
     * @param distanceMatrix Matriz de distancias
     * @param iterations Número de iteraciones para encontrar la mejor solución
     * @return Lista de 20 rutas (algunas pueden estar vacías si no es necesario)
     */
    public List<List<Point>> probabilisticAllVehicles(List<Point> points, Point startPoint, 
                                                     double[][] distanceMatrix, int iterations) {
        List<List<Point>> bestRoutes = null;
        double bestTotalDistance = Double.MAX_VALUE;
        
        // Ejecutar múltiples iteraciones para encontrar la mejor solución
        for (int iter = 0; iter < iterations; iter++) {
            List<List<Point>> currentRoutes = solveAllVehiclesIteration(points, distanceMatrix);
            double totalDistance = calculateTotalDistance(currentRoutes, distanceMatrix, points);
            
            if (totalDistance < bestTotalDistance) {
                bestTotalDistance = totalDistance;
                bestRoutes = new ArrayList<>();
                for (List<Point> route : currentRoutes) {
                    bestRoutes.add(new ArrayList<>(route));
                }
            }
        }
        
        System.out.println("Mejor distancia total (todos los vehículos): " + bestTotalDistance);
        return bestRoutes;
    }
    
    /**
     * Resuelve una iteración usando todos los vehículos disponibles
     */
    private List<List<Point>> solveAllVehiclesIteration(List<Point> points, double[][] distanceMatrix) {
        final int TOTAL_VEHICLES = 20;
        final int MAX_CLIENTS_PER_VEHICLE = 12;
        final int TOTAL_CLIENTS = 199;
        
        // Inicializar rutas para todos los vehículos
        List<List<Point>> routes = new ArrayList<>();
        for (int i = 0; i < TOTAL_VEHICLES; i++) {
            routes.add(new ArrayList<>());
        }
        
        boolean[] visited = new boolean[points.size()];
        visited[0] = true; // Marcar el depósito como visitado
        
        // Calcular distribución óptima de clientes por vehículo
        int baseClientsPerVehicle = TOTAL_CLIENTS / TOTAL_VEHICLES;
        int extraClients = TOTAL_CLIENTS % TOTAL_VEHICLES;
        
        // Asignar clientes a cada vehículo
        for (int vehicleId = 0; vehicleId < TOTAL_VEHICLES; vehicleId++) {
            int clientsForThisVehicle = baseClientsPerVehicle;
            if (vehicleId < extraClients) {
                clientsForThisVehicle++; // Distribuir clientes extra
            }
            
            // Limitar por la capacidad máxima del vehículo
            clientsForThisVehicle = Math.min(clientsForThisVehicle, MAX_CLIENTS_PER_VEHICLE);
            
            List<Point> route = buildRouteForVehicle(vehicleId, clientsForThisVehicle, 
                                                   points, distanceMatrix, visited);
            routes.set(vehicleId, route);
        }
        
        return routes;
    }
    
    /**
     * Construye una ruta para un vehículo específico usando selección probabilística
     */
    private List<Point> buildRouteForVehicle(int vehicleId, int maxClients, List<Point> points, 
                                           double[][] distanceMatrix, boolean[] visited) {
        List<Point> route = new ArrayList<>();
        int currentClient = 0; // Comenzar desde el depósito
        int servedClients = 0;
        
        // Usar diferentes semillas para cada vehículo para diversificar
        Random vehicleRandom = new Random(random.nextLong() + vehicleId);
        
        while (servedClients < maxClients) {
            int nextClient = selectNextClientWithVehiclePreference(currentClient, visited, 
                                                                 distanceMatrix, vehicleId, vehicleRandom);
            
            if (nextClient == -1) {
                break; // No hay más clientes disponibles
            }
            
            route.add(points.get(nextClient));
            visited[nextClient] = true;
            currentClient = nextClient;
            servedClients++;
        }
        
        return route;
    }
    
    /**
     * Selecciona el próximo cliente con preferencia por vehículo para diversificar rutas
     */
    private int selectNextClientWithVehiclePreference(int currentClient, boolean[] visited, 
                                                    double[][] distanceMatrix, int vehicleId, Random vehicleRandom) {
        List<Integer> candidates = new ArrayList<>();
        List<Double> probabilities = new ArrayList<>();
        double totalInverseProbability = 0.0;
        
        // Encontrar candidatos y calcular probabilidades
        for (int i = 1; i < distanceMatrix.length; i++) {
            if (!visited[i]) {
                candidates.add(i);
                
                // Calcular probabilidad base con distancia inversa
                double inverseProbability = Math.pow(1.0 / distanceMatrix[currentClient][i], alpha);
                
                // Agregar factor de diversificación basado en el ID del vehículo
                // Esto ayuda a que diferentes vehículos exploren diferentes áreas
                double diversificationFactor = 1.0 + 0.1 * Math.sin((vehicleId * i) * 0.1);
                inverseProbability *= diversificationFactor;
                
                probabilities.add(inverseProbability);
                totalInverseProbability += inverseProbability;
            }
        }
        
        if (candidates.isEmpty()) {
            return -1;
        }
        
        // Normalizar probabilidades
        for (int i = 0; i < probabilities.size(); i++) {
            probabilities.set(i, probabilities.get(i) / totalInverseProbability);
        }
        
        // Selección por ruleta con el random específico del vehículo
        return selectByRouletteWithRandom(candidates, probabilities, vehicleRandom);
    }
    
    /**
     * Selección por ruleta con generador aleatorio específico
     */
    private int selectByRouletteWithRandom(List<Integer> candidates, List<Double> probabilities, Random rand) {
        double randomValue = rand.nextDouble();
        double cumulativeProbability = 0.0;
        
        for (int i = 0; i < candidates.size(); i++) {
            cumulativeProbability += probabilities.get(i);
            if (randomValue <= cumulativeProbability) {
                return candidates.get(i);
            }
        }
        
        // Fallback: retornar el último candidato
        return candidates.get(candidates.size() - 1);
    }
    
    /**
     * Resuelve una iteración del algoritmo probabilístico
     */
    private List<List<Point>> solveSingleIteration(List<Point> points, double[][] distanceMatrix) {
        List<List<Point>> routes = new ArrayList<>();
        boolean[] visited = new boolean[points.size()];
        visited[0] = true; // Marcar el depósito como visitado
        
        int remainingClients = 199; // Total de clientes (excluyendo depósito)
        
        while (remainingClients > 0) {
            List<Point> route = new ArrayList<>();
            int currentClient = 0; // Comenzar desde el depósito
            int servedClients = 0;
            
            // Construir una ruta para un vehículo
            while (servedClients < 12 && remainingClients > 0) {
                int nextClient = selectNextClientProbabilistically(currentClient, visited, distanceMatrix);
                
                if (nextClient == -1) {
                    break; // No hay más clientes disponibles
                }
                
                route.add(points.get(nextClient));
                visited[nextClient] = true;
                currentClient = nextClient;
                servedClients++;
                remainingClients--;
            }
            
            if (!route.isEmpty()) {
                routes.add(route);
            }
        }
        
        return routes;
    }
    
    /**
     * Selecciona el próximo cliente usando probabilidades basadas en distancias inversas
     */
    private int selectNextClientProbabilistically(int currentClient, boolean[] visited, double[][] distanceMatrix) {
        List<Integer> candidates = new ArrayList<>();
        List<Double> probabilities = new ArrayList<>();
        double totalInverseProbability = 0.0;
        
        // Encontrar candidatos y calcular probabilidades
        for (int i = 1; i < distanceMatrix.length; i++) {
            if (!visited[i]) {
                candidates.add(i);
                // Usar distancia inversa elevada a alpha para aumentar la preferencia por distancias cortas
                double inverseProbability = Math.pow(1.0 / distanceMatrix[currentClient][i], alpha);
                probabilities.add(inverseProbability);
                totalInverseProbability += inverseProbability;
            }
        }
        
        if (candidates.isEmpty()) {
            return -1;
        }
        
        // Normalizar probabilidades
        for (int i = 0; i < probabilities.size(); i++) {
            probabilities.set(i, probabilities.get(i) / totalInverseProbability);
        }
        
        // Selección por ruleta
        return selectByRoulette(candidates, probabilities);
    }
    
    /**
     * Selección por ruleta basada en probabilidades
     */
    private int selectByRoulette(List<Integer> candidates, List<Double> probabilities) {
        double randomValue = random.nextDouble();
        double cumulativeProbability = 0.0;
        
        for (int i = 0; i < candidates.size(); i++) {
            cumulativeProbability += probabilities.get(i);
            if (randomValue <= cumulativeProbability) {
                return candidates.get(i);
            }
        }
        
        // Fallback: retornar el último candidato
        return candidates.get(candidates.size() - 1);
    }
    
    /**
     * Calcula la distancia total de todas las rutas
     */
    private double calculateTotalDistance(List<List<Point>> routes, double[][] distanceMatrix, List<Point> points) {
        double totalDistance = 0.0;
        
        for (List<Point> route : routes) {
            if (route.isEmpty()) continue;
            
            // Distancia del depósito al primer cliente
            int firstClientIndex = points.indexOf(route.get(0));
            totalDistance += distanceMatrix[0][firstClientIndex];
            
            // Distancias entre clientes consecutivos en la ruta
            for (int i = 0; i < route.size() - 1; i++) {
                int currentIndex = points.indexOf(route.get(i));
                int nextIndex = points.indexOf(route.get(i + 1));
                totalDistance += distanceMatrix[currentIndex][nextIndex];
            }
            
            // Distancia del último cliente de vuelta al depósito
            int lastClientIndex = points.indexOf(route.get(route.size() - 1));
            totalDistance += distanceMatrix[lastClientIndex][0];
        }
        
        return totalDistance;
    }
    
    /**
     * Mejora local usando 2-opt dentro de cada ruta
     */
    public List<List<Point>> improve2Opt(List<List<Point>> routes, double[][] distanceMatrix, List<Point> points) {
        List<List<Point>> improvedRoutes = new ArrayList<>();
        
        for (List<Point> route : routes) {
            if (route.size() <= 2) {
                improvedRoutes.add(new ArrayList<>(route));
                continue;
            }
            
            List<Point> bestRoute = new ArrayList<>(route);
            boolean improved = true;
            
            while (improved) {
                improved = false;
                double bestDistance = calculateRouteDistance(bestRoute, distanceMatrix, points);
                
                for (int i = 0; i < bestRoute.size() - 1; i++) {
                    for (int j = i + 2; j < bestRoute.size(); j++) {
                        List<Point> newRoute = perform2OptSwap(bestRoute, i, j);
                        double newDistance = calculateRouteDistance(newRoute, distanceMatrix, points);
                        
                        if (newDistance < bestDistance) {
                            bestRoute = newRoute;
                            bestDistance = newDistance;
                            improved = true;
                        }
                    }
                }
            }
            
            improvedRoutes.add(bestRoute);
        }
        
        return improvedRoutes;
    }
    
    private List<Point> perform2OptSwap(List<Point> route, int i, int j) {
        List<Point> newRoute = new ArrayList<>();
        
        // Agregar elementos antes del primer índice
        for (int k = 0; k <= i; k++) {
            newRoute.add(route.get(k));
        }
        
        // Agregar elementos invertidos entre i+1 y j
        for (int k = j; k >= i + 1; k--) {
            newRoute.add(route.get(k));
        }
        
        // Agregar elementos después del segundo índice
        for (int k = j + 1; k < route.size(); k++) {
            newRoute.add(route.get(k));
        }
        
        return newRoute;
    }
    
    private double calculateRouteDistance(List<Point> route, double[][] distanceMatrix, List<Point> points) {
        if (route.isEmpty()) return 0.0;
        
        double distance = 0.0;
        
        // Distancia del depósito al primer cliente
        int firstIndex = points.indexOf(route.get(0));
        distance += distanceMatrix[0][firstIndex];
        
        // Distancias entre clientes consecutivos
        for (int i = 0; i < route.size() - 1; i++) {
            int currentIndex = points.indexOf(route.get(i));
            int nextIndex = points.indexOf(route.get(i + 1));
            distance += distanceMatrix[currentIndex][nextIndex];
        }
        
        // Distancia del último cliente de vuelta al depósito
        int lastIndex = points.indexOf(route.get(route.size() - 1));
        distance += distanceMatrix[lastIndex][0];
        
        return distance;
    }
}