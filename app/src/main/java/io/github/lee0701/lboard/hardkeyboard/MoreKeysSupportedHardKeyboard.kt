package io.github.lee0701.lboard.hardkeyboard

interface MoreKeysSupportedHardKeyboard: HardKeyboard {

    fun getMoreKeys(keyCode: Int, shift: Boolean, alt: Boolean): List<Int>

}
