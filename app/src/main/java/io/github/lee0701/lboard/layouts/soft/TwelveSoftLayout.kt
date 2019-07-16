package io.github.lee0701.lboard.layouts.soft

import io.github.lee0701.lboard.R
import io.github.lee0701.lboard.softkeyboard.Key
import io.github.lee0701.lboard.softkeyboard.Layout
import io.github.lee0701.lboard.softkeyboard.Row

object TwelveSoftLayout {

    val LAYOUT_12KEY_4COLS = Layout(listOf(Row(listOf(
            Key(0x0201, "1"),
            Key(0x0202, "2"),
            Key(0x0203, "3"),
            Key(67, "DEL", repeatable = true)
    ), Row.Type.ODD), Row(listOf(
            Key(0x0204, "4"),
            Key(0x0205, "5"),
            Key(0x0206, "6"),
            Key(204, "ABC", keyWidth = 0.125f),
            Key(57, "123", keyWidth = 0.125f)
    ), Row.Type.EVEN), Row(listOf(
            Key(0x0207, "7"),
            Key(0x0208, "8"),
            Key(0x0209, "9"),
            Key(62, " ")
    ), Row.Type.ODD), Row(listOf(
            Key(0x020b, "*"),
            Key(0x020a, "0"),
            Key(0x020c, "#"),
            Key(66, "RET")
    ), Row.Type.EVEN)), keyWidth = 0.25f, key = "12key-4cols", nameStringKey = R.string.pref_method_soft_layout_12key_4cols)

}
