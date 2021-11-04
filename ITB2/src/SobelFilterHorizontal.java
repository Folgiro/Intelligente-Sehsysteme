public class SobelFilterHorizontal extends ConvolutionFilter {

    @Override
    double[][] getKernel() {
        return new double[][]{{-1, 0, 1}, {-2, 0, 2}, {-1, 0, 1}};
    }
}
