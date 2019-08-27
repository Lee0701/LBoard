package io.github.lee0701.lboard.inputmethod

import android.view.KeyEvent
import io.github.lee0701.lboard.ComposingText
import io.github.lee0701.lboard.event.InputProcessCompleteEvent
import io.github.lee0701.lboard.event.LBoardKeyEvent
import io.github.lee0701.lboard.event.PreferenceChangeEvent
import io.github.lee0701.lboard.hangul.HangulComposer
import io.github.lee0701.lboard.hangul.SingleVowelDubeolHangulComposer
import io.github.lee0701.lboard.hardkeyboard.HardKeyboard
import io.github.lee0701.lboard.hardkeyboard.CommonHardKeyboard
import io.github.lee0701.lboard.softkeyboard.SoftKeyboard
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.json.JSONObject
import java.util.*
import kotlin.concurrent.timerTask

class HangulInputMethod(
        override val info: InputMethodInfo,
        override val softKeyboard: SoftKeyboard,
        override val hardKeyboard: HardKeyboard,
        val hangulConverter: HangulComposer,
        val timeout: Int = 0
): CommonInputMethod() {

    val states: MutableList<HangulComposer.State> = mutableListOf()
    val lastState: HangulComposer.State get() = if(states.isEmpty()) HangulComposer.State() else states.last()

    private val timer = Timer()
    private var timeoutTask: TimerTask? = null

    @Subscribe
    override fun onPreferenceChange(event: PreferenceChangeEvent) {
        super.onPreferenceChange(event)
        hangulConverter.setPreferences(event.preferences)
    }

    override fun onKeyPress(event: LBoardKeyEvent): Boolean {
        if(ignoreNextInput) return true
        timeoutTask?.cancel()
        when(event.lastKeyCode) {
            KeyEvent.KEYCODE_DEL -> {
                hardKeyboard.reset()
                if(states.size > 0) {
                    states.remove(states.last())
                    updateShinStatus(lastState)
                } else {
                    updateShinStatus(lastState)
                    return false
                }
            }
            KeyEvent.KEYCODE_SPACE -> {
                // 천지인 등 스페이스로 조합 끊는 자판일 시
                if(hardKeyboard is CommonHardKeyboard && hardKeyboard.layout.spaceForSeparation &&
                        (lastState.cho != null || lastState.jung != null || lastState.jong != null)) {
                    states += HangulComposer.State(other = hangulConverter.display(lastState))
                    hardKeyboard.reset()
                } else {
                    reset()
                    EventBus.getDefault().post(InputProcessCompleteEvent(info, event,
                            ComposingText(commitPreviousText = true, textToCommit = " ")))
                }
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
                if(converted.backspace && states.size > 0) states.remove(states.last())
                if(converted.resultChar == null) {
                    if(converted.defaultChar) {
                        reset()
                        EventBus.getDefault().post(InputProcessCompleteEvent(info, event, null, true))
                    }
                } else if(converted.resultChar == 0) {
                    reset()
                } else {
                    val composed = hangulConverter.compose(lastState, converted.resultChar)
                    states += composed
                    updateShinStatus(composed)
                }
                processStickyKeysOnInput()
                converted.shiftOn?.let { shift = it }
                converted.altOn?.let { alt = it }
                
                timeoutTask = timerTask {
                    val state = lastState
                    states.remove(state)
                    states += hangulConverter.timeout(state)
                }
                if(hangulConverter is SingleVowelDubeolHangulComposer && timeout > 0) timer.schedule(timeoutTask, timeout.toLong())
            }
        }
        EventBus.getDefault().post(InputProcessCompleteEvent(info, event,
                ComposingText(newComposingText = hangulConverter.display(lastState))))
        return true
    }

    private fun updateShinStatus(composed: HangulComposer.State) {
        val status = composed.status
        if(hardKeyboard is CommonHardKeyboard) hardKeyboard.status = status
    }

    override fun reset() {
        states.clear()
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
