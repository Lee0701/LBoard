package io.github.lee0701.lboard.hardkeyboard

data class TwelveKeyboardLayout(
        val layout: Map<Int, LayoutItem>,
        val strokes: List<Map<Int, Int>> = listOf(),
        val labels: Map<Int, String> = mapOf(),
        val cycle: Boolean = true,
        val spaceForSeparation: Boolean = false
) {

    data class LayoutItem(
            val normal: List<Int> = listOf(),
            val shift: List<Int> = listOf()
    ) {
        constructor(normal: Int, shift: Int): this(listOf(normal), listOf(shift))
        constructor(normal: List<Int>): this(normal, normal.toList())
        constructor(normal: Int): this(listOf(normal), listOf(normal))
    }

}
