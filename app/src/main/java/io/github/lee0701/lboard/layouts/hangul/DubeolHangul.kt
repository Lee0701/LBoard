package io.github.lee0701.lboard.layouts.hangul

import io.github.lee0701.lboard.hardkeyboard.SimpleKeyboardLayout

object DubeolHangul {

    val LAYOUT_DUBEOL_STANDARD = mapOf(
            45 to SimpleKeyboardLayout.LayoutItem(0x3142.toChar(), 0x3143.toChar()),
            51 to SimpleKeyboardLayout.LayoutItem(0x3148.toChar(), 0x3149.toChar()),
            33 to SimpleKeyboardLayout.LayoutItem(0x3137.toChar(), 0x3138.toChar()),
            46 to SimpleKeyboardLayout.LayoutItem(0x3131.toChar(), 0x3132.toChar()),
            48 to SimpleKeyboardLayout.LayoutItem(0x3145.toChar(), 0x3146.toChar()),
            53 to SimpleKeyboardLayout.LayoutItem(0x315b.toChar()),
            49 to SimpleKeyboardLayout.LayoutItem(0x3155.toChar()),
            37 to SimpleKeyboardLayout.LayoutItem(0x3151.toChar()),
            43 to SimpleKeyboardLayout.LayoutItem(0x3150.toChar(), 0x3152.toChar()),
            44 to SimpleKeyboardLayout.LayoutItem(0x3154.toChar(), 0x3156.toChar()),

            29 to SimpleKeyboardLayout.LayoutItem(0x3141.toChar()),
            47 to SimpleKeyboardLayout.LayoutItem(0x3134.toChar()),
            32 to SimpleKeyboardLayout.LayoutItem(0x3147.toChar()),
            34 to SimpleKeyboardLayout.LayoutItem(0x3139.toChar()),
            35 to SimpleKeyboardLayout.LayoutItem(0x314e.toChar()),
            36 to SimpleKeyboardLayout.LayoutItem(0x3157.toChar()),
            38 to SimpleKeyboardLayout.LayoutItem(0x3153.toChar()),
            39 to SimpleKeyboardLayout.LayoutItem(0x314f.toChar()),
            40 to SimpleKeyboardLayout.LayoutItem(0x3163.toChar()),

            54 to SimpleKeyboardLayout.LayoutItem(0x314b.toChar()),
            52 to SimpleKeyboardLayout.LayoutItem(0x314c.toChar()),
            31 to SimpleKeyboardLayout.LayoutItem(0x314a.toChar()),
            50 to SimpleKeyboardLayout.LayoutItem(0x314d.toChar()),
            30 to SimpleKeyboardLayout.LayoutItem(0x3160.toChar()),
            42 to SimpleKeyboardLayout.LayoutItem(0x315c.toChar()),
            41 to SimpleKeyboardLayout.LayoutItem(0x3161.toChar())
    )

    val COMBINATION_DUBEOL_STANDARD = mapOf(
            0x1169.toChar() to 0x1161.toChar() to 0x116a.toChar(),
            0x1169.toChar() to 0x1162.toChar() to 0x116b.toChar(),
            0x1169.toChar() to 0x1175.toChar() to 0x116c.toChar(),
            0x116e.toChar() to 0x1165.toChar() to 0x116f.toChar(),
            0x116e.toChar() to 0x1166.toChar() to 0x1170.toChar(),
            0x116e.toChar() to 0x1175.toChar() to 0x1171.toChar(),
            0x1173.toChar() to 0x1175.toChar() to 0x1174.toChar(),

            0x11a8.toChar() to 0x11ba.toChar() to 0x11aa.toChar(),
            0x11ab.toChar() to 0x11bd.toChar() to 0x11ac.toChar(),
            0x11ab.toChar() to 0x11c2.toChar() to 0x11ad.toChar(),
            0x11af.toChar() to 0x11a8.toChar() to 0x11b0.toChar(),
            0x11af.toChar() to 0x11b7.toChar() to 0x11b1.toChar(),
            0x11af.toChar() to 0x11b8.toChar() to 0x11b2.toChar(),
            0x11af.toChar() to 0x11ba.toChar() to 0x11b3.toChar(),
            0x11af.toChar() to 0x11c0.toChar() to 0x11b4.toChar(),
            0x11af.toChar() to 0x11c1.toChar() to 0x11b5.toChar(),
            0x11af.toChar() to 0x11c2.toChar() to 0x11b6.toChar(),
            0x11b8.toChar() to 0x11ba.toChar() to 0x11b9.toChar()
    )

}
