package io.github.lee0701.lboard.layouts.soft

import io.github.lee0701.lboard.softkeyboard.Key
import io.github.lee0701.lboard.softkeyboard.Layout
import io.github.lee0701.lboard.softkeyboard.Row

object SoftLayout {

    val LAYOUT_10COLS_MOBILE = Layout(listOf(Row(listOf(
            Key(45, "q"),
            Key(51, "w"),
            Key(33, "e"),
            Key(46, "r"),
            Key(48, "t"),
            Key(53, "y"),
            Key(49, "u"),
            Key(37, "i"),
            Key(43, "o"),
            Key(44, "p")
    ), Row.Type.ODD), Row(listOf(
            Key(29, "a"),
            Key(47, "s"),
            Key(32, "d"),
            Key(34, "f"),
            Key(35, "g"),
            Key(36, "h"),
            Key(38, "j"),
            Key(39, "k"),
            Key(40, "l")
    ), Row.Type.EVEN, paddingLeft = 0.05f, paddingRight = 0.05f), Row(listOf(
            Key(59, "SFT", keyWidth = 0.15f),
            Key(54, "z"),
            Key(52, "x"),
            Key(31, "c"),
            Key(50, "v"),
            Key(30, "b"),
            Key(42, "n"),
            Key(41, "m"),
            Key(67, "DEL", repeatable = true, keyWidth = 0.15f)
    ), Row.Type.ODD), Row(listOf(
            Key(keyCode = 63, label = "?12", keyWidth = 1.5f/10f),
            Key(keyCode = 204, label = "ABC", keyWidth = 1.5f/10f),
            Key(keyCode = 62, keyWidth = 4/10f),
            Key(keyCode = 56, label = ".", keyWidth = 1/10f),
            Key(keyCode = 66, label = "RETURN", keyWidth = 2/10f)
    ), Row.Type.BOTTOM)))

    val LAYOUT_10COLS_MOBILE_WITH_NUM = Layout(listOf(Row(listOf(
            Key(8, "1"),
            Key(9, "2"),
            Key(10, "3"),
            Key(11, "4"),
            Key(12, "5"),
            Key(13, "6"),
            Key(14, "7"),
            Key(15, "8"),
            Key(16, "9"),
            Key(7, "0")
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
            Key(44, "p")
    ), Row.Type.ODD), Row(listOf(
            Key(29, "a"),
            Key(47, "s"),
            Key(32, "d"),
            Key(34, "f"),
            Key(35, "g"),
            Key(36, "h"),
            Key(38, "j"),
            Key(39, "k"),
            Key(40, "l")
    ), Row.Type.EVEN, paddingLeft = 0.05f, paddingRight = 0.05f), Row(listOf(
            Key(59, "SFT", keyWidth = 0.15f),
            Key(54, "z"),
            Key(52, "x"),
            Key(31, "c"),
            Key(50, "v"),
            Key(30, "b"),
            Key(42, "n"),
            Key(41, "m"),
            Key(67, "DEL", repeatable = true, keyWidth = 0.15f)
    ), Row.Type.ODD), Row(listOf(
            Key(keyCode = 63, label = "?12", keyWidth = 1.5f/10f),
            Key(keyCode = 204, label = "ABC", keyWidth = 1.5f/10f),
            Key(keyCode = 62, keyWidth = 4/10f),
            Key(keyCode = 56, label = ".", keyWidth = 1/10f),
            Key(keyCode = 66, label = "RETURN", keyWidth = 2/10f)
    ), Row.Type.BOTTOM)))

    val LAYOUT_10COLS_MOD_QUOTE = Layout(listOf(Row(listOf(
            Key(8, "1"),
            Key(9, "2"),
            Key(10, "3"),
            Key(11, "4"),
            Key(12, "5"),
            Key(13, "6"),
            Key(14, "7"),
            Key(15, "8"),
            Key(16, "9"),
            Key(7, "0")
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
            Key(44, "p")
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
            Key(74, ";")
    ), Row.Type.EVEN), Row(listOf(
            Key(59, "SFT"),
            Key(54, "z"),
            Key(52, "x"),
            Key(31, "c"),
            Key(50, "v"),
            Key(30, "b"),
            Key(42, "n"),
            Key(41, "m"),
            Key(75, "'"),
            Key(67, "DEL", repeatable = true)
    ), Row.Type.ODD), Row(listOf(
            Key(keyCode = 63, label = "?12", keyWidth = 0.125f),
            Key(keyCode = 56, label = ".", keyWidth = 0.1f),
            Key(keyCode = 204, label = "ABC", keyWidth = 0.125f),
            Key(keyCode = 62, keyWidth = 0.4f),
            Key(keyCode = 76, label = "/", keyWidth = 0.1f),
            Key(keyCode = 66, label = "RETURN", keyWidth = 0.15f)
    ), Row.Type.BOTTOM)))

}