package io.github.lee0701.lboard.layouts.hangul

import io.github.lee0701.lboard.hangul.CombinationTable
import io.github.lee0701.lboard.hardkeyboard.CommonKeyboardLayout

import io.github.lee0701.lboard.hardkeyboard.CommonKeyboardLayout.*
import io.github.lee0701.lboard.layouts.alphabet.Alphabet

object DubeolHangul {

    val LAYOUT_DUBEOL_STANDARD = Alphabet.LAYOUT_QWERTY + CommonKeyboardLayout(LayoutLayer(mapOf(
            45 to LayoutItem(0x3142, 0x3143),
            51 to LayoutItem(0x3148, 0x3149),
            33 to LayoutItem(0x3137, 0x3138),
            46 to LayoutItem(0x3131, 0x3132),
            48 to LayoutItem(0x3145, 0x3146),
            53 to LayoutItem(0x315b),
            49 to LayoutItem(0x3155),
            37 to LayoutItem(0x3151),
            43 to LayoutItem(0x3150, 0x3152),
            44 to LayoutItem(0x3154, 0x3156),

            29 to LayoutItem(0x3141),
            47 to LayoutItem(0x3134),
            32 to LayoutItem(0x3147),
            34 to LayoutItem(0x3139),
            35 to LayoutItem(0x314e),
            36 to LayoutItem(0x3157),
            38 to LayoutItem(0x3153),
            39 to LayoutItem(0x314f),
            40 to LayoutItem(0x3163),

            54 to LayoutItem(0x314b),
            52 to LayoutItem(0x314c),
            31 to LayoutItem(0x314a),
            50 to LayoutItem(0x314d),
            30 to LayoutItem(0x3160),
            42 to LayoutItem(0x315c),
            41 to LayoutItem(0x3161)
    )))

    val COMBINATION_DUBEOL_STANDARD = CombinationTable(mapOf(
            0x1169 to 0x1161 to 0x116a,
            0x1169 to 0x1162 to 0x116b,
            0x1169 to 0x1175 to 0x116c,
            0x116e to 0x1165 to 0x116f,
            0x116e to 0x1166 to 0x1170,
            0x116e to 0x1175 to 0x1171,
            0x1173 to 0x1175 to 0x1174,

            0x11a8 to 0x11ba to 0x11aa,
            0x11ab to 0x11bd to 0x11ac,
            0x11ab to 0x11c2 to 0x11ad,
            0x11af to 0x11a8 to 0x11b0,
            0x11af to 0x11b7 to 0x11b1,
            0x11af to 0x11b8 to 0x11b2,
            0x11af to 0x11ba to 0x11b3,
            0x11af to 0x11c0 to 0x11b4,
            0x11af to 0x11c1 to 0x11b5,
            0x11af to 0x11c2 to 0x11b6,
            0x11b8 to 0x11ba to 0x11b9
    ))

    val LAYOUT_DUBEOL_GOOGLE = Alphabet.LAYOUT_QWERTY + CommonKeyboardLayout(LayoutLayer(mapOf(
            45 to LayoutItem(0x3142, 0x3143),
            51 to LayoutItem(0x3148, 0x3149),
            33 to LayoutItem(0x3137, 0x3138),
            46 to LayoutItem(0x3131, 0x3132),
            48 to LayoutItem(0x3145, 0x3146),
            36 to LayoutItem(0x3157),
            43 to LayoutItem(0x3150, 0x3152),
            44 to LayoutItem(0x3154, 0x3156),

            29 to LayoutItem(0x3141),
            47 to LayoutItem(0x3134),
            32 to LayoutItem(0x3147),
            34 to LayoutItem(0x3139),
            35 to LayoutItem(0x314e),
            38 to LayoutItem(0x3153),
            39 to LayoutItem(0x314f),
            40 to LayoutItem(0x3163),

            54 to LayoutItem(0x314b),
            52 to LayoutItem(0x314c),
            31 to LayoutItem(0x314a),
            50 to LayoutItem(0x314d),
            42 to LayoutItem(0x315c),
            41 to LayoutItem(0x3161)
    )))

    val COMBINATION_DUBEOL_GOOGLE = CombinationTable(mapOf(
            // 쌍자음 조합
            0x1100 to 0x1100 to 0x1101,
            0x1103 to 0x1103 to 0x1104,
            0x1107 to 0x1107 to 0x1108,
            0x1109 to 0x1109 to 0x110a,
            0x110c to 0x110c to 0x110d,

            // 이중모음 조합
            0x1161 to 0x1161 to 0x1163,
            0x1165 to 0x1165 to 0x1167,
            0x1162 to 0x1162 to 0x1164,
            0x1166 to 0x1166 to 0x1168,
            0x1169 to 0x1169 to 0x116d,
            0x116e to 0x116e to 0x1172,

            // 복모음 조합
            0x1169 to 0x1161 to 0x116a,
            0x1169 to 0x1162 to 0x116b,
            0x1169 to 0x1175 to 0x116c,
            0x116e to 0x1165 to 0x116f,
            0x116e to 0x1166 to 0x1170,
            0x116e to 0x1175 to 0x1171,
            0x1173 to 0x1175 to 0x1174,

            // 받침 조합
            0x11a8 to 0x11a8 to 0x11a9,
            0x11a8 to 0x11ba to 0x11aa,
            0x11ab to 0x11bd to 0x11ac,
            0x11ab to 0x11c2 to 0x11ad,
            0x11af to 0x11a8 to 0x11b0,
            0x11af to 0x11b7 to 0x11b1,
            0x11af to 0x11b8 to 0x11b2,
            0x11af to 0x11ba to 0x11b3,
            0x11af to 0x11c0 to 0x11b4,
            0x11af to 0x11c1 to 0x11b5,
            0x11af to 0x11c2 to 0x11b6,
            0x11b8 to 0x11ba to 0x11b9,
            0x11ba to 0x11ba to 0x11bb
    ))

}
