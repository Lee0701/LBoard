package io.github.lee0701.lboard.softkeyboard

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.view.Gravity
import android.view.MotionEvent
import android.view.View

class BasicMoreKeyPopup(context: Context, key: io.github.lee0701.lboard.softkeyboard.Key, val list: List<Pair<Int, String>>, background: Int, backgroundActive: Int, val color: Int): KeyboardPopup(context, key) {

    private val background = ContextCompat.getDrawable(context, background)!!
    val keys = list.map { Key(it.first, it.second) }
    private val keyboardView = KeyboardView(context, listOf(keys), ContextCompat.getDrawable(context, backgroundActive)!!, color)

    override fun show(parent: View) {
        popupWindow.setBackgroundDrawable(background)
        popupWindow.width = key.width * list.size
        popupWindow.height = key.height
        popupWindow.contentView = keyboardView
        popupWindow.isClippingEnabled = false
        popupWindow.isTouchable = true
        popupWindow.showAtLocation(parent, Gravity.NO_GRAVITY, key.x, key.y - key.height)
    }

    override fun update() {

    }

    override fun touchMove(x: Int, y: Int) {
        val innerX = x - key.x
        val innerY = y - (key.y - key.height)

        var changed = false
        keys.forEach { key ->
            val active = key.rect.contains(innerX, innerY)
            if(key.active != active) changed = true
            key.active = active
        }
        if(changed) keyboardView.invalidate()

    }

    override fun fade(alpha: Float) {
        background.alpha = (alpha * 255).toInt()
        popupWindow.contentView?.alpha = alpha
    }

    override fun dismiss() {
        popupWindow.dismiss()
    }

    data class Key(
            val keyCode: Int,
            val label: String
    ) {
        var x: Int = 0
        var y: Int = 0
        var width: Int = 0
        var height: Int = 0
        var active: Boolean = false
        val rect: Rect get() = Rect(x, y, x + width, y + height)
    }

    class KeyboardView(
            context: Context,
            val keys: List<List<Key>>,
            val keyBackgroundActive: Drawable,
            val keyForegroundColor: Int
    ): View(context, null) {

        private val paint = Paint()
        private val rect = Rect()

        init {
            paint.textAlign = Paint.Align.CENTER
            paint.isAntiAlias = true
        }

        override fun onDraw(canvas: Canvas) {
            getLocalVisibleRect(rect)
            val keyboardWidth = rect.width()
            val keyboardHeight = rect.height()
            val keyWidth = keyboardWidth / keys.first().size
            val keyHeight = keyboardHeight / keys.size
            keys.forEachIndexed { j, row ->
                val y = j * keyHeight
                row.forEachIndexed { i, key ->
                    val x = i * keyWidth
                    key.x = x
                    key.y = y
                    key.width = keyWidth
                    key.height = keyHeight
                    onDrawKey(canvas, key)
                }
            }
        }

        fun onDrawKey(canvas: Canvas, key: Key) {
            // Background (active key only)
            if(key.active) {
                val drawable = keyBackgroundActive
                drawable.setBounds(key.x, key.y, key.x+key.width, key.y+key.height)
                drawable.draw(canvas)
            }

            // Foreground (text)
            paint.color = keyForegroundColor
            val boundString = key.label.map { "W" }.joinToString("")
            paint.textSize = 4f
            paint.textSize = paint.textSize * (if(key.width > key.height) key.height else key.width) / paint.measureText(boundString) / 3 * 2
            val x = (key.x + key.width/2).toFloat()
            val y = (key.y + key.height/2 - (paint.descent() + paint.ascent())/2).toFloat()
            canvas.drawText(key.label, x, y, paint)
        }

    }

}
