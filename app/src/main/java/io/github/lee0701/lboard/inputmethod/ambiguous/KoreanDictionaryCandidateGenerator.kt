package io.github.lee0701.lboard.inputmethod.ambiguous

import io.github.lee0701.lboard.dictionary.Dictionary
import io.github.lee0701.lboard.dictionary.EditableDictionary
import io.github.lee0701.lboard.dictionary.WritableDictionary
import io.github.lee0701.lboard.prediction.Candidate
import io.github.lee0701.lboard.prediction.CompoundCandidate
import io.github.lee0701.lboard.prediction.SingleCandidate
import org.jetbrains.anko.doAsync
import java.text.Normalizer

class KoreanDictionaryCandidateGenerator(val dictionary: Dictionary): CandidateGenerator {

    override fun init() {
        if(dictionary is WritableDictionary) doAsync { dictionary.read() }
    }

    override fun destroy() {
        if(dictionary is WritableDictionary) doAsync { dictionary.write() }
    }

    override fun generate(string: String): List<Candidate> {
        val words = string.mapIndexed { i, _ ->
            val result = mutableListOf<Dictionary.Word>()
            for(j in i .. string.length) {
                val word = string.substring(i, j)
                if(word.isEmpty()) continue
                result += dictionary.search(Normalizer.normalize(word, Normalizer.Form.NFD))
                        .map { it.copy(text = Normalizer.normalize(it.text, Normalizer.Form.NFC)) }
                        .sortedByDescending { it.frequency }
                        .distinctBy { it.text }
            }
            result
        }

        val result = mutableListOf(CompoundCandidate(listOf()) to 0)

        words.forEachIndexed { i, list ->
            val targets = result.filter { it.first.text.length == i }
            result -= targets
            result += targets.flatMap { target ->
                list.map { target.first.copy(target.first.candidates + SingleCandidate(it.text, it.text, it.pos, it.frequency, false)) to target.second + 1 }
            }
        }

        return result.map { it.first }
                .sortedBy { it.candidates.size }
                .sortedByDescending { it.frequency }
    }

    override fun learn(candidate: Candidate) {
        if(dictionary is EditableDictionary) {
            if(candidate is CompoundCandidate) {
                candidate.candidates.forEach {
                    val text = Normalizer.normalize(it.text, Normalizer.Form.NFD)
                    val existing = dictionary.search(text).maxBy { it.frequency }
                    if(existing == null || existing.frequency < candidate.frequency) {
                        dictionary.insert(Dictionary.Word(text, it.frequency, it.pos))
                    }
                }
            }
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
