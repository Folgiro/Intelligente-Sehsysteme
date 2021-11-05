import Utility.doubleArrayToImage
import itb2.filter.AbstractFilter
import itb2.image.GrayscaleImage
import itb2.image.Image
import itb2.image.ImageFactory

abstract class ConvolutionFilter : AbstractFilter() {
    //Has to be square
    abstract val kernel: Array<DoubleArray>

    /**
     * Returns double array of convolution results
     * Turns RGB Images into gray scale
     */
    fun applyConvolution(input: Image?): Array<DoubleArray> {
        val grayImage = Grayfilter().filter(input!!)
        val width = grayImage.width
        val height = grayImage.height
        val result = Array(width) { DoubleArray(height) }
        for (col in 0 until width) {
            for (row in 0 until height) {

                //apply filter for every pixel
                val kernel = kernel
                val range = kernel.size / 2
                var sum = 0.0
                for (offsetCol in -range until range + 1) {
                    for (offsetRow in -range until range + 1) {
                        //default value for outside of the image
                        var value = 0.0

                        // only if indices are not beyond the edge getValue is valid
                        if (!(col - offsetCol < 0 || col - offsetCol >= width || row - offsetRow < 0 || row - offsetRow >= height)) {
                            value = grayImage.getValue(col - offsetCol, row - offsetRow, GrayscaleImage.GRAYSCALE)
                        }
                        sum += value * kernel[offsetCol + range][offsetRow + range]
                    }
                }
                result[col][row] = sum
            }
        }
        return result
    }

    override fun filter(input: Image): Image {
        val output: Image = ImageFactory.bytePrecision().gray(input.size)
        val values = applyConvolution(input)
        val width = input.width
        val height = input.height
        return doubleArrayToImage(values, output, width, height)
    }
}