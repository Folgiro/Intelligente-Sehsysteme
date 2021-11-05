class Meanx5Filter : ConvolutionFilter() {
    override val kernel: Array<DoubleArray> = Array(3){ DoubleArray(3){1.0/25} }
}