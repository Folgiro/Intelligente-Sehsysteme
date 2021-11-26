import itb2.filter.AbstractFilter;
import itb2.image.Image;
import itb2.image.ImageFactory;

import java.util.ArrayList;

import java.util.ArrayList;
import java.util.Arrays;

public class ScissorsFilter extends AbstractFilter {

    /**
     * creates adjacency matrix which is stored in form of a List of Vertices.
     *
     * @param absValues contains absolute gradient values for each pixel
     * @param start     coordinates of start pixel
     * @param end       coordinates of end pixel
     * @return List of Vertices
     */
    protected ArrayList<Vertex> createGraph(double[][] absValues, int[] start, int[] end, int width, int height) {
        ArrayList<Vertex> result = new ArrayList<>(width * height);
        for (int col = 0; col < width; col++) {
            for (int row = 0; row < height; row++) {
                // Find indices of neighbors in the graph
                ArrayList<Integer> neighbors = new ArrayList<>(4);
                for (int offset = -1; offset < 2; offset += 2) {
                    if (!(col + offset < 0 || col + offset >= width)) {
                        neighbors.add(width * (col + offset) + row);
                    }
                    if (!(row + offset < 0 || row + offset >= width)) {
                        neighbors.add(width * col + (row + offset));
                    }
                }
                result.add(new Vertex(listToArray(neighbors), absValues[col][row] - (absValues[start[0]][start[1]] + absValues[end[0]][end[1]])));
            }
        }
        return result;
    }


    protected ArrayList<Integer> executeDijkstra(ArrayList<Vertex> graph, int start, int end) {
        double max_cost = 0;
        for(Vertex v : graph){
            max_cost += v.getCost() + 1;
        }
        double[] pathCosts = new double[graph.size()];
        Arrays.fill(pathCosts, max_cost);
        pathCosts[start] = graph.get(start).getCost();

        ArrayList<Integer> activeNodes = new ArrayList<>(1);
        activeNodes.add(start);

        int min = -1;
        while(min != end){
            min = selectDeleteMin(activeNodes);
            Vertex vMin = graph.get(min);

            for(int v : vMin.getAdjacencyArray()){
                if(pathCosts[min] + graph.get(v).getCost() < pathCosts[v]){
                    pathCosts[v] = pathCosts[min] + graph.get(v).getCost();
                    activeNodes.add(v);
                }
            }

        }

        //back tracing
        ArrayList<Integer> nodeList = new ArrayList<>(1);
        activeNodes.add(end);
        int k = end;
        while(k != start){
            Vertex vK = graph.get(k);
            int prevNode = 0;
            for(int v : vK.getAdjacencyArray()){
                if(pathCosts[v] + graph.get(k).getCost() == pathCosts[k]){
                    pathCosts[v] = pathCosts[min] + graph.get(v).getCost();
                    prevNode = v;
                }
            }
            nodeList.add(prevNode);
            k = prevNode;
        }
        return nodeList;
    }

    private int selectDeleteMin(ArrayList<Integer> list){
        int min = list.get(0);
        int index = 0;
        for(int i = 0; i < list.size(); i++){
            if (list.get(i) < min){
                min = list.get(i);
                index = i;
            }
        }
        list.remove(index);
        return min;
    }


    private int[] listToArray(ArrayList<Integer> list){
        int[] result = new int[list.size()];
        for (int i = 0; i < list.size(); i++) {
            result[i] = list.get(i);
        }
        return result;
    }

    @Override
    public Image filter(Image input) {
        Image output = new Grayfilter().filter(input);
        int width = input.getWidth();
        int height = input.getHeight();
        double[][] horizontal = new SobelFilterHorizontal().applyConvolution(input);
        double[][] vertical = new SobelFilterVertical().applyConvolution(input);
        double[][] absValues = new SobelOperatorFilter().calculateAbsoluteGradient(horizontal, vertical, width, height);

        // start and end arrays first value is column, second is row
        int[] start = new int[]{0, 0};
        int[] end = new int[]{10, 10};
        ArrayList<Vertex> graph = createGraph(absValues, start, end, width, height);
        ArrayList<Integer> path = executeDijkstra(graph, start[0]*width + start[1], end[0]*width + end[1]);

        //convert adjacency Matrix indices to image indices
        int[][] imagePath = new int[path.size()][2];
        for(int i = 0; i < path.size(); i++){
            imagePath[i][0] = path.get(i)/width;
            imagePath[i][1] = path.get(i)%width;
            //set pixel of path to black
            output.setValue(imagePath[i][0], imagePath[i][1], 0);
        }
        return output;
    }
}
