package io.github.lee0701.lboard.softkeyboard

import android.content.Context
import android.view.View
import io.github.lee0701.lboard.inputmethod.InputMethodModule

interface SoftKeyboard: InputMethodModule {

    var shift: Int
    var alt: Int

    fun initView(context: Context): View?
    fun getView(): View?

    fun updateOneHandedMode(oneHandedMode: Int)
    fun updateLabels(labels: Map<Int, String>)

}
