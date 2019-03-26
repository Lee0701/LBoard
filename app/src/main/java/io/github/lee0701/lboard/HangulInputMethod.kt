package io.github.lee0701.lboard

import android.content.Context
import android.view.KeyCharacterMap
import android.view.KeyEvent
import android.view.View
import io.github.lee0701.lboard.event.CommitComposingEvent
import io.github.lee0701.lboard.event.CommitStringEvent
import io.github.lee0701.lboard.event.ComposeEvent
import io.github.lee0701.lboard.event.UpdateViewEvent
import io.github.lee0701.lboard.hangul.HangulConverter
import io.github.lee0701.lboard.hardkeyboard.HangulConverterLinkedHardKeyboard
import io.github.lee0701.lboard.hardkeyboard.HardKeyboard
import io.github.lee0701.lboard.hardkeyboard.TwelveKeyHardKeyboard
import io.github.lee0701.lboard.softkeyboard.SoftKeyboard
import org.greenrobot.eventbus.EventBus

class HangulInputMethod(
        override val softKeyboard: SoftKeyboard,
        override val hardKeyboard: HardKeyboard,
        val hangulConverter: HangulConverter
): InputMethod {

    var shift: Boolean = false
    var alt: Boolean = false

    var capsLock: Boolean = false
    var altLock: Boolean = false

    var inputOnShift = false
    var inputOnAlt = false

    val states: MutableList<HangulConverter.State> = mutableListOf()
    val lastState: HangulConverter.State get() = if(states.isEmpty()) HangulConverter.State() else states.last()

    override fun initView(context: Context): View? {
        return softKeyboard.initView(context)
    }

    override fun updateView(context: Context): View? {
        softKeyboard.setLabels(hardKeyboard.getLabels(shift, alt))
        return null
    }

    override fun onKeyPress(keyCode: Int): Boolean {
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
                if(hardKeyboard is TwelveKeyHardKeyboard && hardKeyboard.layout.spaceForSeparation &&
                        (lastState.cho != null || lastState.jung != null || lastState.jong != null)) {
                    states += HangulConverter.State(other = hangulConverter.display(lastState))
                    hardKeyboard.reset()
                } else {
                    reset()
                    EventBus.getDefault().post(CommitStringEvent(" "))
                }
            }
            KeyEvent.KEYCODE_ENTER -> {
                reset()
                return false
            }
            KeyEvent.KEYCODE_SHIFT_LEFT, KeyEvent.KEYCODE_SHIFT_RIGHT -> {
                if(capsLock) {
                    capsLock = false
                    shift = false
                }
                else if(shift && !inputOnShift) capsLock = true
                else shift = !shift
                inputOnShift = false
            }
            KeyEvent.KEYCODE_ALT_LEFT, KeyEvent.KEYCODE_ALT_RIGHT -> {
                if(altLock) {
                    altLock = false
                    alt = false
                }
                else if(alt && !inputOnAlt) altLock = true
                else alt = !alt
                inputOnAlt = false
            }
            else -> {
                val converted = hardKeyboard.convert(keyCode, shift, alt)
                if(converted.backspace && states.size > 0) states.remove(states.last())
                if(converted.resultChar == null) {
                    reset()
                    EventBus.getDefault().post(CommitStringEvent(KeyCharacterMap.load(KeyCharacterMap.FULL)
                            .get(keyCode, 0).toChar().toString()))
                } else if(converted.resultChar == 0) {
                    reset()
                } else {
                    val composed = hangulConverter.compose(lastState, converted.resultChar)
                    states += composed
                    updateShinStatus(composed)
                }
                if(shift && !capsLock) {
                    shift = false
                } else {
                    inputOnShift = true
                }
                if(alt && !altLock) {
                    alt = false
                } else {
                    inputOnAlt = true
                }
            }
        }
        EventBus.getDefault().post(ComposeEvent(hangulConverter.display(lastState)))
        EventBus.getDefault().post(UpdateViewEvent())
        return true
    }

    private fun updateShinStatus(composed: HangulConverter.State) {
        if(hardKeyboard is HangulConverterLinkedHardKeyboard) {
            hardKeyboard.status =
                    if(composed.jong != null && composed.jong < 0x01000000) 3
                    else if(composed.jung != null && composed.jung < 0x01000000) 2
                    else if(composed.cho != null && composed.cho < 0x01000000) 1
                    else 0
        }
    }

    override fun onKeyRelease(keyCode: Int): Boolean {
        when(keyCode) {
            KeyEvent.KEYCODE_SHIFT_LEFT, KeyEvent.KEYCODE_SHIFT_RIGHT -> {
                if(shift && !capsLock) shift = !inputOnShift
            }
            KeyEvent.KEYCODE_ALT_LEFT, KeyEvent.KEYCODE_ALT_RIGHT -> {
                if(alt && !altLock) alt = !inputOnAlt
            }
        }
        return true
    }

    override fun reset() {
        shift = false
        alt = false
        capsLock = false
        altLock = false

        EventBus.getDefault().post(CommitComposingEvent())
        hardKeyboard.reset()
        states.clear()
        EventBus.getDefault().post(UpdateViewEvent())
    }

}
