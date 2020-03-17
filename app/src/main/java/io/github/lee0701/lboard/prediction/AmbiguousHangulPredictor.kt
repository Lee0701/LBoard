package io.github.lee0701.lboard.prediction

import io.github.lee0701.lboard.dictionary.Dictionary
import io.github.lee0701.lboard.dictionary.EditableDictionary
import io.github.lee0701.lboard.dictionary.WritableDictionary
import io.github.lee0701.lboard.hangul.HangulComposer
import io.github.lee0701.lboard.inputmethod.KeyInputHistory
import io.github.lee0701.lboard.inputmethod.ambiguous.Scorer
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
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

    override fun predict(source: List<KeyInputHistory<Any>>, length: Int): Flow<Candidate> = flow {
        val converted = source.map { layout[it.keyCode]?.let { item -> if(it.shift) item.second else item.first } ?: listOf() }

        val syllables = getSyllables(converted).toList()

        val words = getWords(syllables).toList()

        val candidates = words
                .mapIndexed { i, list ->
                    list.map { pair -> pair.first.let { word -> SingleCandidate(word.text, word.text, word.pos, word.frequency) } to pair.second } +
                            (syllables.getOrNull(i) ?: listOf())
                                    .map { s -> SingleCandidate(s.first, s.first, -1, conversionScorer.calculateScore(s.first) / 2, s.first.all { c -> c in '가' .. '힣' }) to s.second }
                }
                .map { list -> list.sortedByDescending { it.first.frequency }.distinctBy { it.first.text } }
                .map { list -> list.let { it.take(sqrt(it.size.toDouble()).toInt() * 4) } }

        getCandidateCombinations(candidates).collect { emit(it) }
    }

    private fun getSyllables(converted: List<List<Int>>): Flow<List<Pair<String, Int>>> = flow {
        fun getSyllablesRecursive(current: HangulComposer.State, start: Int, index: Int = start): List<Pair<String, Int>> {
            if(index >= converted.size || index-start+1 > 6) return listOf()
            val list = converted[index]
                    .map { c -> hangulComposer.compose(current, c) }
                    .filter { !it.isEmpty() && it.other.isEmpty() }
            return list.map { hangulComposer.display(it) to index-start+1 } +
                    list.flatMap { getSyllablesRecursive(it, start, index + 1) }
        }
        converted.forEachIndexed { i, _ -> emit(getSyllablesRecursive(HangulComposer.State(), i)) }
    }

    private fun getWords(syllables: List<List<Pair<String, Int>>>): Flow<List<Pair<Dictionary.Word, Int>>> = flow {
        val filtered = syllables.map { list -> list.filter { s -> s.second >= 2 } }
        fun getWordsRecursive(current: Pair<String, Int>, start: Int, index: Int = start): List<Pair<Dictionary.Word, Int>> {
            if(index >= filtered.size) return listOf()
            return filtered[index].flatMap { s -> dictionary.search(Normalizer.normalize(current.first + s.first, Normalizer.Form.NFD)).map { it to current.second + s.second } } +
                    filtered[index].flatMap { s -> getWordsRecursive(current.first + s.first to current.second + s.second, start, index + s.second) }
        }
        filtered.forEachIndexed { i, _ -> emit(getWordsRecursive("" to 0, i)) }
    }

    private fun getCandidateCombinations(candidates: List<List<Pair<SingleCandidate, Int>>>): Flow<Candidate> = flow {
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
        candidates.forEachIndexed { i, _ -> getCombinationsRecursive(null, i).forEach { emit(it) } }
    }

    override fun learn(candidate: Candidate) {
        if(candidate.text.none { it in '가' .. '힣' }) return
        if(dictionary is EditableDictionary) {
            if(candidate is CompoundCandidate) {
                candidate.candidates.forEach {
                    if(it.text.length <= 1) return@forEach
                    val text = Normalizer.normalize(it.text, Normalizer.Form.NFD)
                    val existing = dictionary.search(text).maxBy { it.frequency }
                    if(existing == null || existing.frequency < it.frequency) {
                        dictionary.insert(Dictionary.Word(text, it.frequency, it.pos))
                    }
                }
            }
            if(candidate.text.length <= 1) return
            val text = Normalizer.normalize(candidate.text, Normalizer.Form.NFD)
            val existing = dictionary.search(text).maxBy { it.frequency }
            if(existing == null || existing.frequency < candidate.frequency) {
                dictionary.insert(Dictionary.Word(text, candidate.frequency, candidate.pos))
            }
        }
    }

    override fun delete(candidate: Candidate) {
        if(dictionary is EditableDictionary) {
            if(candidate is CompoundCandidate) {
                candidate.candidates.forEach {
                    val text = Normalizer.normalize(it.text, Normalizer.Form.NFD)
                    val existing = dictionary.search(text)
                    existing.forEach { dictionary.remove(it) }
                }
            }
            val existing = dictionary.search(Normalizer.normalize(candidate.text, Normalizer.Form.NFD))
            existing.forEach { dictionary.remove(it) }
        }
    }

}
