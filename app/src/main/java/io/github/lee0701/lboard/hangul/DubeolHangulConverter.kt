package io.github.lee0701.lboard.hangul

class DubeolHangulConverter(
        combinationTable: CombinationTable,
        virtualJamoTable: VirtualJamoTable
): HangulConverter(combinationTable, virtualJamoTable) {

    val reversedCombinations = combinationTable.combinations.map { it.value to it.key }.toMap()

    override fun compose(composing: State, input: Int): State =
        if(isConsonant(input)) consonant(correct(composing), input)
        else if(isVowel(input)) vowel(correct(composing), input)
        else State(other = display(composing) + input.toChar())

    private fun correct(composing: State): State =
            if(composing.cho != null && isConsonant(composing.cho)) composing.copy(cho = toCho(composing.cho))
            else if(composing.jung != null && isVowel(composing.jung)) composing.copy(jung = toJung(composing.jung))
            else composing

    private fun consonant(composing: State, input: Int): State =
            if(composing.jong != null) combinationTable.combinations[composing.jong to toJong(input)]?.let { composing.copy(jong = it) } ?: State(other = display(composing), cho = toCho(input))
            else if(composing.cho != null && composing.jung != null) composing.copy(jong = toJong(input))
            else if(composing.cho != null) combinationTable.combinations[composing.cho to toCho(input)]?.let { composing.copy(cho = it) } ?: State(other = display(composing), cho = toCho(input))
            else composing.copy(cho = toCho(input))

    private fun vowel(composing: State, input: Int): State =
            if(composing.jong != null) reversedCombinations[composing.jong]?.let { State(other = display(composing.copy(jong = it.first)), cho = ghostLight(it.second), jung = toJung(input)) }
                    ?: State(other = display(composing.copy(jong = null)), cho = ghostLight(composing.jong), jung = toJung(input))
            else if(composing.jung != null) combinationTable.combinations[composing.jung to toJung(input)]?.let { composing.copy(jung = it) } ?: State(other = display(composing), jung = toJung(input))
            else composing.copy(jung = toJung(input))

    companion object {
        fun isConsonant(char: Int) = char.toInt() in 0x3131 .. 0x314e
        fun isVowel(char: Int) = char.toInt() in 0x314f .. 0x3163

        fun toCho(char: Int) = CONVERT_CHO[COMPAT_CHO.indexOf(char.toChar())].toInt()
        fun toJung(char: Int) = STD_JUNG[COMPAT_JUNG.indexOf(char.toChar())].toInt()
        fun toJong(char: Int) = CONVERT_JONG[COMPAT_CHO.indexOf(char.toChar())].toInt()

        fun ghostLight(char: Int) = toCho(COMPAT_CHO[CONVERT_JONG.indexOf(char.toChar())].toInt())

    }

}
