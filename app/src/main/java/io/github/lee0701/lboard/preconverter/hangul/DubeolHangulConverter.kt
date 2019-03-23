package io.github.lee0701.lboard.preconverter.hangul

import io.github.lee0701.lboard.preconverter.ComposingText
import io.github.lee0701.lboard.preconverter.PreConverter
import org.json.JSONObject
import java.text.Normalizer

class DubeolHangulConverter(override val name: String, val combinationTable: CombinationTable): PreConverter {

    val reversedCombinations = combinationTable.combinations.map { it.value to it.key }.toMap()

    override fun convert(text: ComposingText): ComposingText {
        val newToken = ComposingText.StringToken(text.layers.last().tokens
                .map { (it as ComposingText.CharToken) }
                .map { HangulToken(it.char) }
                .reduce { acc, token -> compose(acc, token) }.let { (it.other ?: "") + it.display })
        return text.copy(layers = text.layers + ComposingText.Layer(listOf(newToken)))
    }

    private fun compose(composing: HangulToken, input: HangulToken): HangulToken {
        println(composing)
        println(input)
        return if(input.cho != null) consonant(correct(composing), input) else if(input.jung != null) vowel(correct(composing), input) else HangulToken(other = (composing.other ?: "") + composing.display + input.display)
    }

    private fun correct(composing: HangulToken): HangulToken =
            if(composing.cho != null && isConsonant(composing.cho)) composing.copy(cho = toCho(composing.cho))
            else if(composing.jung != null && isVowel(composing.jung)) composing.copy(jung = toJung(composing.jung))
            else composing

    private fun consonant(composing: HangulToken, input: HangulToken): HangulToken =
            if(composing.jong != null) combinationTable.combinations[composing.jong to toJong(input.cho!!)]?.let { composing.copy(jong = it) } ?: input.copy(other = (composing.other ?: "") + composing.display)
            else if(composing.cho != null && composing.jung != null) composing.copy(jong = toJong(input.cho!!))
            else if(composing.cho != null) combinationTable.combinations[composing.cho to toCho(input.cho!!)]?.let { composing.copy(cho = it) } ?: input.copy(other = (composing.other ?: "") + composing.display)
            else composing.copy(cho = toCho(input.cho!!))

    private fun vowel(composing: HangulToken, input: HangulToken): HangulToken =
            if(composing.jong != null) reversedCombinations[composing.jong]?.let { HangulToken(other = (composing.other ?: "") + composing.copy(jong = it.first).display, cho = ghostLight(it.second), jung = toJung(input.jung!!)) }
                    ?: HangulToken(other = (composing.other ?: "") + composing.copy(jong = null).display, cho = ghostLight(composing.jong), jung = toJung(input.jung!!))
            else if(composing.jung != null) combinationTable.combinations[composing.jung to toJung(input.jung!!)]?.let { composing.copy(jung = it) } ?: input.copy(other = (composing.other ?: "") + composing.display)
            else composing.copy(jung = toJung(input.jung!!))

    data class HangulToken(val cho: Char? = null, val jung: Char? = null, val jong: Char? = null, val other: String? = null): ComposingText.Token {
        val display = when {
            cho != null && jung != null -> Normalizer.normalize(cho.toString() + jung + (jong ?: ""), Normalizer.Form.NFC)
            cho != null -> cho.toString()
            jung != null -> jung.toString()
            jong != null -> jong.toString()
            else -> other ?: ""
        }
        constructor(char: Char): this(
                if(isCho(char) || isConsonant(char)) char else null,
                if(isJung(char) || isVowel(char)) char else null,
                if(isJong(char)) char else null,
                if(!isCho(char) && !isConsonant(char) && !isJung(char) && !isVowel(char) && !isJong(char)) char.toString() else null
        )
    }

    override fun serialize(): JSONObject {
        return super.serialize().apply {
            put("combinationTable", combinationTable.serialize())
        }
    }

    companion object {
        fun isCho(char: Char) = char.toInt() in 0x1100 .. 0x115f
        fun isJung(char: Char) = char.toInt() in 0x1160 .. 0x11a7
        fun isJong(char: Char) = char.toInt() in 0x11a8 .. 0x11ff

        fun isConsonant(char: Char) = char.toInt() in 0x3131 .. 0x314e
        fun isVowel(char: Char) = char.toInt() in 0x314f .. 0x3163

        fun toCho(char: Char) = CONVERT_CHO[COMPAT_CHO.indexOf(char)]
        fun toJung(char: Char) = STD_JUNG[COMPAT_JUNG.indexOf(char)]
        fun toJong(char: Char) = CONVERT_JONG[COMPAT_CHO.indexOf(char)]

        fun ghostLight(char: Char) = toCho(COMPAT_CHO[CONVERT_JONG.indexOf(char)])

        const val COMPAT_CHO = "ㄱㄲㄳㄴㄵㄶㄷㄸㄹㄺㄻㄼㄽㄾㄿㅀㅁㅂㅃㅄㅅㅆㅇㅈㅉㅊㅋㅌㅍㅎ"
        const val COMPAT_JUNG = "ㅏㅐㅑㅒㅓㅔㅕㅖㅗㅘㅙㅚㅛㅜㅝㅞㅟㅠㅡㅢㅣ"

        const val CONVERT_CHO = "ᄀᄁ ᄂ  ᄃᄄᄅ       ᄆᄇᄈ ᄉᄊᄋᄌᄍᄎᄏᄐᄑᄒ"
        const val CONVERT_JONG = "ᆨᆩᆪᆫᆬᆭᆮ ᆯᆰᆱᆲᆳᆴᆵᆶᆷᆸ ᆹᆺᆻᆼᆽ ᆾᆿᇀᇁᇂ"
        const val STD_JUNG = "ᅡᅢᅣᅤᅥᅦᅧᅨᅩᅪᅫᅬᅭᅮᅯᅰᅱᅲᅳᅴᅵ"

        @JvmStatic fun deserialize(json: JSONObject): HangulConverter {
            return HangulConverter(json.getString("name"), CombinationTable.deserialize(json.getJSONObject("combinationTable")))
        }

    }

}
