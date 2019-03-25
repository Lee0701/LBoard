package io.github.lee0701.lboard.softkeyboard

import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.app.Service
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.util.DisplayMetrics
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager

class StripeKeyboardView(
        context: Context,
        val keyboardHeight: Int,
        val layout: Layout,
        val theme: KeyboardTheme,
        val listener: Listener
): View(context) {

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
            val width = keyboardWidth - ((row.paddingLeft + row.paddingRight) * keyboardWidth).toInt()
            var x = (row.paddingLeft * keyboardWidth).toInt()
            row.y = j * keyHeight
            row.height = keyHeight
            row.keys.forEachIndexed { i, key ->
                key.x = x
                key.y = row.y
                key.width = if(key.relativeWidth == 0f) width / row.keys.size else (width * key.relativeWidth).toInt()
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
        val alpha = ((key.alpha ?: 0f) * 255).toInt()

        if(widthRatio != null) {
            val drawable = theme.backgroundPressed
            val width = (key.width * widthRatio).toInt()
            val x = key.x + key.width/2 - width/2
            drawable.setBounds(x, key.y, x + width, key.y + key.height)
            drawable.alpha = alpha
            drawable.draw(canvas)
        } else {
            val drawable = theme.background
            drawable.setBounds(key.x, key.y, key.x + key.width, key.y + key.height)
            drawable.draw(canvas)

        }

    }

    private fun onDrawKeyForeground(canvas: Canvas, key: Key) {
        val theme = theme.keyTheme[key.keyCode] ?: theme.keyTheme[null] ?: return

        paint.color = theme.textColor

        paint.textSize = key.textSize

        canvas.drawText(key.label, (key.x + key.width/2).toFloat(), (key.y + key.height/4*3).toFloat(), paint)

    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when(event.action) {
            MotionEvent.ACTION_DOWN -> {
                val key = getKey(event.x.toInt(), event.y.toInt())
                key?.onPressed { invalidate() }
                pointers += 0 to TouchPointer(event.x.toInt(), event.y.toInt(), event.pressure, key)
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                pointers[0]?.let {
                    it.x = event.x.toInt()
                    it.y = event.y.toInt()
                    it.pressure = event.pressure
                }
                return true
            }
            MotionEvent.ACTION_UP -> {
                pointers[0]?.let {
                    it.key?.let { key ->
                        key.onReleased { invalidate() }
                        listener.onKey(key.keyCode, it.x, it.y)
                    }
                }
                pointers.clear()
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    fun getKey(x: Int, y: Int): Key? {
        layout.rows.forEach { row ->
            if(y in row.y until row.y + row.height) {
                row.keys.forEach { key ->
                    if(x in key.x until key.x + key.width) return key
                }
            }
        }
        return null
    }

    data class TouchPointer(
            var x: Int,
            var y: Int,
            var pressure: Float,
            val key: Key?
    )

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(measuredWidth, keyboardHeight)
    }

    data class KeyTheme(
            val background: Drawable,
            val backgroundPressed: Drawable,
            val textColor: Int,
            val foreground: Drawable? = null
    )

    data class RowTheme(
            val background: Drawable
    )

    data class KeyboardTheme(
            val background: Drawable,
            val rowTheme: Map<Row.Type?, RowTheme> = mapOf(),
            val keyTheme: Map<Int?, KeyTheme> = mapOf()
    )

    interface Listener {
        fun onKey(keyCode: Int, x: Int, y: Int)
    }


    data class Layout(
            val rows: List<Row>
    )

    data class Row(
            val keys: List<Key>,
            val type: Type? = null,
            val paddingLeft: Float = 0f,
            val paddingRight: Float = 0f
    ) {
        var y: Int = 0
        var height: Int = 0
        enum class Type {
            ODD, EVEN, NUMBER, BOTTOM
        }
    }

    data class Key (
            val keyCode: Int = 0,
            var label: String = "",

            val relativeWidth: Float = 0f
    ) {
        var shifted: Boolean = false
        var x: Int = 0
        var y: Int = 0
        var width: Int = 0
        var height: Int = 0
        var textSize: Float = 0f

        private var pressAnimator: ValueAnimator? = null
        private var releaseAnimator: ValueAnimator? = null

        val alpha: Float? get() = releaseAnimator?.getAnimatedValue("alpha") as Float?

        fun onPressed(updateListener: (ValueAnimator) -> Unit) {
            this.pressAnimator = ValueAnimator().apply {
                val scale = PropertyValuesHolder.ofFloat("alpha", 0f, 1f)
                this.setValues(scale)
                this.addUpdateListener(updateListener)
                this.duration = 50
                this.start()
            }
            this.releaseAnimator = null
        }

        fun onReleased(updateListener: (ValueAnimator) -> Unit) {
            this.releaseAnimator = ValueAnimator().apply {
                val alpha = PropertyValuesHolder.ofFloat("alpha", 1f, 0f)
                this.setValues(alpha)
                this.addUpdateListener(updateListener)
                this.duration = 200
                this.start()
            }
        }

    }

}
