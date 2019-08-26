package io.github.lee0701.lboard.event

class SoftKeyEvent(
        methodId: String,
        keyCode: Int,
        actions: List<Action>
): LBoardKeyEvent(methodId, keyCode, actions)
