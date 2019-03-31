package io.github.lee0701.lboard.event

class SoftKeyFlickEvent(val keyCode: Int, val direction: FlickDirection) {
    enum class FlickDirection {
        LEFT, RIGHT, UP, DOWN
    }
}
