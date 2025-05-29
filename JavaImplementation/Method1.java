package JavaImplementation;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Method1 {
    private Random random;
    private double alpha;

    public Method1() {
        this.random = new Random();
        this.alpha = 2.0;
    }

    public Method1(long seed, double alpha) {
        this.random = new Random(seed);
        this.alpha = alpha;
    }

    public List<List<Point>> solveVRP(List<Point> points, Point startPoint, 
                                                         double[][] distanceMatrix, int iterations, double[][] matrix) {
        List<List<Point>> bestRoutes = null;
        double bestTotalDistance = Double.MAX_VALUE;

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

        System.out.println("Mejor distancia (Nearest Neightboor): " + bestTotalDistance);
        System.out.println("Aplicando mejora local 2-opt...");
        bestRoutes = improve2Opt(bestRoutes, matrix, points);

        System.out.println("Aplicando intercambios entre rutas...");
        bestRoutes = improveInterRoute(bestRoutes, matrix, points);

        double finalDistance = calculateTotalDistance(bestRoutes, matrix, points);
        System.out.println("Mejor distancia (algoritmo de inserción inteligente + NNA): " + finalDistance);

        return bestRoutes;
    }

    /**
     * Resuelve una iteración combinando construcción probabilística e inserción inteligente
     */
    private List<List<Point>> solveSingleIteration(List<Point> points, double[][] distanceMatrix) {
        List<List<Point>> routes = new ArrayList<>();
        boolean[] visited = new boolean[points.size()];
        visited[0] = true;

        int remainingClients = 199;

        while (remainingClients > 0) {
            List<Point> route = new ArrayList<>();
            int servedClients = 0;

            // ENFOQUE HÍBRIDO: Alternar entre probabilístico e inserción inteligente
            boolean useProbabilistic = random.nextBoolean();

            while (servedClients < 12 && remainingClients > 0) {
                int nextClient = -1;

                if (useProbabilistic && servedClients < 8) {
                    // Usar construcción probabilística para los primeros clientes
                    int currentClient = route.isEmpty() ? 0 : points.indexOf(route.get(route.size() - 1));
                    nextClient = selectNextClientProbabilistically(currentClient, visited, distanceMatrix);
                } else {
                    // Usar inserción inteligente para completar la ruta
                    nextClient = selectBestInsertion(route, visited, distanceMatrix, points);
                }

                if (nextClient == -1) {
                    break;
                }

                route.add(points.get(nextClient));
                visited[nextClient] = true;
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
     * MÉTODO CORREGIDO: Selecciona el mejor cliente para insertar en la ruta
     */
    private int selectBestInsertion(List<Point> currentRoute, boolean[] visited, 
                                   double[][] distanceMatrix, List<Point> points) {
        int bestClient = -1;
        double bestInsertionCost = Double.MAX_VALUE;

        for (int client = 1; client < visited.length; client++) {
            if (!visited[client]) {
                double minInsertionCost = calculateBestInsertionCost(currentRoute, client, distanceMatrix, points);
                
                if (minInsertionCost < bestInsertionCost) {
                    bestInsertionCost = minInsertionCost;
                    bestClient = client;
                }
            }
        }
        return bestClient;
    }

    /**
     * MÉTODO CORREGIDO: Calcula el mejor costo de inserción para un cliente
     */
    private double calculateBestInsertionCost(List<Point> route, int clientIndex, 
                                             double[][] distanceMatrix, List<Point> points) {
        if (route.isEmpty()) {
            // Si la ruta está vacía, el costo es ida y vuelta desde el depósito
            return 2 * distanceMatrix[0][clientIndex];
        }

        double minCost = Double.MAX_VALUE;

        // Probar inserción en cada posición posible
        for (int pos = 0; pos <= route.size(); pos++) {
            double insertionCost = calculateInsertionCostAtPosition(route, clientIndex, pos, distanceMatrix, points);
            minCost = Math.min(minCost, insertionCost);
        }

        return minCost;
    }

    /**
     *  Calcular el costo de insertar un cliente en una posición específica
     */
    private double calculateInsertionCostAtPosition(List<Point> route, int clientIndex, int position, 
                                                   double[][] distanceMatrix, List<Point> points) {
        double insertionCost = 0.0;

        if (position == 0) {
            // Insertar al principio
            if (route.isEmpty()) {
                insertionCost = 2 * distanceMatrix[0][clientIndex];
            } else {
                int firstClientIndex = points.indexOf(route.get(0));
                insertionCost = distanceMatrix[0][clientIndex] + distanceMatrix[clientIndex][firstClientIndex] 
                              - distanceMatrix[0][firstClientIndex];
            }
        } else if (position == route.size()) {
            // Insertar al final
            int lastClientIndex = points.indexOf(route.get(route.size() - 1));
            insertionCost = distanceMatrix[lastClientIndex][clientIndex] + distanceMatrix[clientIndex][0] 
                          - distanceMatrix[lastClientIndex][0];
        } else {
            // Insertar en el medio
            int prevClientIndex = points.indexOf(route.get(position - 1));
            int nextClientIndex = points.indexOf(route.get(position));
            insertionCost = distanceMatrix[prevClientIndex][clientIndex] + distanceMatrix[clientIndex][nextClientIndex] 
                          - distanceMatrix[prevClientIndex][nextClientIndex];
        }

        return insertionCost;
    }

    /**
     * Selecciona el siguiente cliente de forma probabilística basado en la distancia
     * @param currentClient Índice del cliente actual
     * @param visited Array de booleanos que indica si un cliente ha sido visitado
     * @param distanceMatrix Matriz de distancias entre clientes
     * @return Índice del siguiente cliente seleccionado, o -1 si no hay más clientes disponibles
     */
    private int selectNextClientProbabilistically(int currentClient, boolean[] visited, double[][] distanceMatrix) {
        List<Integer> candidates = new ArrayList<>();
        List<Double> probabilities = new ArrayList<>();
        double totalInverseProbability = 0.0;

        for (int i = 1; i < distanceMatrix.length; i++) {
            if (!visited[i]) {
                candidates.add(i);
                double inverseProbability = Math.pow(1.0 / distanceMatrix[currentClient][i], alpha);
                probabilities.add(inverseProbability);
                totalInverseProbability += inverseProbability;
            }
        }

        if (candidates.isEmpty()) {
            return -1;
        }

        for (int i = 0; i < probabilities.size(); i++) {
            probabilities.set(i, probabilities.get(i) / totalInverseProbability);
        }

        return selectByRoulette(candidates, probabilities);
    }

    /**
     * Selecciona un cliente usando el método de ruleta basado en las probabilidades
     * @param candidates Lista de candidatos (índices de clientes)
     * @param probabilities Lista de probabilidades normalizadas
     * @return Índice del cliente seleccionado
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
        
        return candidates.get(candidates.size() - 1);
    }
    
    private double calculateTotalDistance(List<List<Point>> routes, double[][] distanceMatrix, List<Point> points) {
        double totalDistance = 0.0;

        for (List<Point> route : routes) {
            if (route.isEmpty()) continue;

            int firstClientIndex = points.indexOf(route.get(0));
            totalDistance += distanceMatrix[0][firstClientIndex];

            for (int i = 0; i < route.size() - 1; i++) {
                int currentIndex = points.indexOf(route.get(i));
                int nextIndex = points.indexOf(route.get(i + 1));
                totalDistance += distanceMatrix[currentIndex][nextIndex];
            }

            int lastClientIndex = points.indexOf(route.get(route.size() - 1));
            totalDistance += distanceMatrix[lastClientIndex][0];
        }

        return totalDistance;
    }

    /*
    Algoritmo de mejora local 2-opt para optimizar las rutas
    Este método toma una lista de rutas y mejora cada ruta individualmente
    utilizando el algoritmo 2-opt. El objetivo es reducir la distancia total de cada ruta
    mediante la eliminación de cruces y la optimización de la secuencia de puntos.
    El algoritmo 2-opt funciona intercambiando dos aristas en la ruta para ver si se reduce la distancia total.
    Este proceso se repite hasta que no se pueden encontrar más mejoras.
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

    /**
     * Realiza un intercambio 2-opt entre dos posiciones i y j en la ruta
     * @param route Ruta original
     * @param i Índice del primer punto a intercambiar
     * @param j Índice del segundo punto a intercambiar
     * @return Nueva ruta después del intercambio 2-opt
     */

    private List<Point> perform2OptSwap(List<Point> route, int i, int j) {
        List<Point> newRoute = new ArrayList<>();

        for (int k = 0; k <= i; k++) {
            newRoute.add(route.get(k));
        }

        for (int k = j; k >= i + 1; k--) {
            newRoute.add(route.get(k));
        }

        for (int k = j + 1; k < route.size(); k++) {
            newRoute.add(route.get(k));
        }

        return newRoute;
    }

    private double calculateRouteDistance(List<Point> route, double[][] distanceMatrix, List<Point> points) {
        if (route.isEmpty()) return 0.0;

        double distance = 0.0;
        int firstIndex = points.indexOf(route.get(0));
        distance += distanceMatrix[0][firstIndex];

        for (int i = 0; i < route.size() - 1; i++) {
            int currentIndex = points.indexOf(route.get(i));
            int nextIndex = points.indexOf(route.get(i + 1));
            distance += distanceMatrix[currentIndex][nextIndex];
        }

        int lastIndex = points.indexOf(route.get(route.size() - 1));
        distance += distanceMatrix[lastIndex][0];

        return distance;
    }

    // Método para mejorar las rutas entre sí
    /**
     * Mejora las rutas entre sí mediante intercambios de clientes
     * @param routes Lista de rutas
     * @param distanceMatrix Matriz de distancias
     * @param points Lista de puntos
     * @return Rutas mejoradas
     */
    public List<List<Point>> improveInterRoute(List<List<Point>> routes, double[][] distanceMatrix, List<Point> points) {
        boolean improved = true;

        while (improved) {
            improved = false;

            for (int i = 0; i < routes.size(); i++) {
                for (int j = i + 1; j < routes.size(); j++) {
                    if (tryRouteExchanges(routes, i, j, distanceMatrix, points)) {
                        improved = true;
                    }
                }
            }
        }

        return routes;
    }



        /**
     * Intenta diferentes tipos de intercambios entre dos rutas
     * @param routes Lista de todas las rutas
     * @param routeIndex1 Índice de la primera ruta
     * @param routeIndex2 Índice de la segunda ruta
     * @param distanceMatrix Matriz de distancias
     * @param points Lista de todos los puntos
     * @return true si se encontró una mejora, false en caso contrario
     */
    private boolean tryRouteExchanges(List<List<Point>> routes, int routeIndex1, int routeIndex2, 
                                    double[][] distanceMatrix, List<Point> points) {
        List<Point> route1 = routes.get(routeIndex1);
        List<Point> route2 = routes.get(routeIndex2);
        
        // Calcular distancia actual de ambas rutas
        double currentDistance = calculateRouteDistance(route1, distanceMatrix, points) + 
                            calculateRouteDistance(route2, distanceMatrix, points);

        boolean improved = false;

        // 1. Intercambios 1-1 (intercambiar un cliente de cada ruta)
        if (try11Exchange(route1, route2, currentDistance, distanceMatrix, points)) {
            improved = true;
        }

        // 2. Intercambios 2-1 (dos clientes de ruta1 por uno de ruta2)
        if (!improved && try21Exchange(route1, route2, currentDistance, distanceMatrix, points)) {
            improved = true;
        }

        // 3. Intercambios 1-2 (un cliente de ruta1 por dos de ruta2)
        if (!improved && try12Exchange(route1, route2, currentDistance, distanceMatrix, points)) {
            improved = true;
        }

        // 4. Intercambios 1-0 (mover un cliente de una ruta a otra)
        if (!improved && try10Exchange(route1, route2, currentDistance, distanceMatrix, points)) {
            improved = true;
        }

        // 5. Intercambios 0-1 (mover un cliente de ruta2 a ruta1)
        if (!improved && try01Exchange(route1, route2, currentDistance, distanceMatrix, points)) {
            improved = true;
        }

        return improved;
    }

    /**
     * Intercambio 1-1: Intercambia un cliente de cada ruta
     */
    private boolean try11Exchange(List<Point> route1, List<Point> route2, double currentDistance,
                                double[][] distanceMatrix, List<Point> points) {
        if (route1.isEmpty() || route2.isEmpty()) return false;

        for (int i = 0; i < route1.size(); i++) {
            for (int j = 0; j < route2.size(); j++) {
                // Verificar restricciones de capacidad antes del intercambio
                if (!isValidExchange11(route1, route2, i, j)) continue;

                // Realizar intercambio temporal
                Point temp = route1.get(i);
                route1.set(i, route2.get(j));
                route2.set(j, temp);

                // Calcular nueva distancia
                double newDistance = calculateRouteDistance(route1, distanceMatrix, points) + 
                                calculateRouteDistance(route2, distanceMatrix, points);

                if (newDistance < currentDistance) {
                    return true; // Mantener el intercambio
                } else {
                    // Revertir intercambio
                    route2.set(j, route1.get(i));
                    route1.set(i, temp);
                }
            }
        }
        return false;
    }

    /**
     * Intercambio 2-1: Dos clientes de route1 por uno de route2
     */
    private boolean try21Exchange(List<Point> route1, List<Point> route2, double currentDistance,
                                double[][] distanceMatrix, List<Point> points) {
        if (route1.size() < 2 || route2.isEmpty()) return false;

        for (int i = 0; i < route1.size() - 1; i++) {
            for (int j = 0; j < route2.size(); j++) {
                // Verificar restricciones de capacidad
                if (!isValidExchange21(route1, route2, i, j)) continue;

                // Guardar elementos para posible reversión
                Point client1Route1 = route1.get(i);
                Point client2Route1 = route1.get(i + 1);
                Point clientRoute2 = route2.get(j);

                // Realizar intercambio: quitar 2 de route1, agregar 1
                route1.remove(i + 1);
                route1.remove(i);
                route1.add(i, clientRoute2);

                // Agregar 2 clientes a route2
                route2.remove(j);
                route2.add(j, client1Route1);
                route2.add(j + 1, client2Route1);

                double newDistance = calculateRouteDistance(route1, distanceMatrix, points) +
                                calculateRouteDistance(route2, distanceMatrix, points);

                if (newDistance < currentDistance) {
                    return true;
                } else {
                    // Revertir cambios
                    route2.remove(j + 1);
                    route2.remove(j);
                    route2.add(j, clientRoute2);

                    route1.remove(i);
                    route1.add(i, client1Route1);
                    route1.add(i + 1, client2Route1);
                }
            }
        }
        return false;
    }

    /**
     * Intercambio 1-2: Un cliente de route1 por dos de route2
     */
    private boolean try12Exchange(List<Point> route1, List<Point> route2, double currentDistance,
                                double[][] distanceMatrix, List<Point> points) {
        if (route1.isEmpty() || route2.size() < 2) return false;
        for (int i = 0; i < route1.size(); i++) {
            for (int j = 0; j < route2.size() - 1; j++) {
                // Verificar restricciones de capacidad
                if (!isValidExchange12(route1, route2, i, j)) continue;

                Point clientRoute1 = route1.get(i);
                Point client1Route2 = route2.get(j);
                Point client2Route2 = route2.get(j + 1);
                // Realizar intercambio
                route1.remove(i);
                route1.add(i, client1Route2);
                route1.add(i + 1, client2Route2);
                route2.remove(j + 1);
                route2.remove(j);
                route2.add(j, clientRoute1);

                double newDistance = calculateRouteDistance(route1, distanceMatrix, points) + 
                                calculateRouteDistance(route2, distanceMatrix, points);
                if (newDistance < currentDistance) {
                    return true;
                } else {
                    // Revertir cambios
                    route2.remove(j);
                    route2.add(j, client1Route2);
                    route2.add(j + 1, client2Route2);
                    
                    route1.remove(i + 1);
                    route1.remove(i);
                    route1.add(i, clientRoute1);
                }
            }
        }
        return false;
    }

    /**
     * Intercambio 1-0: Mover un cliente de route1 a route2
     */
    private boolean try10Exchange(List<Point> route1, List<Point> route2, double currentDistance,
                                double[][] distanceMatrix, List<Point> points) {
        if (route1.isEmpty()) return false;
        
        for (int i = 0; i < route1.size(); i++) {
            // Verificar si route2 puede aceptar un cliente más (capacidad)
            if (route2.size() >= 12) continue; // Asumiendo capacidad máxima de 12
            
            Point client = route1.get(i);
            route1.remove(i);
            
            // Probar insertar en cada posición posible de route2
            int bestPosition = -1;
            double bestDistance = Double.MAX_VALUE;

            for (int pos = 0; pos <= route2.size(); pos++) {
                route2.add(pos, client);

                double newDistance = calculateRouteDistance(route1, distanceMatrix, points) + 
                                calculateRouteDistance(route2, distanceMatrix, points);

                if (newDistance < bestDistance) {
                    bestDistance = newDistance;
                    bestPosition = pos;
                }

                route2.remove(pos);
            }

            if (bestDistance < currentDistance) {
                route2.add(bestPosition, client);
                return true;
            } else {
                // Revertir: devolver cliente a route1
                route1.add(i, client);
            }
        }
        return false;
    }

    /**
     * Intercambio 0-1: Mover un cliente de route2 a route1
     */
    private boolean try01Exchange(List<Point> route1, List<Point> route2, double currentDistance,
                                double[][] distanceMatrix, List<Point> points) {
        return try10Exchange(route2, route1, currentDistance, distanceMatrix, points);
    }

    /**
     * Verifica si un intercambio 1-1 es válido (respeta restricciones de capacidad)
     */
    private boolean isValidExchange11(List<Point> route1, List<Point> route2, int pos1, int pos2) {
        // En este caso, el intercambio 1-1 no cambia el número de clientes por ruta
        // por lo que siempre es válido en términos de capacidad
        return true;
    }

    /**
     * Verifica si un intercambio 2-1 es válido
     */
    private boolean isValidExchange21(List<Point> route1, List<Point> route2, int pos1, int pos2) {
        // route1 pierde 2 clientes y gana 1: debe tener al menos 2
        // route2 pierde 1 cliente y gana 2: no debe exceder capacidad máxima
        return route1.size() >= 2 && route2.size() + 1 <= 12;
    }

    /**
     * Verifica si un intercambio 1-2 es válido
     */
    private boolean isValidExchange12(List<Point> route1, List<Point> route2, int pos1, int pos2) {
        // route1 pierde 1 cliente y gana 2: no debe exceder capacidad máxima
        // route2 pierde 2 clientes y gana 1: debe tener al menos 2
        return route1.size() + 1 <= 12 && route2.size() >= 2;
    }
}