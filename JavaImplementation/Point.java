package JavaImplementation;

public class Point {
     // Point class to represent (x, y) coordinates
   
        public double x, y;

        public Point(double x, double y) {
            this.x = x;
            this.y = y;
        }

        public double distance(Point otherPoint) {
            // Calculate the Euclidean distance between two points
            return Math.sqrt(Math.pow(this.x - otherPoint.x, 2) + Math.pow(this.y - otherPoint.y, 2));
        }


        @Override
        public String toString() {
            return "(" + x + ", " + y + ")";
        }
    
}
