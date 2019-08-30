package io.github.lee0701.lboard.dictionary

import io.github.lee0701.lboard.prediction.Candidate

interface Dictionary {

    fun search(word: String): List<Candidate>
    fun searchPrefix(prefix: String, length: Int): List<Candidate>
    fun searchSequence(seq: List<Int>, layout: Map<Int, List<Int>>): List<Candidate>

}
