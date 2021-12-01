// funktioniert
import itb2.image.GrayscaleImage;
import itb2.image.Image;
import itb2.image.ImageFactory;
import java.lang.Math;
import itb2.filter.AbstractFilter;

public class linGrauwertSpreizung extends AbstractFilter{
	
	public double[][] spreizung(double[][] input) {
		double[][] output=new double[input.length][input[0].length];
		double t = 0; //helper var
		
		// get I_minGiven and I_maxGiven
		double i_minGiven = input[0][0];
		double i_maxGiven = input[0][0];
		double wert = 0;
		for (int row=0; row<input.length;row++) {
			for (int col=0; col<input[0].length; col++) {
				wert = input[row][col];
				if(wert<i_minGiven) {
					i_minGiven = wert;
				}
				else if(wert>i_maxGiven) {
					i_maxGiven = wert;
				}
			}
		}
		
		// wende Spreizung an
		for (int row=0; row<input.length;row++) {
			for (int col=0; col<input[0].length; col++) {
				t = Math.round((input[row][col] - i_minGiven)*(255/(i_maxGiven-i_minGiven)));
				
				output[row][col]=t;
			}
		}
		
		return output;
	}
	
	public Image filter(Image input) {
		// müsste nicht unbedingt mit Umweg über Matrix gehen, aber Methode oben nützlich, falls Intwerte außerhalb des 
		//  darstellbaren Bereichs
		
		GrayscaleImage output = ImageFactory.bytePrecision().gray(input.getSize());
		double[][] image_matrix = new double[input.getHeight()][input.getWidth()];
		
		// kopiere inputbild in Matrix
		for (int row=0; row<input.getHeight();row++) {
			for (int col=0; col<input.getWidth(); col++) {
				image_matrix[row][col]=input.getValue(col, row, GrayscaleImage.GRAYSCALE);
			}
		}
		
		//wende Spreizung an
		image_matrix = spreizung(image_matrix);
		
		// kopiere Matrix in Outputbild
		for (int row=0; row<input.getHeight();row++) {
			for (int col=0; col<input.getWidth(); col++) {
				output.setValue(col, row, GrayscaleImage.GRAYSCALE, image_matrix[row][col]);
			}
		}
		
		return output;
	}
}
