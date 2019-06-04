package io.github.lee0701.lboard.layouts.hangul

import io.github.lee0701.lboard.hangul.CombinationTable
import io.github.lee0701.lboard.hardkeyboard.CommonKeyboardLayout

import io.github.lee0701.lboard.hardkeyboard.CommonKeyboardLayout.*
import io.github.lee0701.lboard.layouts.alphabet.Alphabet

object SebeolHangul {
    val LAYOUT_SEBEOL_391 = Alphabet.LAYOUT_QWERTY + CommonKeyboardLayout(LayoutLayer(mapOf(
            68 to LayoutItem(0x002a, 0x203b),

            8 to LayoutItem(0x11c2, 0x11a9),
            9 to LayoutItem(0x11bb, 0x11b0),
            10 to LayoutItem(0x11b8, 0x11bd),
            11 to LayoutItem(0x116d, 0x11b5),
            12 to LayoutItem(0x1172, 0x11b4),
            13 to LayoutItem(0x1163, 0x003d),
            14 to LayoutItem(0x1168, 0x201c),
            15 to LayoutItem(0x1174, 0x201d),
            16 to LayoutItem(0x116e, 0x0027),
            7 to LayoutItem(0x110f, 0x007e),
            69 to LayoutItem(0x0029, 0x003b),
            70 to LayoutItem(0x003e, 0x002b),

            45 to LayoutItem(0x11ba, 0x11c1),
            51 to LayoutItem(0x11af, 0x11c0),
            33 to LayoutItem(0x1167, 0x11ac),
            46 to LayoutItem(0x1162, 0x11b6),
            48 to LayoutItem(0x1165, 0x11b3),
            53 to LayoutItem(0x1105, 0x0035),
            49 to LayoutItem(0x1103, 0x0036),
            37 to LayoutItem(0x1106, 0x0037),
            43 to LayoutItem(0x110e, 0x0038),
            44 to LayoutItem(0x1111, 0x0039),
            71 to LayoutItem(0x0028, 0x0025),
            72 to LayoutItem(0x003c, 0x002f),
            73 to LayoutItem(0x003a, 0x005c),

            29 to LayoutItem(0x11bc, 0x11ae),
            47 to LayoutItem(0x11ab, 0x11ad),
            32 to LayoutItem(0x1175, 0x11b2),
            34 to LayoutItem(0x1161, 0x11b1),
            35 to LayoutItem(0x1173, 0x1164),
            36 to LayoutItem(0x1102, 0x0030),
            38 to LayoutItem(0x110b, 0x0031),
            39 to LayoutItem(0x1100, 0x0032),
            40 to LayoutItem(0x110c, 0x0033),
            74 to LayoutItem(0x1107, 0x0034),
            75 to LayoutItem(0x1110, 0x00b7),

            54 to LayoutItem(0x11b7, 0x11be),
            52 to LayoutItem(0x11a8, 0x11b9),
            31 to LayoutItem(0x1166, 0x11bf),
            50 to LayoutItem(0x1169, 0x11aa),
            30 to LayoutItem(0x116e, 0x003f),
            42 to LayoutItem(0x1109, 0x002d),
            41 to LayoutItem(0x1112, 0x0022),
            55 to LayoutItem(0x002c, 0x002c),
            56 to LayoutItem(0x002e, 0x002e),
            76 to LayoutItem(0x1169, 0x0021)
    )))

    val COMBINATION_SEBEOL_391 = CombinationTable(mapOf(
            0x1100 to 0x1100 to 0x1101,	// ㄲ
            0x1103 to 0x1103 to 0x1104,	// ㄸ
            0x1107 to 0x1107 to 0x1108,	// ㅃ
            0x1109 to 0x1109 to 0x110a,	// ㅆ
            0x110c to 0x110c to 0x110d,	// ㅉ

            0x1169 to 0x1161 to 0x116a,	// ㅘ
            0x1169 to 0x1162 to 0x116b,	// ㅙ
            0x1169 to 0x1175 to 0x116c,	// ㅚ
            0x116e to 0x1165 to 0x116f,	// ㅝ
            0x116e to 0x1166 to 0x1170,	// ㅞ
            0x116e to 0x1175 to 0x1171	// ㅟ
    ))

    val LAYOUT_SEBEOL_390 = Alphabet.LAYOUT_QWERTY + CommonKeyboardLayout(LayoutLayer(mapOf(
            8 to LayoutItem(0x11c2, 0x11bd),
            9 to LayoutItem(0x11bb, 0x0040),
            10 to LayoutItem(0x11b8, 0x0023),
            11 to LayoutItem(0x116d, 0x0024),
            12 to LayoutItem(0x1172, 0x0025),
            13 to LayoutItem(0x1163, 0x005e),
            14 to LayoutItem(0x1168, 0x0026),
            15 to LayoutItem(0x1174, 0x002a),
            16 to LayoutItem(0x116e, 0x0028),
            7 to LayoutItem(0x110f, 0x0029),

            45 to LayoutItem(0x11ba, 0x11c1),
            51 to LayoutItem(0x11af, 0x11c0),
            33 to LayoutItem(0x1167, 0x11bf),
            46 to LayoutItem(0x1162, 0x1164),
            48 to LayoutItem(0x1165, 0x003b),
            53 to LayoutItem(0x1105, 0x003c),
            49 to LayoutItem(0x1103, 0x0037),
            37 to LayoutItem(0x1106, 0x0038),
            43 to LayoutItem(0x110e, 0x0039),
            44 to LayoutItem(0x1111, 0x003e),

            29 to LayoutItem(0x11bc, 0x11ae),
            47 to LayoutItem(0x11ab, 0x11ad),
            32 to LayoutItem(0x1175, 0x11b0),
            34 to LayoutItem(0x1161, 0x11a9),
            35 to LayoutItem(0x1173, 0x002f),
            36 to LayoutItem(0x1102, 0x0027),
            38 to LayoutItem(0x110b, 0x0034),
            39 to LayoutItem(0x1100, 0x0035),
            40 to LayoutItem(0x110c, 0x0036),
            74 to LayoutItem(0x1107, 0x003a),
            75 to LayoutItem(0x1110, 0x0022),

            54 to LayoutItem(0x11b7, 0x11be),
            52 to LayoutItem(0x11a8, 0x11b9),
            31 to LayoutItem(0x1166, 0x11b1),
            50 to LayoutItem(0x1169, 0x11b6),
            30 to LayoutItem(0x116e, 0x0021),
            42 to LayoutItem(0x1109, 0x0030),
            41 to LayoutItem(0x1112, 0x0031),
            55 to LayoutItem(0x002c, 0x0032),
            56 to LayoutItem(0x002e, 0x0033),
            76 to LayoutItem(0x1169, 0x003f)
    )))

    val COMBINATION_SEBEOL_390 = CombinationTable(mapOf(
            0x1100 to 0x1100 to 0x1101,	// ㄲ
            0x1103 to 0x1103 to 0x1104,	// ㄸ
            0x1107 to 0x1107 to 0x1108,	// ㅃ
            0x1109 to 0x1109 to 0x110a,	// ㅆ
            0x110c to 0x110c to 0x110d,	// ㅉ

            0x1169 to 0x1161 to 0x116a,	// ㅘ
            0x1169 to 0x1162 to 0x116b,	// ㅙ
            0x1169 to 0x1175 to 0x116c,	// ㅚ
            0x116e to 0x1165 to 0x116f,	// ㅝ
            0x116e to 0x1166 to 0x1170,	// ㅞ
            0x116e to 0x1175 to 0x1171,	// ㅟ
            0x1173 to 0x1175 to 0x1174,	// ㅢ

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
