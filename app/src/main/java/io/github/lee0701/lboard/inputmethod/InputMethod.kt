package io.github.lee0701.lboard.inputmethod

import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.EventBusException
import org.json.JSONObject

interface InputMethod {

    val info: InputMethodInfo

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

    fun serialize(): JSONObject {
        return JSONObject().apply {
            put("device", this@InputMethod.javaClass.name)
        }
    }

    companion object {
        fun deserializeModule(json: JSONObject): InputMethodModule {
            val type = json.getString("device")
            return Class.forName(type).getDeclaredMethod("deserialize", JSONObject::class.java)
                    .invoke(null, json) as InputMethodModule
        }

    }

}
