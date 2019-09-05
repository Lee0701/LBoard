package io.github.lee0701.lboard.dictionary

interface Dictionary {

    fun search(text: String): Iterable<Word>
    fun searchPrefix(prefix: String, length: Int): Iterable<Word>
    fun searchSequence(seq: List<Int>, layout: Map<Int, List<Int>>): Iterable<Word>
    fun searchSequencePrefix(seqPrefix: List<Int>, layout: Map<Int, List<Int>>, length: Int): Iterable<Word>

    data class Word(
            val text: String,
            val frequency: Float,
            val pos: Int
    )

}
