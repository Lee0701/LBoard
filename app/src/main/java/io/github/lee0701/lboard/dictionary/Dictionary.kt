package io.github.lee0701.lboard.dictionary

interface Dictionary {

    fun search(text: String): List<Word>
    fun searchPrefix(prefix: String, length: Int): List<Word>
    fun searchSequence(seq: List<Int>, layout: Map<Int, List<Int>>): List<Word>

    data class Word(
            val text: String,
            val frequency: Float,
            val pos: Int
    )

}
