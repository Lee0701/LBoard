package io.github.lee0701.lboard.event

import io.github.lee0701.lboard.prediction.Candidate

class CandidateSelectEvent(
        val selected: Candidate
): Event()
