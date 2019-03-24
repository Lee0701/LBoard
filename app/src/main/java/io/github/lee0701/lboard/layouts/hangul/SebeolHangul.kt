package io.github.lee0701.lboard.layouts.hangul

import io.github.lee0701.lboard.hardkeyboard.SimpleKeyboardLayout

object SebeolHangul {
    val LAYOUT_SEBEOL_391 = mapOf(
            68 to SimpleKeyboardLayout.LayoutItem(0x002a.toChar(), 0x203b.toChar()),

            8 to SimpleKeyboardLayout.LayoutItem(0x11c2.toChar(), 0x11a9.toChar()),
            9 to SimpleKeyboardLayout.LayoutItem(0x11bb.toChar(), 0x11b0.toChar()),
            10 to SimpleKeyboardLayout.LayoutItem(0x11b8.toChar(), 0x11bd.toChar()),
            11 to SimpleKeyboardLayout.LayoutItem(0x116d.toChar(), 0x11b5.toChar()),
            12 to SimpleKeyboardLayout.LayoutItem(0x1172.toChar(), 0x11b4.toChar()),
            13 to SimpleKeyboardLayout.LayoutItem(0x1163.toChar(), 0x003d.toChar()),
            14 to SimpleKeyboardLayout.LayoutItem(0x1168.toChar(), 0x201c.toChar()),
            15 to SimpleKeyboardLayout.LayoutItem(0x1174.toChar(), 0x201d.toChar()),
            16 to SimpleKeyboardLayout.LayoutItem(0x116e.toChar(), 0x0027.toChar()),
            7 to SimpleKeyboardLayout.LayoutItem(0x110f.toChar(), 0x007e.toChar()),
            69 to SimpleKeyboardLayout.LayoutItem(0x0029.toChar(), 0x003b.toChar()),
            70 to SimpleKeyboardLayout.LayoutItem(0x003e.toChar(), 0x002b.toChar()),

            45 to SimpleKeyboardLayout.LayoutItem(0x11ba.toChar(), 0x11c1.toChar()),
            51 to SimpleKeyboardLayout.LayoutItem(0x11af.toChar(), 0x11c0.toChar()),
            33 to SimpleKeyboardLayout.LayoutItem(0x1167.toChar(), 0x11ac.toChar()),
            46 to SimpleKeyboardLayout.LayoutItem(0x1162.toChar(), 0x11b6.toChar()),
            48 to SimpleKeyboardLayout.LayoutItem(0x1165.toChar(), 0x11b3.toChar()),
            53 to SimpleKeyboardLayout.LayoutItem(0x1105.toChar(), 0x0035.toChar()),
            49 to SimpleKeyboardLayout.LayoutItem(0x1103.toChar(), 0x0036.toChar()),
            37 to SimpleKeyboardLayout.LayoutItem(0x1106.toChar(), 0x0037.toChar()),
            43 to SimpleKeyboardLayout.LayoutItem(0x110e.toChar(), 0x0038.toChar()),
            44 to SimpleKeyboardLayout.LayoutItem(0x1111.toChar(), 0x0039.toChar()),
            71 to SimpleKeyboardLayout.LayoutItem(0x0028.toChar(), 0x0025.toChar()),
            72 to SimpleKeyboardLayout.LayoutItem(0x003c.toChar(), 0x002f.toChar()),
            73 to SimpleKeyboardLayout.LayoutItem(0x003a.toChar(), 0x005c.toChar()),

            29 to SimpleKeyboardLayout.LayoutItem(0x11bc.toChar(), 0x11ae.toChar()),
            47 to SimpleKeyboardLayout.LayoutItem(0x11ab.toChar(), 0x11ad.toChar()),
            32 to SimpleKeyboardLayout.LayoutItem(0x1175.toChar(), 0x11b2.toChar()),
            34 to SimpleKeyboardLayout.LayoutItem(0x1161.toChar(), 0x11b1.toChar()),
            35 to SimpleKeyboardLayout.LayoutItem(0x1173.toChar(), 0x1164.toChar()),
            36 to SimpleKeyboardLayout.LayoutItem(0x1102.toChar(), 0x0030.toChar()),
            38 to SimpleKeyboardLayout.LayoutItem(0x110b.toChar(), 0x0031.toChar()),
            39 to SimpleKeyboardLayout.LayoutItem(0x1100.toChar(), 0x0032.toChar()),
            40 to SimpleKeyboardLayout.LayoutItem(0x110c.toChar(), 0x0033.toChar()),
            74 to SimpleKeyboardLayout.LayoutItem(0x1107.toChar(), 0x0034.toChar()),
            75 to SimpleKeyboardLayout.LayoutItem(0x1110.toChar(), 0x00b7.toChar()),

            54 to SimpleKeyboardLayout.LayoutItem(0x11b7.toChar(), 0x11be.toChar()),
            52 to SimpleKeyboardLayout.LayoutItem(0x11a8.toChar(), 0x11b9.toChar()),
            31 to SimpleKeyboardLayout.LayoutItem(0x1166.toChar(), 0x11bf.toChar()),
            50 to SimpleKeyboardLayout.LayoutItem(0x1169.toChar(), 0x11aa.toChar()),
            30 to SimpleKeyboardLayout.LayoutItem(0x116e.toChar(), 0x003f.toChar()),
            42 to SimpleKeyboardLayout.LayoutItem(0x1109.toChar(), 0x002d.toChar()),
            41 to SimpleKeyboardLayout.LayoutItem(0x1112.toChar(), 0x0022.toChar()),
            55 to SimpleKeyboardLayout.LayoutItem(0x002c.toChar(), 0x002c.toChar()),
            56 to SimpleKeyboardLayout.LayoutItem(0x002e.toChar(), 0x002e.toChar()),
            76 to SimpleKeyboardLayout.LayoutItem(0x1169.toChar(), 0x0021.toChar())
    )

    val COMBINATION_SEBEOL_391 = mapOf(
            0x1100.toChar() to 0x1100.toChar() to 0x1101.toChar(),	// ㄲ
            0x1103.toChar() to 0x1103.toChar() to 0x1104.toChar(),	// ㄸ
            0x1107.toChar() to 0x1107.toChar() to 0x1108.toChar(),	// ㅃ
            0x1109.toChar() to 0x1109.toChar() to 0x110a.toChar(),	// ㅆ
            0x110c.toChar() to 0x110c.toChar() to 0x110d.toChar(),	// ㅉ

            0x1169.toChar() to 0x1161.toChar() to 0x116a.toChar(),	// ㅘ
            0x1169.toChar() to 0x1162.toChar() to 0x116b.toChar(),	// ㅙ
            0x1169.toChar() to 0x1175.toChar() to 0x116c.toChar(),	// ㅚ
            0x116e.toChar() to 0x1165.toChar() to 0x116f.toChar(),	// ㅝ
            0x116e.toChar() to 0x1166.toChar() to 0x1170.toChar(),	// ㅞ
            0x116e.toChar() to 0x1175.toChar() to 0x1171.toChar()	// ㅟ
    )

    val LAYOUT_SEBEOL_390 = mapOf(8 to SimpleKeyboardLayout.LayoutItem(0x11c2.toChar(), 0x11bd.toChar()),
            9 to SimpleKeyboardLayout.LayoutItem(0x11bb.toChar(), 0x0040.toChar()),
            10 to SimpleKeyboardLayout.LayoutItem(0x11b8.toChar(), 0x0023.toChar()),
            11 to SimpleKeyboardLayout.LayoutItem(0x116d.toChar(), 0x0024.toChar()),
            12 to SimpleKeyboardLayout.LayoutItem(0x1172.toChar(), 0x0025.toChar()),
            13 to SimpleKeyboardLayout.LayoutItem(0x1163.toChar(), 0x005e.toChar()),
            14 to SimpleKeyboardLayout.LayoutItem(0x1168.toChar(), 0x0026.toChar()),
            15 to SimpleKeyboardLayout.LayoutItem(0x1174.toChar(), 0x002a.toChar()),
            16 to SimpleKeyboardLayout.LayoutItem(0x116e.toChar(), 0x0028.toChar()),
            7 to SimpleKeyboardLayout.LayoutItem(0x110f.toChar(), 0x0029.toChar()),

            45 to SimpleKeyboardLayout.LayoutItem(0x11ba.toChar(), 0x11c1.toChar()),
            51 to SimpleKeyboardLayout.LayoutItem(0x11af.toChar(), 0x11c0.toChar()),
            33 to SimpleKeyboardLayout.LayoutItem(0x1167.toChar(), 0x11bf.toChar()),
            46 to SimpleKeyboardLayout.LayoutItem(0x1162.toChar(), 0x1164.toChar()),
            48 to SimpleKeyboardLayout.LayoutItem(0x1165.toChar(), 0x003b.toChar()),
            53 to SimpleKeyboardLayout.LayoutItem(0x1105.toChar(), 0x003c.toChar()),
            49 to SimpleKeyboardLayout.LayoutItem(0x1103.toChar(), 0x0037.toChar()),
            37 to SimpleKeyboardLayout.LayoutItem(0x1106.toChar(), 0x0038.toChar()),
            43 to SimpleKeyboardLayout.LayoutItem(0x110e.toChar(), 0x0039.toChar()),
            44 to SimpleKeyboardLayout.LayoutItem(0x1111.toChar(), 0x003e.toChar()),

            29 to SimpleKeyboardLayout.LayoutItem(0x11bc.toChar(), 0x11ae.toChar()),
            47 to SimpleKeyboardLayout.LayoutItem(0x11ab.toChar(), 0x11ad.toChar()),
            32 to SimpleKeyboardLayout.LayoutItem(0x1175.toChar(), 0x11b0.toChar()),
            34 to SimpleKeyboardLayout.LayoutItem(0x1161.toChar(), 0x11a9.toChar()),
            35 to SimpleKeyboardLayout.LayoutItem(0x1173.toChar(), 0x002f.toChar()),
            36 to SimpleKeyboardLayout.LayoutItem(0x1102.toChar(), 0x0027.toChar()),
            38 to SimpleKeyboardLayout.LayoutItem(0x110b.toChar(), 0x0034.toChar()),
            39 to SimpleKeyboardLayout.LayoutItem(0x1100.toChar(), 0x0035.toChar()),
            40 to SimpleKeyboardLayout.LayoutItem(0x110c.toChar(), 0x0036.toChar()),
            74 to SimpleKeyboardLayout.LayoutItem(0x1107.toChar(), 0x003a.toChar()),
            75 to SimpleKeyboardLayout.LayoutItem(0x1110.toChar(), 0x0022.toChar()),

            54 to SimpleKeyboardLayout.LayoutItem(0x11b7.toChar(), 0x11be.toChar()),
            52 to SimpleKeyboardLayout.LayoutItem(0x11a8.toChar(), 0x11b9.toChar()),
            31 to SimpleKeyboardLayout.LayoutItem(0x1166.toChar(), 0x11b1.toChar()),
            50 to SimpleKeyboardLayout.LayoutItem(0x1169.toChar(), 0x11b6.toChar()),
            30 to SimpleKeyboardLayout.LayoutItem(0x116e.toChar(), 0x0021.toChar()),
            42 to SimpleKeyboardLayout.LayoutItem(0x1109.toChar(), 0x0030.toChar()),
            41 to SimpleKeyboardLayout.LayoutItem(0x1112.toChar(), 0x0031.toChar()),
            55 to SimpleKeyboardLayout.LayoutItem(0x002c.toChar(), 0x0032.toChar()),
            56 to SimpleKeyboardLayout.LayoutItem(0x002e.toChar(), 0x0033.toChar()),
            76 to SimpleKeyboardLayout.LayoutItem(0x1169.toChar(), 0x003f.toChar())
    )

    val COMBINATION_SEBEOL_390 = mapOf(
            0x1100.toChar() to 0x1100.toChar() to 0x1101.toChar(),	// ㄲ
            0x1103.toChar() to 0x1103.toChar() to 0x1104.toChar(),	// ㄸ
            0x1107.toChar() to 0x1107.toChar() to 0x1108.toChar(),	// ㅃ
            0x1109.toChar() to 0x1109.toChar() to 0x110a.toChar(),	// ㅆ
            0x110c.toChar() to 0x110c.toChar() to 0x110d.toChar(),	// ㅉ

            0x1169.toChar() to 0x1161.toChar() to 0x116a.toChar(),	// ㅘ
            0x1169.toChar() to 0x1162.toChar() to 0x116b.toChar(),	// ㅙ
            0x1169.toChar() to 0x1175.toChar() to 0x116c.toChar(),	// ㅚ
            0x116e.toChar() to 0x1165.toChar() to 0x116f.toChar(),	// ㅝ
            0x116e.toChar() to 0x1166.toChar() to 0x1170.toChar(),	// ㅞ
            0x116e.toChar() to 0x1175.toChar() to 0x1171.toChar(),	// ㅟ
            0x1173.toChar() to 0x1175.toChar() to 0x1174.toChar(),	// ㅢ

            0x11a8.toChar() to 0x11a8.toChar() to 0x11a9.toChar(),	// ㄲ
            0x11a8.toChar() to 0x11ba.toChar() to 0x11aa.toChar(),	// ㄳ
            0x11ab.toChar() to 0x11bd.toChar() to 0x11ac.toChar(),	// ㄵ
            0x11ab.toChar() to 0x11c2.toChar() to 0x11ad.toChar(),	// ㄶ
            0x11af.toChar() to 0x11a8.toChar() to 0x11b0.toChar(),	// ㄺ
            0x11af.toChar() to 0x11b7.toChar() to 0x11b1.toChar(),	// ㄻ
            0x11af.toChar() to 0x11b8.toChar() to 0x11b2.toChar(),	// ㄼ
            0x11af.toChar() to 0x11ba.toChar() to 0x11b3.toChar(),	// ㄽ
            0x11af.toChar() to 0x11c0.toChar() to 0x11b4.toChar(),	// ㄾ
            0x11af.toChar() to 0x11c1.toChar() to 0x11b5.toChar(),	// ㄿ
            0x11af.toChar() to 0x11c2.toChar() to 0x11b6.toChar(),	// ㅀ
            0x11b8.toChar() to 0x11ba.toChar() to 0x11b9.toChar(),	// ㅄ
            0x11ba.toChar() to 0x11ba.toChar() to 0x11bb.toChar()	// ㅆ
    )

}
