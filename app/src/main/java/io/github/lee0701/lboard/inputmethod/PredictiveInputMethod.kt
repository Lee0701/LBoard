package io.github.lee0701.lboard.inputmethod

import android.view.KeyEvent
import io.github.lee0701.lboard.ComposingText
import io.github.lee0701.lboard.event.*
import io.github.lee0701.lboard.hardkeyboard.CommonHardKeyboard
import io.github.lee0701.lboard.hardkeyboard.HardKeyboard
import io.github.lee0701.lboard.prediction.Candidate
import io.github.lee0701.lboard.prediction.SingleCandidate
import io.github.lee0701.lboard.prediction.Predictor
import io.github.lee0701.lboard.softkeyboard.SoftKeyboard
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import kotlin.concurrent.timerTask

class PredictiveInputMethod(
        override val info: InputMethodInfo,
        override val softKeyboard: SoftKeyboard,
        override val hardKeyboard: HardKeyboard,
        val predictor: Predictor<KeyInputHistory<Any>>
): CommonInputMethod() {

    val states: MutableList<KeyInputHistory<String>> = mutableListOf()
    val lastState: KeyInputHistory<String> get() = states.lastOrNull() ?: KeyInputHistory(0, composing = "")

    var candidates: List<Candidate> = listOf()
    var candidateIndex: Int = -1
    val currentCandidate: Candidate get() = candidates[if(candidateIndex >= 0) candidateIndex else 0]

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
                if(candidates.isNotEmpty() && candidateIndex < 0) {
                    states.clear()
                    candidateIndex = 0
                    val candidate = candidates[candidateIndex]
                    EventBus.getDefault().post(InputProcessCompleteEvent(info, event,
                            ComposingText(newComposingText = candidate.text + if(candidate.endingSpace) " " else "")))
                } else {
                    reset()
                    return super.onKeyPress(event)
                }
                return true
            }
            KeyEvent.KEYCODE_ENTER -> {
                learnCurrentCandidateAndReset()
                return super.onKeyPress(event)
            }
            KeyEvent.KEYCODE_SHIFT_LEFT, KeyEvent.KEYCODE_SHIFT_RIGHT -> {
                return super.onKeyPress(event)
            }
            KeyEvent.KEYCODE_ALT_LEFT, KeyEvent.KEYCODE_ALT_RIGHT -> {
                return super.onKeyPress(event)
            }
            else -> {
                learnCurrentCandidateAndReset()

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
                    states += KeyInputHistory(event.lastKeyCode, shift, alt,
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

        val states = states.filterIsInstance<KeyInputHistory<Any>>()
        candidates = predictor.predict(states, states.size)
                .toList()
                .let { list ->
                    var candidates = listOf<Candidate>()
                    if(list.isEmpty() && states.size > 2) for(i in 1 .. 5) {
                        candidates = predictor.predict(states.filterIsInstance<KeyInputHistory<Any>>(), states.size + i).toList()
                        if(candidates.size >= 3) break
                    } else return@let list
                    candidates
                }
                .sortedByDescending { it.frequency }
                .map {
                    val withMissing = addMissing(states, it.text)
                    val text = it.text.mapIndexed { i, c -> if(withMissing[i].shift) c.toUpperCase() else c }.joinToString("")
                    SingleCandidate(text, it.text, it.pos, states.size, it.frequency)
                } + SingleCandidate(lastState.composing, lastState.composing, states.size, -1, 0.1f)
        candidateIndex = -1

        EventBus.getDefault().post(CandidateUpdateEvent(this.info, candidates))

        val composing =
                if(candidates.isEmpty()) lastState.composing
                else currentCandidate.text.let { it.substring(0, states.size + countMissing(it.substring(0, states.size))) }
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
        candidates = listOf()
        candidateIndex = -1
        EventBus.getDefault().post(CandidateUpdateEvent(this.info, candidates))
    }

    private fun learnCurrentCandidateAndReset() {
        if(candidateIndex >= 0) candidates[candidateIndex].let {
            predictor.learn(it)
            reset()
        }
    }

    private fun countMissing(word: String): Int {
        val layout = (hardKeyboard as CommonHardKeyboard).layout[0]!!.layout.mapValues { it.value.normal + it.value.shift }
        return word.sumBy { c -> if(layout.none { it.value.contains(c.toInt()) }) 1 else 0 }
    }

    private fun addMissing(states: List<KeyInputHistory<Any>>, word: String): List<KeyInputHistory<Any>> {
        val layout = (hardKeyboard as CommonHardKeyboard).layout[0]!!.layout.mapValues { it.value.normal + it.value.shift }
        val result = mutableListOf<KeyInputHistory<Any>>()
        var j = 0
        word.forEachIndexed { i, c ->
            if(i >= states.size || layout.none { it.value.contains(c.toInt()) })
                result += KeyInputHistory<Any>(0, composing = (result.lastOrNull()?.composing.toString() ?: "") + c)
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
