package io.github.lee0701.lboard.hardkeyboard

import io.github.lee0701.lboard.InputMethodModule

abstract class HardKeyboard(override val name: String): InputMethodModule {

    abstract fun onKey(keyCode: Int): Boolean

}
