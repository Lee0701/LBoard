package io.github.lee0701.lboard.hangul

import org.json.JSONObject

open class SebeolHangulComposer(
        combinationTable: CombinationTable,
        virtualJamoTable: VirtualJamoTable = VirtualJamoTable(mapOf()),
        moajugi: Boolean
): HangulComposer(combinationTable, virtualJamoTable, moajugi) {

    override fun compose(composing: State, input: Int): State =
        if(isCho(input)) cho(composing, input)
        else if(isJung(input)) jung(composing, input)
        else if(isJong(input)) jong(composing, input)
        else State(other = display(composing) + input.toChar())

    override fun timeout(composing: State): State =
            composing

    private fun cho(composing: State, input: Int): State {
        if(moajugi || composing.jung == null && composing.jong == null) {
            return if(composing.cho != null) (if(composing.jung == null) combinationTable.combinations[composing.cho to input]?.let { composing.copy(cho = it) } else null) ?: State(other = display(composing), cho = input)
            else composing.copy(cho = input)
        } else return State(other = display(composing), cho = input)
    }
    private fun jung(composing: State, input: Int): State {
        if(moajugi || composing.jong == null) {
            return if(composing.jung != null) combinationTable.combinations[composing.jung to input]?.let { composing.copy(jung = it) } ?: State(other = display(composing), jung = input)
            else composing.copy(jung = input)
        } else return State(other = display(composing), jung = input)
    }
    private fun jong(composing: State, input: Int): State {
        if(moajugi || composing.cho != null && composing.jung != null) {
            return if(composing.jong != null) combinationTable.combinations[composing.jong to input]?.let { composing.copy(jong = it) } ?: State(other = display(composing), jong = input)
            else composing.copy(jong = input)
        } else return State(other = display(composing), jong = input)
    }

    companion object {

    }

}
