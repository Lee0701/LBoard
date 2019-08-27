package io.github.lee0701.lboard.hardkeyboard

import io.github.lee0701.lboard.inputmethod.InputMethodModule

interface HardKeyboard: InputMethodModule {

    fun convert(keyCode: Int, shift: Boolean, alt: Boolean): ConvertResult

    fun reset()

    fun getLabels(shift: Boolean, alt: Boolean): Map<Int, String>

    data class ConvertResult(
            val resultChar: Int?,
            val backspace: Boolean = false,
            val shiftOn: Boolean? = null,
            val altOn: Boolean? = null,
            val defaultChar: Boolean = false
    )

}
