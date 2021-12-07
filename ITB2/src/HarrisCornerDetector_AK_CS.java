import itb2.filter.AbstractFilter;
import itb2.image.GrayscaleImage;
import itb2.image.Image;
import itb2.image.ImageFactory;

import javax.swing.*;

public class HarrisCornerDetector_AK_CS extends AbstractFilter{
	private String KAPPA = "kappa";
	private String SIGMA = "sigma";
	private String THRESHOLD = "threshold";
	
	public HarrisCornerDetector_AK_CS() {
		this.properties.addDoubleProperty("kappa", 0.04);
		this.properties.addDoubleProperty("sigma", 1);
		this.properties.addDoubleProperty("threshold", 1);
	}
	
	private double get_response_value(double[][] harris_mat, double kappa) {
		// berechne Determinante
		double det = harris_mat[0][0]*harris_mat[1][1]-harris_mat[0][1]*harris_mat[1][0];
		// berechne Spur
		double trace = harris_mat[0][0]+harris_mat[1][1];
		// berechne return-value
		return det + kappa*trace*trace;
	}
	
	private double[][] get_harris_matrix(Image input, int row, int col, double sigma){
		// berechne I_x und I_y
		double[][] i_x = new double[3][3];
		double[][] i_y = new double[3][3];
		for (int i =-1; i<2;i++) {
			for(int j = -1; j<2; j++) {
				i_x[i+1][j+1]= 1*input.getValue(col+j-1, row+i, GrayscaleImage.GRAYSCALE)-1*input.getValue(col+j+1, row+i, GrayscaleImage.GRAYSCALE);
				i_y[i+1][j+1]= 1*input.getValue(col+j, row+i+1, GrayscaleImage.GRAYSCALE)-1*input.getValue(col+j, row+i-1, GrayscaleImage.GRAYSCALE);
			}
		}		
		
		// get weight matrix
		double[][] weights = get_weight_matrix(sigma);
		double[][] harris_mat = {{0,0},{0,0}};
		for(int i =0; i<3;i++) {
			for(int j=0; j<3;j++) {
				// berechne <I_x^2> (weighted sum (weights[i][j] of i_x[i][j]^2)
				harris_mat[0][0]+=weights[i][j]*i_x[i][j]*i_x[i][j];
				// berechne <I_xI_y>
				harris_mat[1][0]+=weights[i][j]*i_x[i][j]*i_y[i][j];
				// berechne <I_y^2>
				harris_mat[1][1]+=weights[i][j]*i_y[i][j]*i_y[i][j];
			}
		}
		harris_mat[0][1] = harris_mat[1][0];
		return harris_mat;
		
	}
	
	private double[][] get_weight_matrix(double sigma){
		double[][] mat= new double[3][3];
		for (int i =-1; i<2;i++) {
			for(int j = -1; j<2; j++) {
				mat[i+1][j+1]=Math.exp(-(i*i+j*j)/(2*sigma*sigma));
			}
		}
		return mat;
	}
	
	private Image color_corners(Image input, int row, int col) {
		// malt 8er Nachbarschaft um gegebenes Pixel rot
		for (int i =-1; i<2;i++) {
			for(int j = -1; j<2; j++) {
				input.setValue(col+j, row+i, 255,0,0);
			}
		}
		return input;
	}
	
	public Image filter(Image input) {
		double sigma = properties.getDoubleProperty(SIGMA);
		double kappa = properties.getDoubleProperty(KAPPA);
		double threshold = Math.abs(properties.getDoubleProperty(THRESHOLD));
		
		// copy input image in new image
		Image output = new CopyImage().filter(input);
		
		// für jedes Pixel 
		for(int row = 2; row < input.getHeight()-2; row++) {
			for(int col = 2; col < input.getWidth()-2;col++) {
			// berechne Harris Matrix
				double[][] harris_mat = get_harris_matrix(input, row, col, sigma);
			// berechne Response-Fkt
				double response = get_response_value(harris_mat, kappa);
			// Klassifiziere Response-Wert und mache Pixel + Nachbarschaft rot
				if (response > threshold) {
					// Ecke liegt vor -> setze Pixel + Umgebung rot
					output = color_corners(output, row, col);
				}
			}
		}
	
		return output;
	}
}
