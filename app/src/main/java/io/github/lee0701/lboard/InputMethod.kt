package io.github.lee0701.lboard

import android.content.Context
import android.view.KeyEvent
import android.view.View
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

    fun initView(context: Context): View? {
        return softKeyboard.initView(context)
    }

    fun onKey(keyCode: Int, shift: Boolean): Boolean {
        when(keyCode) {
            KeyEvent.KEYCODE_DEL -> if(states.size > 0) states.remove(states.last()) else return false
            KeyEvent.KEYCODE_SPACE -> {
                hardKeyboard.reset()
                states.clear()
                EventBus.getDefault().post(CommitStringEvent(" "))
            }
            else -> {
                val converted = hardKeyboard.convert(keyCode, shift)
                if(converted.backspace && states.size > 0) states.remove(states.last())
                if(converted.resultChar != null) {
                    val composed = hangulConverter.compose(if(states.isEmpty()) HangulConverter.State() else states.last(), converted.resultChar)
                    states += composed
                } else return false
            }
        }
        EventBus.getDefault().post(ComposeEvent(states.last().other + states.last().display))
        return true
    }

    fun reset() {

    }

}
