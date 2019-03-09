package io.github.lee0701.lboard.softkeyboard

import android.content.Context
import android.view.View
import io.github.lee0701.lboard.InputMethodModule

abstract class SoftKeyboard(override val name: String): InputMethodModule {

    abstract fun initView(context: Context): View?

}
