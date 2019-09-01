package io.github.lee0701.lboard.inputmethod

import android.view.KeyEvent
import io.github.lee0701.lboard.ComposingText
import io.github.lee0701.lboard.dictionary.Dictionary
import io.github.lee0701.lboard.dictionary.EditableDictionary
import io.github.lee0701.lboard.dictionary.WritableDictionary

import io.github.lee0701.lboard.event.*
import io.github.lee0701.lboard.hangul.DubeolHangulComposer
import io.github.lee0701.lboard.hangul.HangulComposer
import io.github.lee0701.lboard.hardkeyboard.HardKeyboard
import io.github.lee0701.lboard.hardkeyboard.CommonHardKeyboard
import io.github.lee0701.lboard.inputmethod.ambiguous.Scorer
import io.github.lee0701.lboard.prediction.Candidate
import io.github.lee0701.lboard.softkeyboard.SoftKeyboard
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.json.JSONObject
import java.text.Normalizer
import java.util.concurrent.Future

class AmbiguousHangulInputMethod(
        override val info: InputMethodInfo,
        override val softKeyboard: SoftKeyboard,
        override val hardKeyboard: HardKeyboard,
        val hangulConverter: HangulComposer,
        val conversionScorer: Scorer,
        val finalScorer: Scorer,
        val dictionary: Dictionary? = null
): CommonInputMethod() {

    val states: MutableList<Pair<Int, Boolean>> = mutableListOf()
    var convertTask: Future<Unit>? = null

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
                if(candidates.isNotEmpty()) {
                    while(convertTask?.isDone != true);
                    states.clear()
                    if(++candidateIndex >= candidates.size) candidateIndex = 0
                    if(candidates.isNotEmpty()) {
                        val candidate = candidates[candidateIndex]
                        EventBus.getDefault().post(InputProcessCompleteEvent(info, event,
                                ComposingText(newComposingText = candidate.text + if(candidate.endingSpace) " " else "")))
                    }
                } else {
                    reset()
                    EventBus.getDefault().post(InputProcessCompleteEvent(info, event,
                            ComposingText(commitPreviousText = true, textToCommit = " ")))
                }
                return true
            }
            KeyEvent.KEYCODE_ENTER -> {
                if(candidateIndex >= 0 && candidates.isNotEmpty()) {
                    val candidate = candidates[candidateIndex]
                    learn(candidate)
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
                    val candidate = candidates[candidateIndex]
                    learn(candidate)
                    EventBus.getDefault().post(InputProcessCompleteEvent(info, event,
                            ComposingText(newComposingText = candidate.text + if(candidate.endingSpace) " " else "")))
                    reset()
                }

                val converted = hardKeyboard.convert(event.lastKeyCode, shift, alt)
                if(converted.resultChar != null) {
                    if(isHangul(converted.resultChar)) states += event.lastKeyCode to shift
                    else {
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

        convertTask?.cancel(true)
        convertTask = doAsync {
            candidates = convertAll()
            candidateIndex = -1

            uiThread {
                EventBus.getDefault().post(CandidateUpdateEvent(this@AmbiguousHangulInputMethod.info, candidates))
                if(candidates.isNotEmpty()) {
                    EventBus.getDefault().post(InputProcessCompleteEvent(info, event,
                            ComposingText(newComposingText = candidates[if(candidateIndex < 0) 0 else candidateIndex].text)))
                } else {
                    EventBus.getDefault().post(InputProcessCompleteEvent(info, event,
                            ComposingText(newComposingText = "")))
                }
            }
        }

        return true
    }

    private fun convertAll(): List<Candidate> {
        val layout = (hardKeyboard as CommonHardKeyboard).layout[0] ?: return listOf()
        val converted = states.map { layout[it.first]?.let { item -> if(it.second) item.shift else item.normal } ?: listOf() }

        val syllables = converted.mapIndexed { i, _ ->
            val result = mutableListOf<Pair<HangulComposer.State, Int>>()
            var currents = listOf(HangulComposer.State() to 0)
            converted.slice(i until Math.min(i+6, converted.size)).forEach { list ->
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

        val result = mutableListOf("" to (0f to 0))
        syllables
                .mapIndexed { i, list -> if(list.size <= 2) list else list.filter { it.first in '가' .. '힣'} }
                .forEachIndexed { i, list ->
                    val targets = result.filter { it.second.second == i }
                    result -= targets
                    result += targets.flatMap { target ->
                        list.map { target.first + it.first to (target.second.first + it.second.first to target.second.second + it.second.second) }
                    }
                }

        return result.map { it.first to it.second.first / it.first.length }
                .sortedByDescending { conversionScorer.calculateScore(it.first) }
                .filter { if(it.first.lastOrNull() in '가' .. '힣') it.first.all { it in '가' .. '힣' } else true }
                .let { if(it.size > 8) it.take(Math.sqrt(it.size.toDouble()).toInt() * 3) else it }
                .sortedByDescending { finalScorer.calculateScore(it.first) }
                .filter { it.first.isNotEmpty() }
                .map { Candidate(0, it.first, frequency = it.second, pos = "0", endingSpace = it.first.any { c -> c in '가' .. '힣' }) }
    }

    private fun learn(candidate: Candidate) {
        if(dictionary is EditableDictionary) {
            val existing = dictionary.search(Normalizer.normalize(candidate.text, Normalizer.Form.NFD)).maxBy { it.frequency }
            if(existing == null || existing.frequency < candidate.frequency) {
                dictionary.insert(Dictionary.Word(Normalizer.normalize(candidate.text, Normalizer.Form.NFD), candidate.frequency, candidate.pos.toInt()))
            }
        }
    }

    private fun resetCandidates() {
        if(candidates.isNotEmpty()) {
            candidates = listOf()
            EventBus.getDefault().post(CandidateUpdateEvent(this.info, candidates))
        }
    }

    override fun reset() {
        states.clear()
        super.reset()
        resetCandidates()
    }

    override fun init() {
        if(dictionary is WritableDictionary) dictionary.read()
        super.init()
    }

    override fun destroy() {
        if(dictionary is WritableDictionary) dictionary.write()
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
