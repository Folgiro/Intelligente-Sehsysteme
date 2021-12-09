/**
 * Vertex of a Graph
 */
public class Vertex_AK_CS {
    /**
     * contains indices of all adjacent Vertices.
     */
    private int[] adjacencyArray;
    private double cost;

    public Vertex_AK_CS(int[] adjacencyArray, double cost){
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
