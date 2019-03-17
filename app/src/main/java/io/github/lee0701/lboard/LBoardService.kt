package io.github.lee0701.lboard

import android.inputmethodservice.InputMethodService
import android.inputmethodservice.Keyboard
import android.view.KeyCharacterMap
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import io.github.lee0701.lboard.event.ComposeEvent
import io.github.lee0701.lboard.event.SoftKeyClickEvent
import io.github.lee0701.lboard.preconverter.KeyboardLayout
import io.github.lee0701.lboard.preconverter.SimpleLayoutConverter
import io.github.lee0701.lboard.preconverter.TwelveKeyLayoutConverter
import io.github.lee0701.lboard.preconverter.hangul.DubeolHangulConverter
import io.github.lee0701.lboard.preconverter.hangul.HangulConverter
import io.github.lee0701.lboard.preconverter.hangul.HangulLayout
import io.github.lee0701.lboard.softkeyboard.DefaultSoftKeyboard
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class LBoardService: InputMethodService() {

    val inputMethods: MutableList<InputMethod> = mutableListOf()
    var currentMethodId: Int = 0
    val currentMethod: InputMethod get() = inputMethods[currentMethodId]

    override fun onCreate() {
        super.onCreate()
        EventBus.getDefault().register(this)
        val layout = HangulLayout(
                mapOf(
                        32 to KeyboardLayout.LayoutItem(listOf(0x3147.toChar(), 0x314e.toChar()), listOf(), listOf(), listOf()),
                        46 to KeyboardLayout.LayoutItem(0x3131.toChar()),
                        48 to KeyboardLayout.LayoutItem(0x3145.toChar()),
                        39 to KeyboardLayout.LayoutItem(0x314f.toChar())
                ),
                mapOf(
                        0x1100.toChar() to 0x1100.toChar() to 0x1101.toChar(),
                        0x11a8.toChar() to 0x11a8.toChar() to 0x11a9.toChar(),
                        0x11a8.toChar() to 0x11ba.toChar() to 0x11aa.toChar()
                )
        )
        inputMethods += InputMethod(
                DefaultSoftKeyboard("Default Soft Keyboard", Keyboard(this, R.xml.keyboard_10cols_mobile)),
                listOf(TwelveKeyLayoutConverter("Layout Converter", layout),
                        DubeolHangulConverter("Hangul Converter", layout))
        )
    }

    override fun onCreateInputView(): View? {
        return currentMethod.initView(this)
    }

    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
        super.onStartInputView(info, restarting)
    }

    @Subscribe fun onSoftKeyClick(event: SoftKeyClickEvent) {
        if(!currentMethod.onKey(event.keyCode)) when(event.keyCode) {
            KeyEvent.KEYCODE_DEL -> currentInputConnection.deleteSurroundingText(1, 0)
            else -> sendKeyChar(KeyCharacterMap.load(KeyCharacterMap.FULL).get(event.keyCode, if(event.shift) KeyEvent.META_SHIFT_ON else 0).toChar())
        }
    }

    @Subscribe fun onCompose(event: ComposeEvent) {
        currentInputConnection.setComposingText(event.composing.layers.last().tokens.map { it.toString() }.joinToString(""), 1)
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
