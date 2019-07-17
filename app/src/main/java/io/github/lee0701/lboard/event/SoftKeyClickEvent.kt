package io.github.lee0701.lboard.event

class SoftKeyClickEvent(val keyCode: Int, val state: State) {
    enum class State {
        DOWN, UP
    }
}
