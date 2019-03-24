package io.github.lee0701.lboard.hardkeyboard

data class SimpleKeyboardLayout(
        val layout: Map<Int, LayoutItem>,
        val altLayout: Map<Int, LayoutItem> = layout.toMap()
) {

    data class LayoutItem(
            val normal: Char,
            val shift: Char
    ) {
        constructor(normal: Char): this(normal, normal)
    }

}
