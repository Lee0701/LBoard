package io.github.lee0701.lboard.softkeyboard

import android.content.Context
import android.view.View
import io.github.lee0701.lboard.InputMethodModule

interface SoftKeyboard: InputMethodModule {

    var shift: Int
    var alt: Int

    fun initView(context: Context): View?

    fun setLabels(labels: Map<Int, String>)

}
