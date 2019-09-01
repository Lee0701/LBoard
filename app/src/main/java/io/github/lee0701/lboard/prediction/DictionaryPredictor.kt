package io.github.lee0701.lboard.prediction

import io.github.lee0701.lboard.dictionary.Dictionary
import io.github.lee0701.lboard.dictionary.EditableDictionary
import io.github.lee0701.lboard.dictionary.WritableDictionary
import io.github.lee0701.lboard.inputmethod.KeyInputHistory
import org.jetbrains.anko.doAsync

class DictionaryPredictor(val dictionary: Dictionary, val layout: Map<Int, List<Int>>): Predictor {

    override fun init() {
        if(dictionary is WritableDictionary) doAsync { dictionary.read() }
    }

    override fun destroy() {
        if(dictionary is WritableDictionary) doAsync { dictionary.write() }
    }

    override fun predict(history: List<KeyInputHistory<Any>>): List<Candidate> {
        return dictionary.searchSequence(history.map { it.keyCode }, layout)
                .map { Candidate(0, it.text, it.text, it.pos.toString(), it.frequency) }
    }

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
            val existing = dictionary.search(candidate.text)
            existing.forEach { dictionary.remove(it) }
            val originalExisting = dictionary.search(candidate.originalText)
            originalExisting.forEach { dictionary.remove(it) }
        }
    }

}
