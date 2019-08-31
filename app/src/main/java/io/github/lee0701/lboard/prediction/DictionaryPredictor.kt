package io.github.lee0701.lboard.prediction

import io.github.lee0701.lboard.dictionary.Dictionary
import io.github.lee0701.lboard.inputmethod.KeyInputHistory

class DictionaryPredictor(val dictionary: Dictionary, val layout: Map<Int, List<Int>>): Predictor {
    override fun predict(history: List<KeyInputHistory<Any>>): List<Candidate> {
        return dictionary.searchSequence(history.map { it.keyCode }, layout)
                .map { Candidate(0, it.text, it.pos.toString(), it.frequency) }
    }
}
