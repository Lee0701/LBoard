package io.github.lee0701.lboard

import android.content.Context
import android.view.KeyCharacterMap
import android.view.KeyEvent
import android.view.View
import io.github.lee0701.lboard.event.CommitComposingEvent
import io.github.lee0701.lboard.event.CommitStringEvent
import io.github.lee0701.lboard.event.ComposeEvent
import io.github.lee0701.lboard.event.UpdateViewEvent
import io.github.lee0701.lboard.hangul.HangulComposer
import io.github.lee0701.lboard.hangul.SebeolHangulComposer
import io.github.lee0701.lboard.hangul.SingleVowelDubeolHangulComposer
import io.github.lee0701.lboard.hardkeyboard.HardKeyboard
import io.github.lee0701.lboard.hardkeyboard.CommonHardKeyboard
import io.github.lee0701.lboard.softkeyboard.SoftKeyboard
import org.greenrobot.eventbus.EventBus
import org.json.JSONObject
import java.util.*
import kotlin.concurrent.timerTask

class HangulInputMethod(
        override val softKeyboard: SoftKeyboard,
        override val hardKeyboard: HardKeyboard,
        val hangulConverter: HangulComposer,
        val timeout: Int = 0
): CommonInputMethod() {

    val states: MutableList<HangulComposer.State> = mutableListOf()
    val lastState: HangulComposer.State get() = if(states.isEmpty()) HangulComposer.State() else states.last()

    private val timer = Timer()
    private var timeoutTask: TimerTask? = null

    override fun initView(context: Context): View? {
        return softKeyboard.initView(context)
    }

    override fun onKeyPress(keyCode: Int): Boolean {
        if(isSystemKey(keyCode)) return false
        timeoutTask?.cancel()
        when(keyCode) {
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
                    EventBus.getDefault().post(CommitStringEvent(" "))
                }
            }
            KeyEvent.KEYCODE_ENTER -> {
                return super.onKeyPress(keyCode)
            }
            KeyEvent.KEYCODE_SHIFT_LEFT, KeyEvent.KEYCODE_SHIFT_RIGHT -> {
                return super.onKeyPress(keyCode)
            }
            KeyEvent.KEYCODE_ALT_LEFT, KeyEvent.KEYCODE_ALT_RIGHT -> {
                return super.onKeyPress(keyCode)
            }
            else -> {
                val converted = hardKeyboard.convert(keyCode, shift, alt)
                if(converted.backspace && states.size > 0) states.remove(states.last())
                if(converted.resultChar == null) {
                    reset()
                    if(converted.defaultChar)
                        EventBus.getDefault().post(CommitStringEvent(KeyCharacterMap.load(KeyCharacterMap.FULL)
                                .get(keyCode, 0).toChar().toString()))
                } else if(converted.resultChar == 0) {
                    reset()
                } else {
                    val composed = hangulConverter.compose(lastState, converted.resultChar)
                    states += composed
                    updateShinStatus(composed)
                }
                processStickyKeysOnInput()
                converted.shift?.let { shift = it }
                converted.alt?.let { alt = it }
                
                timeoutTask = timerTask {
                    val state = lastState
                    states.remove(state)
                    states += hangulConverter.timeout(state)
                }
                if(hangulConverter is SingleVowelDubeolHangulComposer && timeout > 0) timer.schedule(timeoutTask, timeout.toLong())
            }
        }
        EventBus.getDefault().post(ComposeEvent(hangulConverter.display(lastState)))
        EventBus.getDefault().post(UpdateViewEvent())
        return true
    }

    private fun updateShinStatus(composed: HangulComposer.State) {
        val status = composed.status
        if(hardKeyboard is CommonHardKeyboard) hardKeyboard.status = status
    }

    override fun reset() {
        EventBus.getDefault().post(CommitComposingEvent())
        states.clear()
        super.reset()
    }

    override fun serialize(): JSONObject {
        return super.serialize().apply {
            put("hangul-converter", hangulConverter.serialize())
        }
    }

    companion object {
        @JvmStatic fun deserialize(json: JSONObject): HangulInputMethod? {
            val softKeyboard = InputMethod.deserializeModule(json.getJSONObject("soft-keyboard")) as SoftKeyboard
            val hardKeyboard = InputMethod.deserializeModule(json.getJSONObject("hard-keyboard")) as HardKeyboard
            val hangulConverter = InputMethod.deserializeModule(json.getJSONObject("hangul-converter")) as SebeolHangulComposer
            return HangulInputMethod(softKeyboard, hardKeyboard, hangulConverter)
        }
    }

}
