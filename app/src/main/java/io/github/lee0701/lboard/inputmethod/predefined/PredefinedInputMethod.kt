package io.github.lee0701.lboard.inputmethod.predefined

import io.github.lee0701.lboard.LBoardService
import io.github.lee0701.lboard.hangul.CombinationTable
import io.github.lee0701.lboard.hangul.VirtualJamoTable
import io.github.lee0701.lboard.hardkeyboard.CommonKeyboardLayout
import io.github.lee0701.lboard.layouts.alphabet.Alphabet
import io.github.lee0701.lboard.layouts.alphabet.MobileAlphabet
import io.github.lee0701.lboard.layouts.hangul.*
import io.github.lee0701.lboard.softkeyboard.Layout

data class PredefinedMethod(
        val softLayouts: List<Layout>,
        val hardLayout: CommonKeyboardLayout,
        val methodType: Type = Type.NONE,
        val combinationTable: CombinationTable = CombinationTable(mapOf()),
        val virtualJamoTable: VirtualJamoTable = VirtualJamoTable(mapOf())
) {
    enum class Type {
        NONE, PREDICTIVE,
        DUBEOL, DUBEOL_SINGLE_VOWEL, SEBEOL,
        DUBEOL_AMBIGUOUS, SEBEOL_AMBIGUOUS
    }

    companion object {
        val PREDEFINED_METHODS = mapOf<String, PredefinedMethod>(
                "alphabet-qwerty" to PredefinedMethod(LBoardService.SOFT_LAYOUT_UNIVERSAL, Alphabet.LAYOUT_QWERTY),
                "alphabet-dvorak" to PredefinedMethod(LBoardService.SOFT_LAYOUT_DVORAK, Alphabet.LAYOUT_DVORAK),
                "alphabet-colemak" to PredefinedMethod(LBoardService.SOFT_LAYOUT_COLEMAK, Alphabet.LAYOUT_COLEMAK),
                "alphabet-7cols-wert" to PredefinedMethod(LBoardService.SOFT_LAYOUT_MINI_7COLS, Alphabet.LAYOUT_7COLS_WERT),
                "alphabet-12key-a" to PredefinedMethod(LBoardService.SOFT_LAYOUT_12KEY, MobileAlphabet.LAYOUT_TWELVE_ALPHABET_A, Type.PREDICTIVE),
                "alphabet-15key-qwerty-compact" to PredefinedMethod(LBoardService.SOFT_LAYOUT_15KEY, MobileAlphabet.LAYOUT_FIFTEEN_QWERTY_COMPACT, Type.PREDICTIVE),
                "alphabet-15key-dvorak-compact" to PredefinedMethod(LBoardService.SOFT_LAYOUT_15KEY, MobileAlphabet.LAYOUT_FIFTEEN_DVORAK_COMPACT, Type.PREDICTIVE),

                "dubeol-standard" to PredefinedMethod(LBoardService.SOFT_LAYOUT_UNIVERSAL, DubeolHangul.LAYOUT_DUBEOL_STANDARD, Type.DUBEOL, DubeolHangul.COMBINATION_DUBEOL_STANDARD),
                "sebeol-390" to PredefinedMethod(LBoardService.SOFT_LAYOUT_SEBEOL_GONG, SebeolHangul.LAYOUT_SEBEOL_390, Type.SEBEOL, SebeolHangul.COMBINATION_SEBEOL_390),
                "sebeol-391" to PredefinedMethod(LBoardService.SOFT_LAYOUT_SEBEOL_GONG, SebeolHangul.LAYOUT_SEBEOL_391, Type.SEBEOL, SebeolHangul.COMBINATION_SEBEOL_390),
                "sebeol-391-strict" to PredefinedMethod(LBoardService.SOFT_LAYOUT_SEBEOL_GONG, SebeolHangul.LAYOUT_SEBEOL_391_STRICT, Type.SEBEOL, SebeolHangul.COMBINATION_SEBEOL_391_STRICT),
                "sebeol-shin-original" to PredefinedMethod(LBoardService.SOFT_LAYOUT_SEBEOL_SHIN, ShinSebeolHangul.LAYOUT_SHIN_ORIGINAL, Type.SEBEOL, ShinSebeolHangul.COMBINATION_SHIN_ORIGINAL),
                "sebeol-shin-edit" to PredefinedMethod(LBoardService.SOFT_LAYOUT_SEBEOL_SHIN, ShinSebeolHangul.LAYOUT_SHIN_EDIT, Type.SEBEOL, ShinSebeolHangul.COMBINATION_SHIN_ORIGINAL),
                "sebeol-mini-shin" to PredefinedMethod(LBoardService.SOFT_LAYOUT_MINI_7COLS, ShinSebeolHangul.LAYOUT_MINI_SHIN_EXPERIMENTAL, Type.SEBEOL, ShinSebeolHangul.COMBINATION_MINI_SHIN_EXPERIMENTAL),
                "dubeol-google" to PredefinedMethod(LBoardService.SOFT_LAYOUT_MINI_8COLS, DubeolHangul.LAYOUT_DUBEOL_GOOGLE, Type.DUBEOL_SINGLE_VOWEL, DubeolHangul.COMBINATION_DUBEOL_GOOGLE),
                "dubeol-cheonjiin" to PredefinedMethod(LBoardService.SOFT_LAYOUT_12KEY, MobileDubeolHangul.LAYOUT_CHEONJIIN, Type.DUBEOL, MobileDubeolHangul.COMBINATION_CHEONJIIN),
                "dubeol-naratgeul" to PredefinedMethod(LBoardService.SOFT_LAYOUT_12KEY, MobileDubeolHangul.LAYOUT_NARATGEUL, Type.DUBEOL, MobileDubeolHangul.COMBINATION_NARATGEUL),
                "dubeol-fifteen-compact" to PredefinedMethod(LBoardService.SOFT_LAYOUT_15KEY, MobileDubeolHangul.LAYOUT_FIFTEEN_DUBEOL, Type.DUBEOL_AMBIGUOUS, DubeolHangul.COMBINATION_DUBEOL_STANDARD),
                "sebeol-fifteen-compact" to PredefinedMethod(LBoardService.SOFT_LAYOUT_15KEY, MobileSebeolHangul.LAYOUT_FIFTEEN_SEBEOL, Type.SEBEOL_AMBIGUOUS, SebeolHangul.COMBINATION_SEBEOL_390)
        )
    }
}