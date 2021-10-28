package task2;

import itb2.filter.AbstractFilter;
import itb2.image.Image;
import itb2.image.ImageFactory;

import static java.lang.Math.round;

public class GammaFilter extends AbstractFilter {
    private static final String GAMMA = "gamma";

    public GammaFilter(){
        this.properties.addDoubleProperty("gamma", 1);
    }

    private double calcGammaCorrection(double x, double gamma){

        x = round(256 * Math.pow(x/255, gamma));
        return ((x == 256) ? 255 : x);
    }

    @Override
    public Image filter(Image input) {

        Image output = ImageFactory.bytePrecision().gray(input.getSize());
        double gamma = properties.getDoubleProperty(GAMMA);
        for (int col = 0; col < input.getWidth(); col++){
            for (int row = 0; row < input.getHeight(); row++){
                double sum = 0;
                for(int chan = 0; chan < input.getChannelCount(); chan++)
                    sum += input.getValue(col, row, chan);
                output.setValue(col, row, calcGammaCorrection(sum/input.getChannelCount(), gamma));
            }
        }
        return output;
    }
}
