class Laplace8Filter : ConvolutionFilter() {
    override val kernel: Array<DoubleArray> = arrayOf(doubleArrayOf(1.0, 1.0, 1.0), doubleArrayOf(1.0, -8.0, 1.0), doubleArrayOf(1.0, 1.0, 1.0))
}