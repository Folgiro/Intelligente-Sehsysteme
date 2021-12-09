import itb2.filter.AbstractFilter;
import itb2.image.GrayscaleImage;
import itb2.image.Image;
import itb2.image.ImageFactory;

public abstract class ConvolutionFilter_AK_CS extends AbstractFilter {

    /**
     * first dimension corresponds to column and second to row.
     */
    abstract double[][] getKernel();

    /**
     * Returns double array of convolution results
     * Turns RGB Images into gray scale
     */
    protected double[][] applyConvolution(Image input) {
        Image grayImage = new Grayfilter_AK_CS().filter(input);
        int width = grayImage.getWidth();
        int height = grayImage.getHeight();
        double[][] result = new double[width][height];

        for (int col = 0; col < width; col++) {
            for (int row = 0; row < height; row++) {

                //apply filter for every pixel
                double[][] kernel = getKernel();
                int rangeX = kernel.length / 2;
                int rangeY = kernel[0].length / 2;
                double sum = 0;
                for (int offsetCol = -rangeX; offsetCol < rangeX + 1; offsetCol++) {
                    for (int offsetRow = -rangeY; offsetRow < rangeY + 1; offsetRow++) {
                        //default value for outside of the image
                        double value = 0;

                        // only if indices are not beyond the edge getValue is valid
                        if (!(col - offsetCol < 0 || col - offsetCol >= width || row - offsetRow < 0 || row - offsetRow >= height)) {
                            value = grayImage.getValue(col - offsetCol, row - offsetRow, GrayscaleImage.GRAYSCALE);
                        }
                        sum += value * kernel[offsetCol + rangeX][offsetRow + rangeY];
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
        return Utility_AK_CS.doubleArrayToImage(values, output, width, height);
    }
}
