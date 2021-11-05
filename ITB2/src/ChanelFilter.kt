import itb2.filter.AbstractFilter
import itb2.image.Image
import itb2.image.ImageFactory

class ChanelFilter : AbstractFilter() {
    override fun filter(input: Image): Image {
        val output: Image = ImageFactory.getPrecision(input).rgb(input.size)
        val scale = properties.getDoubleProperty(SCALE)
        val channel = properties.getOptionProperty<Int>(CHANEL)
        for (col in 0 until input.width) {
            for (row in 0 until input.height) {
                output.setValue(col, row, *input.getValue(col, row))
                output.setValue(col, row, channel, input.getValue(col, row, channel) * scale)
            }
        }
        return output
    }

    companion object {
        private const val SCALE = "scale"
        private const val CHANEL = "chanel"
    }

    init {
        properties.addDoubleProperty("scale", 1.0)
        properties.addOptionProperty("chanel", 0, 0, 1, 2)
    }
}