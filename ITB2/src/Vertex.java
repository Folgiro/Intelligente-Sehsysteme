/**
 * Vertex of a Graph
 */
public class Vertex {
    /**
     * contains indices of all adjacent Vertices.
     */
    private int[] adjacencyArray;
    private double cost;

    public Vertex(int[] adjacencyArray, double cost){
        this.adjacencyArray = adjacencyArray;
        this.cost = cost;
    }

    public int[] getAdjacencyArray() {
        return adjacencyArray;
    }

    public double getCost() {
        return cost;
    }
}
