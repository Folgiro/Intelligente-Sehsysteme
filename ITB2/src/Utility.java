import itb2.image.GrayscaleImage;
import itb2.image.Image;
import itb2.image.ImageFactory;

public class Utility {

    /**
     * Returns double array of values
     * Converts RGB Images to gray scale
     */
    protected static double[][] imageToDoubleArray(Image input) {
        Image grayImage = ImageFactory.bytePrecision().gray(input.getSize());
        int width = grayImage.getWidth();
        int height = grayImage.getHeight();
        double[][] results = new double[height][width];

        for (int col = 0; col < width; col++) {
            for (int row = 0; row < height; row++) {
                results[row][col] = input.getValue(col, row, GrayscaleImage.GRAYSCALE);
            }
        }
        return results;
    }

    /**
     * Returns gray scale image
     *
     * @param values gray scale values of an image
     */
    protected static Image doubleArrayToImage(double[][] values, Image output, int width, int height) {
        for (int col = 0; col < width; col++) {
            for (int row = 0; row < height; row++) {
                output.setValue(col, row, GrayscaleImage.GRAYSCALE, values[row][col]);
            }
        }
        return output;
    }
    
    /**
     * Returns binning visualization of angular values
     * Color Intervalls have these centers
     * blue = 0; red = 45; yellow = 90; green = 135;
     */
    protected static Image angleBinning(double[][] angles, Image output, int width, int height, double[][] absoluteGradient) {
        // angles: Gradientenwinkel
    	// absolutGradient: Gradientenbetraege
    	if (absoluteGradient == null) {
            absoluteGradient = new double[width][height];
            for (int j = 0; j < width; j++) {
                for (int i = 0; i < height; i++) {
                    absoluteGradient[i][j] = 255;
                }
            }
        }
        for (int col = 0; col < width; col++) {
            for (int row = 0; row < height; row++) {
                double[] color = new double[3];
                double angle = angles[row][col];
                double intensity = absoluteGradient[row][col];
                if(angle == -1000) {
                	color = new double[]{0,0,0};
                }
                if ((angle > 157.5 && angle <= 180 ) || (angle >= 0 && angle <= 22.5)) {
                    color = new double[]{0, 0, intensity};
                }
                if (angle > 22.5 && angle <= 67.5) {
                    color = new double[]{intensity, 0, 0};
                }
                if (angle > 67.5 && angle <= 112.5) {
                    color = new double[]{intensity, intensity, 0};
                }
                if (angle > 112.5 && angle <= 157.5) {
                    color = new double[]{0, intensity, 0};
                }
                output.setValue(col, row, color);
            }
        }
        return output;
    }
  /*  
    protected static Image binBetraege(double[][] orientierung, double[][] betraege, Image output, int width, int height) {    	
    	for(int row=0; row < height; row ++) {
    		for(int col = 0; col< width; col ++) {
    			double[] color = new double[3];
    	        double angle = orientierung[row][col];
    	        double intensity = betraege[row][col];
    	        if (angle==0){
    	            color = new double[]{0, 0, intensity};
    	        }
    	        if (angle == 45) {
    	            color = new double[]{intensity, 0, 0};
    	        }
    	        if (angle == 90) {
    	            color = new double[]{intensity, intensity, 0};
    	        }
    	        if (angle == 135) {
    	            color = new double[]{0, intensity, 0};
    	        }
    	        output.setValue(col, row, color);
    		}
    	}
    	return output;
    }*/
}