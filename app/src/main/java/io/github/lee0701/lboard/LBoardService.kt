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
import io.github.lee0701.lboard.hangul.DubeolHangulConverter
import io.github.lee0701.lboard.hangul.HangulConverter
import io.github.lee0701.lboard.hangul.VirtualJamoTable
import io.github.lee0701.lboard.hardkeyboard.HangulConverterLinkedHardKeyboard
import io.github.lee0701.lboard.hardkeyboard.SimpleHardKeyboard
import io.github.lee0701.lboard.hardkeyboard.TwelveKeyHardKeyboard
import io.github.lee0701.lboard.layouts.alphabet.Alphabet
import io.github.lee0701.lboard.layouts.hangul.ShinSebeolHangul
import io.github.lee0701.lboard.layouts.hangul.Symbols
import io.github.lee0701.lboard.layouts.hangul.TwelveDubeolHangul
import io.github.lee0701.lboard.softkeyboard.*
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

        val layout = Layout(listOf(Row(listOf(
                Key(8, "1"),
                Key(9, "2"),
                Key(10, "3"),
                Key(11, "4"),
                Key(12, "5"),
                Key(13, "6"),
                Key(14, "7"),
                Key(15, "8"),
                Key(16, "9"),
                Key(7, "0")
        ), Row.Type.NUMBER), Row(listOf(
                Key(45, "q"),
                Key(51, "w"),
                Key(33, "e"),
                Key(46, "r"),
                Key(48, "t"),
                Key(53, "y"),
                Key(49, "u"),
                Key(37, "i"),
                Key(43, "o"),
                Key(44, "p")
        ), Row.Type.ODD), Row(listOf(
                Key(29, "a"),
                Key(47, "s"),
                Key(32, "d"),
                Key(34, "f"),
                Key(35, "g"),
                Key(36, "h"),
                Key(38, "j"),
                Key(39, "k"),
                Key(40, "l")
        ), Row.Type.EVEN, 0.05f, 0.05f), Row(listOf(
                Key(59, "SFT"),
                Key(54, "z"),
                Key(52, "x"),
                Key(31, "c"),
                Key(50, "v"),
                Key(30, "b"),
                Key(42, "n"),
                Key(41, "m"),
                Key(67, "DEL")
        ), Row.Type.ODD), Row(listOf(
                Key(keyCode = 63, label = "SYM", relativeWidth = 1.5f/10f),
                Key(keyCode = 204, label = "ABC", relativeWidth = 1.5f/10f),
                Key(keyCode = 62, relativeWidth = 4/10f),
                Key(keyCode = 56, label = ".", relativeWidth = 1/10f),
                Key(keyCode = 66, label = "RETURN", relativeWidth = 2/10f)
        ), Row.Type.BOTTOM)))

        val cheonjiin = HangulInputMethod(
                DefaultSoftKeyboard("keyboard_12key_4cols"),
                TwelveKeyHardKeyboard(TwelveDubeolHangul.LAYOUT_CHEONJIIN),
                DubeolHangulConverter(TwelveDubeolHangul.COMBINATION_CHEONJIIN, TwelveDubeolHangul.VIRTUAL_CHEONJIIN)
        )
        val naratgeul = HangulInputMethod(
                DefaultSoftKeyboard("keyboard_12key_4cols"),
                TwelveKeyHardKeyboard(TwelveDubeolHangul.LAYOUT_NARATGEUL),
                DubeolHangulConverter(TwelveDubeolHangul.COMBINATION_NARATGEUL, VirtualJamoTable(mapOf()))
        )
        val shin = HangulInputMethod(
                DefaultSoftKeyboard("keyboard_10cols_mod_quote"),
                HangulConverterLinkedHardKeyboard(ShinSebeolHangul.LAYOUT_SHIN_ORIGINAL.map { Alphabet.LAYOUT_QWERTY + it }),
                HangulConverter(ShinSebeolHangul.COMBINATION_SHIN_ORIGINAL)
        )
        val qwerty = WordComposingInputMethod(
                ThemeableSoftKeyboard(layout, 50f),
                SimpleHardKeyboard(Alphabet.LAYOUT_QWERTY)
        )
        val symbols = AlphabetInputMethod(
                DefaultSoftKeyboard("keyboard_10cols_mobile"),
                SimpleHardKeyboard(Symbols.LAYOUT_SYMBOLS_A)
        )
        inputMethods += InputMethodSet(naratgeul, symbols)
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
