package io.github.lee0701.lboard.hardkeyboard

data class CommonKeyboardLayout(
        val layers: Map<Int, LayoutLayer> = mapOf(0 to LayoutLayer()),
        val strokes: List<StrokeTable> = listOf(),
        val cycle: Boolean = true,
        val spaceForSeparation: Boolean = false
) {

    constructor(layerId: Int, main: LayoutLayer, strokes: List<StrokeTable> = listOf(), cycle: Boolean = true, spaceForSeparation: Boolean = false):
            this(mapOf(layerId to main), strokes, cycle, spaceForSeparation)

    constructor(main: LayoutLayer, strokes: List<StrokeTable> = listOf(), cycle: Boolean = true, spaceForSeparation: Boolean = false):
            this(0, main, strokes, cycle, spaceForSeparation)

    operator fun get(i: Int): LayoutLayer? = layers[i]

    operator fun plus(other: CommonKeyboardLayout): CommonKeyboardLayout {
        return CommonKeyboardLayout(
                (this.layers + other.layers).map { it.key to (this[it.key] ?: LayoutLayer(mapOf())) + (other[it.key] ?: LayoutLayer(mapOf())) }.toMap(),
                other.strokes,
                other.cycle,
                other.spaceForSeparation
        )
    }

    operator fun times(other: CommonKeyboardLayout): CommonKeyboardLayout {
        return CommonKeyboardLayout(
                (this.layers + other.layers).map { it.key to (this[it.key] ?: this[0] ?: LayoutLayer(mapOf())) + (other[it.key] ?: LayoutLayer(mapOf())) }.toMap(),
                other.strokes,
                other.cycle,
                other.spaceForSeparation
        )
    }

    data class LayoutLayer(
            val layout: Map<Int, LayoutItem> = mapOf(),
            val labels: Map<Int, String> = mapOf()
    ) {
        operator fun get(i: Int): LayoutItem? = layout[i]
        operator fun plus(other: LayoutLayer): LayoutLayer {
            return LayoutLayer(this.layout + other.layout, this.labels + other.labels)
        }
    }

    data class LayoutItem(
            val normal: List<Int>,
            val shift: List<Int>,
            val caps: List<Int>
    ) {
        constructor(normal: List<Int>, shift: List<Int>): this(normal, shift, normal)
        constructor(normal: List<Int>): this(normal, normal, normal)

        constructor(normal: Int, shift: Int, caps: Int): this(listOf(normal), listOf(shift), listOf(caps))
        constructor(normal: Int, shift: Int): this(normal, shift, normal)
        constructor(normal: Int): this(normal, normal, normal)
    }

    data class StrokeTable(
            val table: Map<Int, Int>
    ) {
        operator fun get(key: Int): Int? = table[key]
    }

}
