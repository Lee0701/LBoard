package io.github.lee0701.lboard.softkeyboard

import android.content.Context
import android.view.View

interface SoftKeyboard {

    fun initView(context: Context): View?

    fun setLabels(labels: Map<Int, String>)

}
