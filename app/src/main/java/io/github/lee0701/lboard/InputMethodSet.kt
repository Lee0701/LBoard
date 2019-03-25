package io.github.lee0701.lboard

class InputMethodSet(
        val keyModes: List<InputMethod>
) {
    constructor(normalMode: InputMethod, symMode: InputMethod): this(listOf(normalMode, symMode))
}
