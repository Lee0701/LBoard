package io.github.lee0701.lboard

import android.content.Context
import android.inputmethodservice.InputMethodService
import android.os.Build
import android.view.KeyCharacterMap
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import io.github.lee0701.lboard.event.*
import io.github.lee0701.lboard.hardkeyboard.SimpleHardKeyboard
import io.github.lee0701.lboard.hangul.DubeolHangulConverter
import io.github.lee0701.lboard.hangul.HangulConverter
import io.github.lee0701.lboard.hardkeyboard.HangulConverterLinkedHardKeyboard
import io.github.lee0701.lboard.hardkeyboard.TwelveKeyHardKeyboard
import io.github.lee0701.lboard.layouts.alphabet.Alphabet
import io.github.lee0701.lboard.layouts.hangul.*
import io.github.lee0701.lboard.softkeyboard.DefaultSoftKeyboard
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class LBoardService: InputMethodService() {

    private val inputMethods: MutableList<InputMethodSet> = mutableListOf()
    private var currentMethodId: Int = 0
    private var currentModeId: Int = 0
    private val currentMethodSet: InputMethodSet get() = inputMethods[currentMethodId]
    private val currentMethod: InputMethod get() = currentMethodSet.keyModes[currentModeId]

    private var lastMethodId: Int = 0
    private var inputAfterSwitch = false
    private var switchedFromOutside = true

    override fun onCreate() {
        super.onCreate()
        EventBus.getDefault().register(this)
        val cheonjiin = HangulInputMethod(
                DefaultSoftKeyboard("keyboard_12key_4cols"),
                TwelveKeyHardKeyboard(TwelveDubeolHangul.LAYOUT_CHEONJIIN, true, true),
                DubeolHangulConverter(TwelveDubeolHangul.COMBINATION_CHEONJIIN, TwelveDubeolHangul.VIRTUAL_CHEONJIIN)
        )
        val shin = HangulInputMethod(
                DefaultSoftKeyboard("keyboard_10cols_mod_quote"),
                HangulConverterLinkedHardKeyboard(ShinSebeolHangul.LAYOUT_SHIN_ORIGINAL.map { Alphabet.LAYOUT_QWERTY + it }),
                HangulConverter(ShinSebeolHangul.COMBINATION_SHIN_ORIGINAL)
        )
        val qwerty = AlphabetInputMethod(
                DefaultSoftKeyboard("keyboard_10cols_mobile"),
                SimpleHardKeyboard(Alphabet.LAYOUT_QWERTY)
        )
        val symbols = AlphabetInputMethod(
                DefaultSoftKeyboard("keyboard_10cols_mobile"),
                SimpleHardKeyboard(Symbols.LAYOUT_SYMBOLS_A)
        )
        inputMethods += InputMethodSet(cheonjiin, symbols)
        inputMethods += InputMethodSet(qwerty, symbols)
        inputMethods += InputMethodSet(shin, symbols)
    }

    override fun onCreateInputView(): View? {
        return currentMethod.initView(this)
    }

    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
        super.onStartInputView(info, restarting)
        currentMethod.reset()
    }

    override fun onFinishInputView(finishingInput: Boolean) {
        super.onFinishInputView(finishingInput)
        if(finishingInput) switchedFromOutside = true
    }

    private fun reset() {
        currentMethodId = 0
        lastMethodId = 0
        inputAfterSwitch = false
    }

    private fun switchInputMethod(switchBetweenApps: Boolean = false) {
        currentMethod.reset()

        val last = currentMethodId
        currentModeId = 0

        val fromOutside = switchedFromOutside
        switchedFromOutside = false

        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val token = window.window.attributes.token

        if(inputAfterSwitch && (currentMethodId != lastMethodId || fromOutside)) {
            if(fromOutside) {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) switchToPreviousInputMethod()
                else imm.switchToLastInputMethod(token)
            } else {
                currentMethodId = lastMethodId
            }
        } else {
            if(++currentMethodId >= inputMethods.size) {
                currentMethodId = 0
                if(switchBetweenApps) {
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)  switchToNextInputMethod(false)
                    else imm.switchToNextInputMethod(token, false)
                }
            }
        }
        if(inputAfterSwitch) lastMethodId = last
        inputAfterSwitch = false
        setInputView(currentMethod.initView(this))
    }

    private fun switchKeyMode() {
        currentMethod.reset()
        val last = currentModeId
        if(inputAfterSwitch && currentModeId != 0) {
            currentModeId = 0
        } else {
            if(++currentModeId >= currentMethodSet.keyModes.size)
                currentModeId = 0
        }
        if(inputAfterSwitch) lastMethodId = last
        setInputView(currentMethod.initView(this))
    }

    @Subscribe fun onUpdateView(event: UpdateViewEvent) {
        currentMethod.updateView(this)?.let {
            setInputView(it)
        }
    }

    @Subscribe fun onSoftKeyClick(event: SoftKeyClickEvent) {
        when(event.keyCode) {
            KeyEvent.KEYCODE_LANGUAGE_SWITCH -> {
                switchInputMethod(true)
                return
            }
            KeyEvent.KEYCODE_SYM -> {
                switchKeyMode()
                return
            }
        }
        inputAfterSwitch = true
        if(!currentMethod.onKeyPress(event.keyCode)) when(event.keyCode) {
            KeyEvent.KEYCODE_DEL -> {
                if(currentInputConnection.getSelectedText(0) != null) currentInputConnection.commitText("", 1)
                else currentInputConnection.deleteSurroundingText(1, 0)
            }
            KeyEvent.KEYCODE_ENTER -> {
                when(currentInputEditorInfo.imeOptions and EditorInfo.IME_MASK_ACTION) {
                    EditorInfo.IME_ACTION_SEARCH, EditorInfo.IME_ACTION_GO -> {
                        sendDefaultEditorAction(true)
                    }
                    else -> {
                        sendKeyChar('\n')
                    }
                }
            }
            else -> sendKeyChar(KeyCharacterMap.load(KeyCharacterMap.FULL).get(event.keyCode, 0).toChar())
        } else currentMethod.onKeyRelease(event.keyCode)
    }

    @Subscribe fun onCompose(event: ComposeEvent) {
        currentInputConnection.setComposingText(event.composing, 1)
    }

    @Subscribe fun onCommitComposing(event: CommitComposingEvent) {
        currentInputConnection.finishComposingText()
    }

    @Subscribe fun onCommitString(event: CommitStringEvent) {
        currentInputConnection.finishComposingText()
        currentInputConnection.commitText(event.string, 1)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        return super.onKeyDown(keyCode, event)
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        return super.onKeyUp(keyCode, event)
    }

    override fun onEvaluateInputViewShown(): Boolean {
        super.onEvaluateInputViewShown()
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }
}
