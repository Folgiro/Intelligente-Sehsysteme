import itb2.filter.AbstractFilter
import itb2.image.Image
import java.lang.Math
import itb2.image.ImageFactory

class GammaFilter : AbstractFilter() {
    private fun calcGammaCorrection(x: Double, gamma: Double): Double {
        var x = x
        x = Math.round(256 * Math.pow(x / 255, gamma)).toDouble()
        return if (x == 256.0) 255.0 else x
    }

    override fun filter(input: Image): Image {
        val output: Image = ImageFactory.bytePrecision().gray(input.size)
        val gamma = properties.getDoubleProperty(GAMMA)
        for (col in 0 until input.width) {
            for (row in 0 until input.height) {
                var sum = 0.0
                for (chan in 0 until input.channelCount) sum += input.getValue(col, row, chan)
                output.setValue(col, row, calcGammaCorrection(sum / input.channelCount, gamma))
            }
        }
        return output
    }

    companion object {
        private const val GAMMA = "gamma"
    }

    init {
        properties.addDoubleProperty(GAMMA, 1.0)
    }
}