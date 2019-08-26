package io.github.lee0701.lboard.event

class HardKeyEvent(
        methodId: String,
        keyCode: Int,
        actions: List<Action>
): KeyEvent(methodId, keyCode) {

    data class Action(
            val type: ActionType,
            val time: Long
    )

    enum class ActionType {
        PRESS,
        RELEASE,
        LONG_PRESS,
        REPEAT
    }

}
