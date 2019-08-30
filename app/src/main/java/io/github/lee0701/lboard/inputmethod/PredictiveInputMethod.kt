package io.github.lee0701.lboard.inputmethod

import android.view.KeyEvent
import io.github.lee0701.lboard.ComposingText
import io.github.lee0701.lboard.event.InputProcessCompleteEvent
import io.github.lee0701.lboard.event.LBoardKeyEvent
import io.github.lee0701.lboard.hardkeyboard.CommonHardKeyboard
import io.github.lee0701.lboard.hardkeyboard.HardKeyboard
import io.github.lee0701.lboard.prediction.Predictor
import io.github.lee0701.lboard.softkeyboard.SoftKeyboard
import org.greenrobot.eventbus.EventBus
import kotlin.concurrent.timerTask

class PredictiveInputMethod(
        override val info: InputMethodInfo,
        override val softKeyboard: SoftKeyboard,
        override val hardKeyboard: HardKeyboard,
        val predictor: Predictor
): CommonInputMethod() {

    val states: MutableList<KeyInputHistory<String>> = mutableListOf()
    val lastState: KeyInputHistory<String> get() = if(states.isEmpty()) KeyInputHistory(0, composing = "") else states.last()

    private val reverseKeycodeMap = (hardKeyboard as CommonHardKeyboard).layout[0]!!.layout
            .flatMap { entry -> entry.value.normal.map { it to entry.key } + entry.value.shift.map { it to entry.key } }
            .map { it.first to (it.second and 0xff) }
            .filter { it.second in 0 .. 0xf }
            .map { it.first to it.second.toString(16) }
            .toMap()

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
                if(converted.resultChar == null) {
                    if(converted.defaultChar) {
                        reset()
                        EventBus.getDefault().post(InputProcessCompleteEvent(info, event, null, true))
                    }
                } else if(converted.resultChar == 0) {
                    reset()
                } else {
                    states += KeyInputHistory(event.lastKeyCode, (event.lastKeyCode and 0xff).toString(16), shift, alt,
                            lastState.composing + converted.resultChar.toChar().toString())
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

        val candidates = predictor.predict(states.map { it as KeyInputHistory<Any> })

        EventBus.getDefault().post(InputProcessCompleteEvent(info, event,
                ComposingText(newComposingText = candidates.firstOrNull()?.word ?: lastState.composing)))
        return true
    }

    override fun reset() {
        states.clear()
        super.reset()
    }

    fun getSequence(word: String): String {
        return word.map { c -> reverseKeycodeMap[c.toInt()] ?: 0 }.joinToString("")
    }

}
