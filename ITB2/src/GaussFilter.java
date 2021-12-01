// funktioniert

import itb2.filter.AbstractFilter;
import itb2.image.Image;
import itb2.image.ImageFactory;
import itb2.image.GrayscaleImage;


public class GaussFilter extends ConvolutionFilter {
	// Rauschunterdrückung
	protected static String DIM = "dim";
	protected static String SIGMA = "sigma";
	private int dim = 3;
	private double sigma = 1;
	private Boolean fct_call = false;
	
	public GaussFilter() {
		this.properties.addOptionProperty("dim",  3,  3,5,7);
		this.properties.addDoubleProperty("sigma", 1);
	}
	
	public GaussFilter(int setdim, double setsigma) {
		this.dim = setdim;
		this.sigma= setsigma;
		this.fct_call=true;
	}

	
	public double[][] getKernel(){
		if (! fct_call) {
			this.dim = properties.getOptionProperty(DIM);
			this.sigma = properties.getDoubleProperty(SIGMA);
		}
		double[][] kernel= new double[dim][dim];
		
		for (int i = 0; i<dim;i++) {
			for(int j = 0; j<dim;j++) {
				kernel[i][j]=1/(2*Math.PI*sigma*sigma)*Math.exp(-(i*i+j*j)/(2*sigma*sigma));
			}
		}
		return kernel;
	}

}
