import itb2.filter.AbstractFilter;
import itb2.image.Image;
import itb2.image.ImageFactory;

public class CopyImageFilter_AK_CS extends AbstractFilter {
    @Override
    public Image filter(Image input) {
        Image output = ImageFactory.getPrecision(input).rgb(input.getSize());
        for (int col = 0; col < input.getWidth(); col++) {
            for (int row = 0; row < input.getHeight(); row++) {
                output.setValue(col, row, input.getValue(col, row));
            }
        }
        return output;
    }
}
