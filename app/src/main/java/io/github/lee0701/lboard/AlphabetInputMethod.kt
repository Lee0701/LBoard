package io.github.lee0701.lboard

import android.content.Context
import android.view.KeyEvent
import android.view.View
import io.github.lee0701.lboard.hardkeyboard.HardKeyboard
import io.github.lee0701.lboard.softkeyboard.SoftKeyboard
import org.json.JSONObject

class AlphabetInputMethod(
        override val methodId: String,
        override val softKeyboard: SoftKeyboard,
        override val hardKeyboard: HardKeyboard
): CommonInputMethod() {

    companion object {

    }

}
