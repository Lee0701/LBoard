package io.github.lee0701.lboard.inputmethod

import android.view.KeyEvent
import io.github.lee0701.lboard.ComposingText
import io.github.lee0701.lboard.event.*
import io.github.lee0701.lboard.hardkeyboard.CommonHardKeyboard
import io.github.lee0701.lboard.hardkeyboard.HardKeyboard
import io.github.lee0701.lboard.prediction.Candidate
import io.github.lee0701.lboard.prediction.Predictor
import io.github.lee0701.lboard.softkeyboard.SoftKeyboard
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import kotlin.concurrent.timerTask

class PredictiveInputMethod(
        override val info: InputMethodInfo,
        override val softKeyboard: SoftKeyboard,
        override val hardKeyboard: HardKeyboard,
        val predictor: Predictor
): CommonInputMethod() {

    val states: MutableList<KeyInputHistory<String>> = mutableListOf()
    val lastState: KeyInputHistory<String> get() = if(states.isEmpty()) KeyInputHistory(0, composing = "") else states.last()

    var candidates: List<Candidate> = listOf()
    var candidateIndex: Int = 0

    @Subscribe
    fun onCandidateSelect(event: CandidateSelectEvent) {
        if(!event.methodInfo.match(this.info)) return
        predictor.learn(event.selected)
    }

    @Subscribe
    fun onCandidateLongClick(event: CandidateLongClickEvent) {
        if(!event.methodInfo.match(this.info)) return
        predictor.delete(event.longClicked)
    }

    override fun onKeyPress(event: LBoardKeyEvent): Boolean {
        if(ignoreNextInput) return true
        timeoutTask?.cancel()
        when(event.lastKeyCode) {
            KeyEvent.KEYCODE_DEL -> {
                hardKeyboard.reset()
                if(states.size > 0) {
                    states.remove(states.last())
                } else {
                    resetCandidates()
                    return false
                }
            }
            KeyEvent.KEYCODE_SPACE -> {
                if(candidates.isNotEmpty()) {
                    states.clear()
                    if(++candidateIndex >= candidates.size) candidateIndex = 0
                    if(candidates.isNotEmpty()) {
                        EventBus.getDefault().post(InputProcessCompleteEvent(info, event,
                                ComposingText(newComposingText = candidates[candidateIndex].text + " ")))
                    }
                } else {
                    reset()
                    return super.onKeyPress(event)
                }
                return true
            }
            KeyEvent.KEYCODE_ENTER -> {
                if(candidateIndex >= 0 && candidates.isNotEmpty()) {
                    predictor.learn(candidates[candidateIndex])
                    reset()
                }
                return super.onKeyPress(event)
            }
            KeyEvent.KEYCODE_SHIFT_LEFT, KeyEvent.KEYCODE_SHIFT_RIGHT -> {
                return super.onKeyPress(event)
            }
            KeyEvent.KEYCODE_ALT_LEFT, KeyEvent.KEYCODE_ALT_RIGHT -> {
                return super.onKeyPress(event)
            }
            else -> {
                if(candidateIndex >= 0 && candidates.isNotEmpty()) {
                    predictor.learn(candidates[candidateIndex])
                    reset()
                }
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
                .map {
                    val withMissing = addMissing(states, it.text)
                    val text = it.text.mapIndexed { i, c -> if(withMissing[i].shift) c.toUpperCase() else c }.joinToString("")
                    it.copy(text = text)
                }.let { if(it.none { it.text == lastState.composing }) it + Candidate(0, lastState.composing, "0", 0.1f) else it }
        candidateIndex = -1

        EventBus.getDefault().post(CandidateUpdateEvent(this.info, candidates))

        val composing = candidates[if(candidateIndex < 0) 0 else candidateIndex].text
        EventBus.getDefault().post(InputProcessCompleteEvent(info, event,
                ComposingText(newComposingText = composing)))
        return true
    }

    override fun reset() {
        states.clear()
        resetCandidates()
        super.reset()
    }

    private fun resetCandidates() {
        if(candidates.isNotEmpty()) {
            candidates = listOf()
            EventBus.getDefault().post(CandidateUpdateEvent(this.info, candidates))
        }
    }

    private fun addMissing(states: List<KeyInputHistory<String>>, word: String): List<KeyInputHistory<String>> {
        val layout = (hardKeyboard as CommonHardKeyboard).layout[0]!!.layout.mapValues { it.value.normal + it.value.shift }
        val result = mutableListOf<KeyInputHistory<String>>()
        var j = 0
        word.forEachIndexed { i, c ->
            if(layout.none { it.value.contains(c.toInt()) })
                result += KeyInputHistory(0, composing = (result.lastOrNull()?.composing ?: "") + c)
            else
                result += states[j++]
        }
        return result
    }

    override fun init() {
        predictor.init()
        super.init()
    }

    override fun destroy() {
        predictor.destroy()
        super.destroy()
    }
}
