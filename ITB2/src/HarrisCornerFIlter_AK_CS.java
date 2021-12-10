import itb2.filter.AbstractFilter;
import itb2.image.Image;

public class HarrisCornerFIlter_AK_CS extends AbstractFilter {

    private static final String KAPPA = "kappa";
    private static final String SIGMA = "sigma";
    private static final String THRESHOLD = "threshold";
    private static final String SIZE = "size";


    public HarrisCornerFIlter_AK_CS() {
        this.properties.addDoubleProperty(KAPPA, 1);
        this.properties.addDoubleProperty(SIGMA, 1);
        this.properties.addDoubleProperty(THRESHOLD, 1);
        this.properties.addIntegerProperty(SIZE, 5);
    }

    /**
     * @return size x size matrix containing 2d gauß distribution
     */
    private double[][] getWeightMatrix(double sigma, int size) {
        double[][] result = new double[size][size];
        int range = size / 2;
        for (int i = -range; i < range + 1; i++) {
            for (int j = -range; j < range + 1; j++) {
                result[i + range][j + range] = Math.exp(-(i * i + j * j) / (2 * sigma * sigma));
            }
        }
        return result;
    }

    /**
     * calculates weighted sum of derivatives to obtain harris matrix
     * @param derivativeY matrix of all derivatives in the image
     * @param derivativeX matrix of all derivatives in the image
     * @return 2x2 harris matrix
     */
    private double[][] calculateHarrisMatrix(double[][] weightMatrix, double[][] derivativeY, double[][] derivativeX, int col, int row, int size) {
        if(col == 69 && row == 36){
            int x = 1;
        }
        double[][] result = {{0, 0}, {0, 0}};
        int range = size / 2;
        for (int offsetCol = -range; offsetCol < range + 1; offsetCol++) {
            for (int offsetRow = -range; offsetRow < range + 1; offsetRow++) {
                try {
                    result[0][0] += weightMatrix[offsetCol + range][offsetRow + range] * derivativeX[col + offsetCol][row + offsetRow] * derivativeX[col + offsetCol][row + offsetRow];
                    result[1][0] += weightMatrix[offsetCol + range][offsetRow + range] * derivativeX[col + offsetCol][row + offsetRow] * derivativeY[col + offsetCol][row + offsetRow];
                    result[1][1] += weightMatrix[offsetCol + range][offsetRow + range] * derivativeY[col + offsetCol][row + offsetRow] * derivativeY[col + offsetCol][row + offsetRow];
                } catch (Exception ignored) {
                }
            }
        }
        result[0][1] = result[1][0];
        return result;
    }

    /**
     * calculates trace and determinant to calculate R
     * @param arr harris matrix
     */
    private double calculateR(double[][] arr, double kappa) {
        return (arr[0][0] * arr[1][1] - arr[1][0] * arr[0][1]) - kappa * Math.pow(arr[0][0] + arr[1][1], 2);
    }

    @Override
    public Image filter(Image input) {
        Image grayImage = new Grayfilter_AK_CS().filter(input);
        Image output = new CopyImageFilter_AK_CS().filter(input);
        int width = input.getWidth();
        int height = input.getHeight();

        double[][] derivativeY = new DerivativeFilterHorizontal_AK_CS().applyConvolution(input);
        double[][] derivativeX = new DerivativeFilterVertical_AK_CS().applyConvolution(input);

        double sigma = properties.getDoubleProperty(SIGMA);
        double kappa = properties.getDoubleProperty(KAPPA);
        double threshold = properties.getDoubleProperty(THRESHOLD);
        int size = properties.getIntegerProperty(SIZE);

        double[][] weightMatrix = getWeightMatrix(sigma, size);

        for (int col = 0; col < width; col++) {
            for (int row = 0; row < height; row++) {
                double[][] harrisMatrix = calculateHarrisMatrix(weightMatrix, derivativeY, derivativeX, col, row, size);
                if (calculateR(harrisMatrix, kappa) > threshold) {
                    int range = 1;
                    for (int offsetCol = -range; offsetCol < range + 1; offsetCol++) {
                        for (int offsetRow = -range; offsetRow < range + 1; offsetRow++) {
                            try {
                                output.setValue(col + offsetCol, row + offsetRow, 255, 0, 0);
                            } catch (Exception ignored) {
                            }
                        }
                    }
                }

            }
        }
        return output;
    }
}
