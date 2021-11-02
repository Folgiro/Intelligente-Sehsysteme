import itb2.filter.AbstractFilter;
import itb2.image.Image;
import itb2.image.ImageFactory;
import itb2.image.GrayscaleImage;

public class LaplaceOperator extends AbstractFilter{
	private static String FILTER = "Filterart";
	
	private double laplace(Image input, int col, int row, String filter) {
		double[][] kernel = new double[3][3];
		double wert = 0;
		
		if(filter=="l4") {
			//fill l4:
			kernel[0][0] = kernel[0][2] = kernel[2][0] = kernel[2][2] = 0;
			kernel[1][0] = kernel[0][1] = kernel[1][2] = kernel[2][1] = 1;
			kernel[1][1] = -4;
		}
		else {
			for(int i = 0; i<3;i++) {
				for(int j = 0; j<3; j++) {
					kernel[i][j] = 1;
				}
			}
			kernel[1][1]=-8;
		}
		
		for(int i = -1; i<2;i++) {
			for(int j = -1;j<2;j++) {
				wert += input.getValue(col+i, row+j, GrayscaleImage.GRAYSCALE)*kernel[1-i][1-j];
			}
		}
		return wert;	
	}
	
	private double sign(Image input, int col, int row) {
		// get sign of input value
		return Math.signum(input.getValue(col, row, GrayscaleImage.GRAYSCALE));
	}
	
	private Image spreizung(Image input) {
		GrayscaleImage output = ImageFactory.bytePrecision().gray(input.getSize());
		double t = 0; //helper var
		
		// get I_minGiven and I_maxGiven
		double i_minGiven = 255;
		double i_maxGiven = 0;
		double wert =0;
		for (int row=0; row<input.getHeight();row++) {
			for (int col=0; col<input.getWidth(); col++) {
				wert = input.getValue(col, row, GrayscaleImage.GRAYSCALE);
				if(wert<i_minGiven) {
					i_minGiven = wert;
				}
				else if(wert>i_maxGiven) {
					i_maxGiven = wert;
				}
			}
		}
		
		// wende Spreizung an
		for (int row=0; row<input.getHeight();row++) {
			for (int col=0; col<input.getWidth(); col++) {
				t = Math.round((input.getValue(col, row, GrayscaleImage.GRAYSCALE) - i_minGiven)*(255/(i_maxGiven-i_minGiven)));
				output.setValue(col, row, GrayscaleImage.GRAYSCALE, t);
			}
		}
		
		return output;
	}

	
	public LaplaceOperator() {
		this.properties.addOptionProperty("Filterart", "l4", "l4","l8"); 
	}
	
	public Image filter(Image input) {
		Image output = ImageFactory.bytePrecision().gray(input.getSize());
		String filter = properties.getOptionProperty(FILTER);
		
		//wende Operator an, Rand nicht betrachtet
		for (int row=1; row<input.getHeight()-1;row++) {
			for (int col=1; col<input.getWidth()-1; col++) {
				output.setValue(col, row,  GrayscaleImage.GRAYSCALE, laplace(input, col, row, filter));
			}
		}
		
		//identifiziere Nulldurchgaenge (Nulldurchgang <=> unterschiedliche VZ in Pixel und bestimmten Nachbarpixeln)
		for (int row=1; row<input.getHeight()-1;row++) {
			for (int col=1; col<input.getWidth()-1; col++) {
				// falls PIxel rechts und unterhalb gleiches VZ haben wie Pixel
				if((sign(output, col, row)==sign(output, col+1, row)) && (sign(output, col, row)==sign(output, col, row+1))){
					if(filter=="l4") {
						// kein Nulldurchgang -> Pixel = schwarz
						output.setValue(col, row, GrayscaleImage.GRAYSCALE, 0);
					}
					else {
						// falls l8 filter ausgewählt: checke auch diagonalen Pixel
						if((sign(output, col, row)==sign(output, col+1, row-1)) && (sign(output, col, row)==sign(output, col+1, row+1))) {
							// kein Nulldurchgang  -> Pixel = schwarz
							output.setValue(col, row, GrayscaleImage.GRAYSCALE, 0);
						}
					}
				}
			}
		}		
		
		// lineare Grauwertspreizung
		output = spreizung(output);
		return output;
	}
}
