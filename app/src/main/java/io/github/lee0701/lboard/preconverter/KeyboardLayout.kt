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
            val shift: List<Char> = listOf(),
            val alt: List<Char> = listOf(),
            val altShift: List<Char> = listOf()
    ) {
        constructor(normal: Char, shift: Char, alt: Char, altShift: Char): this(listOf(normal), listOf(shift), listOf(alt), listOf(altShift))
        constructor(normal: Char, shift: Char, alt: Char): this(listOf(normal), listOf(shift), listOf(alt))
        constructor(normal: Char, shift: Char): this(listOf(normal), listOf(shift), listOf())
        constructor(normal: Char): this(listOf(normal), listOf(), listOf())

        fun serialize(): JSONObject {
            return JSONObject().apply {
                put("normal", JSONArray().apply { normal.forEach { put(it.toInt()) } })
                put("shift", JSONArray().apply { shift.forEach { put(it.toInt()) } })
                put("alt", JSONArray().apply { alt.forEach { put(it.toInt()) } })
                put("altShift", JSONArray().apply { altShift.forEach { put(it.toInt()) } })
            }
        }

        companion object {
            fun deserialize(json: JSONObject): LayoutItem {
                return LayoutItem(
                        getKeycodeList(json.getJSONArray("normal")),
                        getKeycodeList(json.getJSONArray("shift")),
                        getKeycodeList(json.getJSONArray("alt")),
                        getKeycodeList(json.getJSONArray("altShift"))
                )
            }

            fun getKeycodeList(array: JSONArray): List<Char> = array.let { (0 until array.length()).map { i -> array.getInt(i).toChar() } }
        }

    }

}
