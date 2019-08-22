package io.github.lee0701.lboard.softkeyboard

import android.content.Context
import android.util.TypedValue
import android.view.KeyEvent
import android.view.View
import io.github.lee0701.lboard.event.SoftKeyClickEvent
import io.github.lee0701.lboard.event.SoftKeyFlickEvent
import io.github.lee0701.lboard.event.SoftKeyLongClickEvent
import io.github.lee0701.lboard.hangul.HangulComposer
import io.github.lee0701.lboard.layouts.soft.*
import io.github.lee0701.lboard.softkeyboard.themes.BasicSoftKeyboardTheme
import org.greenrobot.eventbus.EventBus
import org.json.JSONObject

class BasicSoftKeyboard(
        val layout: Layout,
        val theme: KeyboardTheme,
        val keyHeight: Float,
        val showLabels: Boolean,
        val compatibleLabels: Boolean,
        val repeatRate: Int,
        val longClickDelay: Int,
        val marginLeft: Int,
        val marginRight: Int,
        val marginBottom: Int
): SoftKeyboard, BasicKeyboardView.OnKeyListener {

    var keyboardView: BasicKeyboardView? = null

    override var shift: Int
        get() = keyboardView?.shift ?: 0
        set(value) {keyboardView?.shift = value}

    override var alt: Int
        get() = keyboardView?.alt ?: 0
        set(value) {keyboardView?.alt = value}

    override fun initView(context: Context): View? {
        val marginBottom = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, marginBottom.toFloat(), context.resources.displayMetrics)
        val marginLeft = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, marginLeft.toFloat(), context.resources.displayMetrics)
        val marginRight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, marginRight.toFloat(), context.resources.displayMetrics)
        val keyboardHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, keyHeight, context.resources.displayMetrics) * layout.rows.size
        keyboardView = BasicKeyboardView(context, layout, theme, this,
                keyboardHeight.toInt(), showLabels, repeatRate, longClickDelay,
                marginLeft.toInt(), marginRight.toInt(), marginBottom.toInt())
        return keyboardView
    }

    override fun getView(): View? {
        return keyboardView
    }

    override fun setLabels(labels: Map<Int, String>) {
        layout.rows.forEach { row ->
            row.keys.forEach { key ->
                key.label = labels[key.keyCode] ?: key.label
                if(compatibleLabels) key.label = convertToCompatible(key.label)
            }
        }
        keyboardView?.invalidate()
    }

    private fun convertToCompatible(label: String): String {
        return label.map { c ->
            if(c == ' ') c
            else if(HangulComposer.isCho(c.toInt())) HangulComposer.COMPAT_CHO[HangulComposer.CONVERT_CHO.indexOf(c)]
            else if(HangulComposer.isJung(c.toInt())) HangulComposer.COMPAT_JUNG[HangulComposer.STD_JUNG.indexOf(c)]
            else if(HangulComposer.isJong(c.toInt())) HangulComposer.COMPAT_CHO[HangulComposer.CONVERT_JONG.indexOf(c)]
            else c
        }.joinToString("")
    }

    override fun onKeyDown(keyCode: Int, x: Int, y: Int) {
        when(keyCode) {
            KeyEvent.KEYCODE_SHIFT_LEFT, KeyEvent.KEYCODE_SHIFT_RIGHT,
            KeyEvent.KEYCODE_ALT_LEFT, KeyEvent.KEYCODE_ALT_RIGHT -> {
                EventBus.getDefault().post(SoftKeyClickEvent(keyCode, SoftKeyClickEvent.State.DOWN))
            }
        }
    }

    override fun onKeyUp(keyCode: Int, x: Int, y: Int) {
        when(keyCode) {
            KeyEvent.KEYCODE_SHIFT_LEFT, KeyEvent.KEYCODE_SHIFT_RIGHT,
            KeyEvent.KEYCODE_ALT_LEFT, KeyEvent.KEYCODE_ALT_RIGHT -> {}
            else -> {
                EventBus.getDefault().post(SoftKeyClickEvent(keyCode, SoftKeyClickEvent.State.DOWN))
            }
        }
        EventBus.getDefault().post(SoftKeyClickEvent(keyCode, SoftKeyClickEvent.State.UP))
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
            put("layout", layout.key)
            put("theme", REVERSE_THEMES[theme])
            put("height", keyHeight.toInt())
            put("labels", showLabels)
            put("compatibleLabels", compatibleLabels)
            put("repeatRate", repeatRate)
            put("longClickDelay", longClickDelay)
            put("marginLeft", marginLeft)
            put("marginRight", marginRight)
            put("marginBottom", marginBottom)
        }
    }

    companion object {

        @JvmStatic fun deserialize(json: JSONObject): BasicSoftKeyboard? {
            val layout = LAYOUTS[json.getString("layout")] ?: return null
            val theme = THEMES[json.getString("theme")] ?: return null
            val keyHeight = json.getInt("height").toFloat()
            val keyLabels = json.optBoolean("labels", true)
            val compatibleLabels = json.optBoolean("compatibleLabels", true)
            val repeatRate = json.optInt("repeatRate", 50)
            val longClickDelay = json.optInt("longPressDelay", 300)
            val marginLeft = json.optInt("marginLeft")
            val marginRight = json.optInt("marginRight")
            val marginBottom = json.optInt("marginBottom")
            return BasicSoftKeyboard(layout, theme, keyHeight,
                    keyLabels, compatibleLabels, repeatRate, longClickDelay, marginLeft, marginRight, marginBottom)
        }

        val LAYOUTS = listOf(
                SoftLayout.LAYOUT_10COLS_MOBILE,
                SoftLayout.LAYOUT_10COLS_MOBILE_WITH_NUM,
                SoftLayout.LAYOUT_10COLS_MOBILE_WITH_APOSTROPHE,
                SoftLayout.LAYOUT_10COLS_MOBILE_WITH_APOSTROPHE_NUM,
                SoftLayout.LAYOUT_10COLS_DVORAK,
                SoftLayout.LAYOUT_10COLS_DVORAK_WITH_NUM,
                SoftLayout.LAYOUT_10COLS_MOD_QUOTE,
                SoftLayout.LAYOUT_10COLS_MOD_QUOTE_WITH_NUM,
                TabletSoftLayout.LAYOUT_11COLS_TABLET,
                TabletSoftLayout.LAYOUT_11COLS_TABLET_WITH_NUM,
                TabletSoftLayout.LAYOUT_11COLS_TABLET_WITH_QUOTE,
                TabletSoftLayout.LAYOUT_11COLS_TABLET_WITH_QUOTE_NUM,
                FullSoftLayout.LAYOUT_FULL,
                MiniSoftLayout.LAYOUT_MINI_7COLS,
                MiniSoftLayout.LAYOUT_MINI_8COLS_GOOGLE,
                TwelveSoftLayout.LAYOUT_12KEY_4COLS
        ).map { it.key to it }.toMap()

        val THEMES = mapOf(
                "white" to BasicSoftKeyboardTheme.WHITE,
                "dark" to BasicSoftKeyboardTheme.DARK,
                "flatwhite" to BasicSoftKeyboardTheme.FLATWHITE,
                "flatdark" to BasicSoftKeyboardTheme.FLATDARK
        )
        val REVERSE_THEMES = THEMES.map { it.value to it.key }.toMap()

    }

}
