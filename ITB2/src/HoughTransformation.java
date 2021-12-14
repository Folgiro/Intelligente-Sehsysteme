
import itb2.filter.AbstractFilter;
import itb2.image.GrayscaleImage;
import itb2.image.Image;
import itb2.image.ImageFactory;

public class HoughTransformation extends AbstractFilter{
	
	private boolean is_contour(Image input, int row, int col) {
		// return true falls in eine der vier Richtungen Intensitaetswertunterschied
		double val_cur = input.getValue(col, row, GrayscaleImage.GRAYSCALE);
		double val_east = input.getValue(col+1, row, GrayscaleImage.GRAYSCALE);
		double val_west = input.getValue(col-1, row, GrayscaleImage.GRAYSCALE);
		double val_south = input.getValue(col, row-1, GrayscaleImage.GRAYSCALE);
		double val_north = input.getValue(col, row+1, GrayscaleImage.GRAYSCALE);
		if(val_cur>0 && (val_cur != val_east || val_cur!= val_west || val_cur != val_north || val_cur != val_south)) {
			return true;
		}
		return false;
	}
	
	public Image filter(Image input) {
		Image output = ImageFactory.bytePrecision().rgb(64*2+1, 181);
		int rho_max = 64;
		int rho_min = -rho_max;
		double rho = 0;
		
		// initialisieren hough Raum mit 0
		double[][] hough = new double[181][2*64+1];
		for(int row = 0; row < 181; row++) {
			for(int col = 0; col < 2*64+1;col++) {
				// initialisere entsprechenden Houghwert mit 0
				hough[row][col] = 0;
			}
		}
		
		// fuer alle Pixel
		for(int row = 1; row < input.getHeight()-1; row++) {
			for(int col = 1; col < input.getWidth()-1;col++) {
				// is contour pixel? Merkmal: Intunterschied
				if(is_contour(input, row, col)) {
					// gehe alle moeglichen theta werte durch
					for (int theta = 0; theta < 181; theta++) {
						rho = col * Math.cos(2*Math.PI/360*theta)+row*Math.sin(2*Math.PI/360*theta);
						if(rho< rho_max && rho > rho_min) {
							hough[theta][(int) rho + 64] += 1; // weil rho \in [-64,64]
						}
					}
				}
			}
		}
		
		hough = new linGrauwertSpreizung_AK_CS().spreizung(hough);
		
		output = Utility_AK_CS.doubleArrayToImage(hough,  output, 2*64+1, 181);
		output = new Grayfilter_AK_CS().filter(output);
		
		return output;
	}

}
