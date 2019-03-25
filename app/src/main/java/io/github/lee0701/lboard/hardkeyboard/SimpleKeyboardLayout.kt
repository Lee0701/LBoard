package io.github.lee0701.lboard.hardkeyboard

data class SimpleKeyboardLayout(
        val layout: Map<Int, LayoutItem>
) {

    operator fun plus(other: SimpleKeyboardLayout): SimpleKeyboardLayout {
        return SimpleKeyboardLayout(this.layout + other.layout)
    }

    data class LayoutItem(
            val normal: Int,
            val shift: Int
    ) {
        constructor(normal: Int): this(normal, normal)
    }

}
