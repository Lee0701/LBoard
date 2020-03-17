package io.github.lee0701.lboard.prediction

import kotlinx.coroutines.flow.Flow

interface Predictor<T> {

    fun init()
    fun destroy()

    fun predict(source: List<T>, length: Int): Flow<Candidate>
    fun learn(candidate: Candidate)
    fun delete(candidate: Candidate)

}
