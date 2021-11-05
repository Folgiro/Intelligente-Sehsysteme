import itb2.filter.AbstractFilter
import itb2.filter.RequireImageType
import itb2.image.*

/**
 * Wandelt ein RGB-Bild in ein Grauwertbild um.
 *
 * @author Micha Strauch
 */
@RequireImageType(RgbImage::class)
class Grayfilter : AbstractFilter() {
    override fun filter(input: Image): Image {
        // Ausgabebild erstellen
        val output = ImageFactory.bytePrecision().gray(input.size)

        // Über Pixel iterieren
        for (col in 0 until input.width) {
            for (row in 0 until input.height) {
                var sum = 0.0

                // Kanäle aufsummieren
                for (chan in 0 until input.channelCount) sum += input.getValue(col, row, chan)

                // Mittelwert abspeichern
                output.setValue(col, row, sum / input.channelCount)
            }
        }
        return output
    }

    init {
        // Als Konverter registrieren
        ImageConverter.register(RgbImage::class.java, ImageFactory.bytePrecision().gray(), this)
    }
}