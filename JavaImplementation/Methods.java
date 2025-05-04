package JavaImplementation;
import java.util.ArrayList;
import java.util.List;


public class Methods {
    // Criteria from the VRP example:
    // 1. Start from the depot (first point in the list) Point 0.
    // 2. Clients: 199 (Remaining Points 1-199).
    // 3. Available Vehicles: 20.
    // 4. Vehicles Capacity: 12 clients.
    // Objetive: Asing clients to vehicles to minimize the distance traveled.

    // Implement the nearest neighbor algorithm to find a path through the points.
    public  List<List<Point>> nearestNeighbor(List<Point> points, Point startPoint, double[][] distanceMatrix) {
        List<List<Point>> routes = new ArrayList<>();
        boolean[] visited = new boolean[points.size()];
        visited[0] = true; // Mark the starting point as visited (Depot)
        
        int remainingClients = 199; // Total clients (excluding the depot) 
        while (remainingClients > 0) {
            // Create a new route for each vehicle
            List<Point> path = new ArrayList<>();
            int currentClient = 0;
            int servedClients = 0;
        
            while (servedClients < 12 && remainingClients > 0) {
                double minDist = Double.MAX_VALUE;
                int next = -1;
        
                for (int i = 1; i < 200; i++) {
                    if (!visited[i] && distanceMatrix[currentClient][i] < minDist) {
                        minDist = distanceMatrix[currentClient][i];
                        next = i;
                    }
                }
        
                if (next != -1) {
                    path.add(points.get(next));
                    visited[next] = true;
                    currentClient = next;
                    servedClients++;
                    remainingClients--;
                } else {
                    break;
                }
            }
        
            routes.add(path);
        }
        return routes;

    }

}
