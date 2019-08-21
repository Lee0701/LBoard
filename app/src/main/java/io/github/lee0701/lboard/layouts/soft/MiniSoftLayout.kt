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
    ), Row.Type.ODD, marginLeft = 0.1f), Row(listOf(
            Key(keyCode = 57, label = "?12", keyWidth = 1.5f/10f),
            Key(keyCode = 204, label = "ABC", keyWidth = 1.5f/10f),
            Key(keyCode = 62, keyWidth = 4/10f),
            Key(keyCode = 56, label = ".", keyWidth = 1/10f),
            Key(keyCode = 66, label = "RETURN", keyWidth = 2/10f)
    ), Row.Type.BOTTOM)), keyWidth = 1/8f, key = "mini-7cols", nameStringKey = R.string.pref_method_soft_layout_mini_7cols)

    val LAYOUT_MINI_7COLS = Layout(listOf(Row(listOf(
            Key(0x2010),
            Key(0x2011),
            Key(0x2012),
            Key(0x2013),
            Key(0x2014),
            Key(0x2015),
            Key(0x2016)
    ), Row.Type.ODD), Row(listOf(
            Key(0x2020),
            Key(0x2021),
            Key(0x2022),
            Key(0x2023),
            Key(0x2024),
            Key(0x2025),
            Key(0x2026)
    ), Row.Type.EVEN), Row(listOf(
            Key(0x2030),
            Key(0x2031),
            Key(0x2032),
            Key(0x2033),
            Key(0x2034),
            Key(0x2035),
            Key(67, "DEL", repeatable = true)
    ), Row.Type.ODD), Row(listOf(
            Key(keyCode = 63, label = "?12", keyWidth = 1.5f/10f),
            Key(keyCode = 204, label = "ABC", keyWidth = 1.5f/10f),
            Key(keyCode = 62, keyWidth = 4/10f),
            Key(keyCode = 56, label = ".", keyWidth = 1/10f),
            Key(keyCode = 66, label = "RETURN", keyWidth = 2/10f)
    ), Row.Type.BOTTOM)), keyWidth = 1/7f, key = "mini-8cols", nameStringKey = R.string.pref_method_soft_layout_mini_7cols)

}
