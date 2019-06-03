package io.github.lee0701.lboard.hardkeyboard

import io.github.lee0701.lboard.layouts.hangul.ShinSebeolHangul
import org.json.JSONObject

class HangulConverterLinkedHardKeyboard(val layouts: List<SimpleKeyboardLayout>): HardKeyboard {

    var status: Int = 0

    private val currentLayout: SimpleKeyboardLayout? get() = layouts.getOrNull(status)

    override fun convert(keyCode: Int, shift: Boolean, alt: Boolean): HardKeyboard.ConvertResult {
        return HardKeyboard.ConvertResult((currentLayout ?: layouts[0]).layout[keyCode]?.let { if(shift) it.shift else it.normal })
    }

    override fun reset() {
        status = 0
    }

    override fun getLabels(shift: Boolean, alt: Boolean): Map<Int, String> {
        return (currentLayout?.layout ?: layouts[0].layout)
                .map { it.key to (if(shift) it.value.shift else it.value.normal).toChar().toString() }.toMap()
    }

    override fun serialize(): JSONObject {
        return super.serialize().apply {
            put("layout", REVERSE_LAYOUTS[layouts])
        }
    }

    companion object {

        @JvmStatic fun deserialize(json: JSONObject): HangulConverterLinkedHardKeyboard? {
            val layout = LAYOUTS[json.getString("layout")] ?: return null
            return HangulConverterLinkedHardKeyboard(layout)
        }

        val LAYOUTS = mapOf<String, List<SimpleKeyboardLayout>>(
//                "sebeol-shin-original" to ShinSebeolHangul.LAYOUT_SHIN_ORIGINAL,
//                "sebeol-shin-edit" to ShinSebeolHangul.LAYOUT_SHIN_EDIT
        )
        val REVERSE_LAYOUTS = LAYOUTS.map { it.value to it.key }.toMap()
    }

}
