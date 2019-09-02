package io.github.lee0701.lboard.event

import io.github.lee0701.lboard.inputmethod.InputMethodInfo
import io.github.lee0701.lboard.prediction.Candidate
import io.github.lee0701.lboard.prediction.SingleCandidate

class CandidateSelectEvent(
        methodInfo: InputMethodInfo,
        val selected: Candidate
): InputMethodEvent(methodInfo)
