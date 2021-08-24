package io.github.lee0701.lboard.prediction

data class SingleCandidate(
        override val text: String,
        override val originalText: String,
        override val strokeCount: Int,
        override val pos: Int = 0,
        override val frequency: Float = 0f,
        override val endingSpace: Boolean = true,
        override val nextWordPredictorWords: List<Int> = emptyList(),
): Candidate() {
    override val score: Float get() = frequency
}
