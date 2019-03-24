package io.github.lee0701.lboard.hardkeyboard

class TwelveKeyHardKeyboard(
        val layout: TwelveKeyboardLayout,
        val cycle: Boolean = true,
        val spaceForSeparation: Boolean = false
): HardKeyboard {

    var lastCode = 0
    var lastIndex = 0
    var lastShift = false
    var lastAlt = false

    override fun convert(keyCode: Int, shift: Boolean, alt: Boolean): HardKeyboard.ConvertResult {
        val codes = layout.layout[keyCode]?.let { if(lastShift) it.shift else it.normal } ?: return HardKeyboard.ConvertResult(null)
        var backspace = false
        if(lastCode == keyCode && lastShift == shift && lastAlt == alt) {
            if(++lastIndex >= codes.size) lastIndex = 0
            if(codes.size > 1 && (cycle || lastIndex != 0)) backspace = true
        } else {
            lastIndex = 0
        }
        lastCode = keyCode
        lastShift = shift
        lastAlt = alt

        return HardKeyboard.ConvertResult(codes[lastIndex], backspace)

    }

    override fun getLabels(shift: Boolean, alt: Boolean): Map<Int, String> =
            (if(alt) layout.altLayout else layout.layout)
                    .map { it.key to (if(shift) it.value.shift else it.value.normal).map { it.toChar() }.joinToString("") }.toMap()

    override fun reset() {
        lastCode = 0
        lastIndex = 0
        lastShift = false
        lastAlt = false
    }

}
