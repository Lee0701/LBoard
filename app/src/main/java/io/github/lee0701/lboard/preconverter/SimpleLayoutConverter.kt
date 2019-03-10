package io.github.lee0701.lboard.preconverter

class SimpleLayoutConverter(override val name: String, val layout: KeyboardLayout): PreConverter {
    override fun convert(text: ComposingText): ComposingText {
        return text.copy(layers = text.layers + ComposingText.Layer(text.layers.last().tokens.map { list ->
            list.copy(tokens = list.tokens.map { token ->
                if(token is ComposingText.KeyInputToken) ComposingText.CharToken(layout.layout[token.keyCode] ?: token.representingChar) else token
            })
        }))
    }
}
