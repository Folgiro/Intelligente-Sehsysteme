import Utility.doubleArrayToImage
import itb2.filter.AbstractFilter
import itb2.image.ImageFactory
import itb2.image.GrayscaleImage
import itb2.image.Image

class GraySpreadFilter : AbstractFilter() {
    /**
     * returns image values which have been distributed over the whole intensity spectrum
     * variables c1 and c2 are determined dynamically to scale the intensities
     */
    fun applySpread(values: Array<DoubleArray>, min: Double, max: Double): Array<DoubleArray> {
        val width = values.size
        val height: Int = values[0].size
        val c1 = -min
        val c2 = 255 / (max - min)
        for (col in 0 until width) {
            for (row in 0 until height) {
                values[col][row] = Math.round((values[col][row] + c1) * c2).toDouble()
            }
        }
        return values
    }

    override fun filter(input: Image): Image {
        val output: Image = ImageFactory.bytePrecision().gray(input.size)
        val grayImage = Grayfilter().filter(input)
        val width = grayImage.width
        val height = grayImage.height
        var min = grayImage.getValue(0, 0, GrayscaleImage.GRAYSCALE)
        var max = min
        var values = Array(width) { DoubleArray(height) }

        //get min and max values
        for (col in 0 until width) {
            for (row in 0 until height) {
                values[col][row] = grayImage.getValue(col, row, GrayscaleImage.GRAYSCALE)
                min = Math.min(min, values[col][row])
                max = Math.max(max, values[col][row])
            }
        }
        values = applySpread(values, min, max)
        return doubleArrayToImage(values, output, width, height)
    }
}