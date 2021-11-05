import itb2.filter.AbstractFilter;
import itb2.image.GrayscaleImage;
import itb2.image.Image;
import itb2.image.ImageFactory;

public class LaplaceOperatorFilter extends AbstractFilter {
    private static final String TYPE = "type";

    public LaplaceOperatorFilter() {
        this.properties.addOptionProperty(TYPE, "L4", "L4", "L8");
    }

    /**
     * displaces image
     *
     * @param output contains result
     */
    protected void applyDisplacement(Image input, Image output, int[] displacement, int width, int height) {
        for (int col = 0; col < width; col++) {
            for (int row = 0; row < height; row++) {
                try {
                    output.setValue(col, row, input.getValue(col + displacement[0], row + displacement[1], GrayscaleImage.GRAYSCALE));
                } catch (Exception e) {
                    output.setValue(col, row, 0);
                }
            }
        }
    }

    /**
     * creates image from zero pass data
     * not displaced image is compared to each displaced image
     *
     * @param output contains result
     */
    protected void getZeroPasses(Image[] inputs, Image output, int width, int height) {
        for (int col = 0; col < width; col++) {
            for (int row = 0; row < height; row++) {
                boolean zeroPass = false;
                for (int i = 1; i < inputs.length; i++) {
                    if (Math.signum(inputs[0].getValue(col, row, GrayscaleImage.GRAYSCALE)) != Math.signum(inputs[i].getValue(col, row, GrayscaleImage.GRAYSCALE))) {
                        zeroPass = true;
                    }
                }
                output.setValue(col, row, zeroPass ? 255 : 0);
            }
        }
    }

    /**
     * returns image from zero pass data
     * for each displacement an image is created by applying the corresponding Laplace Filter
     *
     * @param output contains result
     */
    private void applyOperator(Image input, Image output, int width, int height, ConvolutionFilter filter, int[][] displacements) {
        Image[] results = new Image[displacements.length];
        input = filter.filter(input);
        for (int i = 0; i < displacements.length; i++) {
            results[i] = ImageFactory.bytePrecision().gray(input.getSize());
            applyDisplacement(input, results[i], displacements[i], width, height);
        }
        getZeroPasses(results, output, width, height);
    }

    @Override
    public Image filter(Image input) {
        Image output = ImageFactory.bytePrecision().gray(input.getSize());
        int width = input.getWidth();
        int height = input.getHeight();

        double[][] result;

        String type = properties.getOptionProperty(TYPE);
        ConvolutionFilter filter = new Laplace4Filter();

        //displace images and apply filter
        switch (type) {
            case "L4" -> {
                int[][] displacements = new int[][]{{0, 0}, {-1, 0}, {0, -1}};
                applyOperator(input, output, width, height, filter, displacements);
            }
            case "L8" -> {
                filter = new Laplace8Filter();
                int[][] displacements = new int[][]{{0, 0}, {-1, 0}, {0, -1}, {-1, -1}, {-1, 1}};
                applyOperator(input, output, width, height, filter, displacements);
            }
        }

        return output;
    }
}
