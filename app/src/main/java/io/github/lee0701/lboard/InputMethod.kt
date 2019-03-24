package io.github.lee0701.lboard

import android.content.Context
import android.view.KeyCharacterMap
import android.view.KeyEvent
import android.view.View
import io.github.lee0701.lboard.event.CommitComposingEvent
import io.github.lee0701.lboard.event.CommitStringEvent
import io.github.lee0701.lboard.event.ComposeEvent
import io.github.lee0701.lboard.hangul.HangulConverter
import io.github.lee0701.lboard.hardkeyboard.HardKeyboard
import io.github.lee0701.lboard.softkeyboard.SoftKeyboard
import org.greenrobot.eventbus.EventBus

class InputMethod(
        val softKeyboard: SoftKeyboard,
        val hardKeyboard: HardKeyboard,
        val hangulConverter: HangulConverter
) {

    val states: MutableList<HangulConverter.State> = mutableListOf()
    val lastState: HangulConverter.State get() = if(states.isEmpty()) HangulConverter.State() else states.last()

    fun initView(context: Context): View? {
        return softKeyboard.initView(context)
    }

    fun onKey(keyCode: Int, shift: Boolean): Boolean {
        when(keyCode) {
            KeyEvent.KEYCODE_DEL -> if(states.size > 0) states.remove(states.last()) else return false
            KeyEvent.KEYCODE_SPACE -> {
                reset()
                EventBus.getDefault().post(CommitStringEvent(" "))
            }
            else -> {
                val converted = hardKeyboard.convert(keyCode, shift)
                if(converted.backspace && states.size > 0) states.remove(states.last())
                if(converted.resultChar != null) {
                    val composed = hangulConverter.compose(lastState, converted.resultChar)
                    states += composed
                } else {
                    reset()
                    EventBus.getDefault().post(CommitStringEvent(KeyCharacterMap.load(KeyCharacterMap.FULL)
                            .get(keyCode, if(shift) KeyEvent.META_SHIFT_ON else 0).toChar().toString()))
                }
            }
        }
        EventBus.getDefault().post(ComposeEvent(lastState.other + lastState.display))
        return true
    }

    fun reset() {
        EventBus.getDefault().post(CommitComposingEvent())
        hardKeyboard.reset()
        states.clear()
    }

}
