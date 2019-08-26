package io.github.lee0701.lboard

import android.content.Context
import android.content.SharedPreferences
import android.view.View
import io.github.lee0701.lboard.old_event.SoftKeyFlickEvent
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.EventBusException
import org.json.JSONObject

interface InputMethod {

    var shift: Boolean
    var alt: Boolean

    fun init() {
        try {
            EventBus.getDefault().register(this)
        } catch(ex: EventBusException) {
            // Do nothing.
        }
    }

    fun destroy() {
        try {
            EventBus.getDefault().unregister(this)
        } catch(ex: EventBusException) {
            // Do nothing.
        }
    }

    fun initView(context: Context): View?
    fun updateView(context: Context): View?

    fun onKeyPress(keyCode: Int): Boolean
    fun onKeyRelease(keyCode: Int): Boolean
    fun onKeyLongPress(keyCode: Int): Boolean
    fun onKeyFlick(keyCode: Int, direction: SoftKeyFlickEvent.FlickDirection): Boolean

    fun reset()

    fun setPreferences(pref: SharedPreferences) {

    }

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
