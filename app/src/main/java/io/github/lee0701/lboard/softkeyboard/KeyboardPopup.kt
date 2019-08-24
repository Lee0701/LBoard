package io.github.lee0701.lboard.softkeyboard

import android.content.Context
import android.view.View
import android.widget.PopupWindow

abstract class KeyboardPopup(val context: Context, val key: Key) {

    protected val popupWindow: PopupWindow = PopupWindow(context, null)

    abstract fun show(parent: View)

    abstract fun dismiss()

    abstract fun fade(alpha: Float)

}
