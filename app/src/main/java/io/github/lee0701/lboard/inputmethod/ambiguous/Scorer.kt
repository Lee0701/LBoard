package io.github.lee0701.lboard.inputmethod.ambiguous

interface Scorer {

    fun calculateScore(string: String): Float

}
