package JavaImplementation;
import java.util.List;
// import javax.swing.JFrame;
// import javax.swing.SwingUtilities;


public class Main {
    public static void main(String[] args) {

        // Read the coordinates from the file
        List<Point> points = Reader.readCoord("JavaImplementation/../VRP_example/Coord.txt"); 
       
        System.out.println("Number of points: " + (points.size() - 1)); // Exclude the depot point);
        Point start = points.get(0); // Depot, where the path starts
        // Generate the distance matrix
        DistanceMatrix distanceMatrix = new DistanceMatrix(points.size());
        distanceMatrix.generateDistances(points.toArray(new Point[0]));
        double[][] matrix = distanceMatrix.getMatrix();
        System.out.println("Distance matrix generated.");
        // for (int i = 0; i < matrix.length; i++) {
        //     for (int j = 0; j < matrix[i].length; j++) {
        //         System.out.print(matrix[i][j] + " ");
        //     }
        //     System.out.println();
        // }

        // Solve using the nearest neighbor algorithm
        Methods method = new Methods();
        long startTime = System.currentTimeMillis();
        List<List<Point>> routes = method.nearestNeighbor(points, start, matrix);
        long endTime = System.currentTimeMillis();
        
        //Print solution
        System.out.println("Routes found: " + routes.size());
        for (int i = 0; i < routes.size(); i++) {
            System.out.println("Route " + (i + 1) + ": " + routes.get(i));
        }
        System.out.println("Execution time: " + (endTime - startTime) + " ms");

        // // Show the path
        // SwingUtilities.invokeLater(() -> {
        //     JFrame frame = new JFrame("Nearest Neighbor Path");
        //     frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //     frame.add(new PathPanel(path)); 
        //     frame.pack();
        //     frame.setLocationRelativeTo(null);
        //     frame.setVisible(true);
        // });
    }
}
