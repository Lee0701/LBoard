package io.github.lee0701.lboard

import android.content.Context
import android.view.KeyEvent
import android.view.View
import io.github.lee0701.lboard.hardkeyboard.HardKeyboard
import io.github.lee0701.lboard.softkeyboard.SoftKeyboard
import org.json.JSONObject

class AlphabetInputMethod(
        override val softKeyboard: SoftKeyboard,
        override val hardKeyboard: HardKeyboard
): CommonInputMethod() {

    override fun initView(context: Context): View? {
        return softKeyboard.initView(context)
    }

    override fun updateView(context: Context): View? {
        softKeyboard.setLabels(hardKeyboard.getLabels(shift, alt))
        return null
    }

    override fun reset() {
        super.reset()
    }

    companion object {
        @JvmStatic fun deserialize(json: JSONObject): AlphabetInputMethod? {
            val softKeyboard = InputMethod.deserializeModule(json.getJSONObject("soft-keyboard")) as SoftKeyboard
            val hardKeyboard = InputMethod.deserializeModule(json.getJSONObject("hard-keyboard")) as HardKeyboard
            return AlphabetInputMethod(softKeyboard, hardKeyboard)
        }
    }

}
