public class Meanx5Filter extends ConvolutionFilter_AK_CS {
    @Override
    double[][] getKernel() {
        double[][] arr = new double[5][5];
        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr.length; j++) {
                arr[i][j] = 1.0 / 25;
            }
        }
        return arr;
    }
}
