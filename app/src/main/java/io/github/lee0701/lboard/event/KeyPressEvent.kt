package io.github.lee0701.lboard.event

class KeyPressEvent(
        val keyCode: Int,
        val shift: Boolean,
        val alt: Boolean,
        val source: LBoardKeyEvent.Source,
        val type: LBoardKeyEvent.ActionType
): Event()
