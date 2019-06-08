package io.github.lee0701.lboard.layouts.soft

import io.github.lee0701.lboard.softkeyboard.Key
import io.github.lee0701.lboard.softkeyboard.Layout
import io.github.lee0701.lboard.softkeyboard.Row

object SingleVowelSoftLayout {

    val LAYOUT_8COLS_GOOGLE = Layout(listOf(Row(listOf(
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
    ), Row.Type.BOTTOM)), keyWidth = 1/8f)

}
