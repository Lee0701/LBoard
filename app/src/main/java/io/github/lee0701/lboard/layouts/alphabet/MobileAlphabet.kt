package io.github.lee0701.lboard.layouts.alphabet

import android.view.KeyEvent
import io.github.lee0701.lboard.hardkeyboard.CommonKeyboardLayout
import io.github.lee0701.lboard.hardkeyboard.CommonKeyboardLayout.*
import io.github.lee0701.lboard.hardkeyboard.SystemCode
import io.github.lee0701.lboard.layouts.symbols.MoreKeys

object MobileAlphabet {

    val LAYOUT_TWELVE_ALPHABET_A = CommonKeyboardLayout(LayoutLayer(mapOf(
            0x2001 to LayoutItem(listOf(0x2e, 0x22)),
            0x2002 to LayoutItem(listOf(0x61, 0x62, 0x63), listOf(0x41, 0x42, 0x43)),
            0x2003 to LayoutItem(listOf(0x64, 0x65, 0x66), listOf(0x44, 0x45, 0x46)),
            0x2004 to LayoutItem(listOf(0x67, 0x68, 0x69), listOf(0x47, 0x48, 0x49)),
            0x2005 to LayoutItem(listOf(0x6a, 0x6b, 0x6c), listOf(0x4a, 0x4b, 0x4c)),
            0x2006 to LayoutItem(listOf(0x6d, 0x6e, 0x6f), listOf(0x4d, 0x4e, 0x4f)),
            0x2007 to LayoutItem(listOf(0x70, 0x71, 0x72, 0x73), listOf(0x50, 0x51, 0x52, 0x53)),
            0x2008 to LayoutItem(listOf(0x74, 0x75, 0x76), listOf(0x54, 0x55, 0x56)),
            0x2009 to LayoutItem(listOf(0x77, 0x78, 0x79, 0x7a), listOf(0x57, 0x58, 0x59, 0x5a)),
            0x200b to LayoutItem(listOf(SystemCode.KEYPRESS or KeyEvent.KEYCODE_SHIFT_LEFT)),
            0x200a to LayoutItem(listOf(0x2d)),
            0x200c to LayoutItem(listOf(0x2c, 0x3f, 0x21))
    ), labels = mapOf(
            0x2001 to ("." to "."),
            0x2002 to ("abc" to "ABC"),
            0x2003 to ("def" to "DEF"),
            0x2004 to ("ghi" to "GHI"),
            0x2005 to ("jkl" to "JKL"),
            0x2006 to ("mno" to "MNO"),
            0x2007 to ("pqrs" to "PQRS"),
            0x2008 to ("tuv" to "TUV"),
            0x2009 to ("wxyz" to "WXYZ"),
            0x200b to ("aA" to "Aa")
    )), timeout = true)

    val LAYOUT_FIFTEEN_QWERTY_COMPACT = MoreKeys.MOREKEYS_FIFTEEN_NUMBERS + CommonKeyboardLayout(LayoutLayer(mapOf(
            0x2001 to LayoutItem("qw".map { it.code }, "QW".map { it.code }),
            0x2002 to LayoutItem("er".map { it.code }, "ER".map { it.code }),
            0x2003 to LayoutItem("ty".map { it.code }, "TY".map { it.code }),
            0x2004 to LayoutItem("ui".map { it.code }, "UI".map { it.code }),
            0x2005 to LayoutItem("op".map { it.code }, "OP".map { it.code }),
            0x2006 to LayoutItem("as".map { it.code }, "AS".map { it.code }),
            0x2007 to LayoutItem("df".map { it.code }, "DF".map { it.code }),
            0x2008 to LayoutItem("gh".map { it.code }, "GH".map { it.code }),
            0x2009 to LayoutItem("jk".map { it.code }, "JK".map { it.code }),
            0x200a to LayoutItem("l".map { it.code }, "L".map { it.code }),
            0x200b to LayoutItem("zx".map { it.code }, "ZX".map { it.code }),
            0x200c to LayoutItem("cv".map { it.code }, "CV".map { it.code }),
            0x200d to LayoutItem("bn".map { it.code }, "BN".map { it.code }),
            0x200e to LayoutItem("m".map { it.code }, "M".map { it.code })
    )), timeout = true)

    val LAYOUT_FIFTEEN_DVORAK_COMPACT = MoreKeys.MOREKEYS_FIFTEEN_NUMBERS + CommonKeyboardLayout(LayoutLayer(mapOf(
            0x2001 to LayoutItem("a".map { it.code }, "A".map { it.code }),
            0x2002 to LayoutItem("oq".map { it.code }, "OQ".map { it.code }),
            0x2003 to LayoutItem("ej".map { it.code }, "EJ".map { it.code }),
            0x2004 to LayoutItem("uk".map { it.code }, "UK".map { it.code }),
            0x2005 to LayoutItem("ix".map { it.code }, "IX".map { it.code }),
            0x2006 to LayoutItem("db".map { it.code }, "DB".map { it.code }),
            0x2007 to LayoutItem("hm".map { it.code }, "HM".map { it.code }),
            0x2008 to LayoutItem("tw".map { it.code }, "TW".map { it.code }),
            0x2009 to LayoutItem("nv".map { it.code }, "NV".map { it.code }),
            0x200a to LayoutItem("sz".map { it.code }, "SZ".map { it.code }),
            0x200b to LayoutItem("py".map { it.code }, "PY".map { it.code }),
            0x200c to LayoutItem("fg".map { it.code }, "FG".map { it.code }),
            0x200d to LayoutItem("cr".map { it.code }, "CR".map { it.code }),
            0x200e to LayoutItem("l".map { it.code }, "L".map { it.code })
    )), timeout = true)

}
