/**
 * detects horizontal edges
 */
public class SobelFilterHorizontal_AK_CS extends ConvolutionFilter_AK_CS {

    @Override
    double[][] getKernel() {
        return new double[][]{{-1, -2, -1}, {0, 0, 0}, {1, 2, 1}};
    }
}
