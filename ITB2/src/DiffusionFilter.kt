import itb2.filter.AbstractFilter
import itb2.image.GrayscaleImage
import itb2.image.Image
import itb2.image.ImageFactory
import kotlin.math.floor
import kotlin.math.pow
import kotlin.math.sqrt

 class DiffusionFilter : AbstractFilter() {

    /**
     * Returns double array of convolution results
     * Turns RGB Images into gray scale
     */
    fun aproximateDerivative(input: Array<DoubleArray>, width: Int, height: Int): Array<Array<DoubleArray>> {
        val result = Array(2) { Array(width) { DoubleArray(height) } }
        for (col in 0 until width) {
            for (row in 0 until height) {
                //default value for outside of the image
                val value = doubleArrayOf(0.0, 0.0)

                // only if indices are not beyond the edge calculation is valid
                if (!(col - 1 < 0 || col + 1 >= width)) {
                    value[0] = (input[col - 1][row] - input[col + 1][row])
                }
                if (!(row - 1 < 0 || row + 1 >= height)) {
                    value[1] = (input[col][row - 1] - input[col][row + 1])
                }
                result[0][col][row] = value[0]
                result[1][col][row] = value[1]
            }
        }
        return result
    }

    /**
     * Returns double array of convolution results
     * Turns RGB Images into gray scale
     */
    fun calculateDivergence(input: Array<Array<DoubleArray>>, width: Int, height: Int): Array<DoubleArray> {
        val result = Array(width) { DoubleArray(height) }
        for (col in 0 until width) {
            for (row in 0 until height) {
                //default value for outside of the image
                var value = 0.0

                // only if indices are not beyond the edge calculation is valid
                if (!(col - 1 < 0 || col + 1 >= width || row - 1 < 0 || row + 1 >= height)) {
                    value =
                        (input[col - 1][row] - input[col + 1][row])[0] + (input[col][row - 1] - input[col][row + 1])[1]
                }
                result[col][row] = value
            }
        }
        return result
    }


    /**
     * returns values of the absolute gradient
     */
    protected fun calculateFlow(
        horizontal: Array<DoubleArray>,
        vertical: Array<DoubleArray>,
        width: Int,
        height: Int,
        e0: Double,
        lambda: Double
    ): Array<Array<DoubleArray>> {
        val result = Array(width) { Array(height) { DoubleArray(2) } }

        //calulate absolute gradient and min max
        for (col in 0 until width) {
            for (row in 0 until height) {
                val tensorComponent = e0 * lambda.pow(2.0) / (lambda.pow(2.0) +
                    horizontal[col][row].pow(2.0) + vertical[col][row].pow(2.0)
                )
                result[col][row] =
                    doubleArrayOf(-tensorComponent * horizontal[col][row], -tensorComponent * vertical[col][row])
            }
        }
        return result
    }


    /**
     *
     * Converts image to gray scale
     */
    override fun filter(input: Image): Image {
        val grayImage = Grayfilter().filter(input)
        val output: Image = ImageFactory.bytePrecision().gray(input.size)
        val width = grayImage.width
        val height = grayImage.height
        val e0 = properties.getDoubleProperty(EPSILONZERO)
        val lambda = properties.getDoubleProperty(LAMBDA)
        val iterations = properties.getIntegerProperty(ITERATIONS)
        var values = Utility.imageToDoubleArray(input)
        for (i in 0 until iterations) {
            val result = Array(width) { DoubleArray(height) }
            val gradient = aproximateDerivative(values, width, height)
            val tensor = calculateFlow(gradient[0], gradient[1], width, height, e0, lambda)
            val divergence = calculateDivergence(tensor, width, height)
            for (col in 0 until width) {
                for (row in 0 until height) {
                    result[col][row] = values[col][row] - divergence[col][row]
                }
            }
            values = result
        }
        return Utility.doubleArrayToImage(values, output, width, height)
    }

    companion object {
        private const val EPSILONZERO = "epsilon zero"
        private const val LAMBDA = "lambda"
        private const val ITERATIONS = "iterations"
    }

    init {
        properties.addDoubleProperty(EPSILONZERO, 1.0)
        properties.addDoubleProperty(LAMBDA, 1.0)
        properties.addIntegerProperty(ITERATIONS, 1)
    }

    private operator fun DoubleArray.minus(substrate: DoubleArray): DoubleArray {
        assert(this.size == substrate.size)
        val result = DoubleArray(this.size)
        for ((i, v) in this.withIndex()) {
            result[i] = v - substrate[i]
        }
        return result
    }

    private operator fun DoubleArray.plus(substrate: DoubleArray): DoubleArray {
        assert(this.size == substrate.size)
        val result = DoubleArray(this.size)
        for ((i, v) in this.withIndex()) {
            result[i] = v + substrate[i]
        }
        return result
    }

}