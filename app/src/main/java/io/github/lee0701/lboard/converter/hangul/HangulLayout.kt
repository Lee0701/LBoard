package io.github.lee0701.lboard.converter.hangul

data class HangulLayout(
        val layout: Map<Int, Char>,
        val combinations: Map<Pair<Char, Char>, Char>
)
