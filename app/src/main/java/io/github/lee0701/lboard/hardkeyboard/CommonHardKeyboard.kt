package io.github.lee0701.lboard.hardkeyboard

import io.github.lee0701.lboard.event.KeyPressEvent
import io.github.lee0701.lboard.event.LBoardKeyEvent
import io.github.lee0701.lboard.layouts.alphabet.Alphabet
import io.github.lee0701.lboard.layouts.hangul.DubeolHangul
import io.github.lee0701.lboard.layouts.hangul.SebeolHangul
import io.github.lee0701.lboard.layouts.hangul.ShinSebeolHangul
import io.github.lee0701.lboard.layouts.hangul.MobileDubeolHangul
import io.github.lee0701.lboard.layouts.symbols.MoreKeys
import io.github.lee0701.lboard.layouts.symbols.Symbols
import org.greenrobot.eventbus.EventBus
import org.json.JSONObject

class CommonHardKeyboard(
        val layout: CommonKeyboardLayout
): MoreKeysSupportedHardKeyboard {

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
        val multiTapShift = layout.timeout && lastShift && lastCode == keyCode
        val codes = layer[keyCode]?.let { if(shift || multiTapShift) it.shift else it.normal } ?: return HardKeyboard.ConvertResult(null, defaultChar = true)
        var backspace = false
        if(lastCode == keyCode && lastAlt == lastAlt) {
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
                sendKeyEvent(result and 0x0000ffff, result and SystemCode.KEYPRESS_SHIFT != 0, result and SystemCode.KEYPRESS_ALT != 0, LBoardKeyEvent.ActionType.PRESS)
                sendKeyEvent(result and 0x0000ffff, result and SystemCode.KEYPRESS_SHIFT != 0, result and SystemCode.KEYPRESS_ALT != 0, LBoardKeyEvent.ActionType.RELEASE)

                return HardKeyboard.ConvertResult(null, defaultChar = false)
            }
        }

        lastChar = result

        return HardKeyboard.ConvertResult(result, backspace)
    }

    private fun sendKeyEvent(keyCode: Int, shift: Boolean, alt: Boolean, type: LBoardKeyEvent.ActionType) {
        EventBus.getDefault().post(KeyPressEvent(keyCode, shift, alt, LBoardKeyEvent.Source.INTERNAL, type))
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
        }.toMap() + currentLayer.labels.mapValues { if(shift) it.value.second else it.value.first }
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

        val LAYOUTS = mapOf<String, CommonKeyboardLayout>(
                "symbols-a" to Symbols.LAYOUT_SYMBOLS_A,
                "symbols-b" to Symbols.LAYOUT_SYMBOLS_B,
                "symbols-blackberry" to Symbols.LAYOUT_SYMBOLS_BLACKBERRY,
                "symbols-google" to Symbols.LAYOUT_SYMBOLS_GOOGLE,

                "more-keys-top-row-numbers" to MoreKeys.MOREKEYS_NUMBERS,
                "more-keys-latin-supplement" to MoreKeys.MOREKEYS_LATIN_SUPPLEMENT,
                "more-keys-romanization" to MoreKeys.MOREKEYS_ROMANIZATION,

                "alphabet-qwerty" to Alphabet.LAYOUT_QWERTY,
                "alphabet-dvorak" to Alphabet.LAYOUT_DVORAK,
                "alphabet-colemak" to Alphabet.LAYOUT_COLEMAK,
                "alphabet-7cols-wert" to Alphabet.LAYOUT_7COLS_WERT,
                "dubeol-standard" to DubeolHangul.LAYOUT_DUBEOL_STANDARD,
                "sebeol-390" to SebeolHangul.LAYOUT_SEBEOL_390,
                "sebeol-391" to SebeolHangul.LAYOUT_SEBEOL_391,

                "sebeol-shin-original" to ShinSebeolHangul.LAYOUT_SHIN_ORIGINAL,
                "sebeol-shin-edit" to ShinSebeolHangul.LAYOUT_SHIN_EDIT,

                "dubeol-cheonjiin" to MobileDubeolHangul.LAYOUT_CHEONJIIN,
                "dubeol-naratgeul" to MobileDubeolHangul.LAYOUT_NARATGEUL
        )
        val REVERSE_LAYOUTS = LAYOUTS.map { it.value to it.key }.toMap()
    }

}
