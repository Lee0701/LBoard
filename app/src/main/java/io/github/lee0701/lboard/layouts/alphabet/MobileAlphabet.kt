package io.github.lee0701.lboard.layouts.alphabet

import io.github.lee0701.lboard.hardkeyboard.CommonKeyboardLayout
import io.github.lee0701.lboard.hardkeyboard.CommonKeyboardLayout.*

object MobileAlphabet {

    val LAYOUT_TWELVE_ALPHABET_A = CommonKeyboardLayout(LayoutLayer(mapOf(
            0x2001 to LayoutItem(listOf(0x2e, 0x27)),
            0x2002 to LayoutItem(listOf(0x61, 0x62, 0x63, 0x41, 0x42, 0x43)),
            0x2003 to LayoutItem(listOf(0x64, 0x65, 0x66, 0x44, 0x45, 0x46)),
            0x2004 to LayoutItem(listOf(0x67, 0x68, 0x69, 0x47, 0x48, 0x49)),
            0x2005 to LayoutItem(listOf(0x6a, 0x6b, 0x6c, 0x4a, 0x4b, 0x4c)),
            0x2006 to LayoutItem(listOf(0x6d, 0x6e, 0x6f, 0x4d, 0x4e, 0x4f)),
            0x2007 to LayoutItem(listOf(0x70, 0x71, 0x72, 0x73, 0x50, 0x51, 0x52, 0x53)),
            0x2008 to LayoutItem(listOf(0x74, 0x75, 0x76, 0x54, 0x55, 0x56)),
            0x2009 to LayoutItem(listOf(0x77, 0x78, 0x79, 0x7a, 0x57, 0x58, 0x59, 0x5a)),
            0x200a to LayoutItem(listOf()),
            0x200b to LayoutItem(listOf()),
            0x200c to LayoutItem(listOf())
    ), labels = mapOf(
            0x2001 to ("." to "."),
            0x2002 to ("abc" to "ABC"),
            0x2003 to ("def" to "DEF"),
            0x2004 to ("ghi" to "GHI"),
            0x2005 to ("jkl" to "JKL"),
            0x2006 to ("mno" to "MNO"),
            0x2007 to ("pqrs" to "PQRS"),
            0x2008 to ("tuv" to "TUV"),
            0x2009 to ("wxyz" to "WXYZ")
    )), timeout = true)

    val LAYOUT_FIFTEEN_QWERTY_A = CommonKeyboardLayout(LayoutLayer(mapOf(
            0x2001 to LayoutItem("qw".map { it.toInt() }, "QW".map { it.toInt() }),
            0x2002 to LayoutItem("er".map { it.toInt() }, "ER".map { it.toInt() }),
            0x2003 to LayoutItem("ty".map { it.toInt() }, "TY".map { it.toInt() }),
            0x2004 to LayoutItem("ui".map { it.toInt() }, "UI".map { it.toInt() }),
            0x2005 to LayoutItem("op".map { it.toInt() }, "OP".map { it.toInt() }),
            0x2006 to LayoutItem("as".map { it.toInt() }, "AS".map { it.toInt() }),
            0x2007 to LayoutItem("df".map { it.toInt() }, "DF".map { it.toInt() }),
            0x2008 to LayoutItem("gh".map { it.toInt() }, "GH".map { it.toInt() }),
            0x2009 to LayoutItem("jk".map { it.toInt() }, "JK".map { it.toInt() }),
            0x200a to LayoutItem("l".map { it.toInt() }, "L".map { it.toInt() }),
            0x200b to LayoutItem("zx".map { it.toInt() }, "ZX".map { it.toInt() }),
            0x200c to LayoutItem("cv".map { it.toInt() }, "CV".map { it.toInt() }),
            0x200d to LayoutItem("bn".map { it.toInt() }, "BN".map { it.toInt() }),
            0x200e to LayoutItem("m".map { it.toInt() }, "M".map { it.toInt() })
    )), timeout = true)

}
