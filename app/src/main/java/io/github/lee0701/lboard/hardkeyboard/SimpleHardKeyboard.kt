package io.github.lee0701.lboard.hardkeyboard

import io.github.lee0701.lboard.layouts.alphabet.Alphabet
import io.github.lee0701.lboard.layouts.hangul.DubeolHangul
import io.github.lee0701.lboard.layouts.hangul.SebeolHangul
import org.json.JSONObject

class SimpleHardKeyboard(val layout: SimpleKeyboardLayout): HardKeyboard {

    override fun convert(keyCode: Int, shift: Boolean, alt: Boolean): HardKeyboard.ConvertResult {
        return HardKeyboard.ConvertResult(layout.layout[keyCode]?.let { if(shift) it.shift else it.normal })
    }

    override fun getLabels(shift: Boolean, alt: Boolean): Map<Int, String> {
        return layout.layout.map { it.key to (if(shift) it.value.shift else it.value.normal).toChar().toString() }.toMap()
    }

    override fun reset() {
    }

    override fun serialize(): JSONObject {
        return super.serialize().apply {
            put("layout", REVERSE_LAYOUTS[layout])
        }
    }

    companion object {

        @JvmStatic fun deserialize(json: JSONObject): SimpleHardKeyboard? {
            val layout = LAYOUTS[json.getString("layout")] ?: return null
            return SimpleHardKeyboard(layout)
        }

        val LAYOUTS = mapOf<String, SimpleKeyboardLayout>(
//                "alphabet-qwerty" to Alphabet.LAYOUT_QWERTY,
//                "dubeol-standard" to DubeolHangul.LAYOUT_DUBEOL_STANDARD,
//                "sebeol-390" to SebeolHangul.LAYOUT_SEBEOL_390,
//                "sebeol-391" to SebeolHangul.LAYOUT_SEBEOL_391
        )
        val REVERSE_LAYOUTS = LAYOUTS.map { it.value to it.key }.toMap()
    }

}
