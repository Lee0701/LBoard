package io.github.lee0701.lboard.preconverter

import org.json.JSONObject

class TwelveKeyLayoutConverter(override val name: String, val layout: KeyboardLayout, val cycle: Boolean = true): PreConverter {

    override fun convert(text: ComposingText): ComposingText {
        val result = mutableListOf<ComposingText.CharToken>()
        var lastCode = 0
        var lastIndex = 0
        var shift = false
        text.layers.last().tokens.forEach { token ->
            if(token is ComposingText.KeyInputToken) {
                val codes = getCodes(token.keyCode, token.shift) ?: return@forEach
                if(lastCode == token.keyCode && shift == token.shift) {
                    if(++lastIndex >= codes.size) lastIndex = 0
                    if(codes.size > 1 && (cycle || lastIndex != 0)) result.remove(result.last())
                } else {
                    lastIndex = 0
                }

                result += ComposingText.CharToken(codes[lastIndex])

                lastCode = token.keyCode
                shift = token.shift
            }
        }
        return text.copy(layers = text.layers + ComposingText.Layer(result))
    }

    override fun serialize(): JSONObject {
        return super.serialize().apply {
            put("layout", layout.serialize())
        }
    }

    private fun getCodes(keyCode: Int, shift: Boolean): List<Char>? =
            layout.layout[keyCode]?.let { if(shift) it.shift else it.normal }

    companion object {
        @JvmStatic fun deserialize(json: JSONObject): SimpleLayoutConverter {
            return SimpleLayoutConverter(json.getString("name"), KeyboardLayout.deserialize(json.getJSONObject("layout")))
        }
    }

}
