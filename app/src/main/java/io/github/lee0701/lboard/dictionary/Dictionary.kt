package io.github.lee0701.lboard.dictionary

interface Dictionary {

    fun search(text: String): Sequence<Word>
    fun searchPrefix(prefix: String, length: Int): Sequence<Word>
    fun searchSequence(seq: List<Int>, layout: Map<Int, List<Int>>): Sequence<Word>
    fun searchSequencePrefix(seqPrefix: List<Int>, layout: Map<Int, List<Int>>, length: Int): Sequence<Word>

    data class Word(
            val text: String,
            val frequency: Float,
            val pos: Int
    )

}
