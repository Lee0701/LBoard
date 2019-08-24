package io.github.lee0701.lboard.softkeyboard

import android.content.Context
import android.support.v4.content.ContextCompat
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.PopupWindow
import android.widget.TextView

class BasicKeyboardPopup(val context: Context, val key: Key, background: Int, val color: Int) {

    private val background = ContextCompat.getDrawable(context, background)!!
    private val popupWindow: PopupWindow = PopupWindow(context, null)
    private val popupShown = validatePopupShown(key)

    fun show(parent: View) {
        if(!popupShown) return
        popupWindow.contentView = TextView(context).apply {
            text = key.label
            gravity = Gravity.CENTER_HORIZONTAL
            setTextSize(TypedValue.COMPLEX_UNIT_PX, key.width.toFloat() / key.label.length)
            setTextColor(color)
        }
        popupWindow.setBackgroundDrawable(background)
        popupWindow.width = key.width
        popupWindow.height = key.height * 2
        popupWindow.isClippingEnabled = false
        popupWindow.isTouchable = false
        popupWindow.showAtLocation(parent, Gravity.NO_GRAVITY, key.x, key.y - key.height)
    }

    fun setAlpha(alpha: Float) {
        if(!popupShown) return
        background.alpha = (alpha * 255).toInt()
        popupWindow.contentView.alpha = alpha
    }

    fun dismiss() {
        if(!popupShown) return
        popupWindow.dismiss()
    }

    private fun validatePopupShown(key: Key): Boolean {
        return key.keyCode in 7 .. 19 || key.keyCode in 29 .. 56 || key.keyCode in 68 .. 78
    }

}
