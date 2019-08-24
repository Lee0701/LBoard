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
        val showLabels: Boolean,
        val showPopups: Boolean,
        val repeatRate: Int,
        val longClickDelay: Int,
        val marginLeft: Int,
        val marginRight: Int,
        val marginBottom: Int
): View(context) {

    var shift: Int = 0
    var alt: Int = 0

    private val rect = Rect()
    private val displayMetrics = DisplayMetrics()

    private val paint = Paint()

    private val pointers = mutableMapOf<Int, TouchPointer>()
    private val popups = mutableMapOf<Int, KeyboardPopup>()

    private val timer = Timer()

    init {
        paint.textAlign = Paint.Align.CENTER
        paint.isAntiAlias = true

        this.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, keyboardHeight + marginBottom)

        val rows = layout.rows

        (context.getSystemService(Service.WINDOW_SERVICE) as WindowManager).defaultDisplay.getMetrics(displayMetrics)

        val keyboardWidth = displayMetrics.widthPixels - (marginLeft + marginRight)
        val keyHeight = keyboardHeight / rows.size

        rows.forEachIndexed { j, row ->
            var x = (row.marginLeft * keyboardWidth).toInt() + marginLeft
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

                key.textSize = 4f

                x += key.width
            }
        }

    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        getLocalVisibleRect(rect)

        val background = ContextCompat.getDrawable(context, theme.background)!!
        background.bounds = rect
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
                if(showLabels) onDrawKeyForeground(canvas, key)
            }
        }

    }

    private fun onDrawKeyBackground(canvas: Canvas, key: Key) {
        val keyTheme = theme.keyTheme[key.keyCode] ?: theme.keyTheme[null] ?: return
        val theme = when(key.keyCode) {
            KeyEvent.KEYCODE_SHIFT_LEFT, KeyEvent.KEYCODE_SHIFT_RIGHT ->
                (if(shift == 1) theme.stickyTheme else if(shift == 2) theme.stickyLockedTheme else null) ?: keyTheme
            KeyEvent.KEYCODE_ALT_LEFT, KeyEvent.KEYCODE_ALT_RIGHT ->
                (if(alt == 1) theme.stickyTheme else if(alt == 2) theme.stickyLockedTheme else null) ?: keyTheme
            else -> keyTheme
        }

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
            val boundString = key.label.map { "W" }.joinToString("")
            paint.textSize = key.textSize
            paint.textSize = key.textSize * (if(key.width > key.height) key.height else key.width) / paint.measureText(boundString) / 3 * 2
            val x = (key.x + key.width/2).toFloat()
            val y = (key.y + key.height/2 - (paint.descent() + paint.ascent())/2).toFloat()
            canvas.drawText(key.label, x, y, paint)
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

                popups[key.keyCode]?.dismiss()
                popups -= key.keyCode
                if(showPopups && validatePopupShown(key)) {
                    val popup = BasicKeyPreviewPopup(context, key, theme.previewBackground, theme.keyTheme[null]?.textColor ?: Color.BLACK)
                    popups += key.keyCode to popup
                    popup.show(this)
                }

                key.onPressed { invalidate() }

                val onLongClick = if(key.repeatable) timerTask {
                    onKeyListener.onKeyDown(key.keyCode, x.toInt(), y.toInt(), true)
                    onKeyListener.onKeyUp(key.keyCode, x.toInt(), y.toInt(), true)
                } else timerTask {
                    onKeyListener.onKeyLongClick(key.keyCode)
                }

                val pointer = TouchPointer(x.toInt(), y.toInt(), pressure, key, onLongClick)
                pointers += pointerId to pointer

                when(pointer.key.keyCode) {
                    KeyEvent.KEYCODE_SHIFT_LEFT, KeyEvent.KEYCODE_SHIFT_RIGHT,
                    KeyEvent.KEYCODE_ALT_LEFT, KeyEvent.KEYCODE_ALT_RIGHT -> {}
                    else -> {
                        if(key.repeatable) timer.scheduleAtFixedRate(onLongClick, longClickDelay.toLong(), repeatRate.toLong())
                        else timer.schedule(onLongClick, longClickDelay.toLong())
                    }
                }

                onKeyListener.onKeyDown(pointer.key.keyCode, pointer.x, pointer.y)

                return true
            }

            MotionEvent.ACTION_MOVE -> {
                pointers[pointerId]?.let { pointer ->
                    pointer.x = x.toInt()
                    pointer.y = y.toInt()
                    pointer.pressure = pressure

                    if(abs(pointer.dy) > abs((pointer.dx))) {
                        if((pointer.y > pointer.key.y + pointer.key.height || pointer.y > pointer.initialY + pointer.key.height/2)
                                && pointer.flickDirection != SoftKeyFlickEvent.FlickDirection.DOWN) {
                            onKeyListener.onKeyFlickDown(pointer.key.keyCode)
                            pointer.flickDirection = SoftKeyFlickEvent.FlickDirection.DOWN
                        } else if((pointer.y < pointer.key.y || pointer.y < pointer.initialY - pointer.key.height/2)
                                && pointer.flickDirection != SoftKeyFlickEvent.FlickDirection.UP) {
                            onKeyListener.onKeyFlickUp(pointer.key.keyCode)
                            pointer.flickDirection = SoftKeyFlickEvent.FlickDirection.UP
                        }
                    } else {
                        if((pointer.x > pointer.key.x + pointer.key.width || pointer.x > pointer.initialX + pointer.key.width/2)
                                && pointer.flickDirection != SoftKeyFlickEvent.FlickDirection.RIGHT) {
                            onKeyListener.onKeyFlickRight(pointer.key.keyCode)
                            pointer.flickDirection = SoftKeyFlickEvent.FlickDirection.RIGHT
                        } else if((pointer.x < pointer.key.x || pointer.x < pointer.initialX - pointer.key.width/2)
                                        && pointer.flickDirection != SoftKeyFlickEvent.FlickDirection.LEFT) {
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

                    val popup = popups[pointer.key.keyCode]

                    pointer.longClickHandler.cancel()
                    pointer.key.onReleased {
                        invalidate()
                        val alpha = pointer.key.alpha ?: 0f
                        popup?.fade(alpha)
                        if(alpha == 0f && popup != null) {
                            popup.dismiss()
                            popups -= pointer.key.keyCode
                        }
                    }

                    onKeyListener.onKeyUp(pointer.key.keyCode, pointer.x, pointer.y)
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
        setMeasuredDimension(measuredWidth, keyboardHeight + marginBottom)
    }

    private fun validatePopupShown(key: Key): Boolean {
        return key.keyCode in 7 .. 19 || key.keyCode in 29 .. 56 || key.keyCode in 68 .. 78
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
        fun onKeyDown(keyCode: Int, x: Int, y: Int, repeated: Boolean = false)
        fun onKeyUp(keyCode: Int, x: Int, y: Int, repeated: Boolean = false)
        fun onKeyLongClick(keyCode: Int)
        fun onKeyFlickLeft(keyCode: Int)
        fun onKeyFlickRight(keyCode: Int)
        fun onKeyFlickUp(keyCode: Int)
        fun onKeyFlickDown(keyCode: Int)
    }

}