package io.github.lee0701.lboard.inputmethod

data class KeyInputHistory<T>(
        val keyCode: Int,
        val seq: String = "",
        val shift: Boolean = false,
        val alt: Boolean = false,
        val composing: T
)

