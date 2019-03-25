package io.github.lee0701.lboard.hardkeyboard

class HangulConverterLinkedHardKeyboard(val layouts: List<SimpleKeyboardLayout>): HardKeyboard {

    var status: Int = 0

    val currentLayout: SimpleKeyboardLayout? get() = layouts.getOrNull(status)

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
}
