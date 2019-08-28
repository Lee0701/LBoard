package io.github.lee0701.lboard.inputmethod

import android.view.KeyEvent
import io.github.lee0701.lboard.ComposingText
import io.github.lee0701.lboard.event.InputProcessCompleteEvent
import io.github.lee0701.lboard.event.LBoardKeyEvent
import io.github.lee0701.lboard.hardkeyboard.CommonHardKeyboard
import io.github.lee0701.lboard.hardkeyboard.HardKeyboard
import io.github.lee0701.lboard.softkeyboard.SoftKeyboard
import org.greenrobot.eventbus.EventBus
import kotlin.concurrent.timerTask

class WordComposingInputMethod(
        override val info: InputMethodInfo,
        override val softKeyboard: SoftKeyboard,
        override val hardKeyboard: HardKeyboard
): CommonInputMethod() {

    val states: MutableList<String> = mutableListOf()
    val lastState: String get() = if(states.isEmpty()) "" else states.last()

    override fun onKeyPress(event: LBoardKeyEvent): Boolean {
        if(ignoreNextInput) return true
        timeoutTask?.cancel()
        when(event.lastKeyCode) {
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
                return super.onKeyPress(event)
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
                val converted = convert(event.lastKeyCode, shift, alt)
                if(converted.backspace && states.size > 0) states.remove(states.last())
                if(converted.resultChar == null) {
                    if(converted.defaultChar) {
                        reset()
                        EventBus.getDefault().post(InputProcessCompleteEvent(info, event, null, true))
                    }
                } else if(converted.resultChar == 0) {
                    reset()
                } else {
                    states += lastState + converted.resultChar.toChar().toString()
                }
                processStickyKeysOnInput()
                converted.shiftOn?.let { shift = it }
                converted.altOn?.let { alt = it }

                if(hardKeyboard is CommonHardKeyboard && hardKeyboard.layout.timeout && timeout > 0) {
                    timeoutTask = timerTask {
                        hardKeyboard.reset()
                    }
                    timer.schedule(timeoutTask, timeout.toLong())
                }
            }
        }
        EventBus.getDefault().post(InputProcessCompleteEvent(info, event,
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
