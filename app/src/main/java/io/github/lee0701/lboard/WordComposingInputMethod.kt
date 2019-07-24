package io.github.lee0701.lboard

import android.content.Context
import android.view.KeyCharacterMap
import android.view.KeyEvent
import android.view.View
import io.github.lee0701.lboard.event.CommitComposingEvent
import io.github.lee0701.lboard.event.CommitStringEvent
import io.github.lee0701.lboard.event.ComposeEvent
import io.github.lee0701.lboard.event.UpdateViewEvent
import io.github.lee0701.lboard.hardkeyboard.HardKeyboard
import io.github.lee0701.lboard.softkeyboard.SoftKeyboard
import org.greenrobot.eventbus.EventBus
import org.json.JSONObject

class WordComposingInputMethod(
        override val softKeyboard: SoftKeyboard,
        override val hardKeyboard: HardKeyboard
): CommonInputMethod() {

    val states: MutableList<String> = mutableListOf()
    val lastState: String get() = if(states.isEmpty()) "" else states.last()

    override fun initView(context: Context): View? {
        return softKeyboard.initView(context)
    }

    override fun onKeyPress(keyCode: Int): Boolean {
        if(isSystemKey(keyCode)) return false
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
                val converted = hardKeyboard.convert(keyCode, shift, alt)
                if(converted.backspace && states.size > 0) states.remove(states.last())
                if(converted.resultChar == null) {
                    reset()
                    if(converted.defaultChar)
                        EventBus.getDefault().post(CommitStringEvent(KeyCharacterMap.load(KeyCharacterMap.FULL)
                                .get(keyCode, 0).toChar().toString()))
                } else if(converted.resultChar == 0) {
                    reset()
                } else {
                    states += lastState + converted.resultChar.toChar().toString()
                }
                processStickyKeysOnInput(converted.resultChar ?: 0)
                converted.shift?.let { shift = it }
                converted.alt?.let { alt = it }
            }
        }
        EventBus.getDefault().post(ComposeEvent(lastState))
        EventBus.getDefault().post(UpdateViewEvent())
        return true
    }

    override fun reset() {
        EventBus.getDefault().post(CommitComposingEvent())
        states.clear()
        super.reset()
    }

    companion object {
        @JvmStatic fun deserialize(json: JSONObject): WordComposingInputMethod? {
            val softKeyboard = InputMethod.deserializeModule(json.getJSONObject("soft-keyboard")) as SoftKeyboard
            val hardKeyboard = InputMethod.deserializeModule(json.getJSONObject("hard-keyboard")) as HardKeyboard
            return WordComposingInputMethod(softKeyboard, hardKeyboard)
        }
    }

}
