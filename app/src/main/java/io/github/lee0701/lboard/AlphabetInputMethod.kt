package io.github.lee0701.lboard

import android.content.Context
import android.view.KeyCharacterMap
import android.view.KeyEvent
import android.view.View
import io.github.lee0701.lboard.event.CommitStringEvent
import io.github.lee0701.lboard.event.UpdateViewEvent
import io.github.lee0701.lboard.hardkeyboard.HardKeyboard
import io.github.lee0701.lboard.softkeyboard.SoftKeyboard
import org.greenrobot.eventbus.EventBus

class AlphabetInputMethod(
        override val softKeyboard: SoftKeyboard,
        override val hardKeyboard: HardKeyboard
): CommonInputMethod() {

    override fun initView(context: Context): View? {
        return softKeyboard.initView(context)
    }

    override fun updateView(context: Context): View? {
        softKeyboard.setLabels(hardKeyboard.getLabels(shift, alt))
        return null
    }

    override fun onKeyPress(keyCode: Int): Boolean {
        when(keyCode) {
            KeyEvent.KEYCODE_DEL -> {
                return super.onKeyPress(keyCode)
            }
            KeyEvent.KEYCODE_SPACE -> {
                return super.onKeyPress(keyCode)
            }
            KeyEvent.KEYCODE_ENTER -> {
                return super.onKeyPress(keyCode)
            }
            KeyEvent.KEYCODE_SHIFT_LEFT, KeyEvent.KEYCODE_SHIFT_RIGHT -> {
                return super.onKeyPress(keyCode)
            }
            KeyEvent.KEYCODE_ALT_LEFT, KeyEvent.KEYCODE_ALT_RIGHT -> {
                return super.onKeyPress(keyCode)
            }
            else -> {
                return super.onKeyPress(keyCode)
            }
        }
    }

    override fun onKeyRelease(keyCode: Int): Boolean {
        when(keyCode) {
            KeyEvent.KEYCODE_SHIFT_LEFT, KeyEvent.KEYCODE_SHIFT_RIGHT -> {
                if(shift && !capsLock) shift = !inputOnShift
            }
            KeyEvent.KEYCODE_ALT_LEFT, KeyEvent.KEYCODE_ALT_RIGHT -> {
                if(alt && !altLock) alt = !inputOnAlt
            }
        }
        return true
    }

    override fun reset() {
        super.reset()
    }
}
