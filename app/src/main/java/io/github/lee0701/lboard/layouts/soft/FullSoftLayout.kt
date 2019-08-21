package io.github.lee0701.lboard.layouts.soft

import io.github.lee0701.lboard.R
import io.github.lee0701.lboard.softkeyboard.Key
import io.github.lee0701.lboard.softkeyboard.Layout
import io.github.lee0701.lboard.softkeyboard.Row

object FullSoftLayout {

    val LAYOUT_FULL = Layout(listOf(Row(listOf(
            Key(8, "1"),
            Key(9, "2"),
            Key(10, "3"),
            Key(11, "4"),
            Key(12, "5"),
            Key(13, "6"),
            Key(14, "7"),
            Key(15, "8"),
            Key(16, "9"),
            Key(7, "0"),
            Key(69, "-"),
            Key(70, "="),
            Key(67, "DEL", repeatable = true)
    ), Row.Type.NUMBER), Row(listOf(
            Key(45, "q"),
            Key(51, "w"),
            Key(33, "e"),
            Key(46, "r"),
            Key(48, "t"),
            Key(53, "y"),
            Key(49, "u"),
            Key(37, "i"),
            Key(43, "o"),
            Key(44, "p"),
            Key(44, "["),
            Key(71, "]"),
            Key(72, "\\")
    ), Row.Type.ODD), Row(listOf(
            Key(29, "a"),
            Key(47, "s"),
            Key(32, "d"),
            Key(34, "f"),
            Key(35, "g"),
            Key(36, "h"),
            Key(38, "j"),
            Key(39, "k"),
            Key(40, "l"),
            Key(74, ";"),
            Key(75, "'"),
            Key(66, "RETURN", keyWidth = 1.5f/13f)
    ), Row.Type.EVEN, marginLeft = 1/2f/13f), Row(listOf(
            Key(59, "SFT"),
            Key(54, "z"),
            Key(52, "x"),
            Key(31, "c"),
            Key(50, "v"),
            Key(30, "b"),
            Key(42, "n"),
            Key(41, "m"),
            Key(55, ","),
            Key(56, "."),
            Key(keyCode = 76, label = "/"),
            Key(60, "SFT", keyWidth = 2/13f)
    ), Row.Type.ODD), Row(listOf(
            Key(keyCode = 63, label = "?12", keyWidth = 2/13f),
            Key(keyCode = 204, label = "ABC", keyWidth = 1/13f),
            Key(keyCode = 68, label = "`", keyWidth = 1/13f),
            Key(keyCode = 62, keyWidth = 6/13f),
            Key(keyCode = 204, label = "ABC", keyWidth = 1/13f),
            Key(keyCode = 57, label = "?12", keyWidth = 2/13f)
    ), Row.Type.BOTTOM)), keyWidth = 1/13f, key = "full-pc", nameStringKey = R.string.pref_method_soft_layout_full_pc)

}
