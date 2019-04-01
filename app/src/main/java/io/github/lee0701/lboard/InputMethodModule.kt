package io.github.lee0701.lboard

import org.json.JSONObject

interface InputMethodModule {
    fun serialize(): JSONObject {
        return JSONObject().apply {
            put("type", this@InputMethodModule.javaClass.name)
        }
    }
}
