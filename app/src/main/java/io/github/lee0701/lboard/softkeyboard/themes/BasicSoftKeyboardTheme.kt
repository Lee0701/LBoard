package io.github.lee0701.lboard.softkeyboard.themes

import android.graphics.Color
import android.view.KeyEvent
import io.github.lee0701.lboard.R
import io.github.lee0701.lboard.softkeyboard.KeyTheme
import io.github.lee0701.lboard.softkeyboard.KeyboardTheme
import io.github.lee0701.lboard.softkeyboard.RowTheme

object BasicSoftKeyboardTheme {

    private val WHITE_KEY_CHARACTER = KeyTheme(
            R.drawable.keybg_white,
            R.drawable.keybg_white_p,
            Color.parseColor("#dd000000"),
            preview = R.drawable.keybg_white_preview
    )

    private val WHITE_KEY_MOD = KeyTheme(
            R.drawable.keybg_white_mod,
            R.drawable.keybg_white_mod_p,
            Color.parseColor("#dd000000")
    )

    private val WHITE_KEY_STICKY = KeyTheme(
            R.drawable.keybg_white_sticky,
            R.drawable.keybg_white_sticky_p,
            Color.parseColor("#dd000000")
    )

    private val WHITE_KEY_STICKY_LOCKED = KeyTheme(
            R.drawable.keybg_white_locked,
            R.drawable.keybg_white_locked_p,
            Color.parseColor("#dd000000")
    )

    private val WHITE_KEY_SPACE = WHITE_KEY_CHARACTER.copy(foreground = R.drawable.keyfg_space_black, preview = null)
    private val WHITE_KEY_SHIFT = WHITE_KEY_MOD.copy(foreground = R.drawable.keyfg_shift_black)
    private val WHITE_KEY_DEL = WHITE_KEY_MOD.copy(foreground = R.drawable.keyfg_del_black)
    private val WHITE_KEY_LANG = WHITE_KEY_MOD.copy(foreground = R.drawable.keyfg_lang_black)

    val WHITE = KeyboardTheme(
            R.drawable.keybg_white_bg,
            mapOf(
                    null to RowTheme()
            ),
            mapOf(
                    null to WHITE_KEY_CHARACTER,
                    KeyEvent.KEYCODE_SPACE to WHITE_KEY_SPACE,
                    KeyEvent.KEYCODE_ENTER to KeyTheme(
                            R.drawable.keybg_white_enter,
                            R.drawable.keybg_white_mod_p,
                            Color.parseColor("#dd000000"),
                            R.drawable.keyfg_enter_white
                    ),
                    KeyEvent.KEYCODE_SHIFT_LEFT to WHITE_KEY_SHIFT,
                    KeyEvent.KEYCODE_SHIFT_RIGHT to WHITE_KEY_SHIFT,
                    KeyEvent.KEYCODE_DEL to WHITE_KEY_DEL,
                    KeyEvent.KEYCODE_SYM to WHITE_KEY_MOD,
                    KeyEvent.KEYCODE_ALT_LEFT to WHITE_KEY_MOD,
                    KeyEvent.KEYCODE_ALT_RIGHT to WHITE_KEY_MOD,
                    KeyEvent.KEYCODE_LANGUAGE_SWITCH to WHITE_KEY_LANG
            ),
            WHITE_KEY_STICKY,
            WHITE_KEY_STICKY_LOCKED
    )

    private val DARK_KEY_CHARACTER = KeyTheme(
            R.drawable.keybg_dark,
            R.drawable.keybg_dark_p,
            Color.WHITE,
            preview = R.drawable.keybg_dark_preview
    )

    private val DARK_KEY_MOD = KeyTheme(
            R.drawable.keybg_dark_mod,
            R.drawable.keybg_dark_p,
            Color.WHITE
    )

    private val DARK_KEY_STICKY = KeyTheme(
            R.drawable.keybg_dark_sticky,
            R.drawable.keybg_dark_sticky_p,
            Color.WHITE
    )

    private val DARK_KEY_STICKY_LOCKED = KeyTheme(
            R.drawable.keybg_dark_locked,
            R.drawable.keybg_dark_locked_p,
            Color.WHITE
    )

    private val DARK_KEY_SPACE = DARK_KEY_CHARACTER.copy(foreground = R.drawable.keyfg_space_white, preview = null)
    private val DARK_KEY_SHIFT = DARK_KEY_MOD.copy(foreground = R.drawable.keyfg_shift_white)
    private val DARK_KEY_DEL = DARK_KEY_MOD.copy(foreground = R.drawable.keyfg_del_white)
    private val DARK_KEY_LANG = DARK_KEY_MOD.copy(foreground = R.drawable.keyfg_lang_white)

    val DARK = KeyboardTheme(
            R.drawable.keybg_dark_bg,
            mapOf(
                    null to RowTheme()
            ),
            mapOf(
                    null to DARK_KEY_CHARACTER,
                    KeyEvent.KEYCODE_SPACE to DARK_KEY_SPACE,
                    KeyEvent.KEYCODE_ENTER to KeyTheme(
                            R.drawable.keybg_dark_enter,
                            R.drawable.keybg_dark_enter_p,
                            Color.WHITE,
                            R.drawable.keyfg_enter_white
                    ),
                    KeyEvent.KEYCODE_SHIFT_LEFT to DARK_KEY_SHIFT,
                    KeyEvent.KEYCODE_SHIFT_RIGHT to DARK_KEY_SHIFT,
                    KeyEvent.KEYCODE_DEL to DARK_KEY_DEL,
                    KeyEvent.KEYCODE_SYM to DARK_KEY_MOD,
                    KeyEvent.KEYCODE_ALT_LEFT to DARK_KEY_MOD,
                    KeyEvent.KEYCODE_ALT_RIGHT to DARK_KEY_MOD,
                    KeyEvent.KEYCODE_LANGUAGE_SWITCH to DARK_KEY_LANG
            ),
            DARK_KEY_STICKY,
            DARK_KEY_STICKY_LOCKED
    )

    private val FLATWHITE_KEY_CHARACTER = KeyTheme(
            R.drawable.keybg_flatwhite,
            R.drawable.keybg_flatwhite_p,
            Color.parseColor("#ff000000"),
            preview = R.drawable.keybg_flatwhite_preview
    )

    private val FLATWHITE_KEY_STICKY = KeyTheme(
            R.drawable.keybg_flatwhite_sticky,
            R.drawable.keybg_flatwhite_sticky_p,
            Color.parseColor("#ff000000")
    )

    private val FLATWHITE_KEY_STICKY_LOCKED = KeyTheme(
            R.drawable.keybg_flatwhite_locked,
            R.drawable.keybg_flatwhite_locked_p,
            Color.parseColor("#ff000000")
    )

    private val FLATWHITE_KEY_SPACE = KeyTheme(
            R.drawable.keybg_flatwhite_space,
            R.drawable.keybg_flatwhite_space_p,
            Color.parseColor("#ff000000")
    )

    private val FLATWHITE_KEY_SHIFT = FLATWHITE_KEY_CHARACTER.copy(foreground = R.drawable.keyfg_shift_black, preview = null)
    private val FLATWHITE_KEY_DEL = FLATWHITE_KEY_CHARACTER.copy(foreground = R.drawable.keyfg_del_black, preview = null)
    private val FLATWHITE_KEY_LANG = FLATWHITE_KEY_CHARACTER.copy(foreground = R.drawable.keyfg_lang_black, preview = null)

    val FLATWHITE = KeyboardTheme(
            R.drawable.keybg_flatwhite_bg,
            mapOf(
                    null to RowTheme()
            ),
            mapOf(
                    null to FLATWHITE_KEY_CHARACTER,
                    KeyEvent.KEYCODE_SPACE to FLATWHITE_KEY_SPACE,
                    KeyEvent.KEYCODE_ENTER to KeyTheme(
                            R.drawable.keybg_flatwhite_enter,
                            R.drawable.keybg_flatwhite_enter_p,
                            Color.parseColor("#ff000000"),
                            R.drawable.keyfg_enter_white
                    ),
                    KeyEvent.KEYCODE_SHIFT_LEFT to FLATWHITE_KEY_SHIFT,
                    KeyEvent.KEYCODE_SHIFT_RIGHT to FLATWHITE_KEY_SHIFT,
                    KeyEvent.KEYCODE_DEL to FLATWHITE_KEY_DEL,
                    KeyEvent.KEYCODE_LANGUAGE_SWITCH to FLATWHITE_KEY_LANG
            ),
            FLATWHITE_KEY_STICKY,
            FLATWHITE_KEY_STICKY_LOCKED
    )

    private val FLATDARK_KEY_CHARACTER = KeyTheme(
            R.drawable.keybg_flatdark,
            R.drawable.keybg_flatdark_p,
            Color.parseColor("#ffffffff"),
            preview = R.drawable.keybg_flatdark_preview
    )

    private val FLATDARK_KEY_STICKY = KeyTheme(
            R.drawable.keybg_flatdark_sticky,
            R.drawable.keybg_flatdark_sticky_p,
            Color.parseColor("#ffffffff")
    )

    private val FLATDARK_KEY_STICKY_LOCKED = KeyTheme(
            R.drawable.keybg_flatdark_locked,
            R.drawable.keybg_flatdark_locked_p,
            Color.parseColor("#ffffffff")
    )

    private val FLATDARK_KEY_SPACE = KeyTheme(
            R.drawable.keybg_flatdark_space,
            R.drawable.keybg_flatdark_space_p,
            Color.parseColor("#ffffffff")
    )

    private val FLATDARK_KEY_SHIFT = FLATDARK_KEY_CHARACTER.copy(foreground = R.drawable.keyfg_shift_white, preview = null)
    private val FLATDARK_KEY_DEL = FLATDARK_KEY_CHARACTER.copy(foreground = R.drawable.keyfg_del_white, preview = null)
    private val FLATDARK_KEY_LANG = FLATDARK_KEY_CHARACTER.copy(foreground = R.drawable.keyfg_lang_white, preview = null)

    val FLATDARK = KeyboardTheme(
            R.drawable.keybg_flatdark_bg,
            mapOf(
                    null to RowTheme()
            ),
            mapOf(
                    null to FLATDARK_KEY_CHARACTER,
                    KeyEvent.KEYCODE_SPACE to FLATDARK_KEY_SPACE,
                    KeyEvent.KEYCODE_ENTER to KeyTheme(
                            R.drawable.keybg_flatdark_enter,
                            R.drawable.keybg_flatdark_enter_p,
                            Color.parseColor("#ffffffff"),
                            R.drawable.keyfg_enter_white
                    ),
                    KeyEvent.KEYCODE_SHIFT_LEFT to FLATDARK_KEY_SHIFT,
                    KeyEvent.KEYCODE_SHIFT_RIGHT to FLATDARK_KEY_SHIFT,
                    KeyEvent.KEYCODE_DEL to FLATDARK_KEY_DEL,
                    KeyEvent.KEYCODE_LANGUAGE_SWITCH to FLATDARK_KEY_LANG
            ),
            FLATDARK_KEY_STICKY,
            FLATDARK_KEY_STICKY_LOCKED
    )

}
