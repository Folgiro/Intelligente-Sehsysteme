import itb2.filter.AbstractFilter;
import itb2.image.GrayscaleImage;
import itb2.image.Image;
import itb2.image.ImageFactory;

// Ergebnisbild: Gradientenbeträge mit linearer Grauwertspreizung

//TODO: - Skalierung bzgl. Gradientenbetrag
//		- Orientierung so richtig?

public class SobelOperator extends AbstractFilter {
	private static String OUT = "output image";
	
	public SobelOperator() {
		this.properties.addOptionProperty("output image", "Betraege", "Betraege", "Orientierung", "Betraege + Orientierung");
	}
	
	private double sobel(Image input, int col, int row, String richtung) {
		// Richtung gibt an, ob horizontaler ("h") oder vertikaler ("v") Sobelfilter 
		double[][] kernel = new double [3][3];
		if(richtung=="h") {
			kernel[0][0] = -1;
			kernel[0][1] =kernel[1][1] = kernel[2][1] = 0;
			kernel[0][2] = 1;
			kernel[1][0]= -2;
			kernel[1][2] =  2;
			kernel[2][0] = -1;
			kernel[2][2] = 1;
					
		}
		else if(richtung=="v") {
			kernel[0][0] = 1;
			kernel[1][0] =kernel[1][1] = kernel[1][2] = 0;
			kernel[0][2] = 1;
			kernel[0][1] = 2;
			kernel[2][0] = -1;
			kernel[2][1] = -2;
			kernel[2][2] = 1;
					
		}
		
		double wert = 0;
		for(int i = -1; i<2;i++) {
			for(int j = -1;j<2;j++) {
				wert += input.getValue(col+i, row+j, GrayscaleImage.GRAYSCALE)*kernel[1-i][1-j];
			}
		}
		return wert;
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

	public Image filter(Image input) {
		Image output = ImageFactory.getPrecision(input).rgb(input.getSize());
		Image s_x = ImageFactory.getPrecision(input).rgb(input.getSize());
		Image s_y = ImageFactory.getPrecision(input).rgb(input.getSize());
		
		String out = properties.getOptionProperty(OUT);
		
		double s = 0; //helper var
		double theta = 0;
		
		// wende horizontalen & vertikalen Sobel an
		for (int row=1; row<input.getHeight()-1;row++) {
			for (int col=1; col<input.getWidth()-1; col++) {
				s_x.setValue(col, row,  GrayscaleImage.GRAYSCALE, sobel(input, col, row,"h"));
				s_y.setValue(col, row,  GrayscaleImage.GRAYSCALE, sobel(input, col, row, "v"));
			}
		}
		if (out == "Betraege"){
			// berechne Gradientenbeträge
			for (int row=1; row<input.getHeight()-1;row++) {
				for (int col=1; col<input.getWidth()-1; col++) {
					s=Math.sqrt(Math.pow(s_x.getValue(col, row, GrayscaleImage.GRAYSCALE),2)+Math.pow(s_y.getValue(col, row, GrayscaleImage.GRAYSCALE), 2));
					output.setValue(col, row, GrayscaleImage.GRAYSCALE, s);
				}
			}
			
			// wende lineare Grauwertspreizung an
			output = spreizung(output);
		}
		else if (out=="Orientierung"){
			//berechne Gradientenorientierung
			for (int row=1; row<input.getHeight()-1;row++) {
				for (int col=1; col<input.getWidth()-1; col++) {
					if(s_x.getValue(col, row, GrayscaleImage.GRAYSCALE)==0 && s_y.getValue(col, row, GrayscaleImage.GRAYSCALE) != 0) {
						theta = 90;
					}
					else if (s_x.getValue(col, row, GrayscaleImage.GRAYSCALE)== 0 && s_y.getValue(col, row, GrayscaleImage.GRAYSCALE) == 0) {
						theta=0;
					}
					else {// in Grad
						theta = 180/Math.PI*Math.atan(s_y.getValue(col, row, GrayscaleImage.GRAYSCALE)/s_x.getValue(col, row, GrayscaleImage.GRAYSCALE));
					}
					
					// binning: ignoriert ob auf oder absteigend, absolute Orientierung aufgetragen
					if(Math.abs(theta)<22.5) {
						output.setValue(col, row, 0,0,255); //blau
					}
					else if(Math.abs(theta)<67.5) {
						output.setValue(col, row, 255,0,0); //rot
					}
					else if(Math.abs(theta)<112.5) {
						output.setValue(col, row, 255,255,0); //gelb
					}
					else if(Math.abs(theta)<157.5) {
						output.setValue(col, row, 0,255,0); //gruen
					}
				}
			}
		}
		else { //"Betraege + Orientierung" -> Helligkeit der Farbe der Orientierung <-> Betrag // TODO: Skalierung
			for (int row=1; row<input.getHeight()-1;row++) {
				for (int col=1; col<input.getWidth()-1; col++) {
					// berechne Betrag
					s=Math.sqrt(Math.pow(s_x.getValue(col, row, GrayscaleImage.GRAYSCALE),2)+Math.pow(s_y.getValue(col, row, GrayscaleImage.GRAYSCALE), 2));
					// berechne Orientierung
					if(s_x.getValue(col, row, GrayscaleImage.GRAYSCALE)==0 && s_y.getValue(col, row, GrayscaleImage.GRAYSCALE) != 0) {
						theta = 90;
					}
					else if (s_x.getValue(col, row, GrayscaleImage.GRAYSCALE)== 0 && s_y.getValue(col, row, GrayscaleImage.GRAYSCALE) == 0) {
						theta=0;
					}
					else {// in Grad
						theta = 180/Math.PI*Math.atan(s_y.getValue(col, row, GrayscaleImage.GRAYSCALE)/s_x.getValue(col, row, GrayscaleImage.GRAYSCALE));
					}
					
					// binning: ignoriert ob auf oder absteigend, absolute Orientierung aufgetragen
					if(Math.abs(theta)<22.5) {
						output.setValue(col, row, 0,0,255); //blau
					}
					else if(Math.abs(theta)<67.5) {
						output.setValue(col, row, 255,0,0); //rot
					}
					else if(Math.abs(theta)<112.5) {
						output.setValue(col, row, 255,255,0); //gelb
					}
					else if(Math.abs(theta)<157.5) {
						output.setValue(col, row, 0,255,0); //gruen
					}
				}
			}
		}
		return output;
		}
	}
