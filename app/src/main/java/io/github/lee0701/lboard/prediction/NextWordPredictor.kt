package io.github.lee0701.lboard.prediction

interface NextWordPredictor {
    fun predict(words: List<Word>): Set<Candidate>

    data class Word(
            val id: Int,
            val text: String,
            val pos: String,
            val frequency: Int,
    )

    data class Candidate(
            val word: Word,
            val confidence: Float
    )
}