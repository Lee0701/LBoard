package io.github.lee0701.lboard.event

import io.github.lee0701.lboard.inputmethod.InputMethodInfo
import io.github.lee0701.lboard.prediction.Candidate

class CandidateSelectEvent(
        methodInfo: InputMethodInfo,
        val selected: Candidate
): InputMethodEvent(methodInfo)
