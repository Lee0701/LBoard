package io.github.lee0701.lboard.softkeyboard

import android.content.Context
import android.inputmethodservice.Keyboard
import android.inputmethodservice.KeyboardView
import android.view.KeyEvent
import android.view.View
import io.github.lee0701.lboard.event.SoftKeyClickEvent
import org.greenrobot.eventbus.EventBus

class DefaultSoftKeyboard(name: String, val layout: Keyboard): SoftKeyboard(name), KeyboardView.OnKeyboardActionListener {

    var keyboardView: KeyboardView? = null

    override fun initView(context: Context): View? {
        keyboardView = KeyboardView(context, null).apply {
            this.keyboard = layout
            this.setOnKeyboardActionListener(this@DefaultSoftKeyboard)
        }
        return keyboardView
    }

    override fun onKey(primaryCode: Int, keyCodes: IntArray) {
        when(primaryCode) {
            KeyEvent.KEYCODE_SHIFT_LEFT, KeyEvent.KEYCODE_SHIFT_RIGHT -> keyboardView?.isShifted = !(keyboardView?.isShifted ?: false)
            else -> EventBus.getDefault().post(SoftKeyClickEvent(primaryCode, keyboardView?.isShifted ?: false, 1))
        }
    }

    override fun onPress(primaryCode: Int) {

    }

    override fun onRelease(primaryCode: Int) {

    }

    override fun swipeLeft() {
    }

    override fun swipeRight() {
    }

    override fun swipeUp() {
    }

    override fun swipeDown() {
    }

    override fun onText(text: CharSequence?) {
    }
}
