package io.github.lee0701.lboard.preconverter

data class ComposingText(val layers: List<Layer>) {

    data class Layer(val tokens: List<Token>)

    interface Token {
        override fun toString(): String
    }

    data class KeyInputToken(val keyCode: Int, val shift: Boolean, val representingChar: Char = keyCode.toChar()): Token {
        override fun toString(): String = keyCode.toString()
    }

    data class CharToken(val char: Char): Token {
        override fun toString(): String = char.toString()
    }

    data class StringToken(val string: String): Token {
        override fun toString(): String = string
    }

}
