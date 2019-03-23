package io.github.lee0701.lboard.preconverter

import org.json.JSONObject

class SimpleLayoutConverter(override val name: String, val layout: KeyboardLayout): PreConverter {

    override fun convert(text: ComposingText): ComposingText {
        return text.copy(layers = text.layers + ComposingText.Layer(text.layers.last().tokens.map { token ->
            if(token is ComposingText.KeyInputToken) {
                val codes = getCodes(token.keyCode, token.shift)
                ComposingText.CharToken(codes?.get(0) ?: token.representingChar)
            } else token
        }))
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
