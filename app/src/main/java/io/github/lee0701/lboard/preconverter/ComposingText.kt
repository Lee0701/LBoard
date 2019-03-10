package io.github.lee0701.lboard.preconverter

data class ComposingText(val layers: List<Layer>) {

    data class Layer(val tokens: List<TokenList>)

    interface Token {
        val score: Double
        override fun toString(): String
    }

    data class KeyInputToken(val keyCode: Int, val representingChar: Char = keyCode.toChar(), override val score: Double = 1.0): Token {
        override fun toString(): String = keyCode.toString()
    }

    data class CharToken(val char: Char, override val score: Double = 1.0): Token {
        override fun toString(): String = char.toString()
    }

    data class StringToken(val string: String, override val score: Double = 1.0): Token {
        override fun toString(): String = string
    }

    data class TokenList(val tokens: List<Token>) {
        val best: Token = tokens.sortedByDescending { it.score }.first()
    }

}
