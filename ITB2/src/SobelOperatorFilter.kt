import itb2.filter.AbstractFilter
import itb2.image.Image
import itb2.image.ImageFactory

class SobelOperatorFilter : AbstractFilter() {
    /**
     * returns values of the absolute gradient which have been distributed over the whole intensity spectrum
     */
    protected fun calculateAbsoluteGradient(horizontal: Array<DoubleArray>, vertical: Array<DoubleArray>, width: Int, height: Int): Array<DoubleArray> {
        val result = Array(width) { DoubleArray(height) }
        var min = Math.sqrt(Math.pow(horizontal[0][0], 2.0) + Math.pow(vertical[0][0], 2.0))
        var max = min

        //calulate absolute gradient and min max
        for (col in 0 until width) {
            for (row in 0 until height) {
                result[col][row] = Math.sqrt(Math.pow(horizontal[col][row], 2.0) + Math.pow(vertical[col][row], 2.0))
                min = Math.min(min, result[col][row])
                max = Math.max(max, result[col][row])
            }
        }
        return GraySpreadFilter().applySpread(result, min, max)
    }

    /**
     * returns angles of the gradient
     */
    protected fun calculateGradientAngle(horizontal: Array<DoubleArray>, vertical: Array<DoubleArray>, width: Int, height: Int): Array<DoubleArray> {
        val result = Array(width) { DoubleArray(height) }
        for (col in 0 until width) {
            for (row in 0 until height) {
                // save angle degrees into array, value of 10000 is a placeholder for no Gradient
                result[col][row] = if (horizontal[col][row] == 0.0) if (vertical[col][row] == 0.0) 0.0 else 90.0 else Math.toDegrees(Math.atan(Math.abs(vertical[col][row] / horizontal[col][row])))
            }
        }
        return result
    }

    override fun filter(input: Image): Image {
        var output: Image = ImageFactory.bytePrecision().gray(input.size)
        val width = input.width
        val height = input.height
        val result: Array<DoubleArray>
        val horizontal = SobelFilterHorizontal().applyConvolution(input)
        val vertical = SobelFilterVertical().applyConvolution(input)
        val type = properties.getOptionProperty<String>(TYPE)

        when (type) {
            "Betrag" -> {
                result = calculateAbsoluteGradient(horizontal, vertical, width, height)
                Utility.doubleArrayToImage(result, output, width, height)
            }
            "Orientierung" -> {
                output = ImageFactory.getPrecision(input).rgb(input.getSize())
                result = calculateGradientAngle(horizontal, vertical, width, height)
                Utility.angleBinning(result, output, width, height)
            }
            "Gewichtete Orientierung" -> {
                val absoluteGradient = calculateAbsoluteGradient(horizontal, vertical, width, height)
                output = ImageFactory.getPrecision(input).rgb(input.getSize())
                result = calculateGradientAngle(horizontal, vertical, width, height)
                Utility.angleBinning(result, output, width, height, absoluteGradient)
            }
        }
        return output
    }

    companion object {
        private const val TYPE = "type"
    }

    init {
        properties.addOptionProperty(TYPE, "Betrag", "Betrag", "Orientierung", "Gewichtete Orientierung")
    }
}