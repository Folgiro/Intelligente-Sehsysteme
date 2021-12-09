/**
 * detects horizontal edges
 */
public class SobelFilterHorizontal_AK_CS extends ConvolutionFilter_AK_CS {

    @Override
    double[][] getKernel() {
        return new double[][]{{-1, 0, 1}, {-2, 0, 2}, {-1, 0, 1}};
    }
}
