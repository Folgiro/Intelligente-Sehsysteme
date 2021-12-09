import itb2.engine.Controller;
import itb2.filter.AbstractFilter;
import itb2.image.DrawableImage;
import itb2.image.Image;
import itb2.image.ImageFactory;
import java.awt.Point;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ScissorsFilter_AK_CS extends AbstractFilter {

    /**
     * creates adjacency matrix which is stored in form of a List of Vertices.
     *
     * @param absValues contains absolute gradient values for each pixel
     * @param start     coordinates of start pixel
     * @param end       coordinates of end pixel
     * @return List of Vertices
     */
    protected ArrayList<Vertex_AK_CS> createGraph(double[][] absValues, int[] start, int[] end, int width, int height) {
        ArrayList<Vertex_AK_CS> result = new ArrayList<>(width * height);
        for (int col = 0; col < width; col++) {
            for (int row = 0; row < height; row++) {
                // Find indices of neighbors in the graph
                ArrayList<Integer> neighbors = new ArrayList<>(4);
                for (int offset = -1; offset < 2; offset += 2) {
                    if (!(col + offset < 0 || col + offset >= width)) {
                        neighbors.add(height * (col + offset) + row);
                    }
                    if (!(row + offset < 0 || row + offset >= height)) {
                        neighbors.add(height * col + (row + offset));
                    }
                }
                result.add(new Vertex_AK_CS(listToArray(neighbors), Math.abs(absValues[col][row] - (absValues[start[0]][start[1]] + absValues[end[0]][end[1]])/2)));
            }
        }
        return result;
    }


    protected ArrayList<Integer> executeDijkstra(ArrayList<Vertex_AK_CS> graph, final int start, final int end, int heigth) {
        // initialize path costs
        double max_cost = 0;
        for(Vertex_AK_CS v : graph){
            max_cost += v.getCost() + 1;
        }
        double[] pathCosts = new double[graph.size()];
        Arrays.fill(pathCosts, max_cost);
        pathCosts[start] = graph.get(start).getCost();

        //disjkstra
        ArrayList<Integer> activeNodes = new ArrayList<>(1);
        activeNodes.add(start);

        int min = -1;
        while (min != end) {
            min = selectDeleteMinCost(activeNodes, graph);
            Vertex_AK_CS vMin = graph.get(min);

            for (int v : vMin.getAdjacencyArray()) {
                if (pathCosts[min] + graph.get(v).getCost() < pathCosts[v]) {
                    pathCosts[v] = pathCosts[min] + graph.get(v).getCost();
                    activeNodes.add(v);
                }
            }

        }

        //back tracing
        ArrayList<Integer> visitedNodes = new ArrayList<>(1);
        ArrayList<Integer> nodeList = new ArrayList<>(1);
        nodeList.add(end);
        int k = end;
        while (k != start) {
            Vertex_AK_CS vK = graph.get(k);
            int prevNode;
            ArrayList<Integer> pathNodes = new ArrayList<>(1);
            for (int v : vK.getAdjacencyArray()) {
                if ((!isIn(visitedNodes, v)) && pathCosts[v] + graph.get(k).getCost() == pathCosts[k] ) {
                    prevNode = v;
                    visitedNodes.add(prevNode);
                    pathNodes.add(prevNode);
                }
            }

            //choose node with lowest euclidian distance to start
            double dMin = Double.POSITIVE_INFINITY;
            int vMin = 0;
            for(Integer v : pathNodes){
                final double distance = Math.sqrt(Math.pow(v / heigth - start / heigth, 2) + Math.pow(v % heigth - start % heigth, 2));
                if(distance < dMin){
                    dMin = distance;
                    vMin = v;
                }
            }

            nodeList.add(vMin);
            k = vMin;
        }
        nodeList.add(start);
        return nodeList;
    }

    private int selectDeleteMinCost(ArrayList<Integer> list, ArrayList<Vertex_AK_CS> graph) {
        double cMin = graph.get(list.get(0)).getCost();
        int min = list.get(0);
        int index = 0;
        for (int i = 0; i < list.size(); i++) {
            if (graph.get(list.get(i)).getCost() < cMin) {
                min = list.get(i);
                cMin = graph.get(list.get(i)).getCost();
                index = i;
            }
        }
        list.remove(index);
        return min;
    }


    private boolean isIn(ArrayList<Integer> list, int searched) {
        for (Integer integer : list) {
            if (integer == searched) {
                return true;
            }
        }
        return false;
    }


    private int[] listToArray(ArrayList<Integer> list) {
        int[] result = new int[list.size()];
        for (int i = 0; i < list.size(); i++) {
            result[i] = list.get(i);
        }
        return result;
    }


    @Override
    public Image filter(Image input) {
        Image grayImage = new Grayfilter_AK_CS().filter(input);
        Image output = new CopyImageFilter_AK_CS().filter(input);
        int width = input.getWidth();
        int height = input.getHeight();

        // Get input for start an end point
        DrawableImage drawImage = ImageFactory.bytePrecision()
                .drawable(grayImage.asBufferedImage());

        Graphics graphics = drawImage.getGraphics();
        graphics.setColor(Color.GREEN);

        // While the user selects points, continue drawing the line
        List<Point> selections;
        List<Point> points = new ArrayList<>();
        Point previous = null;
        do {
            // Ask user for selection
            selections = Controller.getCommunicationManager().getSelections("Please select point or close window.", 1, drawImage);
            if(selections.size() > 0){
                points.add(selections.get(0));
            }

            for(Point current : selections) {
                if(previous == null)
                    graphics.fillOval(current.x - 3, current.y - 3, 7, 7);
                else
                    graphics.drawLine(previous.x, previous.y, current.x, current.y);

                previous = current;
            }

        } while(selections.size() > 0);


        double[][] horizontal = new SobelFilterHorizontal_AK_CS().applyConvolution(input);
        double[][] vertical = new SobelFilterVertical_AK_CS().applyConvolution(input);
        double[][] absValues = new SobelOperatorFilter_AK_CS().calculateAbsoluteGradient(horizontal, vertical, width, height);

        for(int i = 0; i < points.size(); i++) {
            System.out.println("Durchgang " + i + " von " + points.size());
            // start and end arrays first value is column, second is row
            int[] start;
            int[] end;
            if(i == points.size()-1){
                start = new int[]{(int) points.get(i).getX(), (int) points.get(i).getY()};
                end = new int[]{(int) points.get(0).getX(), (int) points.get(0).getY()};
            }else {
                start = new int[]{(int) points.get(i).getX(), (int) points.get(i).getY()};
                end = new int[]{(int) points.get(i + 1).getX(), (int) points.get(i + 1).getY()};
            }
            ArrayList<Vertex_AK_CS> graph = createGraph(absValues, start, end, width, height);
            ArrayList<Integer> path = executeDijkstra(graph, start[0] * height + start[1], end[0] * height + end[1], height);

            //convert adjacency Matrix indices to image indices
            int[][] imagePath = new int[path.size()][2];
            for (int j = 0; j < path.size(); j++) {
                imagePath[j][0] = path.get(j) / height;
                imagePath[j][1] = path.get(j) % height;
                //set pixel of path to green
                output.setValue(imagePath[j][0], imagePath[j][1], 0, 255, 0);
            }
            output.setValue(imagePath[0][0], imagePath[0][1], 255, 0, 0);
            output.setValue(imagePath[imagePath.length - 1][0], imagePath[imagePath.length - 1][1], 255, 0, 0);
        }
        return output;
    }
}
