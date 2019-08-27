package io.github.lee0701.lboard

import android.view.KeyEvent
import io.github.lee0701.lboard.event.InputProcessCompleteEvent
import io.github.lee0701.lboard.event.LBoardKeyEvent
import io.github.lee0701.lboard.hardkeyboard.HardKeyboard
import io.github.lee0701.lboard.softkeyboard.SoftKeyboard
import org.greenrobot.eventbus.EventBus

class WordComposingInputMethod(
        override val methodId: String,
        override val softKeyboard: SoftKeyboard,
        override val hardKeyboard: HardKeyboard
): CommonInputMethod() {

    val states: MutableList<String> = mutableListOf()
    val lastState: String get() = if(states.isEmpty()) "" else states.last()

    override fun onKeyPress(event: LBoardKeyEvent): Boolean {
        if(ignoreNextInput) return true
        when(event.keyCode) {
            KeyEvent.KEYCODE_DEL -> {
                hardKeyboard.reset()
                if(states.size > 0) {
                    states.remove(states.last())
                } else {
                    return false
                }
            }
            KeyEvent.KEYCODE_SPACE -> {
                reset()
                EventBus.getDefault().post(InputProcessCompleteEvent(methodId, event,
                        ComposingText(commitPreviousText = true, textToCommit = " ")))
                return true
            }
            KeyEvent.KEYCODE_ENTER -> {
                return super.onKeyPress(event)
            }
            KeyEvent.KEYCODE_SHIFT_LEFT, KeyEvent.KEYCODE_SHIFT_RIGHT -> {
                return super.onKeyPress(event)
            }
            KeyEvent.KEYCODE_ALT_LEFT, KeyEvent.KEYCODE_ALT_RIGHT -> {
                return super.onKeyPress(event)
            }
            else -> {
                val converted = convert(event.keyCode, shift, alt)
                if(converted.backspace && states.size > 0) states.remove(states.last())
                if(converted.resultChar == null) {
                    if(converted.defaultChar)
                        EventBus.getDefault().post(InputProcessCompleteEvent(methodId, event, null, true))
                } else if(converted.resultChar == 0) {
                    reset()
                } else {
                    states += lastState + converted.resultChar.toChar().toString()
                }
                processStickyKeysOnInput()
                converted.shiftOn?.let { shift = it }
                converted.altOn?.let { alt = it }
            }
        }
        EventBus.getDefault().post(InputProcessCompleteEvent(methodId, event,
                ComposingText(newComposingText = lastState)))
        return true
    }

    override fun reset() {
        states.clear()
        super.reset()
    }

    companion object {

    }

}
