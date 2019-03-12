package io.github.lee0701.lboard.preconverter.hangul

import io.github.lee0701.lboard.preconverter.KeyboardLayout

data class HangulLayout(
        override val layout: Map<Int, KeyboardLayout.LayoutItem>,
        val combinations: Map<Pair<Char, Char>, Char>
): KeyboardLayout
