//funktioniert
import itb2.filter.AbstractFilter;
import itb2.image.GrayscaleImage;
import itb2.image.Image;
import itb2.image.ImageFactory;

public class MedianFilter extends AbstractFilter{
// für jedes Pixel in geg. Nachbarschaft Intwerte sortieren. Median ausgeben (mittlerer Wert)
	private static String DIM = "Filterdimension";
	
	public MedianFilter() {
		this.properties.addOptionProperty("Filterdimension", 3, 3,5,7);
	}
	
	double getMedian(Image input, int row, int col, int dim) {
		double[] subimage = getSubimage(row, col, dim, input);
		
		// sortiere subimage
		subimage = bubblesort(subimage);
		
		return subimage[(subimage.length+1)/2];
	}
	
	private static double[] bubblesort(double[] zusortieren) {
		double temp;
		for(int i=1; i<zusortieren.length; i++) {
			for(int j=0; j<zusortieren.length-i; j++) {
				if(zusortieren[j]>zusortieren[j+1]) {
					temp=zusortieren[j];
					zusortieren[j]=zusortieren[j+1];
					zusortieren[j+1]=temp;
				}
				
			}
		}
		return zusortieren;
	}
	
	private static double[] getSubimage(int row, int col, int dim, Image input){
		// schreibt Pixel aus geg. Nachbarschaft in array
		double[] subimage = new double[dim*dim];
		int zaehler=0;
		
		for(int i= -(dim-1)/2; i<(dim-1)/2; i++) {
			for(int j=-(dim-1)/2; j<(dim-1)/2;j++) {
				subimage[zaehler]=input.getValue(col+j, row+i, GrayscaleImage.GRAYSCALE);
				zaehler+=1;
			}
		}
		return subimage;
	}
	
	public Image filter(Image input) {
		GrayscaleImage output = ImageFactory.bytePrecision().gray(input.getSize());
		int dim = properties.getOptionProperty(DIM);
		
		//keine Randbehandlung
		for (int row=(dim-1)/2; row<input.getHeight()-(dim-1)/2; row++) {
			for (int col=(dim-1)/2; col<input.getWidth()-(dim-1)/2; col++) {
				output.setValue(col, row, GrayscaleImage.GRAYSCALE, getMedian(input, row, col, dim));
			}
		}
		
		return output;
		
	}
	
	
}
