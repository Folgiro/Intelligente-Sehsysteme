import itb2.filter.AbstractFilter;
import itb2.image.Image;
import itb2.image.ImageFactory;

import java.util.Arrays;

public class BinarySegmentationFilter extends AbstractFilter {

    private static final String[] THRESHOLDS = new String[]{"threshold 1", "threshold 2", "threshold 3", "threshold 4", "threshold 5", "threshold 6", "threshold 7", "threshold 8", "threshold 9"};

    public BinarySegmentationFilter() {
        for (String THRESHOLD : THRESHOLDS) {
            this.properties.addIntegerProperty(THRESHOLD, 0);
        }
    }


    @Override
    public Image filter(Image input) {
        Image output = ImageFactory.bytePrecision().rgb(input.getSize());
        input = new Grayfilter_AK_CS().filter(input);
        int width = input.getWidth();
        int height = input.getHeight();
        double[] thresholds = new double[9];
        for (int i = 0; i < thresholds.length; i++) {
            thresholds[i] = this.properties.getIntegerProperty(THRESHOLDS[i]);
        }
        Arrays.sort(thresholds);

        final double[][] colors = new double[][]{{0, 100, 0}, {0, 0, 139}, {176, 48, 96}, {255, 0, 0}, {255, 255, 0}, {222, 184, 135}, {0, 255, 0}, {0, 0, 0}, {255, 255, 255}};

        for (int col = 0; col < width; col++) {
            for (int row = 0; row < height; row++) {
                //initializes with default color. will only be used if there are 9 thresholds and values beyond the last threshold
                double[] color = new double[]{255, 255, 255};
                double value = input.getValue(col, row, 0);
                for (int i = 0; i < thresholds.length - 1; i++) {
                    if (value >= thresholds[i] && value < thresholds[i + 1]) {
                        color = colors[i];
                    }
                }
                output.setValue(col, row, color);
            }
        }
        return output;
    }
}
