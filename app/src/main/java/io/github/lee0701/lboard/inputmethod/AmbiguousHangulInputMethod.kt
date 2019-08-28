package io.github.lee0701.lboard.inputmethod

import android.view.KeyEvent
import io.github.lee0701.lboard.ComposingText
import io.github.lee0701.lboard.event.InputProcessCompleteEvent
import io.github.lee0701.lboard.event.LBoardKeyEvent
import io.github.lee0701.lboard.event.PreferenceChangeEvent
import io.github.lee0701.lboard.hangul.HangulComposer
import io.github.lee0701.lboard.hardkeyboard.HardKeyboard
import io.github.lee0701.lboard.hardkeyboard.CommonHardKeyboard
import io.github.lee0701.lboard.hardkeyboard.CommonKeyboardLayout
import io.github.lee0701.lboard.inputmethod.ambiguous.Hangul2350Scorer
import io.github.lee0701.lboard.inputmethod.ambiguous.HangulSyllableFrequencyScorer
import io.github.lee0701.lboard.inputmethod.ambiguous.Scorer
import io.github.lee0701.lboard.softkeyboard.SoftKeyboard
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.json.JSONObject

class AmbiguousHangulInputMethod(
        override val info: InputMethodInfo,
        override val softKeyboard: SoftKeyboard,
        override val hardKeyboard: HardKeyboard,
        val hangulConverter: HangulComposer
): CommonInputMethod() {

    val states: MutableList<Int> = mutableListOf()
    val scorer: Scorer = HangulSyllableFrequencyScorer()

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
                if(states.size > 0) {
                    states.remove(states.last())
                } else {
                    candidates = listOf()
                    return false
                }
            }
            KeyEvent.KEYCODE_SPACE -> {
                if(candidates.isNotEmpty()) {
                    if(++candidateIndex >= candidates.size) candidateIndex = 0
                    if(candidates.isNotEmpty()) {
                        EventBus.getDefault().post(InputProcessCompleteEvent(info, event,
                                ComposingText(newComposingText = candidates[candidateIndex])))
                    }
                } else {
                    EventBus.getDefault().post(InputProcessCompleteEvent(info, event,
                            ComposingText(commitPreviousText = true, textToCommit = " ")))
                }
                return true
            }
            KeyEvent.KEYCODE_ENTER -> {
                if(candidates.isNotEmpty()) {
                    reset()
                    return true
                } else {
                    return super.onKeyPress(event)
                }
            }
            KeyEvent.KEYCODE_SHIFT_LEFT, KeyEvent.KEYCODE_SHIFT_RIGHT -> {
                return super.onKeyPress(event)
            }
            KeyEvent.KEYCODE_ALT_LEFT, KeyEvent.KEYCODE_ALT_RIGHT -> {
                return super.onKeyPress(event)
            }
            else -> {
                if(candidateIndex >= 0) reset()

                states += event.lastKeyCode

                processStickyKeysOnInput()
            }
        }

        candidates = convertAll()
        candidateIndex = -1

        if(candidates.isNotEmpty()) {
            EventBus.getDefault().post(InputProcessCompleteEvent(info, event,
                    ComposingText(newComposingText = candidates[if(candidateIndex < 0) 0 else candidateIndex])))
        }
        return true
    }

    private fun convertAll(): List<String> {
        val layout = (hardKeyboard as CommonHardKeyboard).layout[0] ?: return listOf()
        return convertRecursive(layout, 0, HangulComposer.State())
                .map { state -> hangulConverter.display(state) }
                .sortedByDescending { scorer.calculateScore(it) }
    }

    private fun convertRecursive(layout: CommonKeyboardLayout.LayoutLayer, index: Int, state: HangulComposer.State): List<HangulComposer.State> {
        if(index >= states.size) return listOf(state)
        val codes = layout[states[index]] ?: return listOf(state)
        return codes.normal.map { code -> hangulConverter.compose(state, code) }
                .sortedByDescending { scorer.calculateScore(hangulConverter.display(it)) }
                .let {
                    it.take(3)
                }
                .flatMap { convertRecursive(layout, index + 1, it) }
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

    }

}
