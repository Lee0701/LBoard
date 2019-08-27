package io.github.lee0701.lboard.inputmethod

import android.view.KeyCharacterMap
import android.view.KeyEvent
import io.github.lee0701.lboard.ComposingText
import io.github.lee0701.lboard.event.*
import io.github.lee0701.lboard.hardkeyboard.ExtendedCode
import io.github.lee0701.lboard.hardkeyboard.HardKeyboard
import io.github.lee0701.lboard.hardkeyboard.MoreKeysSupportedHardKeyboard
import io.github.lee0701.lboard.softkeyboard.MoreKeysSupportedSoftKeyboard
import io.github.lee0701.lboard.softkeyboard.SoftKeyboard
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
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

    protected val pressEvents: MutableMap<Int, LBoardKeyEvent> = mutableMapOf()
    protected var ignoreNextInput: Boolean = false

    @Subscribe
    open fun onPreferenceChange(event: PreferenceChangeEvent) {
        softKeyboard.setPreferences(event.preferences)
        hardKeyboard.setPreferences(event.preferences)
    }

    @Subscribe
    fun onInputStart(event: InputStartEvent) {
        shift = false
        alt = false
        capsLock = false
        altLock = false

        reset()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onInputViewInit(event: InputViewInitEvent) {
        if(softKeyboard.getView() == null || event.requiresInit) softKeyboard.initView(event.context)
        EventBus.getDefault().post(InputViewChangeEvent(info, softKeyboard.getView()))
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onInputViewRequiresUpdate(event: InputViewRequiresUpdateEvent) {
        if(event.methodInfo == this.info) updateView()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onOneHandedModeUpdate(event: OneHandedModeUpdateEvent) {
        softKeyboard.updateOneHandedMode(event.oneHandedMode)
    }

    @Subscribe
    fun onKeyEvent(event: LBoardKeyEvent) {
        val result = when(event.actions.last().type) {
            LBoardKeyEvent.ActionType.PRESS -> {
                if(event.source == LBoardKeyEvent.Source.VIRTUAL_KEYBOARD) {
                    when(event.originalKeyCode) {
                        KeyEvent.KEYCODE_SHIFT_LEFT, KeyEvent.KEYCODE_SHIFT_RIGHT,
                        KeyEvent.KEYCODE_ALT_LEFT, KeyEvent.KEYCODE_ALT_RIGHT -> onKeyPress(event)
                        else -> {
                            pressEvents += event.originalKeyCode to event
                            true
                        }
                    }
                }
                else onKeyPress(event)
            }
            LBoardKeyEvent.ActionType.RELEASE -> {
                pressEvents[event.originalKeyCode]?.let {
                    val result = onKeyPress(LBoardKeyEvent(it.methodInfo, event.actions.last().keyCode, event.source, it.actions))
                    if(!result) {
                        reset()
                        EventBus.getDefault().post(InputProcessCompleteEvent(info, it, null, false, true))
                    }
                    pressEvents -= event.originalKeyCode
                }
                onKeyRelease(event)
            }
            LBoardKeyEvent.ActionType.LONG_PRESS -> onKeyLongPress(event)
            LBoardKeyEvent.ActionType.SELECT_MORE_KEYS -> onMoreKeySelect(event)
            LBoardKeyEvent.ActionType.REPEAT -> onKeyRepeat(event)
            LBoardKeyEvent.ActionType.FLICK_LEFT, LBoardKeyEvent.ActionType.FLICK_RIGHT,
                LBoardKeyEvent.ActionType.FLICK_UP, LBoardKeyEvent.ActionType.FLICK_DOWN -> onKeyFlick(event)
        }
        if(!result) {
            reset()
            EventBus.getDefault().post(InputProcessCompleteEvent(info, event, null, false, true))
        }
    }

    protected open fun onMoreKeySelect(event: LBoardKeyEvent): Boolean {
        pressEvents -= event.originalKeyCode
        val result = onKeyPress(event)
        if(result) onKeyRelease(event)
        return true
    }

    protected open fun reset() {
        hardKeyboard.reset()
        EventBus.getDefault().post(InputResetEvent(info))
        EventBus.getDefault().post(InputViewRequiresUpdateEvent(this.info))
    }

    protected open fun updateView() {
        softKeyboard.shift = if(capsLock) 2 else if(shift) 1 else 0
        softKeyboard.alt = if(altLock) 2 else if(alt) 1 else 0
        softKeyboard.updateLabels(hardKeyboard.getLabels(shift, alt))
    }

    protected open fun onKeyPress(event: LBoardKeyEvent): Boolean {
        if(ignoreNextInput) return true
        when(event.lastKeyCode) {
            KeyEvent.KEYCODE_DEL -> {
                hardKeyboard.reset()
                return false
            }
            KeyEvent.KEYCODE_SPACE -> {
                hardKeyboard.reset()
                EventBus.getDefault().post(InputProcessCompleteEvent(info, event,
                        ComposingText(commitPreviousText = true, textToCommit = " ")))
            }
            KeyEvent.KEYCODE_ENTER -> {
                hardKeyboard.reset()
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
                val converted = convert(event.lastKeyCode, shift, alt)
                if(converted.backspace) onKeyPress(LBoardKeyEvent(event.methodInfo, event.originalKeyCode, LBoardKeyEvent.Source.INTERNAL,
                        event.actions + LBoardKeyEvent.Action(LBoardKeyEvent.ActionType.PRESS, KeyEvent.KEYCODE_DEL, System.currentTimeMillis())))
                if(converted.resultChar == null) {
                    if(converted.defaultChar) {
                        reset()
                        EventBus.getDefault().post(InputProcessCompleteEvent(info, event, null, true))
                    }
                } else if(converted.resultChar == 0) {
                    hardKeyboard.reset()
                } else {
                    EventBus.getDefault().post(InputProcessCompleteEvent(info, event,
                            ComposingText(textToCommit = converted.resultChar.toChar().toString())))
                }
                processStickyKeysOnInput()
                converted.shiftOn?.let { shift = it }
                converted.altOn?.let { alt = it }
            }
        }
        EventBus.getDefault().post(InputViewRequiresUpdateEvent(this.info))
        return true
    }

    protected open fun onKeyRelease(event: LBoardKeyEvent): Boolean {
        when(event.lastKeyCode) {
            KeyEvent.KEYCODE_SHIFT_LEFT, KeyEvent.KEYCODE_SHIFT_RIGHT -> {
                if(shift && !capsLock) shift = !inputOnShift
                shiftPressing = false
            }
            KeyEvent.KEYCODE_ALT_LEFT, KeyEvent.KEYCODE_ALT_RIGHT -> {
                if(alt && !altLock) alt = !inputOnAlt
                altPressing = false
            }
        }
        EventBus.getDefault().post(InputViewRequiresUpdateEvent(this.info))
        ignoreNextInput = false
        return true
    }

    protected open fun onKeyLongPress(event: LBoardKeyEvent): Boolean {
        if(hardKeyboard is MoreKeysSupportedHardKeyboard) {
            val moreKeys = (hardKeyboard as MoreKeysSupportedHardKeyboard).getMoreKeys(event.lastKeyCode, shift, alt)
            if(softKeyboard is MoreKeysSupportedSoftKeyboard) {
                (softKeyboard as MoreKeysSupportedSoftKeyboard).showMoreKeysKeyboard(event.lastKeyCode, moreKeys)
            }
        }
        return true
    }

    protected open fun onKeyRepeat(event: LBoardKeyEvent): Boolean {
        return onKeyPress(event)
    }

    protected open fun onKeyFlick(event: LBoardKeyEvent): Boolean {
        if((event.lastKeyCode and ExtendedCode.TWELVE_KEYPAD) != 0) {
            val code = event.lastKeyCode or when(event.actions.last().type) {
                LBoardKeyEvent.ActionType.FLICK_UP -> ExtendedCode.TWELVE_FLICK_UP
                LBoardKeyEvent.ActionType.FLICK_DOWN -> ExtendedCode.TWELVE_FLICK_DOWN
                LBoardKeyEvent.ActionType.FLICK_LEFT -> ExtendedCode.TWELVE_FLICK_LEFT
                LBoardKeyEvent.ActionType.FLICK_RIGHT -> ExtendedCode.TWELVE_FLICK_RIGHT
                else -> 0
            }
            val result = onKeyPress(LBoardKeyEvent(event.methodInfo, code, event.source, event.actions))
            if(result && onKeyRelease(LBoardKeyEvent(event.methodInfo, code, event.source, event.actions))) {
                ignoreNextInput = true
                return true
            }
        }

        when(event.actions.last().type) {
            LBoardKeyEvent.ActionType.FLICK_UP -> {
                if(!shift) {
                    shift = true
                    shiftPressing = false
                }
            }
            LBoardKeyEvent.ActionType.FLICK_DOWN -> {
                if(!alt) {
                    alt = true
                    altPressing = false
                }
            }
        }
        EventBus.getDefault().post(InputViewRequiresUpdateEvent(this.info))
        return true
    }

    protected fun convert(keyCode: Int, shift: Boolean, alt: Boolean): HardKeyboard.ConvertResult {
        return hardKeyboard.convert(keyCode, shift, alt)
    }

    protected fun getDefaultChar(keyCode: Int, shift: Boolean, alt: Boolean): Int {
        val metaState = (if(shift) KeyEvent.META_SHIFT_ON else 0) or (if(alt) KeyEvent.META_ALT_ON else 0)
        return KeyCharacterMap.load(KeyCharacterMap.BUILT_IN_KEYBOARD)
                .get(keyCode, metaState)
    }

    protected fun processStickyKeysOnInput() {
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

    override fun serialize(): JSONObject {
        return super.serialize().apply {
            put("soft-keyboard", softKeyboard.serialize())
            put("hard-keyboard", hardKeyboard.serialize())
        }
    }

}