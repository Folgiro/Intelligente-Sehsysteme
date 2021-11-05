class SobelFilterHorizontal : ConvolutionFilter() {
    override val kernel: Array<DoubleArray> = arrayOf(doubleArrayOf(-1.0, 0.0, 1.0), doubleArrayOf(-2.0, 0.0, 2.0), doubleArrayOf(-1.0, 0.0, 1.0))
}