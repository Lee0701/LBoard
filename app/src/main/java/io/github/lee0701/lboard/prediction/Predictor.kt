package io.github.lee0701.lboard.prediction

import io.github.lee0701.lboard.inputmethod.KeyInputHistory

interface Predictor {

    fun init()
    fun destroy()

    fun predict(history: List<KeyInputHistory<Any>>): List<Candidate>
    fun learn(candidate: Candidate)
    fun delete(candidate: Candidate)

}
