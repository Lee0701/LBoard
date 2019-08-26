package io.github.lee0701.lboard

import android.content.Context
import android.content.SharedPreferences
import android.view.KeyCharacterMap
import android.view.KeyEvent
import android.view.View
import io.github.lee0701.lboard.event.CommitStringEvent
import io.github.lee0701.lboard.event.SetSymbolModeEvent
import io.github.lee0701.lboard.event.SoftKeyFlickEvent
import io.github.lee0701.lboard.event.UpdateViewEvent
import io.github.lee0701.lboard.hardkeyboard.ExtendedCode
import io.github.lee0701.lboard.hardkeyboard.HardKeyboard
import io.github.lee0701.lboard.hardkeyboard.MoreKeysSupportedHardKeyboard
import io.github.lee0701.lboard.softkeyboard.MoreKeysSupportedSoftKeyboard
import io.github.lee0701.lboard.softkeyboard.SoftKeyboard
import org.greenrobot.eventbus.EventBus
import org.json.JSONObject

abstract class CommonInputMethod: InputMethod {

    abstract val softKeyboard: SoftKeyboard
    abstract val hardKeyboard: HardKeyboard

    override var shift: Boolean = false
    override var alt: Boolean = false
    var shiftPressing: Boolean = false
    var altPressing: Boolean = false

    var capsLock: Boolean = false
    var altLock: Boolean = false

    var inputOnShift = false
    var inputOnAlt = false

    var ignoreNextInput: Boolean = false

    override fun initView(context: Context): View? {
        return softKeyboard.initView(context)
    }

    override fun updateView(context: Context): View? {
        softKeyboard.shift = if(capsLock) 2 else if(shift) 1 else 0
        softKeyboard.alt = if(altLock) 2 else if(alt) 1 else 0
        softKeyboard.setLabels(hardKeyboard.getLabels(shift, alt))
        return softKeyboard.getView()
    }

    override fun onKeyPress(keyCode: Int): Boolean {
        if(isSystemKey(keyCode)) return false
        if(ignoreNextInput) return true
        when(keyCode) {
            KeyEvent.KEYCODE_DEL -> {
                hardKeyboard.reset()
                return false
            }
            KeyEvent.KEYCODE_SPACE -> {
                hardKeyboard.reset()
                EventBus.getDefault().post(SetSymbolModeEvent(false))
                EventBus.getDefault().post(CommitStringEvent(" "))
            }
            KeyEvent.KEYCODE_ENTER -> {
                reset()
                EventBus.getDefault().post(SetSymbolModeEvent(false))
                return false
            }
            KeyEvent.KEYCODE_SHIFT_LEFT, KeyEvent.KEYCODE_SHIFT_RIGHT -> {
                if(capsLock) {
                    capsLock = false
                    shift = false
                }
                else if(shift && !inputOnShift) capsLock = true
                else shift = !shift
                shiftPressing = true
                inputOnShift = false
            }
            KeyEvent.KEYCODE_ALT_LEFT, KeyEvent.KEYCODE_ALT_RIGHT -> {
                if(altLock) {
                    altLock = false
                    alt = false
                }
                else if(alt && !inputOnAlt) altLock = true
                else alt = !alt
                altPressing = true
                inputOnAlt = false
            }
            else -> {
                val converted = convert(keyCode, shift, alt)
                if(converted.backspace) onKeyPress(KeyEvent.KEYCODE_DEL)
                if(converted.resultChar == null) {
                    if(converted.defaultChar) {
                        hardKeyboard.reset()
                        val defaultChar = getDefaultChar(keyCode, shift, alt)
                        if(defaultChar != 0) EventBus.getDefault().post(CommitStringEvent(defaultChar.toChar().toString()))
                    }
                } else if(converted.resultChar == 0) {
                    reset()
                } else {
                    EventBus.getDefault().post(CommitStringEvent(converted.resultChar.toChar().toString()))
                }
                processStickyKeysOnInput(converted.resultChar ?: 0)
                converted.shiftOn?.let { shift = it }
                converted.altOn?.let { alt = it }
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
                shiftPressing = false
            }
            KeyEvent.KEYCODE_ALT_LEFT, KeyEvent.KEYCODE_ALT_RIGHT -> {
                if(alt && !altLock) alt = !inputOnAlt
                altPressing = false
            }
        }
        EventBus.getDefault().post(UpdateViewEvent())
        ignoreNextInput = false
        return true
    }

    override fun onKeyLongPress(keyCode: Int): Boolean {
        if(hardKeyboard is MoreKeysSupportedHardKeyboard) {
            val moreKeys = (hardKeyboard as MoreKeysSupportedHardKeyboard).getMoreKeys(keyCode, shift, alt)
            if(softKeyboard is MoreKeysSupportedSoftKeyboard) {
                (softKeyboard as MoreKeysSupportedSoftKeyboard).showMoreKeysKeyboard(keyCode, moreKeys)
            }
        }
        return true
    }

    override fun onKeyFlick(keyCode: Int, direction: SoftKeyFlickEvent.FlickDirection): Boolean {
        if((keyCode and ExtendedCode.TWELVE_KEYPAD) != 0) {
            val code = keyCode or when(direction) {
                SoftKeyFlickEvent.FlickDirection.UP -> ExtendedCode.TWELVE_FLICK_UP
                SoftKeyFlickEvent.FlickDirection.DOWN -> ExtendedCode.TWELVE_FLICK_DOWN
                SoftKeyFlickEvent.FlickDirection.LEFT -> ExtendedCode.TWELVE_FLICK_LEFT
                SoftKeyFlickEvent.FlickDirection.RIGHT -> ExtendedCode.TWELVE_FLICK_RIGHT
            }
            val result = onKeyPress(code)
            if(result && onKeyRelease(code)) {
                ignoreNextInput = true
                return true
            }
        }

        when(direction) {
            SoftKeyFlickEvent.FlickDirection.UP -> {
                if(!shift) {
                    onKeyPress(KeyEvent.KEYCODE_SHIFT_LEFT)
                    onKeyRelease(KeyEvent.KEYCODE_SHIFT_RIGHT)
                }
            }
            SoftKeyFlickEvent.FlickDirection.DOWN -> {
                if(!alt) {
                    onKeyPress(KeyEvent.KEYCODE_ALT_LEFT)
                    onKeyRelease(KeyEvent.KEYCODE_ALT_LEFT)
                }
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

    protected fun convert(keyCode: Int, shift: Boolean, alt: Boolean): HardKeyboard.ConvertResult {
        return hardKeyboard.convert(keyCode, shift, alt)
    }

    protected fun getDefaultChar(keyCode: Int, shift: Boolean, alt: Boolean): Int {
        val metaState = (if(shift) KeyEvent.META_SHIFT_ON else 0) or (if(alt) KeyEvent.META_ALT_ON else 0)
        return KeyCharacterMap.load(KeyCharacterMap.BUILT_IN_KEYBOARD)
                .get(keyCode, metaState)
    }

    protected fun processStickyKeysOnInput(resultChar: Int) {
        if(shift && !capsLock && !shiftPressing) {
            shift = false
        } else {
            inputOnShift = true
        }
        if(alt && !altLock && !altPressing) {
            alt = false
        } else {
            inputOnAlt = true
        }
    }

    fun isSystemKey(keyCode: Int): Boolean = keyCode in 0 .. 6 || keyCode in 24 .. 28 || keyCode in 79 .. 85

    override fun setPreferences(pref: SharedPreferences) {
        super.setPreferences(pref)
        softKeyboard.setPreferences(pref)
        hardKeyboard.setPreferences(pref)
    }

    override fun serialize(): JSONObject {
        return super.serialize().apply {
            put("soft-keyboard", softKeyboard.serialize())
            put("hard-keyboard", hardKeyboard.serialize())
        }
    }

}
