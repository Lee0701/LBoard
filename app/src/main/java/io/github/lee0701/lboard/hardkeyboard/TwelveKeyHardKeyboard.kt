package io.github.lee0701.lboard.hardkeyboard

import io.github.lee0701.lboard.layouts.hangul.TwelveDubeolHangul
import org.json.JSONObject

class TwelveKeyHardKeyboard(
        val layout: TwelveKeyboardLayout
): HardKeyboard {

    var lastCode = 0
    var lastIndex = 0
    var lastShift = false

    var lastChar = 0

    override fun convert(keyCode: Int, shift: Boolean, alt: Boolean): HardKeyboard.ConvertResult {
        val codes = layout.layout[keyCode]?.let { if(lastShift) it.shift else it.normal } ?: return HardKeyboard.ConvertResult(null)
        var backspace = false
        if(lastCode == keyCode && lastShift == shift) {
            if(++lastIndex >= codes.size) lastIndex = 0
            if(codes.size > 1 && (layout.cycle || lastIndex != 0)) backspace = true
        } else {
            lastIndex = 0
        }
        lastCode = keyCode
        lastShift = shift

        var result = codes[lastIndex]

        if(result and 0x70000000 == 0x70000000) {
            val strokeTableIndex = result and 0x000000ff
            result = layout.strokes[strokeTableIndex][lastChar] ?: lastChar
            backspace = true
        }

        lastChar = result

        return HardKeyboard.ConvertResult(result, backspace)
    }

    override fun getLabels(shift: Boolean, alt: Boolean): Map<Int, String> {
        return layout.layout.map { entry ->
            entry.key to (if(shift) entry.value.shift else entry.value.normal)
                    .map { it.toChar() }.joinToString("")
        }.toMap() + layout.labels
    }

    override fun reset() {
        lastCode = 0
        lastIndex = 0
        lastShift = false

        lastChar = 0
    }

    override fun serialize(): JSONObject {
        return super.serialize().apply {
            put("layout", REVERSE_LAYOUTS[layout])
        }
    }

    companion object {

        @JvmStatic fun deserialize(json: JSONObject): TwelveKeyHardKeyboard? {
            val layout = LAYOUTS[json.getString("layout")] ?: return null
            return TwelveKeyHardKeyboard(layout)
        }

        val LAYOUTS = mapOf<String, TwelveKeyboardLayout>(
//                "dubeol-cheonjiin" to TwelveDubeolHangul.LAYOUT_CHEONJIIN,
//                "dubeol-naratgeul" to TwelveDubeolHangul.LAYOUT_NARATGEUL
        )
        val REVERSE_LAYOUTS = LAYOUTS.map { it.value to it.key }.toMap()
    }

}
