package io.github.lee0701.lboard.hardkeyboard

class SimpleHardKeyboard(val layout: SimpleKeyboardLayout): HardKeyboard {

    override fun convert(keyCode: Int, shift: Boolean, alt: Boolean): HardKeyboard.ConvertResult {
        return HardKeyboard.ConvertResult(layout.layout[keyCode]?.let { if(shift) it.shift else it.normal })
    }

    override fun getLabels(shift: Boolean, alt: Boolean): Map<Int, String> {
        return layout.layout.map { it.key to (if(shift) it.value.shift else it.value.normal).toChar().toString() }.toMap()
    }

    override fun reset() {
    }
}
