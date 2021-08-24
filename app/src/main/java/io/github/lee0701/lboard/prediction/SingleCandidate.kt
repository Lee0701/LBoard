package io.github.lee0701.lboard.prediction

data class SingleCandidate(
        override val text: String,
        override val originalText: String,
        override val pos: Int = 0,
        override val frequency: Float = 0f,
        override val endingSpace: Boolean = true,
        override val nextWordPredictorWords: List<NextWordPredictor.Word> = emptyList(),
): Candidate() {
    override val score: Float get() = frequency / text.length
}
