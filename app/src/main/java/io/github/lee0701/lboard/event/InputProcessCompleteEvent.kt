package io.github.lee0701.lboard.event

import io.github.lee0701.lboard.ComposingText

open class InputProcessCompleteEvent(
        methodId: String,
        val keyEvent: LBoardKeyEvent,
        val composingText: ComposingText?,
        val commitDefaultChar: Boolean = false,
        val sendRawInput: Boolean = false
): InputMethodEvent(methodId)
