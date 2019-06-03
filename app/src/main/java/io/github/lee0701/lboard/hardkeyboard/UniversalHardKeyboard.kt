package io.github.lee0701.lboard.hardkeyboard

import io.github.lee0701.lboard.layouts.alphabet.Alphabet
import io.github.lee0701.lboard.layouts.hangul.DubeolHangul
import io.github.lee0701.lboard.layouts.hangul.SebeolHangul
import io.github.lee0701.lboard.layouts.hangul.ShinSebeolHangul
import io.github.lee0701.lboard.layouts.hangul.TwelveDubeolHangul
import org.json.JSONObject

class UniversalHardKeyboard(val layout: UniversalKeyboardLayout): HardKeyboard {

    var status: Int = 0

    private val currentLayer: UniversalKeyboardLayout.LayoutLayer get() = layout[status] ?: layout[0] ?: UniversalKeyboardLayout.LayoutLayer()

    var lastCode = 0
    var lastIndex = 0
    var lastShift = false

    var lastChar = 0

    override fun convert(keyCode: Int, shift: Boolean, alt: Boolean): HardKeyboard.ConvertResult {
        val codes = currentLayer[keyCode]?.let { if(shift) it.shift else it.normal } ?: return HardKeyboard.ConvertResult(null)
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

    override fun reset() {
        status = 0

        lastCode = 0
        lastIndex = 0
        lastShift = false

        lastChar = 0
    }

    override fun getLabels(shift: Boolean, alt: Boolean): Map<Int, String> {
        return currentLayer.layout.map { it.key to (if(shift) it.value.shift else it.value.normal).map { it.toChar() }.joinToString("") }.toMap() +
                layout.labels
    }

    override fun serialize(): JSONObject {
        return super.serialize().apply {
            put("layout", REVERSE_LAYOUTS[layout])
        }
    }

    companion object {

        @JvmStatic fun deserialize(json: JSONObject): UniversalHardKeyboard? {
            val layout = LAYOUTS[json.getString("layout")] ?: return null
            return UniversalHardKeyboard(layout)
        }

        val LAYOUTS = mapOf<String, UniversalKeyboardLayout>(
                "alphabet-qwerty" to Alphabet.LAYOUT_QWERTY,
                "dubeol-standard" to DubeolHangul.LAYOUT_DUBEOL_STANDARD,
                "sebeol-390" to SebeolHangul.LAYOUT_SEBEOL_390,
                "sebeol-391" to SebeolHangul.LAYOUT_SEBEOL_391,

                "sebeol-shin-original" to ShinSebeolHangul.LAYOUT_SHIN_ORIGINAL,
                "sebeol-shin-edit" to ShinSebeolHangul.LAYOUT_SHIN_EDIT,

                "dubeol-cheonjiin" to TwelveDubeolHangul.LAYOUT_CHEONJIIN,
                "dubeol-naratgeul" to TwelveDubeolHangul.LAYOUT_NARATGEUL
        )
        val REVERSE_LAYOUTS = LAYOUTS.map { it.value to it.key }.toMap()
    }

}
