package io.github.lee0701.lboard

import android.content.Context
import android.view.KeyCharacterMap
import android.view.KeyEvent
import android.view.View
import io.github.lee0701.lboard.event.CommitStringEvent
import io.github.lee0701.lboard.event.UpdateViewEvent
import org.greenrobot.eventbus.EventBus
import org.json.JSONObject

abstract class CommonInputMethod: InputMethod {

    override var shift: Boolean = false
    override var alt: Boolean = false

    var capsLock: Boolean = false
    var altLock: Boolean = false

    var inputOnShift = false
    var inputOnAlt = false

    override fun initView(context: Context): View? {
        return softKeyboard.initView(context)
    }

    override fun updateView(context: Context): View? {
        softKeyboard.setLabels(hardKeyboard.getLabels(shift, alt))
        return null
    }

    override fun onKeyPress(keyCode: Int): Boolean {
        if(isSystemKey(keyCode)) return false
        when(keyCode) {
            KeyEvent.KEYCODE_DEL -> {
                hardKeyboard.reset()
                return false
            }
            KeyEvent.KEYCODE_SPACE -> {
                reset()
                EventBus.getDefault().post(CommitStringEvent(" "))
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
                if(converted.backspace) onKeyPress(KeyEvent.KEYCODE_DEL)
                if(converted.resultChar == null) {
                    reset()
                    EventBus.getDefault().post(CommitStringEvent(KeyCharacterMap.load(KeyCharacterMap.FULL)
                            .get(keyCode, 0).toChar().toString()))
                } else if(converted.resultChar == 0) {
                    reset()
                } else {
                    EventBus.getDefault().post(CommitStringEvent(converted.resultChar.toChar().toString()))
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
        EventBus.getDefault().post(UpdateViewEvent())
        return true
    }

    override fun onKeyRelease(keyCode: Int): Boolean {
        if(isSystemKey(keyCode)) return false
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

        hardKeyboard.reset()
        EventBus.getDefault().post(UpdateViewEvent())
    }

    fun isSystemKey(keyCode: Int): Boolean = keyCode in 0 .. 6 || keyCode in 24 .. 28 || keyCode in 79 .. 85

    override fun serialize(): JSONObject {
        return super.serialize().apply {
            put("soft-keyboard", softKeyboard.serialize())
            put("hard-keyboard", hardKeyboard.serialize())
        }
    }

}
