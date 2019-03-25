package io.github.lee0701.lboard.softkeyboard

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.TypedValue
import android.view.View
import io.github.lee0701.lboard.event.SoftKeyClickEvent
import org.greenrobot.eventbus.EventBus

class StripeSoftKeyboard(val layout: StripeKeyboardView.Layout, val keyHeight: Float): SoftKeyboard, StripeKeyboardView.Listener {

    var keyboardView: StripeKeyboardView? = null

    override fun initView(context: Context): View? {
        val theme = StripeKeyboardView.KeyboardTheme(
                ColorDrawable(Color.argb(0xff, 0x15, 0x65, 0xc0)),
                mapOf(
                        null to StripeKeyboardView.RowTheme(ColorDrawable(Color.TRANSPARENT)),
                        StripeKeyboardView.Row.Type.EVEN to StripeKeyboardView.RowTheme(ColorDrawable(Color.argb(64, 255, 255, 255))),
                        StripeKeyboardView.Row.Type.BOTTOM to StripeKeyboardView.RowTheme(ColorDrawable(Color.argb(64, 255, 255, 255)))
                ),
                mapOf(
                        null to StripeKeyboardView.KeyTheme(ColorDrawable(Color.TRANSPARENT), ColorDrawable(Color.argb(64, 255, 255, 255)), Color.WHITE)
                )
        )
        val keyboardHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, keyHeight, context.resources.displayMetrics) * layout.rows.size
        keyboardView = StripeKeyboardView(context, keyboardHeight.toInt(), layout, theme, this)
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
