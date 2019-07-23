package io.github.lee0701.lboard.layouts.soft

import io.github.lee0701.lboard.R
import io.github.lee0701.lboard.softkeyboard.Key
import io.github.lee0701.lboard.softkeyboard.Layout
import io.github.lee0701.lboard.softkeyboard.Row

object TwelveSoftLayout {

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
            Key(57, "123", keyWidth = 0.125f)
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

}
