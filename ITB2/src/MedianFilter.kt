import itb2.filter.AbstractFilter
import itb2.image.GrayscaleImage
import itb2.image.Image
import itb2.image.ImageFactory
import kotlin.math.floor

class MedianFilter : AbstractFilter() {

    /**
     *
     * Converts image to geay scale
     */
    override fun filter(input: Image): Image {
        val grayImage = Grayfilter().filter(input)
        val output: Image = ImageFactory.bytePrecision().gray(input.size)
        val width = grayImage.width
        val height = grayImage.height
        val type = properties.getIntegerProperty(SIZE)
        for (col in 0 until width) {
            for (row in 0 until height) {
                val range = type/2
                val environment = mutableListOf<Double>()
                for (offsetCol in -range until range + 1) {
                    for (offsetRow in -range until range + 1) {

                        // only if indices are not beyond the edge getValue is valid
                        if (!(col - offsetCol < 0 || col - offsetCol >= width || row - offsetRow < 0 || row - offsetRow >= height)) {
                            environment.add(grayImage.getValue(col - offsetCol, row - offsetRow, GrayscaleImage.GRAYSCALE))
                        }
                    }
                }
                environment.sort()
                output.setValue(col, row, environment[environment.size/2])
            }
        }
        return output
    }

    companion object {
        private const val SIZE = "size"
    }

    init {
        properties.addIntegerProperty(SIZE, 3)
    }

}