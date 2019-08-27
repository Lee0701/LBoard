package io.github.lee0701.lboard.event

import io.github.lee0701.lboard.ComposingText
import io.github.lee0701.lboard.inputmethod.InputMethodInfo

open class InputProcessCompleteEvent(
        methodInfo: InputMethodInfo,
        val keyEvent: LBoardKeyEvent,
        val composingText: ComposingText?,
        val commitDefaultChar: Boolean = false,
        val sendRawInput: Boolean = false
): InputMethodEvent(methodInfo)
