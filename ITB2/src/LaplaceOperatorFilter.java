import itb2.filter.AbstractFilter;
import itb2.image.Image;
import itb2.image.ImageFactory;

public class LaplaceOperatorFilter extends AbstractFilter {
    private static final String TYPE = "type";

    public LaplaceOperatorFilter() {
        this.properties.addOptionProperty(TYPE, "L4", "L4", "L8");
    }

    /**
     * displaces image according to displacement values
     * @param displacement int array containing 2 values, which correspond to vertical and horizontal displacement
     */
    protected double[][] applyDisplacement(double[][] input, int[] displacement, int width, int height) {
        double[][] results = new double[width][height];
        for (int col = 0; col < width; col++) {
            for (int row = 0; row < height; row++) {
                try {
                    results[col][row] =  input[col + displacement[0]][row + displacement[1]];
                } catch (Exception e) {
                    assert results[col] != null;
                    results[col][row] = 0;
                }
            }
        }
        return results;
    }

    /**
     * creates image values from zero pass data
     * not displaced image is compared to each displaced image
     * @param inputs contains displaced images. first image is without displacement
     */
    protected double[][] getZeroPasses(double[][][] inputs, int width, int height) {
        double[][] results = new double[width][height];
        double min = 0;
        double max = 0;
        for (int col = 0; col < width; col++) {
            for (int row = 0; row < height; row++) {
                boolean zeroPass = false;
                for (int i = 1; i < inputs.length; i++) {
                    if (Math.signum(inputs[0][col][row]) != Math.signum(inputs[i][col][row])) {
                        zeroPass = true;
                        break;
                    }
                }
                results[col][row] =  zeroPass ? inputs[0][col][row] : 0;
                if(col ==0 && row == 0)
                {
                    min = results[col][row];
                    max = min;
                }
                min = Math.min(min, results[col][row]);
                max = Math.max(max, results[col][row]);
            }
        }
        return new GraySpreadFilter_AK_CS().applySpread(results, min, max);
    }

    /**
     * returns image from zero pass data
     * for each displacement an image is created by applying the corresponding Laplace Filter
     *
     * @param output contains result
     */
    private void applyOperator(Image input, Image output, int width, int height, ConvolutionFilter_AK_CS filter, int[][] displacements) {
        double[][][] results = new double[displacements.length][width][height];
        results[0] = filter.applyConvolution(input);
        for (int i = 1; i < displacements.length; i++) {
            results[i] = applyDisplacement(results[0], displacements[i], width, height);
        }
        Utility_AK_CS.doubleArrayToImage(getZeroPasses(results, width, height), output, width, height);
    }

    @Override
    public Image filter(Image input) {
        Image output = ImageFactory.bytePrecision().gray(input.getSize());
        int width = input.getWidth();
        int height = input.getHeight();

        String type = properties.getOptionProperty(TYPE);
        ConvolutionFilter_AK_CS filter = new Laplace4Filter();

        //displace images and apply filter
        switch (type) {
            case "L4" -> {
                int[][] displacements = new int[][]{{0, 0}, {1, 0}, {0, 1}};
                applyOperator(input, output, width, height, filter, displacements);
            }
            case "L8" -> {
                filter = new Laplace8Filter();
                int[][] displacements = new int[][]{{0, 0}, {1, 0}, {0, 1}, {1, -1}, {1, 1}};
                applyOperator(input, output, width, height, filter, displacements);
            }
        }

        return output;
    }
}
