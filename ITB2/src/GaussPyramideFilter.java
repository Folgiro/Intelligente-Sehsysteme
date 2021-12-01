// reduce laeuft aber erzeugt schwarzen balken am Rand

import java.awt.image.BufferedImage;

import itb2.filter.AbstractFilter;
import itb2.image.GrayscaleImage;
import itb2.image.Image;
import itb2.image.ImageFactory;


public class GaussPyramideFilter extends AbstractFilter {
	private String SIGMA = "sigma";
	private String LAYER = "layer";
	
	public GaussPyramideFilter() {
		this.properties.addDoubleProperty("sigma", 2);
		this.properties.addIntegerProperty("layer", -1); // -1 -> letzte Ebene, -2 -> expand auf 1 Ebene
	}
	
	private double[][] get_binomial_kernel(String version){
		double norm_faktor=1;
		if(version == "r") {
			norm_faktor = (double) 1/(16*16);
		}
		else if(version=="e"){
			norm_faktor = (double) 1/(8*8);
		}
		double[][] kernel = {{1,	 4,	 6,	 4,	1},
							  {4,	16,	24,	16,	4},
							  {6,	24,	36,	24,	6},
							  {4,	16,	24,	16,	40},
							  {1,	 4,	 6,	 4,	1}};
		for(int i = 0; i<5; i++) {
			for(int j = 0; j<5; j++) {
				kernel[i][j] = kernel[i][j]*norm_faktor;
			}
		}

		return kernel;
	}
	
	private Image expand(Image input) {
		Image output = ImageFactory.bytePrecision().rgb(input.getWidth()*2, input.getHeight()*2);
		double[][] binkernel = get_binomial_kernel("e");
		
		// gehe Zeilenweise durch:
		int rowout = 0; // Zeilen des Outputbildes
		int colout = 0; // Spalten des Outputbildes
		for(int rowin = 2; rowin<input.getHeight()/2; rowin ++) {
			for (int colin = 2; colin < input.getWidth()/2; colin++) {
				double value = 0;
				for(int rowconv = -2; rowconv<3; rowconv ++) {
					for(int colconv = -2; colconv<3; colconv ++) {
						try {
							value = value + binkernel[2+rowconv][2+colconv]*input.getValue((colin-colconv)/2, (rowin-rowconv)/2,GrayscaleImage.GRAYSCALE);
						} catch(ArrayIndexOutOfBoundsException e) {
							; // do nothing
						}
					}
				}
				output.setValue(colout, rowout, GrayscaleImage.GRAYSCALE, value);
				colout = colout +1;
			}
			colout = 0;
			rowout = rowout +1;
		}
			
		return output;
	}

 	private Image reduce(Image input) {
		Image output = ImageFactory.bytePrecision().rgb(input.getWidth()/2, input.getHeight()/2);
		
		// Gaussfilter zur Reduzierung des Frequenzumfangs
		Image gauss = new GaussFilter().filter(input);
		double[][] binkernel = get_binomial_kernel("r");
		
		// gehe Zeilenweise durch:
		int rowout = 0; // Zeilen des Outputbildes
		int colout = 0; // Spalten des Outputbildes
		for(int rowin = 2; rowin<input.getHeight()/2; rowin ++) {
			for (int colin = 2; colin < input.getWidth()/2; colin++) {
				double value = 0;
				for(int rowconv = -2; rowconv<3; rowconv ++) {
					for(int colconv = -2; colconv<3; colconv ++) {
						try {
							value = value + binkernel[2+rowconv][2+colconv]*gauss.getValue(2*colin+colconv, 2*rowin+rowconv,GrayscaleImage.GRAYSCALE);
						} catch(ArrayIndexOutOfBoundsException e) {
							; // do nothing
						}
					}
				}
				if(colout == output.getWidth()) {
					int blo = 0;
					blo = blo+1;
				}
				output.setValue(colout, rowout, GrayscaleImage.GRAYSCALE, value);
				colout = colout +1;
			}
			colout = 0;
			rowout = rowout +1;
		}
		
		return new Grayfilter().filter(output);
	}
	
	public Image filter(Image input) {
		double sigma = this.properties.getDoubleProperty(SIGMA);
		int layer_out = this.properties.getIntegerProperty(LAYER);
		
		// wandele in Grauwertbild um
		Image input0 = new Grayfilter().filter(input);
		input0 = new GaussFilter().filter(input0);
		int anz_ebenen = (int) Math.floor(Math.log(input0.getHeight())/Math.log(2));
		Image [] output = new Image[anz_ebenen];
		output[0] = input0;
		for(int ebene = 1; ebene < anz_ebenen; ebene++) {
			output[ebene]=reduce(output[ebene-1]);
			}
	
		if(layer_out == -1) {
			return output[anz_ebenen-1];
		}
		else if(layer_out == -2) {
			return expand(output[1]);
		}
		else if(layer_out>=anz_ebenen) {
			System.out.println("Layer does not exist! Returning last layer.");
			return output[anz_ebenen-1];
		}
		else {
			return  output[layer_out];
		}
	}

}
