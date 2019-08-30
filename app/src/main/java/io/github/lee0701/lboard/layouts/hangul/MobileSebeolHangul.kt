package io.github.lee0701.lboard.layouts.hangul

import io.github.lee0701.lboard.hardkeyboard.CommonKeyboardLayout

object MobileSebeolHangul {

    // Unfinished.
    val LAYOUT_FIFTEEN_SEBEOL = CommonKeyboardLayout(CommonKeyboardLayout.LayoutLayer(mapOf(
            0x2001 to CommonKeyboardLayout.LayoutItem("ᄅᆺ".map { it.toInt() }),
            0x2002 to CommonKeyboardLayout.LayoutItem("ᄃᆯ".map { it.toInt() }),
            0x2003 to CommonKeyboardLayout.LayoutItem("몁".map { it.toInt() }),
            0x2004 to CommonKeyboardLayout.LayoutItem("채".map { it.toInt() }),
            0x2005 to CommonKeyboardLayout.LayoutItem("퍼".map { it.toInt() }),
            0x2006 to CommonKeyboardLayout.LayoutItem("ᄂᆼ".map { it.toInt() }),
            0x2007 to CommonKeyboardLayout.LayoutItem("ᄋᆫ".map { it.toInt() }),
            0x2008 to CommonKeyboardLayout.LayoutItem("기".map { it.toInt() }),
            0x2009 to CommonKeyboardLayout.LayoutItem("자".map { it.toInt() }),
            0x200a to CommonKeyboardLayout.LayoutItem("브".map { it.toInt() }),
            0x200b to CommonKeyboardLayout.LayoutItem("ᄉᆷ".map { it.toInt() }),
            0x200c to CommonKeyboardLayout.LayoutItem("헥".map { it.toInt() }),
            0x200d to CommonKeyboardLayout.LayoutItem("토".map { it.toInt() }),
            0x200e to CommonKeyboardLayout.LayoutItem("쿠".map { it.toInt() })
    ), labels = mapOf(
            /*
            0x2001 to ("ㅂㅛ" to "ㅃ"),
            0x2002 to ("ㅈㅕ" to "ㅉ"),
            0x2003 to ("ㄷㅑ" to "ㄸ"),
            0x2004 to ("ㄱㅐ" to "ㄲㅒ"),
            0x2005 to ("ㅅㅔ" to "ㅆㅖ"),
            0x2006 to ("ㅁ" to "ㅁ"),
            0x2007 to ("ㄴㅗ" to "ㄴㅗ"),
            0x2008 to ("ㅇㅓ" to "ㅇㅓ"),
            0x2009 to ("ㄹㅏ" to "ㄹㅏ"),
            0x200a to ("ㅎㅣ" to "ㅎㅣ"),
            0x200b to ("ㅋ" to "ㅋ"),
            0x200c to ("ㅌㅠ" to "ㅌㅠ"),
            0x200d to ("ㅊㅜ" to "ㅊㅜ"),
            0x200e to ("ㅍㅡ" to "ㅍㅡ")
            */
    )), timeout = true)

}
