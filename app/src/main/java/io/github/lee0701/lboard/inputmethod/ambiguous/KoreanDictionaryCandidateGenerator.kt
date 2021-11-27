package io.github.lee0701.lboard.inputmethod.ambiguous

import io.github.lee0701.lboard.dictionary.Dictionary
import io.github.lee0701.lboard.dictionary.EditableDictionary
import io.github.lee0701.lboard.dictionary.WritableDictionary
import io.github.lee0701.lboard.prediction.Candidate
import io.github.lee0701.lboard.prediction.CompoundCandidate
import io.github.lee0701.lboard.prediction.SingleCandidate
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import java.text.Normalizer

class KoreanDictionaryCandidateGenerator(val dictionary: Dictionary): CandidateGenerator {

    override fun init() {
        if(dictionary is WritableDictionary) GlobalScope.launch { dictionary.read() }
    }

    override fun destroy() {
        if(dictionary is WritableDictionary) dictionary.write()
    }

    override fun generate(string: String): Flow<Candidate> = flow {
        getWordCombinationRecursive(getWords(string).toList(), 0)
                .collect { emit(it) }
    }

    private fun getWords(string: String): Sequence<List<Dictionary.Word>> = sequence {
        string.forEachIndexed { i, _ ->
            val result = mutableListOf<Dictionary.Word>()
            for(j in i .. string.length) {
                val word = string.substring(i, j)
                if(word.isEmpty()) continue
                result += dictionary.search(Normalizer.normalize(word, Normalizer.Form.NFD))
                        .map { it.copy(text = Normalizer.normalize(it.text, Normalizer.Form.NFC)) }
                        .sortedByDescending { it.frequency }
                        .distinctBy { it.text }
            }
            yield(result)
        }
    }

    private fun getWordCombinationRecursive(words: List<List<Dictionary.Word>>, index: Int)
            : Flow<CompoundCandidate> = flow {
        if(index >= words.size) {
            emit(CompoundCandidate(listOf<SingleCandidate>()))
            return@flow
        }
        words[index].forEach { word ->
            getWordCombinationRecursive(words, index + word.text.length).collect { candidate ->
                emit(candidate.copy(candidates = listOf(SingleCandidate(word.text, word.text, word.pos, word.frequency, false)) + candidate.candidates))
            }
        }
    }

    override fun learn(candidate: Candidate) {
        if(candidate.text.none { it in '가' .. '힣' }) return
        if(dictionary is EditableDictionary) {
            if(candidate is CompoundCandidate) {
                candidate.candidates.forEach {
                    if(it.text.length <= 1) return@forEach
                    val text = Normalizer.normalize(it.text, Normalizer.Form.NFD)
                    val existing = dictionary.search(text).maxByOrNull { it.frequency }
                    if(existing == null || existing.frequency < it.frequency) {
                        dictionary.insert(Dictionary.Word(text, it.frequency, it.pos))
                    }
                }
            }
            if(candidate.text.length <= 1) return
            val text = Normalizer.normalize(candidate.text, Normalizer.Form.NFD)
            val existing = dictionary.search(text).maxByOrNull { it.frequency }
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
