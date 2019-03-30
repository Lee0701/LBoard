package io.github.lee0701.lboard.softkeyboard

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.inputmethodservice.Keyboard
import android.support.v4.content.ContextCompat
import android.util.TypedValue
import android.view.View
import io.github.lee0701.lboard.R
import io.github.lee0701.lboard.event.SoftKeyClickEvent
import org.greenrobot.eventbus.EventBus



class ThemeableSoftKeyboard(val layout: Layout, val keyHeight: Float): SoftKeyboard, OnKeyListener {

    var keyboardView: ThemeableKeyboardView? = null

    /** Qwerty keyboard [ENTER]  */
    val KEYCODE_QWERTY_ENTER = 66
    /** Qwerty keyboard [SHIFT]  */
    val KEYCODE_QWERTY_SHIFT = 59

    override fun initView(context: Context): View? {
        val theme = KeyboardTheme(
                ContextCompat.getDrawable(context, R.drawable.keybg_white_bg)!!,
                mapOf(
                        null to RowTheme(ColorDrawable(Color.TRANSPARENT))
                ),
                mapOf(
                        null to KeyTheme(
                                ContextCompat.getDrawable(context, R.drawable.keybg_white)!!,
                                ContextCompat.getDrawable(context, R.drawable.keybg_white_p)!!,
                                Color.parseColor("#dd000000")
                        ),
                        KEYCODE_QWERTY_ENTER to KeyTheme(
                                ContextCompat.getDrawable(context, R.drawable.keybg_white_enter)!!,
                                ContextCompat.getDrawable(context, R.drawable.keybg_white_mod_p)!!,
                                Color.parseColor("#dd000000"),
                                ContextCompat.getDrawable(context, R.drawable.key_qwerty_enter)
                        ),
                        KEYCODE_QWERTY_SHIFT to KeyTheme(
                                ContextCompat.getDrawable(context, R.drawable.keybg_white_mod)!!,
                                ContextCompat.getDrawable(context, R.drawable.keybg_white_mod_p)!!,
                                Color.parseColor("#dd000000"),
                                ContextCompat.getDrawable(context, R.drawable.key_qwerty_shift)
                        )
                )
        )
        val keyboardHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, keyHeight, context.resources.displayMetrics) * layout.rows.size
        keyboardView = ThemeableKeyboardView(context, keyboardHeight.toInt(), layout, theme, this)
        return keyboardView
    }

    override fun setLabels(labels: Map<Int, String>) {
        layout.rows.forEach { row ->
            row.keys.forEach { key ->
                key.label = labels[key.keyCode] ?: key.label
            }
        }
    }

    override fun onKey(keyCode: Int, x: Int, y: Int) {
        EventBus.getDefault().post(SoftKeyClickEvent(keyCode))
    }
}
