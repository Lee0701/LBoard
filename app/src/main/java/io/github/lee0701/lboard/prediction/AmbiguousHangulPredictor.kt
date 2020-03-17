package io.github.lee0701.lboard.prediction

import io.github.lee0701.lboard.dictionary.Dictionary
import io.github.lee0701.lboard.dictionary.WritableDictionary
import io.github.lee0701.lboard.hangul.HangulComposer
import io.github.lee0701.lboard.inputmethod.KeyInputHistory
import io.github.lee0701.lboard.inputmethod.ambiguous.Scorer
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.Normalizer
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
        val words = getWords(syllables)

        val candidates = words
                .mapIndexed { i, list ->
                    list.map { pair -> pair.first.let { word -> SingleCandidate(word.text, word.text, word.pos, word.frequency) } to pair.second } +
                            (syllables.getOrNull(i) ?: listOf())
                                    .map { s -> SingleCandidate(s.first, s.first, -1, conversionScorer.calculateScore(s.first) / 2, s.first.all { c -> c in '가' .. '힣' }) to s.second }
                }
                .map { list -> list.sortedByDescending { it.first.frequency }.distinctBy { it.first.text } }
                .map { list -> list.let { it.take(sqrt(it.size.toDouble()).toInt() * 4) } }

        val result = getCandidateCombinations(candidates)

        return result
    }

    private fun getSyllables(converted: List<List<Int>>): List<List<Pair<String, Int>>> {
        fun getSyllablesRecursive(current: HangulComposer.State, start: Int, index: Int = start): List<Pair<String, Int>> {
            if(index >= converted.size || index-start+1 > 6) return listOf()
            val list = converted[index]
                    .map { c -> hangulComposer.compose(current, c) }
                    .filter { !it.isEmpty() && it.other.isEmpty() }
            return list.map { hangulComposer.display(it) to index-start+1 } +
                    list.flatMap { getSyllablesRecursive(it, start, index + 1) }
        }
        return converted.mapIndexed { i, _ -> getSyllablesRecursive(HangulComposer.State(), i) }
    }

    private fun getWords(syllables: List<List<Pair<String, Int>>>): List<List<Pair<Dictionary.Word, Int>>> {
        val filtered = syllables.map { list -> list.filter { s -> s.second >= 2 } }
        fun getWordsRecursive(current: Pair<String, Int>, start: Int, index: Int = start): List<Pair<Dictionary.Word, Int>> {
            if(index >= filtered.size) return listOf()
            return filtered[index].flatMap { s -> dictionary.search(Normalizer.normalize(current.first + s.first, Normalizer.Form.NFD)).map { it to current.second + s.second } } +
                    filtered[index].flatMap { s -> getWordsRecursive(current.first + s.first to current.second + s.second, start, index + s.second) }
        }
        return filtered.mapIndexed { i, _ -> getWordsRecursive("" to 0, i) }
    }

    private fun getCandidateCombinations(candidates: List<List<Pair<SingleCandidate, Int>>>): List<Candidate> {
        fun getCombinationsRecursive(current: Pair<Candidate, Int>?, start: Int, index: Int = start): List<Candidate> {
            if(index >= candidates.size) return listOf()
            val list = candidates[index].map { candidate -> when(current?.first) {
                is CompoundCandidate -> (current.first as CompoundCandidate).let { compound -> compound.copy(candidates = compound.candidates + candidate.first) to current.second + candidate.second }
                is SingleCandidate -> CompoundCandidate(candidates = listOf(current.first as SingleCandidate, candidate.first)) to current.second + candidate.second
                else -> candidate
            } }.filter { index == candidates.size - 1 || it.first.text.all { c -> c in '가' .. '힣' } }
            return list.filter { it.second == candidates.size }.map { it.first } +
                    list.flatMap { getCombinationsRecursive(it, start, start + it.second) }
        }
        return candidates.mapIndexed { i, _ -> getCombinationsRecursive(null, i) }.flatten()
    }

    override fun learn(candidate: Candidate) {
        TODO("Not yet implemented")
    }

    override fun delete(candidate: Candidate) {
        TODO("Not yet implemented")
    }

}
