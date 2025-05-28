# Numeric Analysis - Vehicle Routing Problem (VRP) Competition

This project implements and visualizes algorithms for the Vehicle Routing Problem (VRP) using Java. It includes a graphical interface to display the routes taken by each vehicle, color-coded for clarity.

## Features

- **VRP Solver:** Probabilistic Nearest Neighbor Algorithm for route optimization.
- **Graphical Visualization:** Interactive Swing-based panel showing all routes, depot, and node IDs.
- **Legend:** Color-coded legend for each vehicle.
- **Flexible Input:** Reads coordinates and distance matrices from provided files.

## Project Structure

```
Numeric-Analysis---Competition/
│
├── JavaImplementation/
│   ├── Main.java           # Main entry point
│   ├── Point.java          # Point class (nodes)
│   ├── DistanceMatrix.java # Distance matrix generator
│   ├── Method1.java        # Probabilistic Nearest Neighbor algorithm
│   ├── PathPanel.java      # Visualization panel
│   └── Reader.java         # File reader for coordinates
│
├── VRP_example/
│   ├── Coord.txt           # Example coordinates
│   ├── Dist.txt            # Example distance matrix
│   └── ...                 # Additional example files
│
└── README.md
```

## How to Run

1. **Compile the Java code:**
   ```sh
   javac JavaImplementation/*.java
   ```

2. **Run the main program:**
   ```sh
   java JavaImplementation.Main
   ```

3. **View the results:**
   - The console will show the routes for each vehicle.
   - A Swing window will open, displaying the routes graphically.

## Requirements

- Java 8 or higher
- Linux, Windows, or macOS

## Customization

- To use your own data, replace `Coord.txt` and `Dist.txt` in the `VRP_example` folder.
- Adjust algorithm parameters in `Main.java` as needed.

## License

This project is for educational and competition purposes.