package io.github.lee0701.lboard.softkeyboard

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.v4.content.ContextCompat
import android.util.TypedValue
import android.view.KeyEvent
import android.view.View
import io.github.lee0701.lboard.R
import io.github.lee0701.lboard.event.SoftKeyClickEvent
import org.greenrobot.eventbus.EventBus



class BasicSoftKeyboard(val layout: Layout, val keyHeight: Float): SoftKeyboard, OnKeyListener {

    var keyboardView: BasicKeyboardView? = null

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
                        KeyEvent.KEYCODE_ENTER to KeyTheme(
                                ContextCompat.getDrawable(context, R.drawable.keybg_white_enter)!!,
                                ContextCompat.getDrawable(context, R.drawable.keybg_white_mod_p)!!,
                                Color.parseColor("#dd000000"),
                                ContextCompat.getDrawable(context, R.drawable.key_qwerty_enter)
                        ),
                        KeyEvent.KEYCODE_SHIFT_LEFT to KeyTheme(
                                ContextCompat.getDrawable(context, R.drawable.keybg_white_mod)!!,
                                ContextCompat.getDrawable(context, R.drawable.keybg_white_mod_p)!!,
                                Color.parseColor("#dd000000"),
                                ContextCompat.getDrawable(context, R.drawable.key_qwerty_shift_b)
                        )
                )
        )
        val keyboardHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, keyHeight, context.resources.displayMetrics) * layout.rows.size
        keyboardView = BasicKeyboardView(context, keyboardHeight.toInt(), layout, theme, this)
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
