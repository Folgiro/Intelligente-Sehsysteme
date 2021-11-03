import itb2.filter.AbstractFilter;
import itb2.image.GrayscaleImage;
import itb2.image.Image;
import itb2.image.ImageFactory;

public class GraySpreadFilter extends AbstractFilter {

    protected double[][] applySpread(double[][] values, double max, double min){
        int width = values.length;
        int height = values[0].length;
        double c1 = -min;
        double c2 = 255 / (max - min);
        for (int col = 0; col < width; col++) {
            for (int row = 0; row < height; row++) {
                values[col][row] = (values[col][row]+c1)*c2;
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

        for (int col = 0; col < width; col++) {
            for (int row = 0; row < height; row++) {
                values[col][row] = grayImage.getValue(col, row, GrayscaleImage.GRAYSCALE);
                min = Math.min(min, values[col][row]);
                max = Math.max(max, values[col][row]);
            }
        }

        values = applySpread(values, min, max);

        return Utility.doubleArrayToImage(values, output, width, height);
    }
}
