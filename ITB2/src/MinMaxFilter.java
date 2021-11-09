import itb2.filter.AbstractFilter;
import itb2.image.GrayscaleImage;
import itb2.image.Image;
import itb2.image.ImageFactory;

public class MinMaxFilter extends AbstractFilter{
// B = Kreuz aus nur 1, D_B Vierernachbarachscft um Bezugspixel
	private static String MODUS ="modus";
	
	public MinMaxFilter() {
		this.properties.addOptionProperty("modus", "Erosion", "Erosion", "Dilatation");
	}
	
	private double[] get4neighborhood(Image input, int row, int col) {
		return new double[] {input.getValue(col, row-1, GrayscaleImage.GRAYSCALE),
							 input.getValue(col-1, row, GrayscaleImage.GRAYSCALE),
							 input.getValue(col, row, GrayscaleImage.GRAYSCALE),
							 input.getValue(col+1, row, GrayscaleImage.GRAYSCALE),
							 input.getValue(col, row+1, GrayscaleImage.GRAYSCALE)};
	}
	
	private static double getMin(double[] array) {
		double min = array[0];
		for(int i = 0; i<array.length; i++) {
			min=Math.min(min, array[i]);
		}
		return min;
	}
	
	private static double getMax(double[] array) {
		double max = array[0];
		for(int i = 0; i<array.length; i++) {
			max=Math.max(max, array[i]);
		}
		return max;
	}
	
	public Image filter(Image input) {
		String modus = properties.getOptionProperty("modus");
		GrayscaleImage output = ImageFactory.bytePrecision().gray(input.getSize());
		
		int[] DBrow = {-1,0,0,0,1};
		int[] DBcol = {0,-1,0,1,0};
		
		// iteriere über alle Pixel, keine Randbehandlung
		for (int row=1; row<input.getHeight()-1; row++) {
			for (int col=1; col<input.getWidth()-1; col++) {
				double [] subimage = get4neighborhood(input, row, col);
				// +/-B(s,t) ist egal, da konstant 1
				if(modus=="Erosion") {
				output.setValue(col, row, GrayscaleImage.GRAYSCALE, getMin(subimage));
				}
				else {
					output.setValue(col, row, GrayscaleImage.GRAYSCALE, getMax(subimage));
				}
			}	
		}
		return output;
	}
	
}
