package io.github.lee0701.lboard.inputmethod

import android.view.KeyEvent
import io.github.lee0701.lboard.ComposingText
import io.github.lee0701.lboard.dictionary.Dictionary
import io.github.lee0701.lboard.dictionary.EditableDictionary

import io.github.lee0701.lboard.event.*
import io.github.lee0701.lboard.hangul.DubeolHangulComposer
import io.github.lee0701.lboard.hangul.HangulComposer
import io.github.lee0701.lboard.hardkeyboard.HardKeyboard
import io.github.lee0701.lboard.hardkeyboard.CommonHardKeyboard
import io.github.lee0701.lboard.inputmethod.ambiguous.Scorer
import io.github.lee0701.lboard.prediction.*
import io.github.lee0701.lboard.softkeyboard.SoftKeyboard
import kotlinx.coroutines.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.json.JSONObject
import java.text.Normalizer
import java.util.*
import kotlin.math.pow
import kotlin.math.sqrt

class AmbiguousHangulInputMethod(
        override val info: InputMethodInfo,
        override val softKeyboard: SoftKeyboard,
        override val hardKeyboard: HardKeyboard,
        val hangulConverter: HangulComposer,
        val conversionScorer: Scorer,
        val dictionary: Dictionary,
        val nextWordPredictor: NextWordPredictor,
): CommonInputMethod() {

    val words: MutableList<Int> = mutableListOf()
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
//                    .sortedBy { candidate -> if(candidate is CompoundCandidate) candidate.candidates.size else Int.MAX_VALUE }
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

        val candidateCache = mutableMapOf<List<List<Int>>, Set<Candidate>>()
        val nextWordCache = mutableMapOf<String, Map<Int, Float>>()
        val queue = PriorityQueue<Candidate>(MAX_CANDIDATES, compareByDescending<Candidate> { it.strokeCount }.thenByDescending { it.frequency })
        val candidates = mutableListOf<Candidate>()
        queue.offer(EmptyCandidate)
        while(true) {
            val item = queue.poll() ?: break
            if(item.strokeCount == converted.size) {
                candidates += item
                if(candidates.size >= MAX_CANDIDATES) break
                continue
            }
            if(item.strokeCount > converted.size) continue
            val nextWords = nextWordCache.getOrPut(item.text) { nextWordPredictor.predict(item.nextWordPredictorWords) }
            val maxConfidence = nextWords.values.maxOrNull() ?: 1f
            for(i in 0 .. converted.size - item.strokeCount) {
                val key = converted.drop(item.strokeCount).dropLast(i)
                candidateCache.getOrPut(key) {
                    composeAllPossibilities(key)
                            .map { hangulConverter.display(it) }
                            .sortedByDescending { conversionScorer.calculateScore(it) }
                            .take(MAX_CONVERSIONS)
                            .filter { text -> text.all { HangulComposer.isSyllable(it.code) } }
                            .flatMap { text -> nextWordPredictor.getWords(text).map { nextWord ->
                                val frequencyScore = 10f.pow(nextWord.frequency) / 10
                                val confidenceScore = nextWords[nextWord.id]?.let { 10f.pow(it / maxConfidence) / 10 } ?: 0f
                                val frequency = frequencyScore * confidenceScore
                                SingleCandidate(text, text, key.size, -1, frequency, false, listOf(nextWord.id))
                            } }
                            .toSet()
                }.map { CompoundCandidate.of(listOf(item) + it) }.filter { it.text != item.text }.forEach { queue.offer(it) }
            }
        }
        val rawCandidates = composeAllPossibilities(converted)
                .map { hangulConverter.display(it) }
                .sortedByDescending { conversionScorer.calculateScore(it) }
                .take(MAX_CONVERSIONS)
                .map { text -> SingleCandidate(text, text, converted.size, -1, 0f) }
        return (candidates + rawCandidates)
                .groupBy { it.text }
                .map { it.value.maxOrNull() }.filterNotNull()
                .sortedDescending()
    }

    private fun composeAllPossibilities(
            keys: List<List<Int>>,
            state: HangulComposer.State = HangulComposer.State(),
            depth: Int = 0): List<HangulComposer.State> {
        if(depth == keys.size) return listOf(state)
        return keys[depth].flatMap { composeAllPossibilities(keys, hangulConverter.compose(state, it), depth + 1) }
    }

    private fun learn(candidate: Candidate) {
        if(candidate.text.none { it in '가' .. '힣' }) return
        if(dictionary is EditableDictionary) {
            if(candidate is CompoundCandidate) {
                candidate.candidates.forEach {
                    if(it.text.length <= 1) return@forEach
                    val text = Normalizer.normalize(it.text, Normalizer.Form.NFD)
                    val existing = dictionary.search(text).maxByOrNull { it.frequency }
                    if(existing == null || existing.frequency < it.frequency) {
                        dictionary.insert(Dictionary.Word(text, it.frequency, it.pos))
                    }
                }
            }
            if(candidate.text.length <= 1) return
            val text = Normalizer.normalize(candidate.text, Normalizer.Form.NFD)
            val existing = dictionary.search(text).maxByOrNull { it.frequency }
            if(existing == null || existing.frequency < candidate.frequency) {
                dictionary.insert(Dictionary.Word(text, candidate.frequency, candidate.pos))
            }
        }
    }

    private fun delete(candidate: Candidate) {
        if(dictionary is EditableDictionary) {
            if(candidate is CompoundCandidate) {
                candidate.candidates.forEach {
                    val text = Normalizer.normalize(it.text, Normalizer.Form.NFD)
                    val existing = dictionary.search(text)
                    existing.forEach { dictionary.remove(it) }
                }
            }
            val existing = dictionary.search(Normalizer.normalize(candidate.text, Normalizer.Form.NFD))
            existing.forEach { dictionary.remove(it) }
        }
    }

    private fun resetCandidates() {
        if(candidates.isNotEmpty()) {
            candidates = listOf()
            EventBus.getDefault().post(CandidateUpdateEvent(this.info, candidates))
        }
    }

    private fun learnCurrentCandidateAndReset() {
        if(candidateIndex >= 0 && candidates.isNotEmpty()) {
            val candidate = candidates[candidateIndex]
            words += candidate.nextWordPredictorWords
            if(candidate.endingSpace) nextWordPredictor.getWord(" ")?.let { words += it.id }
            learn(candidate)
            reset()
        }
    }

    override fun reset() {
        words.clear()
        states.clear()
        super.reset()
        resetCandidates()
    }

    override fun serialize(): JSONObject {
        return super.serialize().apply {
            put("hangul-converter", hangulConverter.serialize())
        }
    }

    companion object {
        const val MAX_CONVERSIONS = 3
        const val MAX_CANDIDATES = 10

        fun isHangul(c: Int): Boolean {
            return HangulComposer.isCho(c) || HangulComposer.isJung(c) || HangulComposer.isJong(c)
                    || DubeolHangulComposer.isConsonant(c) || DubeolHangulComposer.isVowel(c)
        }

    }

}
