package io.github.lee0701.lboard.preconverter

import org.json.JSONArray
import org.json.JSONObject

data class KeyboardLayout(val layout: Map<Int, LayoutItem>) {

    fun serialize(): JSONObject {
        return JSONObject().apply {
            put("layout", JSONArray().apply {
                layout.forEach { this.put(JSONObject().apply {
                    put("keyCode", it.key)
                    put("item", it.value.serialize())
                }) }
            })
        }
    }

    companion object {
        fun deserialize(json: JSONObject): KeyboardLayout {
            val layout = json.getJSONArray("layout").let { layout ->
                (0 until layout.length()).map { i ->
                    layout.getJSONObject(i).let { it.getInt("keyCode") to KeyboardLayout.LayoutItem.deserialize(it.getJSONObject("item")) }
                }
            }.toMap()
            return KeyboardLayout(layout)
        }
    }

    data class LayoutItem(
            val normal: List<Char> = listOf(),
            val shift: List<Char> = listOf()
    ) {
        constructor(normal: Char, shift: Char): this(listOf(normal), listOf(shift))
        constructor(normal: Char): this(listOf(normal), listOf())

        fun serialize(): JSONObject {
            return JSONObject().apply {
                put("normal", JSONArray().apply { normal.forEach { put(it.toInt()) } })
                put("shift", JSONArray().apply { shift.forEach { put(it.toInt()) } })
            }
        }

        companion object {
            fun deserialize(json: JSONObject): LayoutItem {
                return LayoutItem(
                        getKeycodeList(json.getJSONArray("normal")),
                        getKeycodeList(json.getJSONArray("shift"))
                )
            }

            fun getKeycodeList(array: JSONArray): List<Char> = array.let { (0 until array.length()).map { i -> array.getInt(i).toChar() } }
        }

    }

}
