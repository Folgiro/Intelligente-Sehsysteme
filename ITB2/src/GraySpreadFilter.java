import itb2.filter.AbstractFilter;
import itb2.image.GrayscaleImage;
import itb2.image.Image;
import itb2.image.ImageFactory;

public class GraySpreadFilter extends AbstractFilter {

    private static final String THRESHOLD = "threshold";

    public GraySpreadFilter() {
        this.properties.addIntegerProperty(THRESHOLD, 0);
    }

    /**
     * returns image values which have been distributed over the whole intensity spectrum
     * variables c1 and c2 are determined dynamically to scale the intensities
     */
    protected double[][] applySpread(double[][] values, double min, double max) {
        return applySpread(values, min, max, 0);
    }

    /**
     * returns image values which have been distributed over the whole intensity spectrum
     * variables c1 and c2 are determined dynamically to scale the intensities
     * @param threshold only values grater then threshold are changed
     */
    protected double[][] applySpread(double[][] values, double min, double max, int threshold) {
        int width = values.length;
        int height = values[0].length;
        double c1 = -min;
        double c2 = (255) / (max - min);
        for (int col = 0; col < width; col++) {
            for (int row = 0; row < height; row++) {
                if(threshold < values[col][row]){
                    values[col][row] = Math.round((values[col][row] + c1) * c2);
                }
            }
        }
        return values;
    }


    @Override
    public Image filter(Image input) {
        Image output = ImageFactory.bytePrecision().gray(input.getSize());
        Image grayImage = new Grayfilter().filter(input);
        int width = grayImage.getWidth();
        int height = grayImage.getHeight();
        double min = grayImage.getValue(0, 0, GrayscaleImage.GRAYSCALE);
        double max = min;
        double[][] values = new double[width][height];
        int threshold = this.properties.getIntegerProperty(THRESHOLD);

        //get min and max values
        for (int col = 0; col < width; col++) {
            for (int row = 0; row < height; row++) {
                values[col][row] = grayImage.getValue(col, row, GrayscaleImage.GRAYSCALE);
                if(threshold < values[col][row]){
                    min = Math.min(min, values[col][row]);
                    max = Math.max(max, values[col][row]);
                }
            }
        }

        values = applySpread(values, min, max, threshold);

        return Utility.doubleArrayToImage(values, output, width, height);
    }
}
