package io.github.lee0701.lboard.softkeyboard

import android.content.Context
import android.util.TypedValue
import android.view.View
import io.github.lee0701.lboard.event.SoftKeyClickEvent
import io.github.lee0701.lboard.event.SoftKeyFlickEvent
import io.github.lee0701.lboard.event.SoftKeyLongClickEvent
import io.github.lee0701.lboard.layouts.soft.SoftLayout
import io.github.lee0701.lboard.layouts.soft.TwelveSoftLayout
import io.github.lee0701.lboard.softkeyboard.themes.BasicSoftKeyboardTheme
import org.greenrobot.eventbus.EventBus
import org.json.JSONObject

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

    override fun serialize(): JSONObject {
        return super.serialize().apply {
            put("layout", REVERSE_LAYOUTS[layout])
            put("theme", REVERSE_THEMES[theme])
            put("height", keyHeight.toInt())
        }
    }

    companion object {

        @JvmStatic fun deserialize(json: JSONObject): BasicSoftKeyboard? {
            val layout = LAYOUTS[json.getString("layout")] ?: return null
            val theme = THEMES[json.getString("theme")] ?: return null
            val keyHeight = json.getInt("height").toFloat()
            return BasicSoftKeyboard(layout, theme, keyHeight)
        }

        val LAYOUTS = mapOf(
                "10cols-mobile" to SoftLayout.LAYOUT_10COLS_MOBILE,
                "10cols-mobile-with-num" to SoftLayout.LAYOUT_10COLS_MOBILE_WITH_NUM,
                "10cols-mod-quote" to SoftLayout.LAYOUT_10COLS_MOD_QUOTE,

                "12key-4cols" to TwelveSoftLayout.LAYOUT_12KEY_4COLS
        )
        val REVERSE_LAYOUTS = LAYOUTS.map { it.value to it.key }.toMap()

        val THEMES = mapOf(
                "white" to BasicSoftKeyboardTheme.WHITE
        )
        val REVERSE_THEMES = THEMES.map { it.value to it.key }.toMap()

    }

}
