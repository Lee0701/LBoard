package io.github.lee0701.lboard.event

import io.github.lee0701.lboard.inputmethod.InputMethodInfo

abstract class InputMethodEvent(
        val methodInfo: InputMethodInfo
): Event()
