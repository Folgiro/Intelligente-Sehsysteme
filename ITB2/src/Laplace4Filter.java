public class Laplace4Filter extends ConvolutionFilter_AK_CS {
    @Override
    double[][] getKernel() {
        return new double[][]{{0, 1, 0}, {1, -4, 1}, {0, 1, 0}};
    }
}
