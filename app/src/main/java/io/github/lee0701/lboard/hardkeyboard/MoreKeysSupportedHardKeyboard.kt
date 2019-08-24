package io.github.lee0701.lboard.hardkeyboard

import io.github.lee0701.lboard.InputMethodModule

interface MoreKeysSupportedHardKeyboard: HardKeyboard {

    fun getMoreKeys(keyCode: Int, shift: Boolean, alt: Boolean): List<Int>

}
