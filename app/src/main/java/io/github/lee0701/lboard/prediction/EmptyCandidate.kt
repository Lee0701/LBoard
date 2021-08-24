package io.github.lee0701.lboard.prediction

object EmptyCandidate: Candidate() {
    override val text: String = ""
    override val originalText: String = ""
    override val strokeCount: Int = 0
    override val pos: Int = -1
    override val frequency: Float = 0f
    override val score: Float = 0f
    override val endingSpace: Boolean = false
    override val nextWordPredictorWords: List<Int> = emptyList()
}