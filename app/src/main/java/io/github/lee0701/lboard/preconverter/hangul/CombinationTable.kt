package io.github.lee0701.lboard.preconverter.hangul

import io.github.lee0701.lboard.preconverter.KeyboardLayout
import org.json.JSONArray
import org.json.JSONObject

data class CombinationTable (
        val combinations: Map<Pair<Char, Char>, Char>
) {

    fun serialize(): JSONObject {
        return JSONObject().apply { put("combinations", JSONArray().apply {
            combinations.forEach { put(JSONObject().apply {
                put("a", it.key.first.toInt())
                put("b", it.key.second.toInt())
                put("result", it.value.toInt())
            }) }
        }) }
    }

    companion object {
        fun deserialize(json: JSONObject): CombinationTable {
            val combinations = json.getJSONArray("combinations").let { combinations ->
                (0 until combinations.length()).map { i ->
                    combinations.getJSONObject(i).let { (it.getInt("a").toChar() to it.getInt("b").toChar()) to it.getInt("result").toChar() }
                }
            }.toMap()
            return CombinationTable(combinations)
        }
    }

}
