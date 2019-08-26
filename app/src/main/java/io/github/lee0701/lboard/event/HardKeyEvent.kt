package io.github.lee0701.lboard.event

class HardKeyEvent(
        methodId: String,
        keyCode: Int,
        actions: List<Action>
): LBoardKeyEvent(methodId, keyCode, actions)
