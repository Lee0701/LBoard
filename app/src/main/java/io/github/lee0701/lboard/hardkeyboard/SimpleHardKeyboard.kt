package io.github.lee0701.lboard.hardkeyboard

class SimpleHardKeyboard(val layout: SimpleKeyboardLayout): HardKeyboard {

    var shift: Boolean = false
    var alt: Boolean = false

    override fun convert(keyCode: Int, shift: Boolean, alt: Boolean): HardKeyboard.ConvertResult {
        return HardKeyboard.ConvertResult(layout.layout[keyCode]?.let { if(shift) it.shift else it.normal })
    }

    override fun getLabels(shift: Boolean, alt: Boolean): Map<Int, String> =
            (if(alt) layout.altLayout else layout.layout)
                    .map { it.key to (if(shift) it.value.shift else it.value.normal).toString() }.toMap()

    override fun reset() {
        shift = false
        alt = false
    }
}
