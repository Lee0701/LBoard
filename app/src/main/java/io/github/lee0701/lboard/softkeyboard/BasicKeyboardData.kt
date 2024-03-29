package io.github.lee0701.lboard.softkeyboard

import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.view.animation.DecelerateInterpolator
import androidx.annotation.DrawableRes

data class KeyTheme(
        @DrawableRes val background: Int,
        @DrawableRes val backgroundPressed: Int,
        val textColor: Int,
        @DrawableRes val foreground: Int? = null
)

data class RowTheme(
        @DrawableRes val background: Int? = null
)

data class KeyboardTheme(
        @DrawableRes val background: Int,
        val rowTheme: Map<Row.Type?, RowTheme> = mapOf(),
        val keyTheme: Map<Int?, KeyTheme> = mapOf(),
        val stickyTheme: KeyTheme? = null,
        val stickyLockedTheme: KeyTheme? = null,
        val previewBackground: Int
)

data class Layout(
        val rows: List<Row>,
        val keyWidth: Float = 0.1f,
        val key: String,
        val nameStringKey: Int
): Cloneable {
    override public fun clone() = Layout(rows.map { it.clone() }, keyWidth, key, nameStringKey)
}

data class Row(
        val keys: List<Key>,
        val type: Type? = null,
        val keyWidth: Float = 0f,
        val marginLeft: Float = 0f,
        val marginRight: Float = 0f
): Cloneable {
    var y: Int = 0
    var height: Int = 0
    var maxLabelLength: Int = 0

    override public fun clone() = Row(keys.map { it.clone() }, type, keyWidth, marginLeft, marginRight)

    enum class Type {
        ODD, EVEN, NUMBER, BOTTOM
    }
}

data class Key (
        val keyCode: Int = 0,
        var label: String = "",
        val repeatable: Boolean = false,
        val keyWidth: Float = 0f
): Cloneable {
    var x: Int = 0
    var y: Int = 0
    var width: Int = 0
    var height: Int = 0
    var labelLength: Int = 0

    private var pressAnimator: ValueAnimator? = null
    private var releaseAnimator: ValueAnimator? = null

    val alpha: Float? get() = releaseAnimator?.getAnimatedValue("alpha") as Float?
            ?: pressAnimator?.getAnimatedValue("alpha") as Float?

    fun onPressed(updateListener: (ValueAnimator) -> Unit) {
        this.pressAnimator = ValueAnimator().apply {
            val scale = PropertyValuesHolder.ofFloat("alpha", 0.3f, 1f)
            this.setValues(scale)
            this.addUpdateListener(updateListener)
            this.interpolator = DecelerateInterpolator()
            this.duration = 0
            this.start()
        }
        this.releaseAnimator = null
    }

    fun onReleased(updateListener: (ValueAnimator) -> Unit) {
        this.releaseAnimator = ValueAnimator().apply {
            val alpha = PropertyValuesHolder.ofFloat("alpha", 1f, 0f)
            this.setValues(alpha)
            this.addUpdateListener(updateListener)
            this.interpolator = DecelerateInterpolator()
            this.duration = 200
            this.start()
        }
    }

    override public fun clone() = Key(keyCode, label, repeatable, keyWidth)

}