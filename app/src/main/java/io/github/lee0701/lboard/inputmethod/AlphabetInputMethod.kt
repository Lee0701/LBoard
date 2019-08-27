package io.github.lee0701.lboard.inputmethod

import io.github.lee0701.lboard.hardkeyboard.HardKeyboard
import io.github.lee0701.lboard.softkeyboard.SoftKeyboard

class AlphabetInputMethod(
        override val methodId: String,
        override val softKeyboard: SoftKeyboard,
        override val hardKeyboard: HardKeyboard
): CommonInputMethod() {

    companion object {

    }

}
