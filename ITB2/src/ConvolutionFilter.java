import itb2.filter.AbstractFilter;
import itb2.image.GrayscaleImage;
import itb2.image.Image;
import itb2.image.ImageFactory;

import javax.swing.*;

public abstract class ConvolutionFilter extends AbstractFilter {

    //Has to be square
    abstract double[][] getKernel();

    /**
     * Returns double array of convolution results
     * Turns RGB Images into gray scale
     */
    protected double[][] applyConvolution(Image input) {
        Image grayImage = new Grayfilter().filter(input);
        int width = grayImage.getWidth();
        int height = grayImage.getHeight();
        double[][] result = new double[width][height];

        for (int col = 0; col < width; col++) {
            for (int row = 0; row < height; row++) {

                double[][] kernel = getKernel();
                int range = kernel.length / 2;
                double sum = 0;
                for (int offsetCol = -range; offsetCol < range + 1; offsetCol++) {
                    for (int offsetRow = -range; offsetRow < range + 1; offsetRow++) {
                        double value = 0;
                        if (!(col - offsetCol < 0 || col - offsetCol >= width || row - offsetRow < 0 || row - offsetRow >= height)) {
                            value = grayImage.getValue(col - offsetCol, row - offsetRow, GrayscaleImage.GRAYSCALE);
                        }
                        sum += value * kernel[offsetCol + range][offsetRow + range];
                    }
                }
                result[col][row] = sum;
            }
        }
        return result;
    }

    @Override
    public Image filter(Image input) {
        Image output = ImageFactory.bytePrecision().gray(input.getSize());
        double[][] values = applyConvolution(input);

        int width = input.getWidth();
        int height = input.getHeight();
        return Utility.doubleArrayToImage(values, output, width, height);
    }
}
