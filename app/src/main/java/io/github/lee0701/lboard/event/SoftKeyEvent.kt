package io.github.lee0701.lboard.event

class SoftKeyEvent(
        methodId: String,
        keyCode: Int,
        val actions: List<Action>
): KeyEvent(methodId, keyCode) {

    data class Action(
            val type: ActionType,
            val time: Long
    )

    enum class ActionType {
        PRESS,
        RELEASE,
        LONG_PRESS,
        REPEAT,
        FLICK_LEFT,
        FLICK_RIGHT,
        FLICK_UP,
        FLICK_DOWN
    }

}
