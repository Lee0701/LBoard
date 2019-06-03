package io.github.lee0701.lboard

import android.content.Context
import android.view.View
import io.github.lee0701.lboard.hardkeyboard.HardKeyboard
import io.github.lee0701.lboard.softkeyboard.SoftKeyboard
import org.json.JSONObject

interface InputMethod {

    var shift: Boolean
    var alt: Boolean

    val softKeyboard: SoftKeyboard
    val hardKeyboard: HardKeyboard

    fun initView(context: Context): View?
    fun updateView(context: Context): View?

    fun onKeyPress(keyCode: Int): Boolean
    fun onKeyRelease(keyCode: Int): Boolean

    fun reset()

    fun serialize(): JSONObject {
        return JSONObject().apply {
            put("type", this@InputMethod.javaClass.name)
        }
    }

    companion object {
        fun deserializeModule(json: JSONObject): InputMethodModule {
            val type = json.getString("type")
            return Class.forName(type).getDeclaredMethod("deserialize", JSONObject::class.java)
                    .invoke(null, json) as InputMethodModule
        }

    }

}
