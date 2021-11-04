public class Meanx3Filter extends ConvolutionFilter {
    @Override
    double[][] getKernel() {
        double[][] arr = new double[3][3];
        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr.length; j++) {
                arr[i][j] = 1.0 / 9;
            }
        }
        return arr;
    }
}
