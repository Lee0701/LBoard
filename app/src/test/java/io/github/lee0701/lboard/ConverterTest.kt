package io.github.lee0701.lboard

import io.github.lee0701.lboard.converter.ComposingText
import io.github.lee0701.lboard.converter.hangul.HangulConverter
import io.github.lee0701.lboard.converter.hangul.HangulLayout
import org.junit.Test

class ConverterTest {
    @Test fun test() {
        val layout = HangulLayout(
                mapOf(
                        0x00 to 0x0041.toChar(),
                        0x10 to 0x1100.toChar(),
                        0x11 to 0x110f.toChar(),
                        0x20 to 0x1161.toChar(),
                        0x30 to 0x11a8.toChar()
                ),
                mapOf(
                        0x1100.toChar() to 0x1100.toChar() to 0x1101.toChar(),
                        0x11a8.toChar() to 0x11a8.toChar() to 0x11a9.toChar()
                )
        )
        val converter = HangulConverter("Hangul Converter", layout)

        val rat = ComposingText(listOf(ComposingText.Layer(listOf(
                ComposingText.TokenList(listOf(ComposingText.KeyInputToken(0x10))),
                ComposingText.TokenList(listOf(ComposingText.KeyInputToken(0x00))),
                ComposingText.TokenList(listOf(ComposingText.KeyInputToken(0x20))),
                ComposingText.TokenList(listOf(ComposingText.KeyInputToken(0x10))),
                ComposingText.TokenList(listOf(ComposingText.KeyInputToken(0x20))),
                ComposingText.TokenList(listOf(ComposingText.KeyInputToken(0x30))),
                ComposingText.TokenList(listOf(ComposingText.KeyInputToken(0x30))),
                ComposingText.TokenList(listOf(ComposingText.KeyInputToken(0x11))),
                ComposingText.TokenList(listOf(ComposingText.KeyInputToken(0x11)))
        ))))

        println(converter.convert(rat).layers.last())
    }
}
