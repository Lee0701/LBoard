package io.github.lee0701.lboard.preconverter.hangul

import io.github.lee0701.lboard.preconverter.KeyboardLayout

data class HangulLayout(
        override val layout: Map<Int, Char>,
        val combinations: Map<Pair<Char, Char>, Char>
): KeyboardLayout
