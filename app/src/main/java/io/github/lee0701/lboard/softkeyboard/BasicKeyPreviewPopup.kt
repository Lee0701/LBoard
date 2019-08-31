package io.github.lee0701.lboard.softkeyboard

import android.content.Context
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat

class BasicKeyPreviewPopup(context: Context, private val showXOffset: Int, private val showYOffset: Int,
                           key: Key, background: Int, val color: Int): KeyboardPopup(context, key) {

    private val background = ContextCompat.getDrawable(context, background)!!
    private val contentView = TextView(context).apply {
        text = key.label
        gravity = Gravity.CENTER_HORIZONTAL
        setTextSize(TypedValue.COMPLEX_UNIT_PX, key.width.toFloat() / key.label.length)
        setTextColor(color)
    }

    override fun show(parent: View) {
        popupWindow.contentView = contentView
        popupWindow.setBackgroundDrawable(background)
        popupWindow.width = key.width
        popupWindow.height = key.height * 2
        popupWindow.isClippingEnabled = false
        popupWindow.isTouchable = false

        val offsetY = key.y - popupWindow.height/2
        popupWindow.showAtLocation(parent, Gravity.NO_GRAVITY, key.x + showXOffset, offsetY + showYOffset)
    }

    override fun update() {
        contentView.text = key.label
    }

    override fun touchMove(x: Int, y: Int) {

    }

    override fun fade(alpha: Float) {
        background.alpha = (alpha * 255).toInt()
        popupWindow.contentView?.alpha = alpha
    }

    override fun dismiss() {
        popupWindow.dismiss()
    }

}
