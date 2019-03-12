package io.github.lee0701.lboard.preconverter

class SimpleLayoutConverter(override val name: String, val layout: KeyboardLayout): PreConverter {

    override fun convert(text: ComposingText): ComposingText {

        return text.copy(layers = text.layers + ComposingText.Layer(text.layers.last().tokens.map { list ->
            list.copy(tokens = list.tokens.map { token ->
                if(token is ComposingText.KeyInputToken) {
                    val codes = getCodes(token.keyCode, token.shift, token.alt)
                    ComposingText.CharToken(if(codes == null) token.representingChar else codes[0])
                } else token
            })
        }))
    }

    private fun getCodes(keyCode: Int, shift: Boolean, alt: Boolean): List<Char>? = layout.layout[keyCode]?.let { if(alt && shift) it.altShift else if(alt) it.alt else if(shift) it.shift else it.normal }

}
