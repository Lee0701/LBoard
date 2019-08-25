package io.github.lee0701.lboard.softkeyboard

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.view.Gravity
import android.view.View

class BasicMoreKeyPopup(context: Context, val showOffset: Int, key: io.github.lee0701.lboard.softkeyboard.Key,
                        val list: List<Pair<Int, String>>,
                        background: Int, backgroundActive: Int, val color: Int
): KeyboardPopup(context, key) {

    private val background = ContextCompat.getDrawable(context, background)!!
    private val keys = list.map { Key(it.first, it.second) }
    private val layout = createKeyboardLayout(keys)
    private val keyboardView = KeyboardView(context, layout,
            key.width * layout.width, key.height * layout.height,
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
        if(layout.width % 2 == 1) offsetX += key.width/2
        while(offsetX < 0) offsetX += key.width
        while(offsetX + popupWindow.width > parent.width) offsetX -= key.width
        offsetY = key.y - popupWindow.height

        val rect = Rect(key.x, key.y, key.x + key.width, key.y + key.height)
        var firstTouchedKey = findKey(rect.centerX(), rect.centerY())

        firstTouchedKey?.let { key ->
            val firstKey = layout.rows.first().keys.first()
            if(firstKey.keyCode < 0x1000) {
                layout.rows.find { row -> row.keys.contains(key) }?.let { row ->
                    row.keys[row.keys.indexOf(key)] = firstKey
                    layout.rows.first().keys[0] = key
                    keyboardView.invalidateAllKeys()
                    firstTouchedKey = findKey(rect.centerX(), rect.centerY())
                }
            }
        }

        popupWindow.showAtLocation(parent, Gravity.NO_GRAVITY, offsetX + showOffset, offsetY)

        firstTouchedKey?.let {
            it.active = true
            keyCode = it.keyCode
        }

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

    private fun findKey(x: Int, y: Int): Key? {
        val innerX = x - offsetX
        val innerY = y - offsetY

        keys.forEach { key ->
            if(key.touchableRect.contains(innerX, innerY)) return key
        }
        return null
    }

    private fun createKeyboardLayout(keys: List<Key>): Layout {
        var rows = 1
        if(keys.size > 5) rows = 2
        if(key.y == 0) rows = 1
        var columns = keys.size / rows
        if(keys.size % rows > 0) columns++
        return Layout((0 until rows).map { j ->
            Row((0 until columns).map { i -> keys.getOrNull(j * columns + i) }.filterNotNull().toMutableList())
        }.toMutableList())
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

    data class Row(
            val keys: MutableList<Key>
    ) {
        val width: Int get() = keys.size
    }

    data class Layout(
            val rows: MutableList<Row>
    ) {
        val width: Int get() = rows.first().width
        val height: Int get() = rows.size
    }

    class KeyboardView(
            context: Context,
            val layout: Layout,
            val keyboardWidth: Int,
            val keyboardHeight: Int,
            val keyBackgroundActive: Drawable,
            val keyForegroundColor: Int
    ): View(context, null) {

        private val paint = Paint()

        init {
            paint.textAlign = Paint.Align.CENTER
            paint.isAntiAlias = true

            invalidateAllKeys()
        }

        override fun onDraw(canvas: Canvas) {
            layout.rows.forEach { row -> row.keys.forEach { key -> onDrawKey(canvas, key) } }
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

        fun invalidateAllKeys() {
            val keyWidth = keyboardWidth / layout.width
            val keyHeight = keyboardHeight / layout.height
            layout.rows.forEachIndexed { j, row ->
                val y = j * keyHeight
                row.keys.forEachIndexed { i, key ->
                    val x = i * keyWidth
                    key.x = x
                    key.y = y
                    key.width = keyWidth
                    key.height = keyHeight
                }
            }
        }

    }

}
