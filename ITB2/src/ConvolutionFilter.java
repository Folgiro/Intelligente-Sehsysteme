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
        double[][] result = new double[height][width];

        for (int col = 0; col < width; col++) {
            for (int row = 0; row < height; row++) {

                //apply filter for every pixel
                double[][] kernel = getKernel();
                int range = kernel.length / 2;
                double sum = 0;
                for (int offsetCol = -range; offsetCol < range + 1; offsetCol++) {
                    for (int offsetRow = -range; offsetRow < range + 1; offsetRow++) {
                        //default value for outside of the image
                        double value = 0;

                        // only if indices are not beyond the edge getValue is valid
                        if (!(col - offsetCol < 0 || col - offsetCol >= width || row - offsetRow < 0 || row - offsetRow >= height)) {
                            value = grayImage.getValue(col - offsetCol, row - offsetRow, GrayscaleImage.GRAYSCALE);
                        }
                        sum += value * kernel[offsetRow + range][offsetCol + range];
                    }
                }
                result[row][col] = sum;
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

/*
//  nicht fertig -> siehe github für Christians Lösung
import itb2.filter.AbstractFilter;
import itb2.image.Image;
import itb2.image.ImageFactory;
import itb2.image.GrayscaleImage;

abstract class ConvolutionFilter extends AbstractFilter{
	//in allen Unterklassen soll hier Kernel eingegeben werden
	// muss quadratisch sein und ungerade Länge haben
	abstract double[][] getKernel();
	
	public double convolution_sum(double[][] image, double[][] kernel) {
		double sum = 0;
		for(int row=0; row<kernel.length;row++) {
			for(int col=0; row<kernel.length;col++) {
				sum+=image[row][col]*kernel[row][col];
			}
		}
		return sum;
	}
	
	public double[][] getSubimage(int col, int row, int dim, Image image){
		double[][] subimage = new double[0][0];
		
		for(int i= -dim; i<dim; i++) {
			for(int j=-dim; j<dim;j++) {
				subimage[i+dim][j+dim]=image.getValue(col+j, row+i, 0);
			}
		}
		return subimage;
	}
	
	//filter-methode: wende Konvolutionsmaske auf Grauwertbild an
	public GrayscaleImage filter(Image input) {
		GrayscaleImage output = ImageFactory.bytePrecision().gray(input.getSize());
		double kernel[][] = getKernel();
		int width_kernel = (kernel.length-1)/2;
		double[][] subimage; 
		double sum = 0;
		// behandele Randpixel
		// obersten Reihen
		for (int row=0;row<width_kernel;row++) {
			for(int col=width_kernel; col< input.getWidth()-width_kernel;col++) {
				
			}
		}
		//behandele Mittelpixel
		for(int col=width_kernel; col< input.getWidth()-width_kernel;col++) {
			for(int row=width_kernel; row<input.getHeight()-width_kernel; row++) {
				subimage = getSubimage(row, col, width_kernel, input);
				output.setValue(col, row, convolution_sum( subimage, kernel) );
			}
		}
		return output;
	}
}
*/