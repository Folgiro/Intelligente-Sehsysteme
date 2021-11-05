import itb2.filter.AbstractFilter
import itb2.image.Image
import itb2.image.ImageFactory

class MaskFilter : AbstractFilter() {
    override fun filter(input: Array<Image>): Array<Image> {
        val output: Image = ImageFactory.getPrecision(input[0]).rgb(input[0].size)
        for (col in 0 until input[0].width) {
            for (row in 0 until input[0].height) {

                // Kanäle aufsummieren
                for (chan in 0 until input[0].channelCount) output.setValue(col, row, chan, input[0].getValue(col, row, chan) * (input[1].getValue(col, row, chan) / 255))
            }
        }
        return arrayOf(output)
    }
}