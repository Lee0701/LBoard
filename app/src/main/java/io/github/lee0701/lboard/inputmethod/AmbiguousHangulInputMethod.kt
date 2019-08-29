package io.github.lee0701.lboard.inputmethod

import android.view.KeyEvent
import io.github.lee0701.lboard.ComposingText
import io.github.lee0701.lboard.event.InputProcessCompleteEvent
import io.github.lee0701.lboard.event.LBoardKeyEvent
import io.github.lee0701.lboard.event.PreferenceChangeEvent
import io.github.lee0701.lboard.hangul.HangulComposer
import io.github.lee0701.lboard.hardkeyboard.HardKeyboard
import io.github.lee0701.lboard.hardkeyboard.CommonHardKeyboard
import io.github.lee0701.lboard.inputmethod.ambiguous.Scorer
import io.github.lee0701.lboard.softkeyboard.SoftKeyboard
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.jetbrains.anko.doAsync
import org.json.JSONObject
import java.util.concurrent.Future

class AmbiguousHangulInputMethod(
        override val info: InputMethodInfo,
        override val softKeyboard: SoftKeyboard,
        override val hardKeyboard: HardKeyboard,
        val hangulConverter: HangulComposer,
        val conversionScorer: Scorer,
        val finalScorer: Scorer
): CommonInputMethod() {

    val states: MutableList<Pair<Int, Boolean>> = mutableListOf()
    var convertTask: Future<Unit>? = null

    var candidates: List<String> = listOf()
    var candidateIndex: Int = 0

    @Subscribe
    override fun onPreferenceChange(event: PreferenceChangeEvent) {
        super.onPreferenceChange(event)
        hangulConverter.setPreferences(event.preferences)
        timeout = event.preferences.getInt("method_ko_timeout", 0)
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
                    candidates = listOf()
                    return false
                }
            }
            KeyEvent.KEYCODE_SPACE -> {
                if(candidates.isNotEmpty()) {
                    while(convertTask?.isDone != true);
                    if(++candidateIndex >= candidates.size) candidateIndex = 0
                    if(candidates.isNotEmpty()) {
                        EventBus.getDefault().post(InputProcessCompleteEvent(info, event,
                                ComposingText(newComposingText = candidates[candidateIndex] + " ")))
                    }
                } else {
                    EventBus.getDefault().post(InputProcessCompleteEvent(info, event,
                            ComposingText(commitPreviousText = true, textToCommit = " ")))
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

                states += event.lastKeyCode to shift

                processStickyKeysOnInput()
            }
        }

        convertTask?.cancel(true)
        convertTask = doAsync {
            candidates = convertAll()
            candidateIndex = -1
            if(candidates.isNotEmpty()) {
                EventBus.getDefault().post(InputProcessCompleteEvent(info, event,
                        ComposingText(newComposingText = candidates[if(candidateIndex < 0) 0 else candidateIndex])))
            }
        }

        return true
    }

    private fun convertAll(): List<String> {
        val layout = (hardKeyboard as CommonHardKeyboard).layout[0] ?: return listOf()
        val converted = states.map { layout[it.first]?.let { item -> if(it.second) item.shift else item.normal } ?: listOf() }

        val syllables = converted.mapIndexed { i, _ ->
            val result = mutableListOf<Pair<HangulComposer.State, Int>>()
            var current = listOf(HangulComposer.State() to 0)
            converted.slice(i until Math.min(i+6, converted.size)).forEach { list ->
                current = current.flatMap { item -> list.map { c -> hangulConverter.compose(item.first, c) to item.second + 1} }
                        .filter { it.first.other.isEmpty() }
                        .ifEmpty { return@forEach }
                result += current
            }
            result.map { hangulConverter.display(it.first) to it.second }
                    .map { it.first[0] to (conversionScorer.calculateScore(it.first) to it.second) }
                    .sortedByDescending { it.second.first }
        }

        val result = mutableListOf("" to (0f to 0))

        syllables.map { list -> list.filter { it.first in '가' .. '힣'} }.forEachIndexed { i, list ->
            val targets = result.filter { it.second.second == i }
            result -= targets
            result += targets.flatMap { target ->
                list.map { target.first + it.first to (target.second.first + it.second.first to target.second.second + it.second.second) }
            }
        }

        return result.map { it.first to it.second.first / it.first.length }
                .sortedByDescending { conversionScorer.calculateScore(it.first) }
                .take(10)
                .sortedByDescending { finalScorer.calculateScore(it.first) }
                .map { it.first }
    }

    override fun reset() {
        states.clear()
        candidates = listOf()
        super.reset()
    }

    override fun serialize(): JSONObject {
        return super.serialize().apply {
            put("hangul-converter", hangulConverter.serialize())
        }
    }

    companion object {
        val DOUBLES = "ᆪᆬᆭᆰᆱᆲᆳᆴᆵᆶᆹ".map { it.toInt() }

    }

}
