package io.github.lee0701.lboard.layouts.hangul

import io.github.lee0701.lboard.hangul.CombinationTable
import io.github.lee0701.lboard.hardkeyboard.SimpleKeyboardLayout

object ShinSebeolHangul {

    private val LAYOUT_SHIN_ORIGINAL_0_2_3 = SimpleKeyboardLayout(mapOf(
            45 to SimpleKeyboardLayout.LayoutItem(0x11ba, 0x1174),
            51 to SimpleKeyboardLayout.LayoutItem(0x11af, 0x1163),
            33 to SimpleKeyboardLayout.LayoutItem(0x11b8, 0x1167),
            46 to SimpleKeyboardLayout.LayoutItem(0x11ae, 0x1162),
            48 to SimpleKeyboardLayout.LayoutItem(0x11c0, 0x1165),
            53 to SimpleKeyboardLayout.LayoutItem(0x1105, 0x1105),
            49 to SimpleKeyboardLayout.LayoutItem(0x1103, 0x1103),
            37 to SimpleKeyboardLayout.LayoutItem(0x1106, 0x100116e),
            43 to SimpleKeyboardLayout.LayoutItem(0x110e, 0x100116e),
            44 to SimpleKeyboardLayout.LayoutItem(0x1111, 0x1001169),

            29 to SimpleKeyboardLayout.LayoutItem(0x11bc, 0x1164),
            47 to SimpleKeyboardLayout.LayoutItem(0x11ab, 0x1168),
            32 to SimpleKeyboardLayout.LayoutItem(0x11c2, 0x1175),
            34 to SimpleKeyboardLayout.LayoutItem(0x11bd, 0x1161),
            35 to SimpleKeyboardLayout.LayoutItem(0x11c1, 0x1173),
            36 to SimpleKeyboardLayout.LayoutItem(0x1102, 0x1102),
            38 to SimpleKeyboardLayout.LayoutItem(0x110b, 0x003b),
            39 to SimpleKeyboardLayout.LayoutItem(0x1100, 0x0027),
            40 to SimpleKeyboardLayout.LayoutItem(0x110c, 0x110c),
            74 to SimpleKeyboardLayout.LayoutItem(0x1107, 0x003a),
            75 to SimpleKeyboardLayout.LayoutItem(0x1110, 0x0022),

            54 to SimpleKeyboardLayout.LayoutItem(0x11b7, 0x1172),
            52 to SimpleKeyboardLayout.LayoutItem(0x11a8, 0x116d),
            31 to SimpleKeyboardLayout.LayoutItem(0x11be, 0x1166),
            50 to SimpleKeyboardLayout.LayoutItem(0x11bf, 0x1169),
            30 to SimpleKeyboardLayout.LayoutItem(0x11bb, 0x116e),
            42 to SimpleKeyboardLayout.LayoutItem(0x1109, 0x1109),
            41 to SimpleKeyboardLayout.LayoutItem(0x1112, 0x002f),

            76 to SimpleKeyboardLayout.LayoutItem(0x110f, 0x003f)
    ))

    private val LAYOUT_SHIN_ORIGINAL_1 = SimpleKeyboardLayout(mapOf(
            45 to SimpleKeyboardLayout.LayoutItem(0x1174, 0x11ba),
            51 to SimpleKeyboardLayout.LayoutItem(0x1163, 0x11af),
            33 to SimpleKeyboardLayout.LayoutItem(0x1167, 0x11b8),
            46 to SimpleKeyboardLayout.LayoutItem(0x1162, 0x11ae),
            48 to SimpleKeyboardLayout.LayoutItem(0x1165, 0x11c0),
            53 to SimpleKeyboardLayout.LayoutItem(0x1105, 0x1105),
            49 to SimpleKeyboardLayout.LayoutItem(0x1103, 0x1103),
            37 to SimpleKeyboardLayout.LayoutItem(0x100116e, 0x1106),
            43 to SimpleKeyboardLayout.LayoutItem(0x100116e, 0x110e),
            44 to SimpleKeyboardLayout.LayoutItem(0x1001169, 0x1111),

            29 to SimpleKeyboardLayout.LayoutItem(0x1164, 0x11bc),
            47 to SimpleKeyboardLayout.LayoutItem(0x1168, 0x11ab),
            32 to SimpleKeyboardLayout.LayoutItem(0x1175, 0x11c2),
            34 to SimpleKeyboardLayout.LayoutItem(0x1161, 0x11bd),
            35 to SimpleKeyboardLayout.LayoutItem(0x1173, 0x11c1),
            36 to SimpleKeyboardLayout.LayoutItem(0x1102, 0x1102),
            38 to SimpleKeyboardLayout.LayoutItem(0x110b, 0x003b),
            39 to SimpleKeyboardLayout.LayoutItem(0x1100, 0x0027),
            40 to SimpleKeyboardLayout.LayoutItem(0x110c, 0x110c),
            74 to SimpleKeyboardLayout.LayoutItem(0x1107, 0x003a),
            75 to SimpleKeyboardLayout.LayoutItem(0x1110, 0x0022),

            54 to SimpleKeyboardLayout.LayoutItem(0x1172, 0x11b7),
            52 to SimpleKeyboardLayout.LayoutItem(0x116d, 0x11a8),
            31 to SimpleKeyboardLayout.LayoutItem(0x1166, 0x11be),
            50 to SimpleKeyboardLayout.LayoutItem(0x1169, 0x11bf),
            30 to SimpleKeyboardLayout.LayoutItem(0x116e, 0x11bb),
            42 to SimpleKeyboardLayout.LayoutItem(0x1109, 0x1109),
            41 to SimpleKeyboardLayout.LayoutItem(0x1112, 0x002f),

            76 to SimpleKeyboardLayout.LayoutItem(0x1001169, 0x003f)
    ))

    val LAYOUT_SHIN_ORIGINAL = listOf(
            LAYOUT_SHIN_ORIGINAL_0_2_3,
            LAYOUT_SHIN_ORIGINAL_1,
            LAYOUT_SHIN_ORIGINAL_0_2_3,
            LAYOUT_SHIN_ORIGINAL_0_2_3
    )

    val COMBINATION_SHIN_ORIGINAL = CombinationTable(mapOf(
            0x1100 to 0x1100 to 0x1101,	// ㄲ
            0x1103 to 0x1103 to 0x1104,	// ㄸ
            0x1107 to 0x1107 to 0x1108,	// ㅃ
            0x1109 to 0x1109 to 0x110a,	// ㅆ
            0x110c to 0x110c to 0x110d,	// ㅉ

            0x01001169 to 0x1161 to 0x116a,	// ㅘ
            0x01001169 to 0x1162 to 0x116b,	// ㅙ
            0x01001169 to 0x1175 to 0x116c,	// ㅚ
            0x0100116e to 0x1165 to 0x116f,	// ㅝ
            0x0100116e to 0x1166 to 0x1170,	// ㅞ
            0x0100116e to 0x1175 to 0x1171,	// ㅟ

            0x11a8 to 0x11a8 to 0x11a9,	// ㄲ
            0x11a8 to 0x11ba to 0x11aa,	// ㄳ
            0x11ab to 0x11bd to 0x11ac,	// ㄵ
            0x11ab to 0x11c2 to 0x11ad,	// ㄶ
            0x11af to 0x11a8 to 0x11b0,	// ㄺ
            0x11af to 0x11b7 to 0x11b1,	// ㄻ
            0x11af to 0x11b8 to 0x11b2,	// ㄼ
            0x11af to 0x11ba to 0x11b3,	// ㄽ
            0x11af to 0x11c0 to 0x11b4,	// ㄾ
            0x11af to 0x11c1 to 0x11b5,	// ㄿ
            0x11af to 0x11c2 to 0x11b6,	// ㅀ
            0x11b8 to 0x11ba to 0x11b9,	// ㅄ
            0x11ba to 0x11ba to 0x11bb	// ㅆ
    ))

}
