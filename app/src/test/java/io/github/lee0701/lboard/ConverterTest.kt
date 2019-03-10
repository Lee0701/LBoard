package io.github.lee0701.lboard

import io.github.lee0701.lboard.converter.ComposingText
import io.github.lee0701.lboard.converter.hangul.HangulConverter
import io.github.lee0701.lboard.converter.hangul.HangulLayout
import org.junit.Test

class ConverterTest {
    @Test fun test() {
        val layout = HangulLayout(
                mapOf(
                        11 to 0x1100.toChar(),
                        12 to 0x110f.toChar(),
                        21 to 0x1161.toChar(),
                        31 to 0x11a8.toChar()
                ),
                mapOf(
                        0x1100.toChar() to 0x1100.toChar() to 0x1101.toChar(),
                        0x11a8.toChar() to 0x11a8.toChar() to 0x11a9.toChar()
                )
        )
        val converter = HangulConverter("Hangul Converter", layout)

        val rat = ComposingText(listOf(ComposingText.Layer(listOf(
                ComposingText.TokenList(listOf(ComposingText.KeyInputToken(11))),
                ComposingText.TokenList(listOf(ComposingText.KeyInputToken(21))),
                ComposingText.TokenList(listOf(ComposingText.KeyInputToken(11))),
                ComposingText.TokenList(listOf(ComposingText.KeyInputToken(21))),
                ComposingText.TokenList(listOf(ComposingText.KeyInputToken(31))),
                ComposingText.TokenList(listOf(ComposingText.KeyInputToken(31))),
                ComposingText.TokenList(listOf(ComposingText.KeyInputToken(12))),
                ComposingText.TokenList(listOf(ComposingText.KeyInputToken(12)))
        ))))

        println(converter.convert(rat).layers.last())
    }
}
