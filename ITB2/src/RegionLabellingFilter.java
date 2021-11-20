import itb2.filter.AbstractFilter;
import itb2.image.Image;
import itb2.image.ImageFactory;

import java.util.*;

public class RegionLabellingFilter extends AbstractFilter {


    @Override
    public Image filter(Image input) {
        Image output = ImageFactory.bytePrecision().rgb(input.getSize());
        input = new Grayfilter().filter(input);
        int width = input.getWidth();
        int height = input.getHeight();
        int[][] labels = new int[width][height];
        HashMap<Integer, Integer> replacementLabels = new HashMap<>();
        int label = 0;

        for (int col = 0; col < width; col++) {
            for (int row = 0; row < height; row++) {

                double value = input.getValue(col, row, 0);
                if (value == 0) {
                    labels[col][row] = label;
                    int range = 1;
                    int minLabel = label;
                    ArrayList<Integer> equivalenceLabels = new ArrayList<>();
                    for (int offsetCol = -range; offsetCol < 1; offsetCol++) {
                        for (int offsetRow = -range; offsetRow < 1; offsetRow++) {
                            try {
                                if (labels[col + offsetCol][row + offsetRow] != -1) {
                                    minLabel = Math.min(labels[col + offsetCol][row + offsetRow], minLabel);
                                    if (labels[col + offsetCol][row + offsetRow] > minLabel && !equivalenceLabels.contains(labels[col + offsetCol][row + offsetRow]) && offsetCol + offsetRow != 0) {
                                        equivalenceLabels.add(labels[col + offsetCol][row + offsetRow]);
                                    }
                                }
                            } catch (Exception ignored) {
                            }
                        }
                    }
                    int finalMinLabel = minLabel;
                    if(replacementLabels.containsKey(minLabel)){
                        equivalenceLabels.forEach((v) -> replacementLabels.put(v, replacementLabels.get(finalMinLabel)));
                    }else {
                        equivalenceLabels.forEach((v) -> replacementLabels.put(v, finalMinLabel));
                    }
                    label = minLabel == label ? label + 1 : label;
                    labels[col][row] = minLabel;
                } else {
                    labels[col][row] = -1;
                }
            }
        }

        // replace equivalent labels while assigning colors
        for (int col = 0; col < width; col++) {
            for (int row = 0; row < height; row++) {
                if (labels[col][row] == -1) {
                    output.setValue(col, row, 255, 255, 255);
                    continue;
                }
                Random rd = new Random();
                rd.setSeed(labels[col][row]);
                if (replacementLabels.containsKey(labels[col][row])) {
                    rd.setSeed(replacementLabels.get(labels[col][row]));
                }
                double[] color = new double[3];
                for (int i = 0; i < color.length; i++) {
                    color[i] = rd.nextInt(255);
                }
                output.setValue(col, row, color);
            }
        }
        return output;
    }
}
