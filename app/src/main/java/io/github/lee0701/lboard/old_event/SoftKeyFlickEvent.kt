package io.github.lee0701.lboard.old_event

class SoftKeyFlickEvent(val keyCode: Int, val direction: FlickDirection) {
    enum class FlickDirection {
        LEFT, RIGHT, UP, DOWN
    }
}
