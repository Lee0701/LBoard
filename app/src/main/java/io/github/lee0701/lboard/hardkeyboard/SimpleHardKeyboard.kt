package io.github.lee0701.lboard.hardkeyboard

class SimpleHardKeyboard(val layout: KeyboardLayout): HardKeyboard {

    override fun convert(keyCode: Int, shift: Boolean): HardKeyboard.ConvertResult {
        return HardKeyboard.ConvertResult(layout.layout[keyCode]?.let { if(shift) it.shift else it.normal }?.get(0))
    }

    override fun reset() {

    }
}
