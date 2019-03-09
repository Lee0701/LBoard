package io.github.lee0701.lboard

import android.inputmethodservice.InputMethodService
import android.inputmethodservice.Keyboard
import android.view.KeyCharacterMap
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import io.github.lee0701.lboard.event.SoftKeyClickEvent
import io.github.lee0701.lboard.hardkeyboard.EmptyHardKeyboard
import io.github.lee0701.lboard.softkeyboard.DefaultSoftKeyboard
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class LBoardService: InputMethodService() {

    val inputMethods: MutableList<LBoardInputMethod> = mutableListOf()
    var currentMethodId: Int = 0
    val currentMethod: LBoardInputMethod get() = inputMethods[currentMethodId]

    override fun onCreate() {
        super.onCreate()
        EventBus.getDefault().register(this)
        inputMethods += LBoardInputMethod(
                DefaultSoftKeyboard("Default Soft Keyboard", Keyboard(this, R.xml.keyboard_10cols_mobile)),
                EmptyHardKeyboard("Empty Hard Keyboard")
        )
    }

    override fun onCreateInputView(): View? {
        return currentMethod.initView(this)
    }

    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
        super.onStartInputView(info, restarting)
    }

    @Subscribe
    fun onSoftKeyClick(event: SoftKeyClickEvent) {
        if(!currentMethod.onKey(event.keyCode)) when(event.keyCode) {
            KeyEvent.KEYCODE_DEL -> currentInputConnection.deleteSurroundingText(1, 0)
            else -> sendKeyChar(KeyCharacterMap.load(KeyCharacterMap.FULL).get(event.keyCode, if(event.shift) KeyEvent.META_SHIFT_ON else 0).toChar())
        }
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
