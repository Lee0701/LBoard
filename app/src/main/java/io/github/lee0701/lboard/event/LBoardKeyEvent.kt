package io.github.lee0701.lboard.event

import io.github.lee0701.lboard.inputmethod.InputMethodInfo

class LBoardKeyEvent(
        methodInfo: InputMethodInfo,
        val originalKeyCode: Int,
        val source: Source,
        val actions: List<Action>,
        val shiftPressed: Boolean = false,
        val altPressed: Boolean = false
): InputMethodEvent(methodInfo) {

    val lastKeyCode: Int get() = actions.last().keyCode

    data class Action(
            val type: ActionType,
            val keyCode: Int,
            val time: Long
    )

    enum class ActionType {
        PRESS,
        RELEASE,
        LONG_PRESS,
        REPEAT,
        SELECT_MORE_KEYS,
        FLICK_LEFT,
        FLICK_RIGHT,
        FLICK_UP,
        FLICK_DOWN
    }

    enum class Source {
        VIRTUAL_KEYBOARD, PHYSICAL_KEYBOARD, INTERNAL, UNKNOWN
    }

}
