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
        val predictor: Predictor
): CommonInputMethod() {

    val states: MutableList<KeyInputHistory<String>> = mutableListOf()
    val lastState: KeyInputHistory<String> get() = if(states.isEmpty()) KeyInputHistory(0, composing = "") else states.last()

    var candidates: Iterable<Candidate> = listOf()
    var candidateIterator: Iterator<Candidate> = candidates.iterator()
    var currentCandidate: Candidate? = null
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
                reset()
                return super.onKeyPress(event)
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

        candidates = sequence {
            predictor.predict(states.map { it as KeyInputHistory<Any> })
                    .asSequence()
                    .sortedByDescending { it.frequency }
                    .forEach {
                        val withMissing = addMissing(states, it.text)
                        val text = it.text.mapIndexed { i, c -> if(withMissing[i].shift) c.toUpperCase() else c }.joinToString("")
                        yield(SingleCandidate(text, it.text, it.pos, it.frequency))
                    }
            yield(SingleCandidate(lastState.composing, lastState.composing, 0, 0.1f))
        }.asIterable()
        candidateIterator = candidates.iterator()

        EventBus.getDefault().post(CandidateUpdateEvent(this.info, candidates))

        val composing = if(candidateIterator.hasNext()) candidateIterator.next().text else lastState.composing
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
        candidateIterator = candidates.iterator()
        EventBus.getDefault().post(CandidateUpdateEvent(this.info, candidates.asSequence().asIterable()))
    }

    private fun learnCurrentCandidateAndReset() {
        if(candidateIndex >= 0) currentCandidate ?.let {
            predictor.learn(it)
            reset()
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
