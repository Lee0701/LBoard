package io.github.lee0701.lboard

import org.json.JSONObject

interface InputMethodModule {

    val name: String

    fun serialize(): JSONObject = JSONObject().apply {
        put("name", name)
        put("class", this@InputMethodModule.javaClass.name)
    }

}
