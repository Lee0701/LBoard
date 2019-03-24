package io.github.lee0701.lboard

import android.content.Context
import android.view.View
import io.github.lee0701.lboard.hardkeyboard.HardKeyboard
import io.github.lee0701.lboard.softkeyboard.SoftKeyboard

interface InputMethod {

    val softKeyboard: SoftKeyboard
    val hardKeyboard: HardKeyboard

    fun initView(context: Context): View?

    fun onKey(keyCode: Int, shift: Boolean): Boolean

    fun reset()

}
