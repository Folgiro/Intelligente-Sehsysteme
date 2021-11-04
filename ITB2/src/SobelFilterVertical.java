public class SobelFilterVertical extends ConvolutionFilter {

    @Override
    double[][] getKernel() {
        return new double[][]{{-1, -2, -1}, {0, 0, 0}, {1, 2, 1}};
    }
}
