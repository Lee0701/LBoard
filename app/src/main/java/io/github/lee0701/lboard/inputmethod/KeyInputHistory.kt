package io.github.lee0701.lboard.inputmethod

data class KeyInputHistory<T>(
        val keyCode: Int,
        val shift: Boolean = false,
        val alt: Boolean = false,
        val composing: T
)

