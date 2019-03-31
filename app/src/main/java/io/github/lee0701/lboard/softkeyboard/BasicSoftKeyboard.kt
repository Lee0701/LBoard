package io.github.lee0701.lboard.softkeyboard

import android.content.Context
import android.util.TypedValue
import android.view.View
import io.github.lee0701.lboard.event.SoftKeyClickEvent
import io.github.lee0701.lboard.event.SoftKeyFlickEvent
import io.github.lee0701.lboard.event.SoftKeyLongClickEvent
import org.greenrobot.eventbus.EventBus

class BasicSoftKeyboard(val layout: Layout, val theme: KeyboardTheme, val keyHeight: Float): SoftKeyboard, BasicKeyboardView.OnKeyListener {

    var keyboardView: BasicKeyboardView? = null

    override fun initView(context: Context): View? {
        val keyboardHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, keyHeight, context.resources.displayMetrics) * layout.rows.size
        keyboardView = BasicKeyboardView(context, layout, theme, this, keyboardHeight.toInt(), 300, 50)
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

    override fun onKeyLongClick(keyCode: Int) {
        EventBus.getDefault().post(SoftKeyLongClickEvent(keyCode))
    }

    override fun onKeyFlickLeft(keyCode: Int) {
        EventBus.getDefault().post(SoftKeyFlickEvent(keyCode, SoftKeyFlickEvent.FlickDirection.LEFT))
    }

    override fun onKeyFlickRight(keyCode: Int) {
        EventBus.getDefault().post(SoftKeyFlickEvent(keyCode, SoftKeyFlickEvent.FlickDirection.RIGHT))
    }

    override fun onKeyFlickUp(keyCode: Int) {
        EventBus.getDefault().post(SoftKeyFlickEvent(keyCode, SoftKeyFlickEvent.FlickDirection.UP))
    }

    override fun onKeyFlickDown(keyCode: Int) {
        EventBus.getDefault().post(SoftKeyFlickEvent(keyCode, SoftKeyFlickEvent.FlickDirection.DOWN))
    }

}
