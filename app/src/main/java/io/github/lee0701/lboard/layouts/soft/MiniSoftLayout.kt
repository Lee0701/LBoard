package io.github.lee0701.lboard.layouts.soft

import io.github.lee0701.lboard.R
import io.github.lee0701.lboard.softkeyboard.Key
import io.github.lee0701.lboard.softkeyboard.Layout
import io.github.lee0701.lboard.softkeyboard.Row

object MiniSoftLayout {

    val LAYOUT_MINI_8COLS_GOOGLE = Layout(listOf(Row(listOf(
            Key(45, ""),    // q
            Key(51, ""),    // w
            Key(33, ""),    // e
            Key(46, ""),    // r
            Key(48, ""),    // t
            Key(36, ""),    // h
            Key(43, ""),    // o
            Key(44, "")     // p
    ), Row.Type.ODD), Row(listOf(
            Key(29, ""),    // a
            Key(47, ""),    // s
            Key(32, ""),    // d
            Key(34, ""),    // f
            Key(35, ""),    // g
            Key(38, ""),    // j
            Key(39, ""),    // k
            Key(40, "")     // l
    ), Row.Type.EVEN), Row(listOf(
            Key(54, ""),    // z
            Key(52, ""),    // x
            Key(31, ""),    // c
            Key(50, ""),    // v
            Key(42, ""),    // n
            Key(41, ""),    // m
            Key(67, "DEL", repeatable = true, keyWidth = 0.15f)
    ), Row.Type.ODD, paddingLeft = 0.1f), Row(listOf(
            Key(keyCode = 57, label = "?12", keyWidth = 1.5f/10f),
            Key(keyCode = 204, label = "ABC", keyWidth = 1.5f/10f),
            Key(keyCode = 62, keyWidth = 4/10f),
            Key(keyCode = 56, label = ".", keyWidth = 1/10f),
            Key(keyCode = 66, label = "RETURN", keyWidth = 2/10f)
    ), Row.Type.BOTTOM)), keyWidth = 1/8f, key = "mini-7cols", nameStringKey = R.string.pref_method_soft_layout_mini_7cols)

    val LAYOUT_MINI_7COLS = Layout(listOf(Row(listOf(
            Key(0x0210),
            Key(0x0211),
            Key(0x0212),
            Key(0x0213),
            Key(0x0214),
            Key(0x0215),
            Key(0x0216)
    ), Row.Type.ODD), Row(listOf(
            Key(0x0220),
            Key(0x0221),
            Key(0x0222),
            Key(0x0223),
            Key(0x0224),
            Key(0x0225),
            Key(0x0226)
    ), Row.Type.EVEN), Row(listOf(
            Key(0x0230),
            Key(0x0231),
            Key(0x0232),
            Key(0x0233),
            Key(0x0234),
            Key(0x0235),
            Key(67, "DEL", repeatable = true)
    ), Row.Type.ODD), Row(listOf(
            Key(keyCode = 57, label = "?12", keyWidth = 1.5f/10f),
            Key(keyCode = 204, label = "ABC", keyWidth = 1.5f/10f),
            Key(keyCode = 62, keyWidth = 4/10f),
            Key(keyCode = 56, label = ".", keyWidth = 1/10f),
            Key(keyCode = 66, label = "RETURN", keyWidth = 2/10f)
    ), Row.Type.BOTTOM)), keyWidth = 1/7f, key = "mini-8cols", nameStringKey = R.string.pref_method_soft_layout_mini_7cols)

}
