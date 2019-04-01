package io.github.lee0701.lboard.hangul

import io.github.lee0701.lboard.InputMethodModule
import io.github.lee0701.lboard.layouts.hangul.DubeolHangul
import io.github.lee0701.lboard.layouts.hangul.SebeolHangul
import io.github.lee0701.lboard.layouts.hangul.ShinSebeolHangul
import io.github.lee0701.lboard.layouts.hangul.TwelveDubeolHangul
import org.json.JSONObject
import java.text.Normalizer

open class HangulConverter(
        val combinationTable: CombinationTable,
        val virtualJamoTable: VirtualJamoTable = VirtualJamoTable(mapOf())
): InputMethodModule {

    open fun compose(composing: State, input: Int): State =
        if(isCho(input)) cho(composing, input)
        else if(isJung(input)) jung(composing, input)
        else if(isJong(input)) jong(composing, input)
        else State(other = display(composing) + input.toChar())
    
    open fun display(state: State): String {
        val cho = state.cho?.let { (virtualJamoTable.virtualJamos[it] ?: it).toChar() }
        val jung = state.jung?.let { (virtualJamoTable.virtualJamos[it] ?: it).toChar() }
        val jong = state.jong?.let { (virtualJamoTable.virtualJamos[it] ?: it).toChar() }
        return state.other + when {
            cho != null && jung != null && cho.toInt() in 0x1100 .. 0x1112 && jung.toInt() in 0x1161 .. 0x1175 && (jong == null || jong.toInt() in 0x11a8 .. 0x11c2) ->
                Normalizer.normalize(cho.toString() + jung + (jong ?: ""), Normalizer.Form.NFC)
            cho != null && jung == null && jong == null -> if(CONVERT_CHO.contains(cho)) COMPAT_CHO[CONVERT_CHO.indexOf(cho)].toString() else cho.toString()
            cho == null && jung != null && jong == null -> if(STD_JUNG.contains(jung)) COMPAT_JUNG[STD_JUNG.indexOf(jung)].toString() else jung.toString()
            cho == null && jung == null && jong != null -> if(CONVERT_JONG.contains(jong)) COMPAT_CHO[CONVERT_JONG.indexOf(jong)].toString() else jong.toString()
            cho != null || jung != null || jong != null -> (cho ?: 0x115f.toChar()).toString() + (jung ?: 0x1160.toChar()) + (jong ?: "")
            else -> ""
        }
    }

    private fun cho(composing: State, input: Int): State =
            if(composing.cho != null) (if(composing.jung == null) combinationTable.combinations[composing.cho to input]?.let { composing.copy(cho = it) } else null) ?: State(other = display(composing), cho = input) else composing.copy(cho = input)
    private fun jung(composing: State, input: Int): State =
            if(composing.jung != null) combinationTable.combinations[composing.jung to input]?.let { composing.copy(jung = it) } ?: State(other = display(composing), jung = input) else composing.copy(jung = input)
    private fun jong(composing: State, input: Int): State =
            if(composing.jong != null) combinationTable.combinations[composing.jong to input]?.let { composing.copy(jong = it) } ?: State(other = display(composing), jong = input) else composing.copy(jong = input)

    data class State(val cho: Int? = null, val jung: Int? = null, val jong: Int? = null, val other: String = "") {

        constructor(Int: Int): this(
                if(isCho(Int)) Int else null,
                if(isJung(Int)) Int else null,
                if(isJong(Int)) Int else null,
                if(!isCho(Int) && !isJung(Int) && !isJong(Int)) Int.toString() else ""
        )

        constructor(other: String): this(null, null, null, other)

    }

    override fun serialize(): JSONObject {
        return super.serialize().apply {
            put("combination-table", REVERSE_COMBINATION_TABLES[combinationTable])
            put("virtual-jamo-table", REVERSE_VIRTUAL_JAMO_TABLES[virtualJamoTable])
        }
    }

    companion object {
        fun isCho(char: Int) = (char and 0xffffff).let { it in 0x1100 .. 0x115f || it in 0xa960 .. 0xa97c }
        fun isJung(char: Int) = (char and 0xffffff).let { it in 0x1160 .. 0x11a7 || it in 0xd7b0 .. 0xd7c6 }
        fun isJong(char: Int) = (char and 0xffffff).let { it in 0x11a8 .. 0x11ff || it in 0xd7cb .. 0xd7fb }

        const val COMPAT_CHO = "ㄱㄲㄳㄴㄵㄶㄷㄸㄹㄺㄻㄼㄽㄾㄿㅀㅁㅂㅃㅄㅅㅆㅇㅈㅉㅊㅋㅌㅍㅎ"
        const val COMPAT_JUNG = "ㅏㅐㅑㅒㅓㅔㅕㅖㅗㅘㅙㅚㅛㅜㅝㅞㅟㅠㅡㅢㅣㆇㆈㆉㆊㆋㆌㆍㆎ"

        const val CONVERT_CHO = "ᄀᄁ ᄂ  ᄃᄄᄅ       ᄆᄇᄈ ᄉᄊᄋᄌᄍᄎᄏᄐᄑᄒ"
        const val CONVERT_JONG = "ᆨᆩᆪᆫᆬᆭᆮ ᆯᆰᆱᆲᆳᆴᆵᆶᆷᆸ ᆹᆺᆻᆼᆽ ᆾᆿᇀᇁᇂ"
        const val STD_JUNG = "ᅡᅢᅣᅤᅥᅦᅧᅨᅩᅪᅫᅬᅭᅮᅯᅰᅱᅲᅳᅴᅵᆄᆅᆈᆑᆒᆔᆞᆡ"

        val COMBINATION_TABLES = mapOf(
                "sebeol-390" to SebeolHangul.COMBINATION_SEBEOL_390,
                "sebeol-391" to SebeolHangul.COMBINATION_SEBEOL_391,
                "sebeol-shin-original" to ShinSebeolHangul.COMBINATION_SHIN_ORIGINAL,
                "dubeol-standard" to DubeolHangul.COMBINATION_DUBEOL_STANDARD,
                "dubeol-cheonjiin" to TwelveDubeolHangul.COMBINATION_CHEONJIIN,
                "dubeol-naratgeul" to TwelveDubeolHangul.COMBINATION_NARATGEUL
        )
        val REVERSE_COMBINATION_TABLES = COMBINATION_TABLES.map { it.value to it.key }.toMap()

        val VIRTUAL_JAMO_TABLES = mapOf(
                "dubeol-cheonjiin" to TwelveDubeolHangul.VIRTUAL_CHEONJIIN
        )
        val REVERSE_VIRTUAL_JAMO_TABLES = VIRTUAL_JAMO_TABLES.map { it.value to it.key }.toMap()

        @JvmStatic fun deserialize(json: JSONObject): HangulConverter? {
            val combinationTable = COMBINATION_TABLES[json.optString("combination-table")] ?: CombinationTable(mapOf())
            val virtualJamoTable = VIRTUAL_JAMO_TABLES[json.optString("virtual-jamo-table")] ?: VirtualJamoTable(mapOf())
            return HangulConverter(combinationTable, virtualJamoTable)
        }

    }

}
