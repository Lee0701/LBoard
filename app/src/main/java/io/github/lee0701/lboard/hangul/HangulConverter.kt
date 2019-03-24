package io.github.lee0701.lboard.hangul

import java.text.Normalizer

open class HangulConverter(val combinationTable: CombinationTable) {

    open fun compose(composing: State, input: Char): State =
        if(isCho(input)) cho(composing, input)
        else if(isJung(input)) jung(composing, input)
        else if(isJong(input)) jong(composing, input)
        else State(other = composing.other + composing.display + input)

    private fun cho(composing: State, input: Char): State =
            if(composing.cho != null) (if(composing.jung == null) combinationTable.combinations[composing.cho to input]?.let { composing.copy(cho = it) } else null) ?: State(other = composing.other + composing.display, cho = input) else composing.copy(cho = input)
    private fun jung(composing: State, input: Char): State =
            if(composing.jung != null) combinationTable.combinations[composing.jung to input]?.let { composing.copy(jung = it) } ?: State(other = composing.other + composing.display, jung = input) else composing.copy(jung = input)
    private fun jong(composing: State, input: Char): State =
            if(composing.jong != null) combinationTable.combinations[composing.jong to input]?.let { composing.copy(jong = it) } ?: State(other = composing.other + composing.display, jong = input) else composing.copy(jong = input)

    data class State(val cho: Char? = null, val jung: Char? = null, val jong: Char? = null, val other: String = "") {

        val display: String = when {
            cho != null && jung != null -> Normalizer.normalize(cho.toString() + jung + (jong ?: ""), Normalizer.Form.NFC)
            cho != null && jung == null && jong == null -> if(CONVERT_CHO.contains(cho)) COMPAT_CHO[CONVERT_CHO.indexOf(cho)].toString() else cho.toString()
            cho == null && jung != null && jong == null -> if(STD_JUNG.contains(jung)) COMPAT_JUNG[STD_JUNG.indexOf(jung)].toString() else jung.toString()
            cho == null && jung == null && jong != null -> if(CONVERT_JONG.contains(jong)) COMPAT_CHO[CONVERT_JONG.indexOf(jong)].toString() else jong.toString()
            cho != null || jung != null || jong != null -> (cho ?: 0x115f.toChar()).toString() + (jung ?: 0x1160.toChar()) + (jong ?: "")
            else -> ""
        }

        constructor(char: Char): this(
                if(isCho(char)) char else null,
                if(isJung(char)) char else null,
                if(isJong(char)) char else null,
                if(!isCho(char) && !isJung(char) && !isJong(char)) char.toString() else ""
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
