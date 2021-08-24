package io.github.lee0701.lboard.prediction

data class CompoundCandidate(
        val candidates: List<SingleCandidate>,
        override val pos: Int = -1,
        override val endingSpace: Boolean = true
): Candidate() {
    override val text: String get() = candidates.map { it.text }.joinToString("")
    override val originalText: String get() = text
    override val frequency: Float get() = candidates.map { it.frequency }.average().toFloat()
    override val score: Float get() = frequency / candidates.size
    override val nextWordPredictorWords: List<NextWordPredictor.Word> = candidates.map { it.nextWordPredictorWords }.flatten()
}
