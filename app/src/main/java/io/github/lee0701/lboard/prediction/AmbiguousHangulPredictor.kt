package io.github.lee0701.lboard.prediction

import io.github.lee0701.lboard.dictionary.Dictionary
import io.github.lee0701.lboard.dictionary.WritableDictionary
import io.github.lee0701.lboard.hangul.HangulComposer
import io.github.lee0701.lboard.inputmethod.KeyInputHistory
import io.github.lee0701.lboard.inputmethod.ambiguous.Scorer
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.math.sqrt

class AmbiguousHangulPredictor(
        val conversionScorer: Scorer,
        val dictionary: Dictionary,
        val layout: Map<Int, Pair<List<Int>, List<Int>>>,
        val hangulComposer: HangulComposer
): Predictor<KeyInputHistory<Any>> {

    override fun init() {
        if(dictionary is WritableDictionary) GlobalScope.launch { dictionary.read() }
    }

    override fun destroy() {
        if(dictionary is WritableDictionary) dictionary.write()
    }

    override fun predict(source: List<KeyInputHistory<Any>>, length: Int): Iterable<Candidate> {
        val converted = source.map { layout[it.keyCode]?.let { item -> if(it.shift) item.second else item.first } ?: listOf() }

        val syllables = getSyllables(converted)

        println(syllables)

        val eojeols = mutableListOf("" to (0f to 0))
        syllables
                .map { list -> if(list.size <= 2) list else list.filter { it.char in '가' .. '힣'} }
                .forEachIndexed { i, list ->
                    val targets = eojeols.filter { it.second.second == i }
                    eojeols -= targets
                    eojeols += targets.flatMap { target ->
                        list.map { target.first + it.char to (target.second.first + it.start to target.second.second + it.length) }
                    }
                }

        val result = eojeols.map { it.first to it.second.first / it.first.length }
                .sortedByDescending { conversionScorer.calculateScore(it.first) }
                .filter { if(it.first.lastOrNull() in '가' .. '힣') it.first.all { c -> c in '가' .. '힣' } else true }
                .let { if(it.size > 16) it.take(sqrt(it.size.toDouble()).toInt() * 4) else it }
                .map {
                    // 사전 검색 결과 조합 중 가장 좋은 후보로 선택
                    dictionary.search(it.first).toList()
                            .map { SingleCandidate(it.text, it.text, it.pos, it.frequency) }
                            .maxBy { candidate -> candidate.frequency }
                            ?: SingleCandidate(it.first, it.first, -1, it.second, endingSpace = it.first.any { c -> c in '가' .. '힣' })
                }

        println(result)

        return result
    }

    private fun getSyllables(converted: List<List<Int>>): List<List<Syllable>> {
        fun getSyllablesRecursive(current: HangulComposer.State, start: Int, index: Int = start): List<Syllable> {
            if(index >= converted.size || start-index+1 > 6) return listOf()
            val list = converted[index]
                    .map { c -> hangulComposer.compose(current, c) }
                    .filter { !it.isEmpty() && it.other.isEmpty() }
            return list.map { Syllable(hangulComposer.display(it)[0], start, index-start+1) } +
                    list.flatMap { getSyllablesRecursive(it, start, index+1) }
        }
        return converted.mapIndexed { i, _ -> getSyllablesRecursive(HangulComposer.State(), i) }
    }

    override fun learn(candidate: Candidate) {
        TODO("Not yet implemented")
    }

    override fun delete(candidate: Candidate) {
        TODO("Not yet implemented")
    }

    private data class Syllable(
            val char: Char,
            val start: Int,
            val length: Int
    )

}
