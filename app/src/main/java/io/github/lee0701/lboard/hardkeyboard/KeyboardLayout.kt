package io.github.lee0701.lboard.hardkeyboard

data class KeyboardLayout(val layout: Map<Int, LayoutItem>) {

    data class LayoutItem(
            val normal: List<Char> = listOf(),
            val shift: List<Char> = listOf()
    ) {
        constructor(normal: Char, shift: Char): this(listOf(normal), listOf(shift))
        constructor(normal: Char): this(listOf(normal), listOf())
    }

}
