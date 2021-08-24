package io.github.lee0701.lboard.prediction

interface NextWordPredictor {
    fun getWord(text: String, pos: String? = null): Word?
    fun getWord(id: Int): Word?
    fun getWords(text: String): Set<Word>
    fun predict(words: List<Int>): Map<Int, Float>

    data class Word(
            val id: Int,
            val text: String,
            val pos: String,
            val frequency: Float,
    )

}