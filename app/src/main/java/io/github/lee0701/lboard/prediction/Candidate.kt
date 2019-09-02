package io.github.lee0701.lboard.prediction

abstract class Candidate {
    abstract val text: String
    abstract val originalText: String
    abstract val pos: Int
    abstract val frequency: Float
    abstract val score: Float
    abstract val endingSpace: Boolean
}
