package io.github.lee0701.lboard.preconverter

interface KeyboardLayout {
    val layout: Map<Int, LayoutItem>

    data class LayoutItem(
            val normal: List<Char> = listOf(),
            val shift: List<Char> = listOf(),
            val alt: List<Char> = listOf(),
            val altShift: List<Char> = listOf()
    ) {
        constructor(normal: Char, shift: Char, alt: Char, altShift: Char): this(listOf(normal), listOf(shift), listOf(alt), listOf(altShift))
        constructor(normal: Char, shift: Char, alt: Char): this(listOf(normal), listOf(shift), listOf(alt))
        constructor(normal: Char, shift: Char): this(listOf(normal), listOf(shift), listOf())
        constructor(normal: Char): this(listOf(normal), listOf(), listOf())
    }

}
