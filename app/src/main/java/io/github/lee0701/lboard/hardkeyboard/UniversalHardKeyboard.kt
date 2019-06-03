package io.github.lee0701.lboard.hardkeyboard

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
        return currentLayer.layout
                .map { it.key to (if(shift) it.value.shift[0] else it.value.normal[0]).toChar().toString() }.toMap() +
                layout.labels
    }

    override fun serialize(): JSONObject {
        return super.serialize()
    }
}
