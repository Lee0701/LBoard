package io.github.lee0701.lboard

import android.inputmethodservice.InputMethodService
import android.view.KeyCharacterMap
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import io.github.lee0701.lboard.event.*
import io.github.lee0701.lboard.hardkeyboard.SimpleKeyboardLayout
import io.github.lee0701.lboard.hardkeyboard.SimpleHardKeyboard
import io.github.lee0701.lboard.hangul.DubeolHangulConverter
import io.github.lee0701.lboard.hangul.CombinationTable
import io.github.lee0701.lboard.hangul.HangulConverter
import io.github.lee0701.lboard.hangul.VirtualJamoTable
import io.github.lee0701.lboard.layouts.hangul.SebeolHangul
import io.github.lee0701.lboard.softkeyboard.DefaultSoftKeyboard
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class LBoardService: InputMethodService() {

    val inputMethods: MutableList<HangulInputMethod> = mutableListOf()
    var currentMethodId: Int = 0
    val currentMethod: HangulInputMethod get() = inputMethods[currentMethodId]

    override fun onCreate() {
        super.onCreate()
        EventBus.getDefault().register(this)
        val layout = SimpleKeyboardLayout(
                SebeolHangul.LAYOUT_SEBEOL_391
        )
        val combinationTable = CombinationTable(
                SebeolHangul.COMBINATION_SEBEOL_391
        )
        inputMethods += HangulInputMethod(
                DefaultSoftKeyboard("keyboard_10cols_mod_quote"),
                SimpleHardKeyboard(layout),
                HangulConverter(combinationTable, VirtualJamoTable(mapOf()))
        )
    }

    override fun onCreateInputView(): View? {
        return currentMethod.initView(this)
    }

    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
        super.onStartInputView(info, restarting)
        currentMethod.reset()
    }

    @Subscribe fun onUpdateView(event: UpdateViewEvent) {
        currentMethod.updateView(this)?.let {
            setInputView(it)
        }
    }

    @Subscribe fun onSoftKeyClick(event: SoftKeyClickEvent) {
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
