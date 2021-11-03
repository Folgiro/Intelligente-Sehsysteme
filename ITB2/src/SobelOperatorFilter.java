import itb2.filter.AbstractFilter;
import itb2.image.GrayscaleImage;
import itb2.image.Image;
import itb2.image.ImageFactory;

public class SobelOperatorFilter extends AbstractFilter {

    @Override
    public Image filter(Image input) {
        Image output = ImageFactory.bytePrecision().gray(input.getSize());
        int width = input.getWidth();
        int height = input.getHeight();
        double[][] result = new double[width][height];
        double[][] horizontal = new SobelFilterHorizontal().applyConvolution(input);
        double[][] vertical = new SobelFilterVertical().applyConvolution(input);
        double min = Math.sqrt(Math.pow(horizontal[0][0], 2) + Math.pow(vertical[0][0], 2));
        double max = min;

        for (int col = 0; col < width; col++) {
            for (int row = 0; row < height; row++) {
                result[col][row] = Math.sqrt(Math.pow(horizontal[col][row], 2) + Math.pow(vertical[col][row], 2));
                min = Math.min(min, result[col][row]);
                max = Math.max(max, result[col][row]);
            }
        }
        result = new GraySpreadFilter().applySpread(result, min, max);

        return Utility.doubleArrayToImage(result, output, width, height);
    }
}
