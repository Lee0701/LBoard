package io.github.lee0701.lboard.event

class MoreKeySelectEvent(
        methodId: String,
        val originalKeyCode: Int,
        val newKeyCode: Int,
        val keyEvent: LBoardKeyEvent
): InputMethodEvent(methodId)
