package io.github.lee0701.lboard.hardkeyboard

class SimpleHardKeyboard(val layout: SimpleKeyboardLayout): HardKeyboard {

    override fun convert(keyCode: Int, shift: Boolean): HardKeyboard.ConvertResult {
        return HardKeyboard.ConvertResult(layout.layout[keyCode]?.let { if(shift) it.shift else it.normal })
    }

    override fun reset() {

    }
}
