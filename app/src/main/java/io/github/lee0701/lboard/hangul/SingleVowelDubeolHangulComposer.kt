package io.github.lee0701.lboard.hangul

import org.json.JSONObject

class SingleVowelDubeolHangulComposer(
        combinationTable: CombinationTable,
        virtualJamoTable: VirtualJamoTable = VirtualJamoTable(mapOf()),
        moajugi: Boolean
): HangulComposer(combinationTable, virtualJamoTable, moajugi) {

    private val reversedCombinations = combinationTable.combinations.map { it.value to it.key }.toMap()
    private val timeoutJongs = combinationTable.combinations.filterKeys { it.first == it.second }.map { it.key.first }

    override fun compose(composing: State, input: Int): State =
        if(isConsonant(input)) consonant(correct(composing), input)
        else if(isVowel(input)) vowel(correct(composing), input)
        else State(other = display(composing) + input.toChar())

    override fun timeout(composing: State): State =
            if(composing.jong != null &&
                    (timeoutJongs.contains(composing.jong) || timeoutJongs.contains(ghostLight(composing.jong))))
                composing.copy(jong = composing.jong or 0x1000000)
            else composing.copy()
    
    private fun correct(composing: State): State =
            if(composing.cho != null && isConsonant(composing.cho)) composing.copy(cho = toCho(composing.cho))
            else if(composing.jung != null && isVowel(composing.jung)) composing.copy(jung = toJung(composing.jung))
            else composing

    private fun consonant(composing: State, input: Int): State {
        if(composing.jong != null) {
            return combinationTable.combinations[composing.jong to toJong(input)]?.let { composing.copy(jong = it) }
                    ?: State(other = display(composing), cho = toCho(input))
        } else if(composing.cho != null) {
            if(composing.jung != null) return toJong(input).let { if(it == 0x20) State(other = display(composing), cho = toCho(input)) else composing.copy(jong = it) }
            else return combinationTable.combinations[composing.cho to toCho(input)]?.let { composing.copy(cho = it) } ?: State(other = display(composing), cho = toCho(input))
        } else {
            if(moajugi || composing.jung == null) return composing.copy(cho = toCho(input))
            else return State(other = display(composing), cho = toCho(input))
        }
    }

    private fun vowel(composing: State, input: Int): State =
            // 받침을 통째로 초성으로 올릴 수 없으면(ᆪ 등) 기본 두벌식 오토마타같이 분리해서 올린다.
            if(composing.jong != null) ghostLight(composing.jong).let { State(other = display(composing.copy(jong = if(it == 0x20) reversedCombinations[composing.jong]?.first else null)), cho = if(it == 0x20) ghostLight(reversedCombinations[composing.jong]?.second ?: composing.jong) else it, jung = toJung(input)) }
            else if(composing.jung != null) combinationTable.combinations[composing.jung to toJung(input)]?.let { composing.copy(jung = it) } ?: State(other = display(composing), jung = toJung(input))
            else composing.copy(jung = toJung(input))

    companion object {
        fun isConsonant(char: Int) = (char and 0xffffff).let { it in 0x3131 .. 0x314e || it in 0x3165 .. 0x3186 }
        fun isVowel(char: Int) = (char and 0xffffff).let { it in 0x314f .. 0x3163 || it in 0x3187 .. 0x318e }

        fun toCho(char: Int) = (char and 0x7f000000) or CONVERT_CHO[COMPAT_CHO.indexOf(char.toChar())].toInt()
        fun toJung(char: Int) = (char and 0x7f000000) or STD_JUNG[COMPAT_JUNG.indexOf(char.toChar())].toInt()
        fun toJong(char: Int) = (char and 0x7f000000) or CONVERT_JONG[COMPAT_CHO.indexOf(char.toChar())].toInt()

        fun ghostLight(char: Int) = (char and 0x7f000000) or toCho(COMPAT_CHO[CONVERT_JONG.indexOf(char.toChar())].toInt())

    }

}
