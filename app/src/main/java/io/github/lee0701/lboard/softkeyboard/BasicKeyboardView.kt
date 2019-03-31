package io.github.lee0701.lboard.softkeyboard

import android.content.Context
import android.app.Service
import android.graphics.*
import android.support.v4.content.ContextCompat
import android.util.DisplayMetrics
import android.view.*
import io.github.lee0701.lboard.event.SoftKeyFlickEvent
import java.util.*
import kotlin.concurrent.timerTask
import kotlin.math.abs


class BasicKeyboardView(
        context: Context,
        val layout: Layout,
        val theme: KeyboardTheme,
        val onKeyListener: OnKeyListener,
        val keyboardHeight: Int,
        val longClickDelay: Int,
        val repeatRate: Int
): View(context) {

    private val rect = Rect()
    private val displayMetrics = DisplayMetrics()

    private val paint = Paint()

    private val pointers = mutableMapOf<Int, TouchPointer>()

    private val timer = Timer()

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

            row.keys.forEachIndexed { _, key ->
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

        val background = ContextCompat.getDrawable(context, theme.background)!!
        background.setBounds(rect)
        background.draw(canvas)

        val rows = layout.rows

        if(rows.isEmpty()) return

        val keyboardWidth = rect.width()

        rows.forEachIndexed { j, row ->
            val rowTheme = theme.rowTheme[row.type] ?: theme.rowTheme[null]
            rowTheme?.background?.let { background ->
                val drawable = ContextCompat.getDrawable(context, background)!!
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

        val bgDrawable = ContextCompat.getDrawable(context, theme.background)!!
        val width = (key.width * widthRatio).toInt()
        val x = key.x + key.width/2 - width/2

        bgDrawable.setBounds(x, key.y, x + width, key.y + key.height)
        bgDrawable.draw(canvas)

        val alpha = ((key.alpha ?: 0f) * 255).toInt()

        val pressedBgDrawable = ContextCompat.getDrawable(context, theme.backgroundPressed)!!
        pressedBgDrawable.setBounds(x, key.y, x + width, key.y + key.height)
        pressedBgDrawable.alpha = alpha
        pressedBgDrawable.draw(canvas)
    }

    private fun onDrawKeyForeground(canvas: Canvas, key: Key) {
        val theme = theme.keyTheme[key.keyCode] ?: theme.keyTheme[null] ?: return

        if (theme.foreground != null) with(ContextCompat.getDrawable(context, theme.foreground)!!) {
                val x = key.x + (key.width - intrinsicWidth)/2
                val y = key.y + (key.height - intrinsicHeight)/2
                setBounds(x, y, x + intrinsicWidth, y + intrinsicHeight)
                draw(canvas)
        } else {
            paint.color = theme.textColor
            paint.textSize = key.textSize
            canvas.drawText(key.label, (key.x + key.width/2).toFloat(), (key.y + key.height/4*3).toFloat(), paint)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val pointerId = event.getPointerId(event.actionIndex)
        val x = event.getX(event.actionIndex)
        val y = event.getY(event.actionIndex)
        val pressure = event.getPressure(event.actionIndex)
        when(event.actionMasked) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> {
                val key = getKey(x.toInt(), y.toInt()) ?: return super.onTouchEvent(event)
                key.onPressed { invalidate() }

                val onLongClick = if(key.repeatable) timerTask {
                    onKeyListener.onKey(key.keyCode, x.toInt(), y.toInt())
                } else timerTask {
                    onKeyListener.onKeyLongClick(key.keyCode)
                }

                if(key.keyCode == KeyEvent.KEYCODE_DEL) timer.scheduleAtFixedRate(onLongClick, longClickDelay.toLong(), repeatRate.toLong())
                else timer.schedule(onLongClick, longClickDelay.toLong())

                pointers += pointerId to TouchPointer(x.toInt(), y.toInt(), pressure, key, onLongClick)
                return true
            }

            MotionEvent.ACTION_MOVE -> {
                pointers[pointerId]?.let { pointer ->
                    pointer.x = x.toInt()
                    pointer.y = y.toInt()
                    pointer.pressure = pressure

                    if(abs(pointer.dy) > abs((pointer.dx))) {
                        if(pointer.y > pointer.key.y + pointer.key.height && pointer.flickDirection != SoftKeyFlickEvent.FlickDirection.DOWN) {
                            onKeyListener.onKeyFlickDown(pointer.key.keyCode)
                            pointer.flickDirection = SoftKeyFlickEvent.FlickDirection.DOWN
                        } else if(pointer.y < pointer.key.y && pointer.flickDirection != SoftKeyFlickEvent.FlickDirection.UP) {
                            onKeyListener.onKeyFlickUp(pointer.key.keyCode)
                            pointer.flickDirection = SoftKeyFlickEvent.FlickDirection.UP
                        }
                    } else {
                        if(pointer.x > pointer.key.x + pointer.key.width && pointer.flickDirection != SoftKeyFlickEvent.FlickDirection.RIGHT) {
                            onKeyListener.onKeyFlickRight(pointer.key.keyCode)
                            pointer.flickDirection = SoftKeyFlickEvent.FlickDirection.RIGHT
                        } else if(pointer.x < pointer.key.x && pointer.flickDirection != SoftKeyFlickEvent.FlickDirection.LEFT) {
                            onKeyListener.onKeyFlickLeft(pointer.key.keyCode)
                            pointer.flickDirection = SoftKeyFlickEvent.FlickDirection.LEFT
                        }
                    }
                }
                return true
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> {
                pointers[pointerId]?.let { pointer ->
                    pointers -= pointerId

                    pointer.longClickHandler.cancel()
                    pointer.key.onReleased { invalidate() }

                    onKeyListener.onKey(pointer.key.keyCode, pointer.x, pointer.y)
                }
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    private fun getKey(x: Int, y: Int): Key? {
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

    data class TouchPointer(
            val initialX: Int,
            val initialY: Int,
            var pressure: Float,
            val key: Key,
            val longClickHandler: TimerTask
    ) {
        var x: Int = initialX
        var y: Int = initialY

        val dx get() = x - initialX
        val dy get() = y - initialY

        var flickDirection: SoftKeyFlickEvent.FlickDirection? = null

    }

    interface OnKeyListener {
        fun onKey(keyCode: Int, x: Int, y: Int)
        fun onKeyLongClick(keyCode: Int)
        fun onKeyFlickLeft(keyCode: Int)
        fun onKeyFlickRight(keyCode: Int)
        fun onKeyFlickUp(keyCode: Int)
        fun onKeyFlickDown(keyCode: Int)
    }

}