package io.github.lee0701.lboard

data class ComposingText(
        val commitPreviousText: Boolean = false,
        val newComposingText: CharSequence? = null,
        val textToCommit: CharSequence? = null,
        val newCursorPosition: Int = 1
)
