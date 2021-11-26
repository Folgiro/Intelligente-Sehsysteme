import itb2.filter.AbstractFilter;
import itb2.image.GrayscaleImage;
import itb2.image.Image;
import itb2.image.ImageFactory;

public class SobelOperatorFilter extends AbstractFilter {

    private static final String TYPE = "type";

    public SobelOperatorFilter() {
        this.properties.addOptionProperty(TYPE, "Betrag", "Betrag", "Orientierung", "Gewichtete Orientierung");
    }

    public SobelOperatorFilter(String type) {
        this.properties.addOptionProperty(TYPE, type, "Betrag", "Orientierung", "Gewichtete Orientierung");
    }

    /**
     * returns values of the absolute gradient which have been distributed over the whole intensity spectrum
     */
    protected double[][] calculateAbsoluteGradient(double[][] horizontal, double[][] vertical, int width, int height) {
        double[][] result = new double[width][height];
        double min = Math.sqrt(Math.pow(horizontal[0][0], 2) + Math.pow(vertical[0][0], 2));
        double max = min;

        //calulate absolute gradient and min max
        for (int col = 0; col < width; col++) {
            for (int row = 0; row < height; row++) {
                result[col][row] = Math.sqrt(Math.pow(horizontal[col][row], 2) + Math.pow(vertical[col][row], 2));
                min = Math.min(min, result[col][row]);
                max = Math.max(max, result[col][row]);
            }
        }
        return new GraySpreadFilter().applySpread(result, min, max);
    }

    /**
     * returns angles of the gradient
     */
    protected double[][] calculateGradientAngle(double[][] horizontal, double[][] vertical, int width, int height) {
        double[][] result = new double[width][height];
        for (int col = 0; col < width; col++) {
            for (int row = 0; row < height; row++) {
                // save angle degrees into array, value of 10000 is a placeholder for no Gradient
                result[col][row] = horizontal[col][row] == 0 ? vertical[col][row] == 0 ? 0 : 90 : Math.toDegrees(Math.atan(Math.abs(vertical[col][row] / horizontal[col][row])));
            }
        }
        return result;
    }

    @Override
    public Image filter(Image input) {
        Image output = ImageFactory.bytePrecision().gray(input.getSize());
        int width = input.getWidth();
        int height = input.getHeight();

        double[][] result;
        double[][] horizontal = new SobelFilterHorizontal().applyConvolution(input);
        double[][] vertical = new SobelFilterVertical().applyConvolution(input);

        String type = properties.getOptionProperty(TYPE);

        switch (type) {
            case "Betrag" -> {
                result = calculateAbsoluteGradient(horizontal, vertical, width, height);
                Utility.doubleArrayToImage(result, output, width, height);
            }
            case "Orientierung" -> {
                output = ImageFactory.getPrecision(input).rgb(input.getSize());
                result = calculateGradientAngle(horizontal, vertical, width, height);
                Utility.angleBinning(result, output, width, height, null);
            }
            case "Gewichtete Orientierung" -> {
                double[][] absoluteGradient = calculateAbsoluteGradient(horizontal, vertical, width, height);
                output = ImageFactory.getPrecision(input).rgb(input.getSize());
                result = calculateGradientAngle(horizontal, vertical, width, height);
                Utility.angleBinning(result, output, width, height, absoluteGradient);
            }
        }

        return output;
    }
}
