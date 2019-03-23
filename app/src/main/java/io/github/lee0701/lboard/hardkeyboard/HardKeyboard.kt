package io.github.lee0701.lboard.hardkeyboard

interface HardKeyboard {

    fun convert(keyCode: Int, shift: Boolean): ConvertResult

    fun reset()

    data class ConvertResult(val resultChar: Char?, val backspace: Boolean = false)

}
