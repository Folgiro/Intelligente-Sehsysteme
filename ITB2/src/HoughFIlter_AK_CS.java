import itb2.filter.AbstractFilter;
import itb2.image.GrayscaleImage;
import itb2.image.Image;
import itb2.image.ImageFactory;

import java.awt.*;

public class HoughFIlter_AK_CS extends AbstractFilter {


    @Override
    public Image filter(Image input) {
        int width = input.getWidth();
        int height = input.getHeight();
        int pMax = 64;
        int pMin = -pMax;
        int range = pMax*2+1;

        Image output = ImageFactory.bytePrecision().gray(new Dimension(180, range));

        double[][] houghSpace = new double[180][range];

        for (int col = 0; col < width; col++) {
            for (int row = 0; row < height; row++) {
                if(input.getValue(col, row, GrayscaleImage.GRAYSCALE) == 0){
                    for(int i = 0; i < 180; i++){
                        double p = col*Math.cos(Math.toRadians(i)) + row*Math.sin(Math.toRadians(i));
                        if(pMin <= p && p <= pMax){
                            houghSpace[i][(int)Math.round(p+pMax)]++;
                        }
                    }
                }
            }
        }
        return new GraySpreadFilter_AK_CS().filter(Utility_AK_CS.doubleArrayToImage(houghSpace, output, 180, range));
    }
}
