import itb2.image.GrayscaleImage;
import itb2.image.Image;

public class Utility {

    /**
     * Returns double array of values
     * Converts RGB Images to gray scale
     */
    protected static double[][] imageToDoubleArray(Image input){
        Image grayImage = new Grayfilter().filter(input);
        int width = grayImage.getWidth();
        int height = grayImage.getHeight();
        double[][] results = new double[width][height];

        for (int col = 0; col < width; col++) {
            for (int row = 0; row < height; row++) {
                results[col][row] = grayImage.getValue(col, row, GrayscaleImage.GRAYSCALE);
            }
        }
        return results;
    }

    /**
     * Returns gray scale image
     */
    protected static Image doubleArrayToImage(double[][] values, Image output, int width, int height){
        for (int col = 0; col < width; col++) {
            for (int row = 0; row < height; row++) {
                output.setValue(col, row, values[col][row]);
            }
        }
        return output;
    }

    /**
     * Returns binning visualization of angular values
     * Color Intervalls have these centers
     * blue = 0; red = 45; yellow = 90; green = 135;
     */
    protected static void angleBinning(double[][] values, Image output, int width, int height, double[][] absoluteGradient){
        if (absoluteGradient == null)
        {
            absoluteGradient = new double[width][height];
            for (int i = 0; i < width; i++){
                for (int j = 0; j < height; j++){
                    absoluteGradient[i][j] = 255;
                }
            }
        }
        for (int col = 0; col < width; col++) {
            for (int row = 0; row < height; row++) {
                double [] color = new double[3];
                    double value = values[col][row];
                    double intensity = absoluteGradient[col][row];
                    if (value > 157.5 && value <= 180 || value >= 0 && value <= 22.5) {
                        color = new double[]{0, 0, intensity};
                    }
                    if (value > 22.5 && value <= 67.5) {
                        color = new double[]{intensity, 0, 0};
                    }
                    if (value > 67.5 && value <= 112.5) {
                        color = new double[]{intensity, intensity, 0};
                    }
                    if (value > 112.5 && value <= 157.5) {
                        color = new double[]{0, intensity, 0};
                    }
                output.setValue(col, row, color);
            }
        }
    }
}
