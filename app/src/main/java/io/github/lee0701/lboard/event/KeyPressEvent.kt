package io.github.lee0701.lboard.event

/**
 * A low-level event that notifies key inputs to main service.
 */
class KeyPressEvent(
        val keyCode: Int,
        val shift: Boolean,
        val alt: Boolean,
        val source: LBoardKeyEvent.Source,
        val type: LBoardKeyEvent.ActionType
): Event()
