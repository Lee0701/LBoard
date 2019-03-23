package io.github.lee0701.lboard.hardkeyboard

class TwelveKeyHardKeyboard(val layout: KeyboardLayout, val cycle: Boolean = true): HardKeyboard {

    var lastCode = 0
    var lastIndex = 0
    var shift = false

    override fun convert(keyCode: Int, shift: Boolean): HardKeyboard.ConvertResult {
        val codes = layout.layout[keyCode]?.let { if(shift) it.shift else it.normal } ?: return HardKeyboard.ConvertResult(null)
        var backspace = false
        if(this.lastCode == keyCode && this.shift == shift) {
            if(++lastIndex >= codes.size) lastIndex = 0
            if(codes.size > 1 && (cycle || lastIndex != 0)) backspace = true
        } else {
            lastIndex = 0
        }
        this.lastCode = keyCode
        this.shift = shift

        return HardKeyboard.ConvertResult(codes[lastIndex], backspace)
    }

    override fun reset() {
        lastCode = 0
        lastIndex = 0
        shift = false
    }

}
