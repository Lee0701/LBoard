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

    var candidates: List<String> = listOf()
    var candidateIndex: Int = 0

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
                    candidates = listOf()
                    return false
                }
            }
            KeyEvent.KEYCODE_SPACE -> {
                if(candidates.isNotEmpty()) {
                    states.clear()
                    if(++candidateIndex >= candidates.size) candidateIndex = 0
                    if(candidates.isNotEmpty()) {
                        EventBus.getDefault().post(InputProcessCompleteEvent(info, event,
                                ComposingText(newComposingText = candidates[candidateIndex] + " ")))
                    }
                } else {
                    reset()
                    return super.onKeyPress(event)
                }
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
                if(candidateIndex >= 0) reset()
                val converted = convert(event.lastKeyCode, shift, alt)
                val composing = if(converted.backspace) lastState.composing.substring(0, lastState.composing.length-1) else lastState.composing
                if(converted.resultChar == null) {
                    if(converted.defaultChar) {
                        reset()
                        EventBus.getDefault().post(InputProcessCompleteEvent(info, event, null, true))
                    }
                } else if(converted.resultChar == 0) {
                    reset()
                } else {
                    states += KeyInputHistory(event.lastKeyCode, (event.lastKeyCode and 0xff).toString(16), shift, alt,
                            composing + converted.resultChar.toChar().toString())
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

        candidates = predictor.predict(states.map { it as KeyInputHistory<Any> })
                .sortedByDescending { it.frequency }
                .map { it.word.mapIndexed { i, c -> if(states[i].shift) c.toUpperCase() else c }.joinToString("") } + lastState.composing
        candidateIndex = -1

        val composing = candidates[if(candidateIndex < 0) 0 else candidateIndex]
        EventBus.getDefault().post(InputProcessCompleteEvent(info, event,
                ComposingText(newComposingText = composing)))
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
