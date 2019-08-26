package io.github.lee0701.lboard.hardkeyboard

import android.view.KeyEvent
import io.github.lee0701.lboard.event.KeyPressEvent
import io.github.lee0701.lboard.event.KeyReleaseEvent
import io.github.lee0701.lboard.layouts.alphabet.Alphabet
import io.github.lee0701.lboard.layouts.hangul.DubeolHangul
import io.github.lee0701.lboard.layouts.hangul.SebeolHangul
import io.github.lee0701.lboard.layouts.hangul.ShinSebeolHangul
import io.github.lee0701.lboard.layouts.hangul.TwelveDubeolHangul
import io.github.lee0701.lboard.layouts.symbols.Symbols
import org.greenrobot.eventbus.EventBus
import org.json.JSONObject

class CommonHardKeyboard(val layout: CommonKeyboardLayout): MoreKeysSupportedHardKeyboard {

    var status: Int = 0

    private val currentLayer: CommonKeyboardLayout.LayoutLayer
        get() = layout[status] ?: layout[0] ?: CommonKeyboardLayout.LayoutLayer()

    private val altLayer: CommonKeyboardLayout.LayoutLayer get() = layout[CommonKeyboardLayout.LAYER_ALT] ?: CommonKeyboardLayout.LayoutLayer()

    var lastCode = 0
    var lastIndex = 0
    var lastShift = false
    var lastAlt = false

    var lastChar = 0

    override fun convert(keyCode: Int, shift: Boolean, alt: Boolean): HardKeyboard.ConvertResult {
        val layer = if(alt) altLayer else currentLayer
        val codes = layer[keyCode]?.let { if(shift) it.shift else it.normal } ?: return HardKeyboard.ConvertResult(null, defaultChar = true)
        var backspace = false
        if(lastCode == keyCode && lastShift == shift && lastAlt == lastAlt) {
            if(++lastIndex >= codes.size) lastIndex = 0
            if(codes.size > 1 && (layout.cycle || lastIndex != 0)) backspace = true
        } else {
            lastIndex = 0
        }
        lastCode = keyCode
        lastShift = shift
        lastAlt = alt

        var result = codes[lastIndex]

        when(result and MASK_SYSTEM_CODE) {
            SystemCode.STROKE -> {
                val strokeTableIndex = result and 0xff
                result = layout.strokes[strokeTableIndex][lastChar] ?: lastChar
                backspace = true
            }
            SystemCode.KEYPRESS -> {
                if(result and SystemCode.KEYPRESS_SHIFT != 0) EventBus.getDefault().post(KeyPressEvent(KeyEvent.KEYCODE_SHIFT_LEFT))
                if(result and SystemCode.KEYPRESS_ALT != 0) EventBus.getDefault().post(KeyPressEvent(KeyEvent.KEYCODE_ALT_LEFT))
                EventBus.getDefault().post(KeyPressEvent(result and 0x0000ffff))
                EventBus.getDefault().post(KeyReleaseEvent(result and 0x0000ffff))
                if(result and SystemCode.KEYPRESS_SHIFT != 0) EventBus.getDefault().post(KeyReleaseEvent(KeyEvent.KEYCODE_SHIFT_LEFT))
                if(result and SystemCode.KEYPRESS_ALT != 0) EventBus.getDefault().post(KeyReleaseEvent(KeyEvent.KEYCODE_ALT_LEFT))

                return HardKeyboard.ConvertResult(null, defaultChar = false)
            }
        }

        lastChar = result

        return HardKeyboard.ConvertResult(result, backspace)
    }

    override fun reset() {
        status = 0

        lastCode = 0
        lastIndex = 0
        lastShift = false
        lastAlt = false

        lastChar = 0
    }

    override fun getLabels(shift: Boolean, alt: Boolean): Map<Int, String> {
        val layer = if(alt) altLayer else currentLayer
        return layer.layout.map { item ->
            item.key to (if(shift) item.value.shift else item.value.normal).map { it.toChar() }.joinToString("")
        }.toMap() + currentLayer.labels
    }

    override fun getMoreKeys(keyCode: Int, shift: Boolean, alt: Boolean): List<Int> {
        if(alt) return listOf()

        val charCodes = (if(shift) currentLayer[keyCode]?.shift else currentLayer[keyCode]?.normal) ?: listOf()
        val charCode = charCodes.firstOrNull() ?: 0

        val keyCodeLayer = layout.layers[CommonKeyboardLayout.LAYER_MORE_KEYS_KEYCODE] ?: CommonKeyboardLayout.LayoutLayer(mapOf())
        val charCodeLayer = layout.layers[CommonKeyboardLayout.LAYER_MORE_KEYS_CHARCODE] ?: CommonKeyboardLayout.LayoutLayer(mapOf())
        return (keyCodeLayer[keyCode]?.normal ?: listOf()) + (charCodeLayer[charCode]?.normal ?: listOf())
    }

    override fun serialize(): JSONObject {
        return super.serialize().apply {
            put("layout", REVERSE_LAYOUTS[layout])
        }
    }

    companion object {

        const val MASK_SYSTEM_CODE = 0x70000000

        @JvmStatic fun deserialize(json: JSONObject): CommonHardKeyboard? {
            val layout = LAYOUTS[json.getString("layout")] ?: return null
            return CommonHardKeyboard(layout)
        }

        val LAYOUTS = mapOf<String, CommonKeyboardLayout>(
                "symbols-a" to Symbols.LAYOUT_SYMBOLS_A,
                "symbols-b" to Symbols.LAYOUT_SYMBOLS_B,
                "symbols-blackberry" to Symbols.LAYOUT_SYMBOLS_BLACKBERRY,
                "symbols-google" to Symbols.LAYOUT_SYMBOLS_GOOGLE,

                "more-keys-top-row-numbers" to Alphabet.MOREKEYS_NUMBERS,
                "more-keys-latin-supplement" to Alphabet.MOREKEYS_LATIN_SUPPLEMENT,
                "more-keys-romanization" to Alphabet.MOREKEYS_ROMANIZATION,

                "alphabet-qwerty" to Alphabet.LAYOUT_QWERTY,
                "alphabet-dvorak" to Alphabet.LAYOUT_DVORAK,
                "alphabet-colemak" to Alphabet.LAYOUT_COLEMAK,
                "alphabet-7cols-wert" to Alphabet.LAYOUT_7COLS_WERT,
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
