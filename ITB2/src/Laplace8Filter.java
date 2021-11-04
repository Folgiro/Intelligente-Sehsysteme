public class Laplace8Filter extends ConvolutionFilter {
    @Override
    double[][] getKernel() {
        return new double[][]{{1, 1, 1}, {1, -8, 1}, {1, 1, 1}};
    }
}
