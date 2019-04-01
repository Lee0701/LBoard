package io.github.lee0701.lboard

import org.json.JSONObject

class InputMethodLoader {

    fun load(json: JSONObject): InputMethod {
        return Class.forName(json.getString("type"))
                .getDeclaredMethod("deserialize", JSONObject::class.java)
                .invoke(null, json) as InputMethod
    }

    fun store(inputMethod: InputMethod): JSONObject {
        return inputMethod.serialize()
    }

}
