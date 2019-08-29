package io.github.lee0701.lboard.prediction

import io.github.lee0701.lboard.dictionary.SQLiteDictionary
import io.github.lee0701.lboard.inputmethod.KeyInputHistory

class SQLiteDictionaryPredictor(val dictionary: SQLiteDictionary, val methodId: Int): Predictor {

    override fun predict(history: List<KeyInputHistory<Any>>): List<Candidate> {
        return dictionary.searchWord(history.map { it.seq }.joinToString(""), methodId)
    }

}
