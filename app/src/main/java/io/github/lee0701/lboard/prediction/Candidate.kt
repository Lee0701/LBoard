package io.github.lee0701.lboard.prediction

abstract class Candidate: Comparable<Candidate> {
    abstract val text: String
    abstract val originalText: String
    abstract val pos: Int
    abstract val strokeCount: Int
    abstract val frequency: Float
    abstract val score: Float
    abstract val endingSpace: Boolean
    abstract val nextWordPredictorWords: List<Int>

    override fun compareTo(other: Candidate): Int {
        return this.score.compareTo(other.score)
    }
}
