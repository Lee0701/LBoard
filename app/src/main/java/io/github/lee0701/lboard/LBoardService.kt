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
import io.github.lee0701.lboard.hardkeyboard.SimpleKeyboardLayout
import io.github.lee0701.lboard.hardkeyboard.SimpleHardKeyboard
import io.github.lee0701.lboard.hangul.DubeolHangulConverter
import io.github.lee0701.lboard.hangul.CombinationTable
import io.github.lee0701.lboard.hangul.HangulConverter
import io.github.lee0701.lboard.hangul.VirtualJamoTable
import io.github.lee0701.lboard.hardkeyboard.HangulConverterLinkedHardKeyboard
import io.github.lee0701.lboard.hardkeyboard.TwelveKeyHardKeyboard
import io.github.lee0701.lboard.layouts.alphabet.Alphabet
import io.github.lee0701.lboard.layouts.hangul.DubeolHangul
import io.github.lee0701.lboard.layouts.hangul.SebeolHangul
import io.github.lee0701.lboard.layouts.hangul.ShinSebeolHangul
import io.github.lee0701.lboard.layouts.hangul.TwelveDubeolHangul
import io.github.lee0701.lboard.softkeyboard.DefaultSoftKeyboard
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class LBoardService: InputMethodService() {

    private val inputMethods: MutableList<InputMethod> = mutableListOf()
    private var currentMethodId: Int = 0
    private val currentMethod: InputMethod get() = inputMethods[currentMethodId]

    private var lastMethodId: Int = 0
    private var inputAfterSwitch = false

    override fun onCreate() {
        super.onCreate()
        EventBus.getDefault().register(this)
        inputMethods += HangulInputMethod(
                DefaultSoftKeyboard("keyboard_12key_4cols"),
                TwelveKeyHardKeyboard(TwelveDubeolHangul.LAYOUT_CHEONJIIN, true, true),
                DubeolHangulConverter(TwelveDubeolHangul.COMBINATION_CHEONJIIN, TwelveDubeolHangul.VIRTUAL_CHEONJIIN)
        )
        inputMethods += AlphabetInputMethod(
                DefaultSoftKeyboard("keyboard_10cols_mobile"),
                SimpleHardKeyboard(Alphabet.LAYOUT_QWERTY)
        )
        inputMethods += HangulInputMethod(
                DefaultSoftKeyboard("keyboard_10cols_mod_quote"),
                HangulConverterLinkedHardKeyboard(ShinSebeolHangul.LAYOUT_SHIN_ORIGINAL.map { Alphabet.LAYOUT_QWERTY + it }),
                HangulConverter(ShinSebeolHangul.COMBINATION_SHIN_ORIGINAL)
        )
    }

    override fun onCreateInputView(): View? {
        return currentMethod.initView(this)
    }

    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
        super.onStartInputView(info, restarting)
        currentMethod.reset()
    }

    private fun reset() {
        currentMethodId = 0
        lastMethodId = 0
        inputAfterSwitch = false
    }

    private fun nextInputMethod(switchBetweenApps: Boolean = false) {
        currentMethod.reset()
        val last = currentMethodId
        if(inputAfterSwitch && currentMethodId != lastMethodId) {
            currentMethodId = lastMethodId
        } else {
            if(++currentMethodId >= inputMethods.size) {
                currentMethodId = 0
                if(switchBetweenApps) {
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) switchToNextInputMethod(true)
                    else (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                            .switchToLastInputMethod(window.window.attributes.token)
                }
            }
        }
        if(inputAfterSwitch) lastMethodId = last
        inputAfterSwitch = false
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
                nextInputMethod(true)
                return
            }
        }
        inputAfterSwitch = true
        if(!currentMethod.onKeyPress(event.keyCode)) when(event.keyCode) {
            KeyEvent.KEYCODE_DEL -> {
                if(currentInputConnection.getSelectedText(0) != null) currentInputConnection.commitText("", 1)
                else currentInputConnection.deleteSurroundingText(1, 0)
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
