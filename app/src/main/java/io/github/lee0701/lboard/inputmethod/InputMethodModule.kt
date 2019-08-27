package io.github.lee0701.lboard.inputmethod

import android.content.SharedPreferences
import org.json.JSONObject

interface InputMethodModule {

    fun setPreferences(pref: SharedPreferences) {

    }

    fun serialize(): JSONObject {
        return JSONObject().apply {
            put("type", this@InputMethodModule.javaClass.name)
        }
    }
}
