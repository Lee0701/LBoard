package io.github.lee0701.lboard.softkeyboard

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.SoundPool
import android.os.Build
import android.os.Vibrator
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
        val theme: KeyboardTheme
): MoreKeysSupportedSoftKeyboard, BasicKeyboardView.OnKeyListener {

    var keyboardView: BasicKeyboardView? = null
    var currentLabels: Map<Int, String> = mapOf()

    override var shift: Int
        get() = keyboardView?.shift ?: 0
        set(value) {keyboardView?.shift = value}

    override var alt: Int
        get() = keyboardView?.alt ?: 0
        set(value) {keyboardView?.alt = value}

    private var pressTime: Long = 0

    private var vibrator: Vibrator? = null
    private var soundPool: SoundPool? = null
    private var downSound: Int? = null
    private var upSound: Int? = null

    private var vibrate: Boolean = false
    private var vibrateDuration: Int = 0
    private var sound: Boolean = false
    private var soundType: BasicKeyboardSound? = null
    private var soundVolume: Float = 0f

    private var keyHeight: Float = 0f
    private var showLabels: Boolean = true
    private var showPopups: Boolean = false
    private var compatibleLabels: Boolean = true
    private var repeatRate: Int = 0
    private var longClickDelay: Int = 0
    private var marginLeft: Int = 0
    private var marginRight: Int = 0
    private var marginBottom: Int = 0

    override fun initView(context: Context): View? {
        if(vibrate) vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if(sound) soundPool = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP -> SoundPool.Builder().setAudioAttributes(AudioAttributes.Builder().setLegacyStreamType(AudioManager.STREAM_SYSTEM).build()).build()
            else -> SoundPool(1, AudioManager.STREAM_SYSTEM, 0)
        }.apply {
            soundType?.down?.let { downSound = load(context, it, 1) }
            soundType?.up?.let { upSound = load(context, it, 1) }
        }

        val marginBottom = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, marginBottom.toFloat(), context.resources.displayMetrics)
        val marginLeft = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, marginLeft.toFloat(), context.resources.displayMetrics)
        val marginRight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, marginRight.toFloat(), context.resources.displayMetrics)
        val keyboardHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, keyHeight, context.resources.displayMetrics) * layout.rows.size

        keyboardView = BasicKeyboardView(context, layout, theme, this,
                keyboardHeight.toInt(), showLabels, showPopups, repeatRate, longClickDelay,
                marginLeft.toInt(), marginRight.toInt(), marginBottom.toInt())
        return keyboardView
    }

    override fun getView(): View? {
        return keyboardView
    }

    override fun setLabels(labels: Map<Int, String>) {
        this.currentLabels = labels
        layout.rows.forEach { row ->
            row.keys.forEach { key ->
                key.label = labels[key.keyCode] ?: key.label
                if(compatibleLabels) key.label = convertToCompatible(key.label)
            }
        }
        keyboardView?.invalidate()
        keyboardView?.updatePopups()
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

    override fun onKeyDown(keyCode: Int, x: Int, y: Int, repeated: Boolean) {
        if(!repeated) {
            vibrator?.vibrate(vibrateDuration.toLong())
            val volume = soundVolume

            (if(keyCode == KeyEvent.KEYCODE_SPACE && upSound != null) upSound else downSound)?.let { soundPool?.play(it, volume, volume, 1, 0, 1f) }
        }
        pressTime = System.currentTimeMillis()

        when(keyCode) {
            KeyEvent.KEYCODE_SHIFT_LEFT, KeyEvent.KEYCODE_SHIFT_RIGHT,
            KeyEvent.KEYCODE_ALT_LEFT, KeyEvent.KEYCODE_ALT_RIGHT -> {
                EventBus.getDefault().post(SoftKeyClickEvent(keyCode, SoftKeyClickEvent.State.DOWN))
            }
        }
    }

    override fun onKeyUp(keyCode: Int, x: Int, y: Int, repeated: Boolean) {
        val timeDiff = System.currentTimeMillis() - pressTime - longClickDelay/5
        val timeRatio = (timeDiff.toFloat() / longClickDelay).let { if(it > 1) 1f else if(it < 0) 0f else it }
        val duration = (timeRatio * vibrateDuration).toLong()
        if(duration > 0) vibrator?.vibrate(duration)

        val volume = timeRatio * soundVolume
        upSound?.let { soundPool?.play(it, volume, volume, 1, 0, 1f) }

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
        vibrator?.vibrate(vibrateDuration.toLong() / 2)
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

    override fun showMoreKeysKeyboard(keyCode: Int, moreKeys: List<Int>) {
        val key = layout.rows.flatMap { row -> row.keys }.filter { key -> key.keyCode == keyCode }.firstOrNull() ?: return
        val keyboardView = keyboardView ?: return
        val list = moreKeys.map { it to (currentLabels[it] ?: it.toString()) }
        if(list.isEmpty()) return
        val popup = BasicMoreKeyPopup(keyboardView.context, key, list, theme.previewBackground, theme.keyTheme[null]?.backgroundPressed!!, theme.keyTheme[null]?.textColor!!)
        keyboardView.showPopup(popup)
    }

    override fun closeMoreKeysKeyboard() {

    }

    override fun setPreferences(pref: SharedPreferences) {
        keyHeight = pref.getInt("common_soft_height", 0).toFloat()
        showLabels = pref.getBoolean("common_soft_labels", true)
        compatibleLabels = pref.getBoolean("common_soft_labels_compatible", true)
        showPopups = pref.getBoolean("common_soft_popups", showPopups)
        marginLeft = pref.getInt("common_soft_margin_horizontal", 0)
        marginRight = marginLeft
        marginBottom = pref.getInt("common_soft_margin_bottom", 0)

        repeatRate = pref.getInt("common_soft_repeat_rate", 0)
        longClickDelay = pref.getInt("common_soft_long_click_delay", 0)

        vibrate = pref.getBoolean("common_soft_vibrate", false)
        vibrateDuration = pref.getInt("common_soft_vibrate_duration", vibrateDuration)

        sound = pref.getBoolean("common_soft_sound", false)
        soundType = BasicKeyboardSound.SOUNDS[pref.getString("common_soft_sound_type", null) ?: ""]
        soundVolume = pref.getInt("common_soft_sound_volume", 0).toFloat() / 100f
        if(soundVolume > 1f) soundVolume = 1f
        if(soundVolume < 0f) soundVolume = 0f

    }

    override fun serialize(): JSONObject {
        return super.serialize().apply {
            put("layout", layout.key)
            put("theme", REVERSE_THEMES[theme])
            put("height", keyHeight.toInt())
            put("labels", showLabels)
            put("popups", showPopups)
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
            return BasicSoftKeyboard(layout, theme)
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
