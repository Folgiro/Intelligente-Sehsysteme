package task1;

import itb2.filter.AbstractFilter;
import itb2.image.Image;
import itb2.image.ImageFactory;

import java.awt.*;

public class ChanelFilter extends AbstractFilter {
    private static final String SCALE = "scale";
    private static final String CHANEL = "chanel";

    public ChanelFilter(){
        this.properties.addDoubleProperty("scale", 1);
        this.properties.addOptionProperty("chanel", 0, 0, 1, 2);
    }

    @Override
    public Image filter(Image input) {

        Image output = ImageFactory.getPrecision(input).rgb(input.getSize());
        double scale = properties.getDoubleProperty(SCALE);
        int channel = properties.getOptionProperty(CHANEL);
        for (int col = 0; col < input.getWidth(); col++){
            for (int row = 0; row < input.getHeight(); row++){
                output.setValue(col, row, input.getValue(col, row));
                output.setValue(col, row, channel, input.getValue(col, row, channel) * scale);
            }
        }
        return output;
    }
}
