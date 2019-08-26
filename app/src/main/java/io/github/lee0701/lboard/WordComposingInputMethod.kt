package io.github.lee0701.lboard

import android.content.Context
import android.view.KeyEvent
import android.view.View
import io.github.lee0701.lboard.old_event.*
import io.github.lee0701.lboard.hardkeyboard.HardKeyboard
import io.github.lee0701.lboard.softkeyboard.SoftKeyboard
import org.greenrobot.eventbus.EventBus
import org.json.JSONObject

class WordComposingInputMethod(
        override val methodId: String,
        override val softKeyboard: SoftKeyboard,
        override val hardKeyboard: HardKeyboard
): CommonInputMethod() {

    val states: MutableList<String> = mutableListOf()
    val lastState: String get() = if(states.isEmpty()) "" else states.last()

    override fun onKeyPress(keyCode: Int): Boolean {
        if(isSystemKey(keyCode)) return false
        if(ignoreNextInput) return true
        when(keyCode) {
            KeyEvent.KEYCODE_DEL -> {
                hardKeyboard.reset()
                if(states.size > 0) {
                    states.remove(states.last())
                } else {
                    return false
                }
            }
            KeyEvent.KEYCODE_SPACE -> {
                EventBus.getDefault().post(CommitComposingEvent())
                states.clear()
                hardKeyboard.reset()
                EventBus.getDefault().post(UpdateViewEvent())
                EventBus.getDefault().post(SetSymbolModeEvent(false))
                EventBus.getDefault().post(CommitStringEvent(" "))
                return true
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
                val converted = convert(keyCode, shift, alt)
                if(converted.backspace && states.size > 0) states.remove(states.last())
                if(converted.resultChar == null) {
                    if(converted.defaultChar) return false
                } else if(converted.resultChar == 0) {
                    reset()
                } else {
                    states += lastState + converted.resultChar.toChar().toString()
                }
                processStickyKeysOnInput(converted.resultChar ?: 0)
                converted.shiftOn?.let { shift = it }
                converted.altOn?.let { alt = it }
            }
        }
        EventBus.getDefault().post(ComposeEvent(lastState))
        EventBus.getDefault().post(UpdateViewEvent())
        return true
    }

    override fun reset() {
        states.clear()
        super.reset()
    }

    companion object {

    }

}
