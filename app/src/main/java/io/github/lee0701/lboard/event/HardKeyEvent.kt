package io.github.lee0701.lboard.event

class HardKeyEvent(
        methodId: String,
        keyCode: Int,
        actions: List<Action>,
        val shiftPressed: Boolean = false,
        val altPressed: Boolean = false
): LBoardKeyEvent(methodId, keyCode, actions)
