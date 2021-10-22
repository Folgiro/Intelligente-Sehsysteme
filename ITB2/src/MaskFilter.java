import itb2.filter.AbstractFilter;
import itb2.image.Image;
import itb2.image.ImageFactory;

public class MaskFilter extends AbstractFilter {
    @Override
    public Image[] filter(Image[] input) {
        Image output = ImageFactory.getPrecision(input[0]).rgb(input[0].getSize());
        for(int col = 0; col < input[0].getWidth(); col++) {
            for(int row = 0; row < input[0].getHeight(); row++) {

                // Kanäle aufsummieren
                for(int chan = 0; chan < input[0].getChannelCount(); chan++)
                    output.setValue(col, row, chan, (input[0].getValue(col, row, chan) * (input[1].getValue(col, row, chan)/255)));
            }
        }
        return new Image[]{output};
    }
}
