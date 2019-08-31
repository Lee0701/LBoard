package io.github.lee0701.lboard.event

import io.github.lee0701.lboard.prediction.Candidate

class CandidateLongClickEvent(
        val clicked: Candidate
): Event()
