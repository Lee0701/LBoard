package io.github.lee0701.lboard.inputmethod.ambiguous

import io.github.lee0701.lboard.prediction.Candidate
import kotlinx.coroutines.flow.Flow

interface CandidateGenerator {

    fun init()
    fun destroy()

    fun generate(string: String): Flow<Candidate>

    fun learn(candidate: Candidate)

    fun delete(candidate: Candidate)

}
