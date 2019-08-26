package io.github.lee0701.lboard.event

abstract class KeyEvent(
        methodId: String,
        val keyCode: Int
): InputMethodEvent(methodId)
