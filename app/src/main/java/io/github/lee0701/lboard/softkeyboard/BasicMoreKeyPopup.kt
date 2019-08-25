package io.github.lee0701.lboard.softkeyboard

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.view.Gravity
import android.view.View

class BasicMoreKeyPopup(context: Context, key: io.github.lee0701.lboard.softkeyboard.Key, val list: List<Pair<Int, String>>, background: Int, backgroundActive: Int, val color: Int): KeyboardPopup(context, key) {

    private val background = ContextCompat.getDrawable(context, background)!!
    private val keys = list.map { Key(it.first, it.second) }
    private val layout = createKeyboardLayout(keys)
    private val keyboardView = KeyboardView(context, layout,
            key.width * layout.first().size, key.height * layout.size,
            ContextCompat.getDrawable(context, backgroundActive)!!, color)

    private var offsetX = 0
    private var offsetY = 0

    var keyCode: Int? = null

    override fun show(parent: View) {
        popupWindow.setBackgroundDrawable(background)
        popupWindow.width = keyboardView.keyboardWidth
        popupWindow.height = keyboardView.keyboardHeight
        popupWindow.contentView = keyboardView
        popupWindow.isClippingEnabled = false
        popupWindow.isTouchable = true

        offsetX = key.x - popupWindow.width/2
        if(layout.first().size % 2 == 1) offsetX += key.width/2
        while(offsetX < 0) offsetX += key.width
        while(offsetX + popupWindow.width > parent.width) offsetX -= key.width
        offsetY = key.y - popupWindow.height

        popupWindow.showAtLocation(parent, Gravity.NO_GRAVITY, offsetX, offsetY)

        val rect = Rect(key.x, key.y, key.x + key.width, key.y + key.height)
        touchMove(rect.centerX(), rect.centerY())
    }

    override fun update() {

    }

    override fun touchMove(x: Int, y: Int) {
        val innerX = x - offsetX
        val innerY = y - offsetY

        keyCode = null

        var changed = false
        keys.forEach { key ->
            val active = key.touchableRect.contains(innerX, innerY)
            if(key.active != active) changed = true
            key.active = active
            if(active) keyCode = key.keyCode
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

    private fun createKeyboardLayout(keys: List<Key>): List<List<Key>> {
        var rows = 1
        if(keys.size > 5) rows = 2
        var columns = keys.size / rows
        if(keys.size % rows > 0) columns++
        return (0 until rows).map { j ->
            (0 until columns).map { i -> keys.getOrNull(j * columns + i) }.filterNotNull()
        }
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
        val touchableRect: Rect get() = Rect(x, y + height, x + width, y + height*2)
    }

    class KeyboardView(
            context: Context,
            val keys: List<List<Key>>,
            val keyboardWidth: Int,
            val keyboardHeight: Int,
            val keyBackgroundActive: Drawable,
            val keyForegroundColor: Int
    ): View(context, null) {

        private val paint = Paint()

        init {
            paint.textAlign = Paint.Align.CENTER
            paint.isAntiAlias = true

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
                }
            }
        }

        override fun onDraw(canvas: Canvas) {
            keys.forEach { row -> row.forEach { key -> onDrawKey(canvas, key) } }
        }

        fun onDrawKey(canvas: Canvas, key: Key) {
            // Background (active key only)
            if(key.active) {
                val drawable = keyBackgroundActive
                drawable.setBounds(key.x, key.y, key.x+key.width, key.y+key.height)
                drawable.draw(canvas)
            }

            // Foreground (text)
            val params = KeyTextSizeAndPositionCalculator.calculate(key.label, key.x, key.y, key.width, key.height)
            paint.textSize = params.size
            paint.color = keyForegroundColor
            canvas.drawText(key.label, params.x, params.y, paint)
        }

    }

}
