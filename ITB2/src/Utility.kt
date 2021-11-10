import itb2.image.GrayscaleImage
import itb2.image.Image

object Utility {
    /**
     * Returns double array of values
     * Converts RGB Images to gray scale
     */
    internal fun imageToDoubleArray(input: Image): Array<DoubleArray> {
        val grayImage = Grayfilter().filter(input)
        val width = grayImage.width
        val height = grayImage.height
        val results = Array(width) { DoubleArray(height) }
        for (col in 0 until width) {
            for (row in 0 until height) {
                results[col][row] = grayImage.getValue(col, row, GrayscaleImage.GRAYSCALE)
            }
        }
        return results
    }

    /**
     * Returns gray scale image
     *
     * @param values gray scale values of an image
     */
    @JvmStatic
    fun doubleArrayToImage(values: Array<DoubleArray>, output: Image, width: Int, height: Int): Image {
        for (col in 0 until width) {
            for (row in 0 until height) {
                output.setValue(col, row, values[col][row])
            }
        }
        return output
    }

    /**
     * Returns binning visualization of angular values
     * Color Intervalls have these centers
     * blue = 0; red = 45; yellow = 90; green = 135;
     */
    @JvmStatic
    fun angleBinning(values: Array<DoubleArray>, output: Image, width: Int, height: Int, absoluteGradient: Array<DoubleArray> = Array(width){ DoubleArray(height){255.0} }) {

        for (col in 0 until width) {
            for (row in 0 until height) {
                var color = DoubleArray(3)
                val value = values[col][row]
                val intensity = absoluteGradient[col][row]
                if (value > 157.5 && value <= 180 || value in 0.0..22.5) {
                    color = doubleArrayOf(0.0, 0.0, intensity)
                }
                if (value > 22.5 && value <= 67.5) {
                    color = doubleArrayOf(intensity, 0.0, 0.0)
                }
                if (value > 67.5 && value <= 112.5) {
                    color = doubleArrayOf(intensity, intensity, 0.0)
                }
                if (value > 112.5 && value <= 157.5) {
                    color = doubleArrayOf(0.0, intensity, 0.0)
                }
                output.setValue(col, row, *color)
            }
        }
    }
}