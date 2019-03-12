package io.github.lee0701.lboard.preconverter

data class ComposingText(val layers: List<Layer>) {

    data class Layer(val tokens: List<Token>)

    interface Token {
        val score: Double
        override fun toString(): String
    }

    data class KeyInputToken(val keyCode: Int, val shift: Boolean, val alt: Boolean, val representingChar: Char = keyCode.toChar(), override val score: Double = 1.0): Token {
        override fun toString(): String = keyCode.toString()
    }

    data class CharToken(val char: Char, override val score: Double = 1.0): Token {
        override fun toString(): String = char.toString()
    }

    data class StringToken(val string: String, override val score: Double = 1.0): Token {
        override fun toString(): String = string
    }

}
