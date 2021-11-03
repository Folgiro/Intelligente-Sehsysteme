import itb2.image.GrayscaleImage;
import itb2.image.Image;
import itb2.image.ImageFactory;

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
}
