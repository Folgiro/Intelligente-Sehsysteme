import itb2.filter.AbstractFilter;
import itb2.image.GrayscaleImage;
import itb2.image.Image;
import itb2.image.ImageFactory;

import javax.swing.*;

public abstract class ConvolutionFilter extends AbstractFilter {

    //Has to be square
    abstract double[][] getKernel();

    @Override
    public Image filter(Image input) {
        Image grayImage = ImageFactory.bytePrecision().gray(input.getSize());
        Image output = ImageFactory.bytePrecision().gray(input.getSize());

        for (int col = 0; col < input.getWidth(); col++) {
            for (int row = 0; row < input.getHeight(); row++) {
                // convert rgb value to grey
                double sum = 0;
                for (int chan = 0; chan < input.getChannelCount(); chan++)
                    sum += input.getValue(col, row, chan);
                grayImage.setValue(col, row, sum / input.getChannelCount());
            }
        }


        int width = grayImage.getWidth();
        int heigth = grayImage.getHeight();
        for (int col = 0; col < width; col++){
            for (int row = 0; row < heigth; row++){

                double result = 0;
                double[][] kernel = getKernel();
                int range = kernel.length/2;
                double sum = 0;
                for (int offsetCol = -range; offsetCol < range +1; offsetCol++){
                    for (int offsetRow = -range; offsetRow < range +1; offsetRow++){
                        double value = 0;
                        if(!(col + offsetCol < 0 || col + offsetCol >= width || row + offsetRow < 0 || row + offsetRow >= heigth)){
                            value = grayImage.getValue(col + offsetCol, row + offsetRow)[0];
                        }
                        sum += value * kernel[offsetCol + range][offsetRow + range];
                    }
                }
                output.setValue(col, row, sum);
            }
        }
        return output;
    }
}
