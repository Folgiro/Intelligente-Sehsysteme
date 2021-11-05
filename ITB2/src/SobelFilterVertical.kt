class SobelFilterVertical : ConvolutionFilter() {
    override val kernel: Array<DoubleArray> = arrayOf(doubleArrayOf(-1.0, -2.0, -1.0), doubleArrayOf(0.0, 0.0, 0.0), doubleArrayOf(1.0, 2.0, 1.0))
}