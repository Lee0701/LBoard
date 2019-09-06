package io.github.lee0701.lboard.prediction

import io.github.lee0701.lboard.dictionary.Dictionary
import io.github.lee0701.lboard.dictionary.EditableDictionary
import io.github.lee0701.lboard.dictionary.WritableDictionary
import io.github.lee0701.lboard.inputmethod.KeyInputHistory
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class DictionaryPredictor(val dictionary: Dictionary, val layout: Map<Int, List<Int>>): Predictor<KeyInputHistory<Any>> {

    override fun init() {
        if(dictionary is WritableDictionary) GlobalScope.launch { dictionary.read() }
    }

    override fun destroy() {
        if(dictionary is WritableDictionary) dictionary.write()
    }

    override fun predict(history: List<KeyInputHistory<Any>>, length: Int): Iterable<Candidate> = sequence {
        dictionary.searchSequencePrefix(history.map { it.keyCode }, layout, length)
                .forEach { yield(SingleCandidate(it.text, it.text, it.pos, it.frequency)) }
    }.asIterable()

    override fun learn(candidate: Candidate) {
        if(dictionary is EditableDictionary) {
            val text = candidate.originalText
            val existing = dictionary.search(text).firstOrNull()
            if(existing == null || candidate.frequency < existing.frequency)
                dictionary.insert(Dictionary.Word(text, candidate.frequency, candidate.pos.toInt()))
        }
    }

    override fun delete(candidate: Candidate) {
        if(dictionary is EditableDictionary) {
            if(candidate.text.length <= 1) return
            val existing = dictionary.search(candidate.text)
            existing.forEach { dictionary.remove(it) }
            val originalExisting = dictionary.search(candidate.originalText)
            originalExisting.forEach { dictionary.remove(it) }
        }
    }

}
