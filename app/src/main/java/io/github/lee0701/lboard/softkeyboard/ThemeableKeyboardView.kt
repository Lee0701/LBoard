package io.github.lee0701.lboard.softkeyboard

import android.content.Context
import android.app.Service
import android.graphics.*
import android.util.DisplayMetrics
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager


class ThemeableKeyboardView(
        context: Context,
        val keyboardHeight: Int,
        val layout: Layout,
        val theme: KeyboardTheme,
        val onKeyListener: io.github.lee0701.lboard.softkeyboard.OnKeyListener)
: View(context){

    val rect = Rect()
    val displayMetrics = DisplayMetrics()

    val paint = Paint()

    val pointers = mutableMapOf<Int, TouchPointer>()

    init {
        paint.textAlign = Paint.Align.CENTER
        paint.isAntiAlias = true

        this.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, keyboardHeight)

        val rows = layout.rows

        (context.getSystemService(Service.WINDOW_SERVICE) as WindowManager).defaultDisplay.getMetrics(displayMetrics)

        val keyboardWidth = displayMetrics.widthPixels
        val keyHeight = keyboardHeight / rows.size

        rows.forEachIndexed { j, row ->
            var x = (row.paddingLeft * keyboardWidth).toInt()
            row.y = j * keyHeight
            row.height = keyHeight

            row.keys.forEachIndexed { i, key ->
                key.x = x
                key.y = row.y
                key.width =
                        if(key.keyWidth != 0f) (keyboardWidth * key.keyWidth).toInt()
                        else if(row.keyWidth != 0f) (keyboardWidth * row.keyWidth).toInt()
                        else (keyboardWidth * layout.keyWidth).toInt()
                key.height = row.height

                val boundString = key.label.map { "W" }.joinToString("")
                key.textSize = key.width / paint.measureText(boundString) * paint.textSize / 3 * 2

                x += key.width
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        getLocalVisibleRect(rect)

        val background = theme.background
        background.setBounds(rect)
        background.draw(canvas)

        val rows = layout.rows

        if(rows.isEmpty()) return

        val keyboardWidth = rect.width()

        rows.forEachIndexed { j, row ->
            val rowTheme = theme.rowTheme[row.type] ?: theme.rowTheme[null]
            rowTheme?.let { theme ->
                val drawable = theme.background
                drawable.setBounds(0, j * row.keys[0].height, keyboardWidth, (j + 1) * row.keys[0].height)
                drawable.draw(canvas)
            }
            row.keys.forEach { key ->
                onDrawKeyBackground(canvas, key)
            }
            row.keys.forEach { key ->
                onDrawKeyForeground(canvas, key)
            }
        }

    }

    private fun onDrawKeyBackground(canvas: Canvas, key: Key) {
        val theme = theme.keyTheme[key.keyCode] ?: theme.keyTheme[null] ?: return

        val widthRatio = 1.0f

        val bgDrawable = theme.background
        val width = (key.width * widthRatio).toInt()
        val x = key.x + key.width/2 - width/2

        bgDrawable.setBounds(x, key.y, x + width, key.y + key.height)
        bgDrawable.draw(canvas)

        val alpha = ((key.alpha ?: 0f) * 255).toInt()

        val pressedBgDrawable = theme.backgroundPressed
        pressedBgDrawable.setBounds(x, key.y, x + width, key.y + key.height)
        pressedBgDrawable.alpha = alpha
        pressedBgDrawable.draw(canvas)
    }

    private fun onDrawKeyForeground(canvas: Canvas, key: Key) {
        val theme = theme.keyTheme[key.keyCode] ?: theme.keyTheme[null] ?: return

        if (theme.foreground != null) with(theme.foreground){
                val x = key.x + (key.width - intrinsicWidth)/2
                val y = key.y + (key.height - intrinsicHeight)/2
                setBounds(x, y, x + intrinsicWidth, y + intrinsicHeight)
                draw(canvas)
        } else{
            paint.color = theme.textColor
            paint.textSize = key.textSize

            canvas.drawText(key.label, (key.x + key.width/2).toFloat(), (key.y + key.height/4*3).toFloat(), paint)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean =
        when(event.action) {
            MotionEvent.ACTION_DOWN -> {
                val key = getKey(event.x.toInt(), event.y.toInt())
                key?.onPressed { invalidate() }
                pointers += 0 to TouchPointer(event.x.toInt(), event.y.toInt(), event.pressure, key)
                true
            }

            MotionEvent.ACTION_MOVE -> {
                pointers[0]?.let { pointer ->
                    pointer.x = event.x.toInt()
                    pointer.y = event.y.toInt()
                    pointer.pressure = event.pressure
                }
                true
            }

            MotionEvent.ACTION_UP -> {
                pointers[0]?.let { pointer ->
                    pointer.key?.let { key ->
                        key.onReleased { invalidate() }
                        onKeyListener.onKey(key.keyCode, pointer.x, pointer.y)
                    }
                }
                pointers.clear()
                true
            }
            else -> super.onTouchEvent(event)
        }

    fun getKey(x: Int, y: Int): Key? {
        layout.rows.forEach { row ->
            if (y in row.y until row.y + row.height) {
                row.keys.forEach { key ->
                    if(x in key.x until key.x + key.width) return key
                }
            }
        }
        return null
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(measuredWidth, keyboardHeight)
    }
}