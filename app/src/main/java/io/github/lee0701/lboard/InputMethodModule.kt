package io.github.lee0701.lboard

import android.content.SharedPreferences
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.EventBusException
import org.json.JSONObject

interface InputMethodModule {

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

    fun setPreferences(pref: SharedPreferences) {

    }

    fun serialize(): JSONObject {
        return JSONObject().apply {
            put("type", this@InputMethodModule.javaClass.name)
        }
    }
}
