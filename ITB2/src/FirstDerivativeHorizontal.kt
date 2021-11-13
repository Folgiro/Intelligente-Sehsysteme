class FirstDerivativeHorizontal : ConvolutionFilter() {
    override val kernel: Array<DoubleArray> = arrayOf(doubleArrayOf(0.0, 1.0, 0.0), doubleArrayOf(0.0, 0.0, 0.0), doubleArrayOf(0.0, -1.0, 0.0))
}