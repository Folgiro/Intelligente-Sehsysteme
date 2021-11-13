import itb2.filter.AbstractFilter
import itb2.image.GrayscaleImage
import itb2.image.Image
import itb2.image.ImageFactory
import kotlin.math.floor

class MinMaxFilter : AbstractFilter() {

    /**
     * Replaces values with min / max value of cross shaped environment
     * Converts image to gray scale
     */
    override fun filter(input: Image): Image {
        val grayImage = Grayfilter().filter(input)
        val output: Image = ImageFactory.bytePrecision().gray(input.size)
        val width = grayImage.width
        val height = grayImage.height
        val type = properties.getOptionProperty<String>(TYPE)
        for (col in 0 until width) {
            for (row in 0 until height) {
                val range = 1
                val environment = mutableListOf<Double>()
                for (offsetCol in -range until range + 1) {
                    for (offsetRow in if(offsetCol == 0) 0 until 0 else -range until range + 1) {

                        // only if indices are not beyond the edge getValue is valid
                        if (!(col - offsetCol < 0 || col - offsetCol >= width || row - offsetRow < 0 || row - offsetRow >= height)) {
                            val dif = if(type == "Max") 1 else -1
                            environment.add(grayImage.getValue(col + offsetCol, row + offsetRow, GrayscaleImage.GRAYSCALE) + dif)
                        }
                    }
                }
                output.setValue(col, row, if(type == "Max") environment.maxOrNull() ?: 0.0 else environment.minOrNull() ?: 0.0)
            }
        }
        return output
    }

    companion object {
        private const val TYPE = "type"
    }

    init {
        properties.addOptionProperty(TYPE, "Max", "Max", "Min")
    }

}