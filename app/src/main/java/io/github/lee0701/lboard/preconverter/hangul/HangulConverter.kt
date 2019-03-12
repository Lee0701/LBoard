package io.github.lee0701.lboard.preconverter.hangul

import io.github.lee0701.lboard.preconverter.ComposingText
import io.github.lee0701.lboard.preconverter.PreConverter
import java.text.Normalizer

class HangulConverter(override val name: String, val layout: HangulLayout): PreConverter {

    override fun convert(text: ComposingText): ComposingText {
        val newToken = ComposingText.StringToken(text.layers.last().tokens
                .map { (it as ComposingText.CharToken) }
                .map { HangulToken(it.char) }
                .reduce { acc, token -> compose(acc, token) }.let { (it.other ?: "") + it.display })
        return text.copy(layers = text.layers + ComposingText.Layer(listOf(newToken)))
    }

    private fun compose(composing: HangulToken, input: HangulToken): HangulToken =
        if(input.cho != null) cho(composing, input) else if(input.jung != null) jung(composing, input) else if(input.jong != null) jong(composing, input) else HangulToken(other = (composing.other ?: "") + composing.display + input.display)

    private fun cho(composing: HangulToken, input: HangulToken): HangulToken =
            if(composing.cho != null) (if(composing.jung == null) layout.combinations[composing.cho to input.cho]?.let { composing.copy(cho = it) } else null) ?: input.copy(other = (composing.other ?: "") + composing.display) else composing.copy(cho = input.cho)
    private fun jung(composing: HangulToken, input: HangulToken): HangulToken =
            if(composing.jung != null) layout.combinations[composing.jung to input.jung]?.let { composing.copy(jung = it) } ?: input.copy(other = (composing.other ?: "") + composing.display) else composing.copy(jung = input.jung)
    private fun jong(composing: HangulToken, input: HangulToken): HangulToken =
            if(composing.jong != null) layout.combinations[composing.jong to input.jong]?.let { composing.copy(jong = it) } ?: input.copy(other = (composing.other ?: "") + composing.display) else composing.copy(jong = input.jong)

    data class HangulToken(val cho: Char? = null, val jung: Char? = null, val jong: Char? = null, val other: String? = null, override val score: Double = 1.0): ComposingText.Token {

        val display = when {
            cho != null && jung != null -> Normalizer.normalize(cho.toString() + jung + (jong ?: ""), Normalizer.Form.NFC)
            cho != null && jung == null && jong == null -> if(CONVERT_CHO.contains(cho)) COMPAT_CHO[CONVERT_CHO.indexOf(cho)].toString() else cho.toString()
            cho == null && jung != null && jong == null -> if(STD_JUNG.contains(jung)) COMPAT_JUNG[STD_JUNG.indexOf(jung)].toString() else jung.toString()
            cho == null && jung == null && jong != null -> if(CONVERT_JONG.contains(jong)) COMPAT_CHO[CONVERT_JONG.indexOf(jong)].toString() else jong.toString()
            cho != null || jung != null || jong != null -> (cho ?: 0x115f.toChar()).toString() + (jung ?: 0x1160.toChar()) + (jong ?: "")
            else -> other ?: ""
        }

        constructor(char: Char): this(
                if(isCho(char)) char else null,
                if(isJung(char)) char else null,
                if(isJong(char)) char else null,
                if(!isCho(char) && !isJung(char) && !isJong(char)) char.toString() else null
        )

        constructor(other: String): this(null, null, null, other)

    }

    companion object {
        fun isCho(char: Char) = char.toInt() in 0x1100 .. 0x115f
        fun isJung(char: Char) = char.toInt() in 0x1160 .. 0x11a7
        fun isJong(char: Char) = char.toInt() in 0x11a8 .. 0x11ff

        const val COMPAT_CHO = "ㄱㄲㄳㄴㄵㄶㄷㄸㄹㄺㄻㄼㄽㄾㄿㅀㅁㅂㅃㅄㅅㅆㅇㅈㅉㅊㅋㅌㅍㅎ"
        const val COMPAT_JUNG = "ㅏㅐㅑㅒㅓㅔㅕㅖㅗㅘㅙㅚㅛㅜㅝㅞㅟㅠㅡㅢㅣ"

        const val CONVERT_CHO = "ᄀᄁ ᄂ  ᄃᄄᄅ       ᄆᄇᄈ ᄉᄊᄋᄌᄍᄎᄏᄐᄑᄒ"
        const val CONVERT_JONG = "ᆨᆩᆪᆫᆬᆭᆮ ᆯᆰᆱᆲᆳᆴᆵᆶᆷᆸ ᆹᆺᆻᆼᆽ ᆾᆿᇀᇁᇂ"
        const val STD_JUNG = "ᅡᅢᅣᅤᅥᅦᅧᅨᅩᅪᅫᅬᅭᅮᅯᅰᅱᅲᅳᅴᅵ"

    }

}
