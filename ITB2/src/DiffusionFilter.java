//funktioniert.

import itb2.filter.AbstractFilter;
import itb2.image.GrayscaleImage;
import itb2.image.Image;
import itb2.image.ImageFactory;

public class DiffusionFilter extends AbstractFilter{
	private static String EPS ="epsilon_0";
	private static String ITER = "number of iterations";
	private static String LAMBDA = "lambda";
	
	public DiffusionFilter() {
		this.properties.addDoubleProperty("epsilon_0", 1);
		this.properties.addIntegerProperty("number of iterations", 500);
		this.properties.addDoubleProperty("lambda", 1);
	}
	
	public Image filter(Image input) {
		double eps = properties.getDoubleProperty(EPS);
		int iter = properties.getIntegerProperty(ITER);
		double lambda = properties.getDoubleProperty(LAMBDA);
		double[][] image_t = Utility.imageToDoubleArray(input);
		double[][] image_t1 = new double[input.getHeight()][input.getWidth()];
		GrayscaleImage output = ImageFactory.bytePrecision().gray(input.getSize());
		
		for(int it =0; it<iter; it++) {
			double [][] dx = new double[input.getHeight()][input.getWidth()];
			double [][] dy = new double[input.getHeight()][input.getWidth()];
			double [][] flussx = new double[input.getHeight()][input.getWidth()];
			double [][] flussy = new double[input.getHeight()][input.getWidth()];
			double [][] dflussx = new double[input.getHeight()][input.getWidth()];
			double [][] dflussy = new double[input.getHeight()][input.getWidth()];
			double [][] divergenz = new double[input.getHeight()][input.getWidth()];
			
			
			//iteriere über Pixel, Randwerte ignoriert
			for (int row=1; row<input.getHeight()-1; row++) {
				for (int col=1; col<input.getWidth()-1; col++) {
					//Gradienten approximieren
					dx[row][col] = image_t[row+1][col]-image_t[row-1][col];
					dy[row][col] = image_t[row][col+1]-image_t[row][col-1];
					
					// Diffusionstensor berechnen
					double [][] diffTensor = {{eps*lambda*lambda/(eps*eps*(dx[row][col]*dx[row][col]+dy[row][col]*dy[row][col])+lambda*lambda), 0},
											  {0, eps*lambda*lambda/(eps*eps*dx[row][col]*dx[row][col]+eps*eps*dy[row][col]*dy[row][col]+lambda*lambda)}};
					// Berechen Fluss
					flussx[row][col] = -diffTensor[0][0]*dx[row][col];
					flussy[row][col] = -diffTensor[1][1]*dy[row][col];
					if(col==input.getWidth()-2) {
						col=col+1;
						col=col-1;
					}
				}
			}
			for (int row=1; row<input.getHeight()-1; row++) {
				for (int col=1; col<input.getWidth()-1; col++) {
					// Gradienten des Fluss schätzen
					dflussx[row][col]=flussx[row+1][col]-flussx[row-1][col];
					dflussy[row][col]=flussy[row][col+1]-flussy[row][col-1];
					
					// Berechne Divergenz pro Pixel
					divergenz[row][col]=dflussx[row][col]+dflussy[row][col];
					
					// berechen Ergebnisintensität
					image_t1[row][col] =image_t[row][col]-divergenz[row][col];
				}
			}
			// damit in nächster Iteration auf Werte der vorherigen Zugegriffen werden kann
			image_t=image_t1;
			
		}

		return Utility.doubleArrayToImage(image_t1, output, input.getWidth(), input.getHeight());
	}
}
