package io.github.lee0701.lboard

import android.content.Context
import android.view.View
import io.github.lee0701.lboard.hardkeyboard.HardKeyboard
import io.github.lee0701.lboard.softkeyboard.SoftKeyboard

data class LBoardInputMethod(val softKeyboard: SoftKeyboard, val hardKeyboard: HardKeyboard) {

    fun initView(context: Context): View? {
        return softKeyboard.initView(context)
    }

    fun onKey(keyCode: Int): Boolean {
        return hardKeyboard.onKey(keyCode)
    }

}
