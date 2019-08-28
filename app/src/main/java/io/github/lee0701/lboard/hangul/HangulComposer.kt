package io.github.lee0701.lboard.hangul

import io.github.lee0701.lboard.inputmethod.InputMethodModule
import io.github.lee0701.lboard.layouts.hangul.DubeolHangul
import io.github.lee0701.lboard.layouts.hangul.SebeolHangul
import io.github.lee0701.lboard.layouts.hangul.ShinSebeolHangul
import io.github.lee0701.lboard.layouts.hangul.MobileDubeolHangul
import org.json.JSONObject
import java.text.Normalizer

abstract class HangulComposer(
        val combinationTable: CombinationTable,
        val virtualJamoTable: VirtualJamoTable = VirtualJamoTable(mapOf()),
        val moajugi: Boolean
): InputMethodModule {

    abstract fun compose(composing: State, input: Int): State
    abstract fun timeout(composing: State): State

    fun display(state: State): String {
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

    data class State(val cho: Int? = null, val jung: Int? = null, val jong: Int? = null, val other: String = "") {

        val status: Int get() =
            if(jong != null && jong < 0x01000000) 3
            else if(jung != null && jung < 0x01000000) 2
            else if(cho != null && cho < 0x01000000) 1
            else 0
        
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
                "dubeol-cheonjiin" to MobileDubeolHangul.COMBINATION_CHEONJIIN,
                "dubeol-naratgeul" to MobileDubeolHangul.COMBINATION_NARATGEUL
        )
        val REVERSE_COMBINATION_TABLES = COMBINATION_TABLES.map { it.value to it.key }.toMap()

        val VIRTUAL_JAMO_TABLES = mapOf(
                "dubeol-cheonjiin" to MobileDubeolHangul.VIRTUAL_CHEONJIIN
        )
        val REVERSE_VIRTUAL_JAMO_TABLES = VIRTUAL_JAMO_TABLES.map { it.value to it.key }.toMap()

    }

}
