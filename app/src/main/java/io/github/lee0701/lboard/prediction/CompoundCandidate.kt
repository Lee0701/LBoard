package io.github.lee0701.lboard.prediction

import java.lang.RuntimeException

data class CompoundCandidate(
        val candidates: List<SingleCandidate>,
): Candidate() {
    override val pos: Int = -1
    override val endingSpace: Boolean = true
    override val text: String get() = candidates.joinToString("") { it.text }
    override val originalText: String get() = text
    override val strokeCount: Int = candidates.sumOf { it.strokeCount }
    override val frequency: Float get() = candidates.map { it.frequency }.average().toFloat()
    override val score: Float get() = frequency / candidates.size
    override val nextWordPredictorWords: List<Int> = candidates.map { it.nextWordPredictorWords }.flatten()

    companion object {
        fun of(candidates: List<Candidate>): CompoundCandidate {
            return CompoundCandidate(candidates.flatMap { when(it) {
                is EmptyCandidate -> emptyList()
                is SingleCandidate -> listOf(it)
                is CompoundCandidate -> it.candidates
                else -> throw RuntimeException("Cannot convert $it to SingleCandidate")
            } })
        }
    }
}
