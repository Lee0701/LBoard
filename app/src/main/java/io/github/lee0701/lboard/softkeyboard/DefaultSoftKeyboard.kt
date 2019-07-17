package io.github.lee0701.lboard.softkeyboard

import android.content.Context
import android.inputmethodservice.Keyboard
import android.inputmethodservice.KeyboardView
import android.view.View
import io.github.lee0701.lboard.event.SoftKeyClickEvent
import org.greenrobot.eventbus.EventBus
import org.json.JSONObject

class DefaultSoftKeyboard(val layoutResId: String): SoftKeyboard, KeyboardView.OnKeyboardActionListener {

    var keyboardView: KeyboardView? = null

    override var shift: Int
        get() = if(keyboardView?.isShifted ?: false) 1 else 0
        set(value) {keyboardView?.isShifted = value > 0}

    override var alt: Int
        get() = 0
        set(value) {}

    override fun initView(context: Context): View? {
        val layout = Keyboard(context, context.resources.getIdentifier(layoutResId, "xml", context.packageName))
        keyboardView = KeyboardView(context, null).apply {
            this.keyboard = layout
            this.setOnKeyboardActionListener(this@DefaultSoftKeyboard)
        }
        return keyboardView
    }

    override fun onKey(primaryCode: Int, keyCodes: IntArray) {
        EventBus.getDefault().post(SoftKeyClickEvent(primaryCode, SoftKeyClickEvent.State.DOWN))
        EventBus.getDefault().post(SoftKeyClickEvent(primaryCode, SoftKeyClickEvent.State.UP))
    }

    override fun setLabels(labels: Map<Int, String>) {
        keyboardView?.keyboard?.keys?.forEach {
            it.label = labels[it.codes[0]] ?: it.label
        }
        keyboardView?.invalidateAllKeys()
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

    override fun serialize(): JSONObject {
        return super.serialize().apply {
            put("layout", layoutResId)
        }
    }

    companion object {
        @JvmStatic fun deserialize(json: JSONObject): DefaultSoftKeyboard? {
            return DefaultSoftKeyboard(json.getString("layout"))
        }
    }

}
