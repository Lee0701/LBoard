package io.github.lee0701.lboard.prediction

import io.github.lee0701.lboard.inputmethod.KeyInputHistory

interface Predictor {

    fun predict(history: List<KeyInputHistory<Any>>): List<Candidate>

}
