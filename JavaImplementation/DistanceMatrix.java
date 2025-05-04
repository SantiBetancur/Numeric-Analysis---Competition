package JavaImplementation;

public class DistanceMatrix {
    double[][] matrix;
    int size;

    public DistanceMatrix(int size) {
        this.size = size;
        matrix = new double[size][size];
    }

    public void generateDistances(Point[] points) {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (i != j) {
                    matrix[i][j] = points[i].distance(points[j]);
                } else {
                    matrix[i][j] = 0;
                }
            }
        }
    }

    double[][] getMatrix() {
        return matrix;
    }
}
