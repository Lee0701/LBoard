package io.github.lee0701.lboard.inputmethod.ambiguous

import io.github.lee0701.lboard.prediction.Candidate

interface CandidateGenerator {

    fun init()
    fun destroy()

    fun generate(string: String): Iterable<Candidate>

    fun learn(candidate: Candidate)

    fun delete(candidate: Candidate)

}
