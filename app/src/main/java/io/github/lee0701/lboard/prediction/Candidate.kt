package io.github.lee0701.lboard.prediction

data class Candidate(
        val wordId: Int,
        val text: String,
        val originalText: String = text,
        val pos: String = "",
        val frequency: Float = 0f,
        val endingSpace: Boolean = true
)
