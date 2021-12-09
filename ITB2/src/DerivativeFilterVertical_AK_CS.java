/**
 * detects vertical edges
 */
public class DerivativeFilterVertical_AK_CS extends ConvolutionFilter_AK_CS {

    @Override
    double[][] getKernel() {
        return new double[][]{{-1}, {0}, {1}};
    }
}
