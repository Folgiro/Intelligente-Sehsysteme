public class Laplace8Filter extends ConvolutionFilter_AK_CS {
    @Override
    double[][] getKernel() {
        return new double[][]{{1, 1, 1}, {1, -8, 1}, {1, 1, 1}};
    }
}
