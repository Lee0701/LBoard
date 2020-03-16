package io.github.lee0701.lboard.inputmethod

import android.view.KeyEvent
import io.github.lee0701.lboard.ComposingText

import io.github.lee0701.lboard.event.*
import io.github.lee0701.lboard.hangul.DubeolHangulComposer
import io.github.lee0701.lboard.hangul.HangulComposer
import io.github.lee0701.lboard.hardkeyboard.HardKeyboard
import io.github.lee0701.lboard.hardkeyboard.CommonHardKeyboard
import io.github.lee0701.lboard.inputmethod.ambiguous.Scorer
import io.github.lee0701.lboard.prediction.Candidate
import io.github.lee0701.lboard.prediction.CompoundCandidate
import io.github.lee0701.lboard.prediction.Predictor
import io.github.lee0701.lboard.prediction.SingleCandidate
import io.github.lee0701.lboard.softkeyboard.SoftKeyboard
import kotlinx.coroutines.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.json.JSONObject
import kotlin.math.min
import kotlin.math.sqrt

class AmbiguousHangulInputMethod(
        override val info: InputMethodInfo,
        override val softKeyboard: SoftKeyboard,
        override val hardKeyboard: HardKeyboard,
        val hangulConverter: HangulComposer,
        val conversionScorer: Scorer,
        val predictor: Predictor<Char>
): CommonInputMethod() {

    val states: MutableList<KeyInputHistory<HangulComposer.State>> = mutableListOf()
    var convertTask: Job? = null

    var candidates: List<Candidate> = listOf()
    var candidateIndex: Int = -1

    @Subscribe
    override fun onPreferenceChange(event: PreferenceChangeEvent) {
        super.onPreferenceChange(event)
        hangulConverter.setPreferences(event.preferences)
        timeout = event.preferences.getInt("method_ko_timeout", 0)
    }

    @Subscribe
    fun onCandidateSelect(event: CandidateSelectEvent) {
        if(!event.methodInfo.match(this.info)) return
        learn(event.selected)
    }

    @Subscribe
    fun onCandidateLongClick(event: CandidateLongClickEvent) {
        if(!event.methodInfo.match(this.info)) return
        delete(event.longClicked)
    }

    override fun onKeyPress(event: LBoardKeyEvent): Boolean {
        if(ignoreNextInput) return true
        timeoutTask?.cancel()
        when(event.lastKeyCode) {
            KeyEvent.KEYCODE_DEL -> {
                hardKeyboard.reset()
                if(states.size > 0 && candidateIndex < 0) {
                    states.removeAt(states.size-1)
                } else {
                    resetCandidates()
                    return false
                }
            }
            KeyEvent.KEYCODE_SPACE -> {
                if(candidates.isNotEmpty() && candidateIndex < 0) {
                    runBlocking { convertTask?.join() }
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
                if(candidateIndex >= 0 && candidates.isNotEmpty()) {
                    val candidate = candidates[candidateIndex]
                    EventBus.getDefault().post(InputProcessCompleteEvent(info, event,
                            ComposingText(newComposingText = candidate.text + if (candidate.endingSpace) " " else "")))
                }
                learnCurrentCandidateAndReset()

                val converted = hardKeyboard.convert(event.lastKeyCode, shift, alt)
                if(converted.resultChar != null) {
                    if(isHangul(converted.resultChar)) {
                        states += KeyInputHistory(event.lastKeyCode, shift, alt,
                                hangulConverter.compose(states.lastOrNull()?.composing ?: HangulComposer.State(), converted.resultChar))
                    } else {
                        states.clear()
                        resetCandidates()
                        if(converted.backspace) EventBus.getDefault().post(InputProcessCompleteEvent(event.methodInfo,
                                LBoardKeyEvent(event.methodInfo, event.originalKeyCode, LBoardKeyEvent.Source.INTERNAL,
                                        event.actions + LBoardKeyEvent.Action(LBoardKeyEvent.ActionType.PRESS,
                                                KeyEvent.KEYCODE_DEL, System.currentTimeMillis())), sendRawInput = true))
                        EventBus.getDefault().post(InputProcessCompleteEvent(info, event,
                                ComposingText(commitPreviousText = true, textToCommit = converted.resultChar.toChar().toString())))
                    }
                }

                processStickyKeysOnInput()
            }
        }

        convertTask?.cancel()
        convertTask = GlobalScope.launch {
            candidates = convertAll().toList()
                    .sortedByDescending { it.score }
                    .sortedBy { candidate -> if(candidate is CompoundCandidate) candidate.candidates.size else Int.MAX_VALUE }
            candidateIndex = -1

            EventBus.getDefault().post(CandidateUpdateEvent(this@AmbiguousHangulInputMethod.info, candidates))
            if(candidates.isNotEmpty()) {
                launch(Dispatchers.Main) {
                    EventBus.getDefault().post(InputProcessCompleteEvent(info, event,
                            ComposingText(newComposingText = candidates[if(candidateIndex < 0) 0 else candidateIndex].text)))
                }
            } else {
                launch(Dispatchers.Main) {
                    EventBus.getDefault().post(InputProcessCompleteEvent(info, event,
                            ComposingText(newComposingText = "")))
                }
            }
        }

        return true
    }

    private fun convertAll(): Iterable<Candidate> {
        val layout = (hardKeyboard as CommonHardKeyboard).layout[0] ?: return listOf()
        val converted = states.map { layout[it.keyCode]?.let { item -> if(it.shift) item.shift else item.normal } ?: listOf() }

        val syllables = converted.mapIndexed { i, _ ->
            val result = mutableListOf<Pair<HangulComposer.State, Int>>()
            var currents = listOf(HangulComposer.State() to 0)
            converted.slice(i until min(i+6, converted.size)).forEach { list ->
                currents = currents
                        .flatMap { item -> list.map { c -> hangulConverter.compose(item.first, c) to item.second + 1} }
                        // 음절이 넘어갔으면(other에 문자열이 있으면) 제외
                        .filter { it.first.other.isEmpty() }
                result += currents
            }
            result.map { hangulConverter.display(it.first) to it.second }
                    .map { it.first[0] to (conversionScorer.calculateScore(it.first) to it.second) }
                    .sortedByDescending { it.second.first }
        }

        val eojeols = mutableListOf("" to (0f to 0))
        syllables
                .map { list -> if(list.size <= 2) list else list.filter { it.first in '가' .. '힣'} }
                .forEachIndexed { i, list ->
                    val targets = eojeols.filter { it.second.second == i }
                    eojeols -= targets
                    eojeols += targets.flatMap { target ->
                        list.map { target.first + it.first to (target.second.first + it.second.first to target.second.second + it.second.second) }
                    }
                }

        val result = eojeols.map { it.first to it.second.first / it.first.length }
                .sortedByDescending { conversionScorer.calculateScore(it.first) }
                .filter { if(it.first.lastOrNull() in '가' .. '힣') it.first.all { c -> c in '가' .. '힣' } else true }
                .let { if(it.size > 16) it.take(sqrt(it.size.toDouble()).toInt() * 4) else it }
                .map {
                    // 사전 검색 결과 조합 중 가장 좋은 후보로 선택
                    predictor.predict(it.first.toList(), it.first.length).toList().let { candidates ->
                        candidates
                                .sortedByDescending { candidate -> candidate.frequency }
                                .sortedBy { candidate -> if(candidate is CompoundCandidate) candidate.candidates.size else Int.MAX_VALUE }
                    }.firstOrNull() ?: SingleCandidate(it.first, it.first, -1, it.second, endingSpace = it.first.any { c -> c in '가' .. '힣' })
                }

        return result
    }

    private fun learn(candidate: Candidate) {
        predictor.learn(candidate)
    }

    private fun delete(candidate: Candidate) {
        predictor.delete(candidate)
    }

    private fun resetCandidates() {
        if(candidates.isNotEmpty()) {
            candidates = listOf()
            EventBus.getDefault().post(CandidateUpdateEvent(this.info, candidates))
        }
    }

    private fun learnCurrentCandidateAndReset() {
        if(candidateIndex >= 0 && candidates.isNotEmpty()) {
            learn(candidates[candidateIndex])
            reset()
        }
    }

    override fun reset() {
        states.clear()
        super.reset()
        resetCandidates()
    }

    override fun init() {
        predictor.init()
        super.init()
    }

    override fun destroy() {
        predictor.destroy()
        super.destroy()
    }

    override fun serialize(): JSONObject {
        return super.serialize().apply {
            put("hangul-converter", hangulConverter.serialize())
        }
    }

    companion object {

        fun isHangul(c: Int): Boolean {
            return HangulComposer.isCho(c) || HangulComposer.isJung(c) || HangulComposer.isJong(c)
                    || DubeolHangulComposer.isConsonant(c) || DubeolHangulComposer.isVowel(c)
        }

    }

}
