import itb2.filter.AbstractFilter
import itb2.image.Image
import itb2.image.ImageFactory

class CopyImageFilter_KB : AbstractFilter() {
    override fun filter(input: Image): Image {
        val output: Image = ImageFactory.getPrecision(input).rgb(input.size)
        for (col in 0 until input.width) {
            for (row in 0 until input.height) {
                output.setValue(col, row, *input.getValue(col, row))
            }
        }
        return output
    }
}