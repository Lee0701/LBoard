package io.github.lee0701.lboard

import android.content.Context
import android.view.KeyEvent
import android.view.View
import io.github.lee0701.lboard.event.ComposeEvent
import io.github.lee0701.lboard.preconverter.ComposingText
import io.github.lee0701.lboard.preconverter.PreConverter
import io.github.lee0701.lboard.softkeyboard.SoftKeyboard
import org.greenrobot.eventbus.EventBus
import org.json.JSONArray
import org.json.JSONObject

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
                composingText += previousState.copy(listOf(ComposingText.Layer(previousState.layers[0].tokens + ComposingText.KeyInputToken(keyCode, false, false))))
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

    fun serialize(): JSONObject {
        return JSONObject().apply {
            put("softKeyboard", softKeyboard.serialize())
            put("preConverters", JSONArray().apply {
                preConverters.forEach { preConverter ->
                    put(preConverter.serialize())
                }
            })
        }
    }

    companion object {
        val DEFAULT_COMPOSING_TEXT = ComposingText(listOf(ComposingText.Layer(listOf())))

        fun deserialize(json: JSONObject): InputMethod {
            val softKeyboard = json.getJSONObject("softKeyboard").let { softKeyboard ->
                Class.forName(softKeyboard.getString("class")).getDeclaredMethod("deserialize", JSONObject::class.java)
                        .invoke(null, softKeyboard) as SoftKeyboard
            }
            val preConverters = json.getJSONArray("preConverters").let { preConverters ->
                (0 until preConverters.length()).map { i ->
                    val preConverter = preConverters.getJSONObject(i)
                    Class.forName(preConverter.getString("class")).getDeclaredMethod("deserialize", JSONObject::class.java)
                            .invoke(null, preConverter) as PreConverter
                }
            }
            return InputMethod(softKeyboard, preConverters)
        }

    }

}
