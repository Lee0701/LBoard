package io.github.lee0701.lboard

import android.content.Context
import android.view.KeyEvent
import android.view.View
import io.github.lee0701.lboard.event.ComposeEvent
import io.github.lee0701.lboard.preconverter.ComposingText
import io.github.lee0701.lboard.preconverter.PreConverter
import io.github.lee0701.lboard.softkeyboard.SoftKeyboard
import org.greenrobot.eventbus.EventBus

class InputMethod(
        val softKeyboard: SoftKeyboard,
        val preConverters: List<PreConverter>
) {

    val composingText: MutableList<ComposingText> = mutableListOf()

    fun initView(context: Context): View? {
        return softKeyboard.initView(context)
    }

    fun onKey(keyCode: Int): Boolean {
        when(keyCode) {
            KeyEvent.KEYCODE_DEL -> if(composingText.size > 0) composingText.remove(composingText.last()) else return false
            else -> {
                val previousState = if(composingText.size > 0) composingText.last() else DEFAULT_COMPOSING_TEXT
                composingText += previousState.copy(listOf(ComposingText.Layer(previousState.layers[0].tokens + ComposingText.TokenList(listOf(ComposingText.KeyInputToken(keyCode))))))
            }
        }
        EventBus.getDefault().post(ComposeEvent(preConvert()))
        return true
    }

    private fun preConvert(): ComposingText {
        if(composingText.isEmpty()) return DEFAULT_COMPOSING_TEXT
        var current = composingText.last()
        preConverters.forEach { current = it.convert(current) }
        return current
    }

    companion object {
        val DEFAULT_COMPOSING_TEXT = ComposingText(listOf(ComposingText.Layer(listOf())))
    }

}
