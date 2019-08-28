package io.github.lee0701.lboard.layouts.soft

import io.github.lee0701.lboard.R
import io.github.lee0701.lboard.softkeyboard.Key
import io.github.lee0701.lboard.softkeyboard.Layout
import io.github.lee0701.lboard.softkeyboard.Row

object MobileSoftLayout {

    val LAYOUT_12KEY_4COLS = Layout(listOf(Row(listOf(
            Key(0x2001, "1"),
            Key(0x2002, "2"),
            Key(0x2003, "3"),
            Key(67, "DEL", repeatable = true)
    ), Row.Type.ODD), Row(listOf(
            Key(0x2004, "4"),
            Key(0x2005, "5"),
            Key(0x2006, "6"),
            Key(204, "ABC", keyWidth = 0.125f),
            Key(63, "123", keyWidth = 0.125f)
    ), Row.Type.EVEN), Row(listOf(
            Key(0x2007, "7"),
            Key(0x2008, "8"),
            Key(0x2009, "9"),
            Key(62, " ")
    ), Row.Type.ODD), Row(listOf(
            Key(0x200b, "*"),
            Key(0x200a, "0"),
            Key(0x200c, "#"),
            Key(66, "RET")
    ), Row.Type.EVEN)), keyWidth = 0.25f, key = "12key-4cols", nameStringKey = R.string.pref_method_soft_layout_12key_4cols)

    val LAYOUT_15KEY_A = Layout(listOf(Row(listOf(
            Key(0x2001, "qw"),
            Key(0x2002, "er"),
            Key(0x2003, "ty"),
            Key(0x2004, "ui"),
            Key(0x2005, "op")
    ), Row.Type.ODD), Row(listOf(
            Key(0x2006, "as"),
            Key(0x2007, "df"),
            Key(0x2008, "gh"),
            Key(0x2009, "jk"),
            Key(0x200a, "l")
    ), Row.Type.EVEN), Row(listOf(
            Key(0x200b, "zx"),
            Key(0x200c, "cv"),
            Key(0x200d, "bn"),
            Key(0x200e, "m"),
            Key(67, "DEL", repeatable = true)
    ), Row.Type.ODD), Row(listOf(
            Key(63, "123"),
            Key(204, "ABC"),
            Key(62, " "),
            Key(59, "SFT"),
            Key(66, "RET")
    ), Row.Type.EVEN)), keyWidth = 0.20f, key = "15key-a", nameStringKey = R.string.pref_method_soft_layout_15key_a)

}
