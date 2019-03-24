package io.github.lee0701.lboard

import android.inputmethodservice.InputMethodService
import android.view.KeyCharacterMap
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import io.github.lee0701.lboard.event.CommitComposingEvent
import io.github.lee0701.lboard.event.CommitStringEvent
import io.github.lee0701.lboard.event.ComposeEvent
import io.github.lee0701.lboard.event.SoftKeyClickEvent
import io.github.lee0701.lboard.hardkeyboard.KeyboardLayout
import io.github.lee0701.lboard.hardkeyboard.SimpleHardKeyboard
import io.github.lee0701.lboard.hangul.DubeolHangulConverter
import io.github.lee0701.lboard.hangul.CombinationTable
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
        val layout = KeyboardLayout(
                mapOf(
                        45 to KeyboardLayout.LayoutItem(0x3142.toChar(), 0x3143.toChar()),
                        51 to KeyboardLayout.LayoutItem(0x3148.toChar(), 0x3149.toChar()),
                        33 to KeyboardLayout.LayoutItem(0x3137.toChar(), 0x3138.toChar()),
                        46 to KeyboardLayout.LayoutItem(0x3131.toChar(), 0x3132.toChar()),
                        48 to KeyboardLayout.LayoutItem(0x3145.toChar(), 0x3146.toChar()),
                        53 to KeyboardLayout.LayoutItem(0x315b.toChar()),
                        49 to KeyboardLayout.LayoutItem(0x3155.toChar()),
                        37 to KeyboardLayout.LayoutItem(0x3151.toChar()),
                        43 to KeyboardLayout.LayoutItem(0x3150.toChar(), 0x3152.toChar()),
                        44 to KeyboardLayout.LayoutItem(0x3154.toChar(), 0x3156.toChar()),

                        29 to KeyboardLayout.LayoutItem(0x3141.toChar()),
                        47 to KeyboardLayout.LayoutItem(0x3134.toChar()),
                        32 to KeyboardLayout.LayoutItem(0x3147.toChar()),
                        34 to KeyboardLayout.LayoutItem(0x3139.toChar()),
                        35 to KeyboardLayout.LayoutItem(0x314e.toChar()),
                        36 to KeyboardLayout.LayoutItem(0x3157.toChar()),
                        38 to KeyboardLayout.LayoutItem(0x3153.toChar()),
                        39 to KeyboardLayout.LayoutItem(0x314f.toChar()),
                        40 to KeyboardLayout.LayoutItem(0x3163.toChar()),

                        54 to KeyboardLayout.LayoutItem(0x314b.toChar()),
                        52 to KeyboardLayout.LayoutItem(0x314c.toChar()),
                        31 to KeyboardLayout.LayoutItem(0x314a.toChar()),
                        50 to KeyboardLayout.LayoutItem(0x314d.toChar()),
                        30 to KeyboardLayout.LayoutItem(0x3160.toChar()),
                        42 to KeyboardLayout.LayoutItem(0x315c.toChar()),
                        41 to KeyboardLayout.LayoutItem(0x3161.toChar())
                )
        )
        val combinationTable = CombinationTable(
                mapOf(
                        0x1169.toChar() to 0x1161.toChar() to 0x116a.toChar(),
                        0x1169.toChar() to 0x1162.toChar() to 0x116b.toChar(),
                        0x1169.toChar() to 0x1175.toChar() to 0x116c.toChar(),
                        0x116e.toChar() to 0x1165.toChar() to 0x116f.toChar(),
                        0x116e.toChar() to 0x1166.toChar() to 0x1170.toChar(),
                        0x116e.toChar() to 0x1175.toChar() to 0x1171.toChar(),
                        0x1173.toChar() to 0x1175.toChar() to 0x1174.toChar(),

                        0x11a8.toChar() to 0x11ba.toChar() to 0x11aa.toChar(),
                        0x11ab.toChar() to 0x11bd.toChar() to 0x11ac.toChar(),
                        0x11ab.toChar() to 0x11c2.toChar() to 0x11ad.toChar(),
                        0x11af.toChar() to 0x11a8.toChar() to 0x11b0.toChar(),
                        0x11af.toChar() to 0x11b7.toChar() to 0x11b1.toChar(),
                        0x11af.toChar() to 0x11b8.toChar() to 0x11b2.toChar(),
                        0x11af.toChar() to 0x11ba.toChar() to 0x11b3.toChar(),
                        0x11af.toChar() to 0x11c0.toChar() to 0x11b4.toChar(),
                        0x11af.toChar() to 0x11c1.toChar() to 0x11b5.toChar(),
                        0x11af.toChar() to 0x11c2.toChar() to 0x11b6.toChar(),
                        0x11b8.toChar() to 0x11ba.toChar() to 0x11b9.toChar()
                )
        )
        inputMethods += InputMethod(
                DefaultSoftKeyboard("keyboard_10cols_mobile"),
                SimpleHardKeyboard(layout),
                DubeolHangulConverter(combinationTable)
        )
    }

    override fun onCreateInputView(): View? {
        return currentMethod.initView(this)
    }

    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
        super.onStartInputView(info, restarting)
    }

    @Subscribe fun onSoftKeyClick(event: SoftKeyClickEvent) {
        if(!currentMethod.onKey(event.keyCode, event.shift)) when(event.keyCode) {
            KeyEvent.KEYCODE_DEL -> currentInputConnection.deleteSurroundingText(1, 0)
            else -> sendKeyChar(KeyCharacterMap.load(KeyCharacterMap.FULL).get(event.keyCode, if(event.shift) KeyEvent.META_SHIFT_ON else 0).toChar())
        }
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
