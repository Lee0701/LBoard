package io.github.lee0701.lboard.softkeyboard

import android.app.Service
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import android.os.Build
import android.os.Vibrator
import android.support.v4.content.ContextCompat
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.LinearLayout
import io.github.lee0701.lboard.R
import io.github.lee0701.lboard.event.LBoardKeyEvent
import io.github.lee0701.lboard.event.MoreKeySelectEvent
import io.github.lee0701.lboard.event.KeyPressEvent
import io.github.lee0701.lboard.event.OneHandedModeUpdateEvent
import io.github.lee0701.lboard.hangul.HangulComposer
import io.github.lee0701.lboard.layouts.soft.*
import io.github.lee0701.lboard.softkeyboard.themes.BasicSoftKeyboardTheme
import org.greenrobot.eventbus.EventBus
import org.json.JSONObject

class BasicSoftKeyboard(
        val layout: Layout,
        val theme: KeyboardTheme
): MoreKeysSupportedSoftKeyboard, BasicKeyboardView.OnKeyListener {

    var keyboardViewHolder: ViewGroup? = null

    var keyboardView: BasicKeyboardView? = null
    var oneHandedButtonsHolder: View? = null
    var flipButton: ImageButton? = null

    lateinit var leftDrawable: Drawable
    lateinit var rightDrawable: Drawable

    var currentLabels: Map<Int, String> = mapOf()

    private val displayMetrics = DisplayMetrics()

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
    private var compatibleLabels: Boolean = true

    private var showPopups: Boolean = false
    private var repeatRate: Int = 0
    private var longClickDelay: Int = 0

    private var marginLeft: Int = 0
    private var marginRight: Int = 0
    private var marginBottom: Int = 0

    private var oneHandedMode: Int = 0
    private var oneHandedMargin: Int = 0

    override fun initView(context: Context): View? {
        (context.getSystemService(Service.WINDOW_SERVICE) as WindowManager).defaultDisplay.getMetrics(displayMetrics)

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
        val keyboardWidth = displayMetrics.widthPixels - if(oneHandedMode != 0) oneHandedMargin else 0

        keyboardView = BasicKeyboardView(context, layout, theme, this,
                keyboardWidth, keyboardHeight.toInt(), showLabels, showPopups, repeatRate, longClickDelay,
                marginLeft.toInt(), marginRight.toInt(), marginBottom.toInt())

        val keyboardViewHolder = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
        }
        this.keyboardViewHolder = keyboardViewHolder

        leftDrawable = ContextCompat.getDrawable(context, R.drawable.ic_chevron_left_black_24dp)!!
        rightDrawable = ContextCompat.getDrawable(context, R.drawable.ic_chevron_right_black_24dp)!!

        val buttonsHolder = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = ViewGroup.LayoutParams(oneHandedMargin, ViewGroup.LayoutParams.MATCH_PARENT)
            background = ContextCompat.getDrawable(context, theme.background)

            addView(ImageButton(context).apply {
                layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                    weight = 1f
                }
                setBackgroundColor(Color.TRANSPARENT)
                setOnClickListener {
                    EventBus.getDefault().post(OneHandedModeUpdateEvent(-oneHandedMode))
                }
                flipButton = this
            })
            addView(ImageButton(context).apply {
                layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                setBackgroundColor(Color.TRANSPARENT)
                setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_zoom_out_map_black_24dp))
                setOnClickListener {
                    EventBus.getDefault().post(OneHandedModeUpdateEvent(0))
                }
            })
        }
        this.oneHandedButtonsHolder = buttonsHolder

        updateOneHandedMode(oneHandedMode)

        return this.keyboardViewHolder
    }

    override fun getView(): View? {
        return keyboardViewHolder
    }

    override fun reset() {
        keyboardView?.reset()
    }

    override fun updateLabels(labels: Map<Int, String>) {
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

    private fun validatePopupShown(keyCode: Int): Boolean {
        return keyCode in 7 .. 19 || keyCode in 29 .. 56 || keyCode in 68 .. 78
    }

    override fun updateOneHandedMode(oneHandedMode: Int) {
        this.oneHandedMode = oneHandedMode

        keyboardView?.let {
            it.keyboardWidth = displayMetrics.widthPixels - if(oneHandedMode != 0) oneHandedMargin else 0
            it.invalidateAllKeys()
            it.invalidate()
        }

        keyboardViewHolder?.let { holder ->
            holder.removeAllViews()
            if(oneHandedMode > 0) holder.addView(oneHandedButtonsHolder)
            holder.addView(keyboardView)
            if(oneHandedMode < 0) holder.addView(oneHandedButtonsHolder)
        }

        flipButton?.setImageDrawable(if(oneHandedMode < 0) rightDrawable else leftDrawable)
    }

    override fun onKeyDown(keyCode: Int, x: Int, y: Int) {
        val keyboardView = keyboardView
        if(showPopups && validatePopupShown(keyCode) && keyboardView != null) {
            val key = layout.rows.flatMap { row -> row.keys }.filter { key -> key.keyCode == keyCode }.firstOrNull() ?: return
            val popup = BasicKeyPreviewPopup(keyboardView.context, if(oneHandedMode > 0) oneHandedMargin else 0, key, theme.previewBackground, theme.keyTheme[null]?.textColor ?: Color.BLACK)
            keyboardView.showPopup(popup)
        }

        vibrator?.vibrate(vibrateDuration.toLong())
        val volume = soundVolume

        (if(keyCode == KeyEvent.KEYCODE_SPACE && upSound != null) upSound else downSound)?.let { soundPool?.play(it, volume, volume, 1, 0, 1f) }
        pressTime = System.currentTimeMillis()

        EventBus.getDefault().post(KeyPressEvent(keyCode, shift > 0, alt > 0, LBoardKeyEvent.Source.VIRTUAL_KEYBOARD, LBoardKeyEvent.ActionType.PRESS))
    }

    override fun onKeyUp(keyCode: Int, x: Int, y: Int) {
        val timeDiff = System.currentTimeMillis() - pressTime - longClickDelay/5
        val timeRatio = (timeDiff.toFloat() / longClickDelay).let { if(it > 1) 1f else if(it < 0) 0f else it }
        val duration = (timeRatio * vibrateDuration).toLong()
        if(duration > 0) vibrator?.vibrate(duration)

        val volume = timeRatio * soundVolume
        upSound?.let { soundPool?.play(it, volume, volume, 1, 0, 1f) }

        EventBus.getDefault().post(KeyPressEvent(keyCode, shift > 0, alt > 0, LBoardKeyEvent.Source.VIRTUAL_KEYBOARD, LBoardKeyEvent.ActionType.RELEASE))
    }

    override fun onKeyLongClick(keyCode: Int) {
        vibrator?.vibrate(vibrateDuration.toLong() / 2)
        EventBus.getDefault().post(KeyPressEvent(keyCode, shift > 0, alt > 0, LBoardKeyEvent.Source.VIRTUAL_KEYBOARD, LBoardKeyEvent.ActionType.LONG_PRESS))
    }

    override fun onKeyRepeat(keyCode: Int) {
        EventBus.getDefault().post(KeyPressEvent(keyCode, shift > 0, alt > 0, LBoardKeyEvent.Source.VIRTUAL_KEYBOARD, LBoardKeyEvent.ActionType.REPEAT))
    }

    override fun onMoreKeySelect(originalKeyCode: Int, keyCode: Int) {
        EventBus.getDefault().post(MoreKeySelectEvent(originalKeyCode, keyCode))
    }

    override fun onKeyFlickLeft(keyCode: Int) {
        EventBus.getDefault().post(KeyPressEvent(keyCode, shift > 0, alt > 0, LBoardKeyEvent.Source.VIRTUAL_KEYBOARD, LBoardKeyEvent.ActionType.FLICK_LEFT))
    }

    override fun onKeyFlickRight(keyCode: Int) {
        EventBus.getDefault().post(KeyPressEvent(keyCode, shift > 0, alt > 0, LBoardKeyEvent.Source.VIRTUAL_KEYBOARD, LBoardKeyEvent.ActionType.FLICK_RIGHT))
    }

    override fun onKeyFlickUp(keyCode: Int) {
        EventBus.getDefault().post(KeyPressEvent(keyCode, shift > 0, alt > 0, LBoardKeyEvent.Source.VIRTUAL_KEYBOARD, LBoardKeyEvent.ActionType.FLICK_UP))
    }

    override fun onKeyFlickDown(keyCode: Int) {
        EventBus.getDefault().post(KeyPressEvent(keyCode, shift > 0, alt > 0, LBoardKeyEvent.Source.VIRTUAL_KEYBOARD, LBoardKeyEvent.ActionType.FLICK_DOWN))
    }

    override fun showMoreKeysKeyboard(keyCode: Int, moreKeys: List<Int>) {
        val key = layout.rows.flatMap { row -> row.keys }.filter { key -> key.keyCode == keyCode }.firstOrNull() ?: return
        val keyboardView = keyboardView ?: return
        val list = moreKeys.map { it to (currentLabels[it] ?: it.toString()) }
        if(list.isEmpty()) return
        val popup = BasicMoreKeyPopup(keyboardView.context, if(oneHandedMode > 0) oneHandedMargin else 0, key, list, theme)
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

        oneHandedMode = pref.getInt("common_soft_one_handed_mode", 0)
        oneHandedMargin = pref.getInt("common_soft_one_handed_margin", 0)

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
                MobileSoftLayout.LAYOUT_12KEY_4COLS,
                MobileSoftLayout.LAYOUT_15KEY_A,
                MobileSoftLayout.LAYOUT_15KEY_B
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
