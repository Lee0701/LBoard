package io.github.lee0701.lboard.prediction

interface Predictor<T> {

    fun init()
    fun destroy()

    fun predict(history: List<T>, length: Int): Sequence<Candidate>
    fun learn(candidate: Candidate)
    fun delete(candidate: Candidate)

}
