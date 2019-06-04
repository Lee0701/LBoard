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
            Color.parseColor("#dd000000")
    )

    private val WHITE_KEY_MOD = KeyTheme(
            R.drawable.keybg_white_mod,
            R.drawable.keybg_white_mod_p,
            Color.parseColor("#dd000000")
    )

    private val WHITE_KEY_SPACE = WHITE_KEY_CHARACTER.copy(foreground = R.drawable.keyfg_space_black)
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
                    KeyEvent.KEYCODE_LANGUAGE_SWITCH to WHITE_KEY_LANG
            )
    )

    private val DARK_KEY_CHARACTER = KeyTheme(
            R.drawable.keybg_dark,
            R.drawable.keybg_dark_p,
            Color.WHITE
    )

    private val DARK_KEY_MOD = KeyTheme(
            R.drawable.keybg_dark_mod,
            R.drawable.keybg_dark_p,
            Color.WHITE
    )

    private val DARK_KEY_SPACE = DARK_KEY_CHARACTER.copy(foreground = R.drawable.keyfg_space_white)
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
                    KeyEvent.KEYCODE_LANGUAGE_SWITCH to DARK_KEY_LANG
            )
    )

}
