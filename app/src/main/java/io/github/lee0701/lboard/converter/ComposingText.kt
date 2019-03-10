package io.github.lee0701.lboard.converter

data class ComposingText(val layers: List<Layer>) {

    data class Layer(val tokens: List<TokenList>)

    interface Token {
        val score: Double
    }

    data class KeyInputToken(val keyCode: Int, val representingChar: Char = keyCode.toChar(), override val score: Double = 1.0): Token

    data class StringToken(val string: String, override val score: Double = 1.0): Token

    data class TokenList(val tokens: List<Token>) {
        val best: Token = tokens.sortedByDescending { it.score }.first()
    }

}
