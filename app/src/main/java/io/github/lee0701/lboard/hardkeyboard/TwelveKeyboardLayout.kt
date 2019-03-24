package io.github.lee0701.lboard.hardkeyboard

data class TwelveKeyboardLayout(
        val layout: Map<Int, LayoutItem>,
        val altLayout: Map<Int, LayoutItem> = layout.toMap()
) {

    data class LayoutItem(
            val normal: List<Char> = listOf(),
            val shift: List<Char> = listOf()
    ) {
        constructor(normal: Char, shift: Char): this(listOf(normal), listOf(shift))
        constructor(normal: List<Char>): this(normal, normal.toList())
        constructor(normal: Char): this(listOf(normal), listOf(normal))
    }

}
