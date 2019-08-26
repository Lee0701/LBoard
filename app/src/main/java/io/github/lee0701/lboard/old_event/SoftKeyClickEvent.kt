package io.github.lee0701.lboard.old_event

class SoftKeyClickEvent(val keyCode: Int, val state: State) {
    enum class State {
        DOWN, UP
    }
}
