package io.github.lee0701.lboard.hardkeyboard

interface HardKeyboard {

    fun convert(keyCode: Int, shift: Boolean, alt: Boolean): ConvertResult

    fun reset()

    fun getLabels(shift: Boolean, alt: Boolean): Map<Int, String>

    data class ConvertResult(val resultChar: Int?, val backspace: Boolean = false)

}
