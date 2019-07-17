package io.github.lee0701.lboard.layouts.soft

import io.github.lee0701.lboard.R
import io.github.lee0701.lboard.softkeyboard.Key
import io.github.lee0701.lboard.softkeyboard.Layout
import io.github.lee0701.lboard.softkeyboard.Row

object TabletSoftLayout {

    val LAYOUT_11COLS_TABLET = Layout(listOf(Row(listOf(
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
            Key(67, "DEL", repeatable = true)
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
            Key(66, "RETURN", keyWidth = 1.5f/11f)
    ), Row.Type.EVEN, paddingLeft = 1/2f/11f), Row(listOf(
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
            Key(60, "SFT")
    ), Row.Type.ODD), Row(listOf(
            Key(keyCode = 57, label = "?12", keyWidth = 2/11f),
            Key(keyCode = 204, label = "ABC", keyWidth = 1/11f),
            Key(keyCode = 62, keyWidth = 5/11f),
            Key(keyCode = 76, label = "/", keyWidth = 1/11f),
            Key(keyCode = 57, label = "?12", keyWidth = 2/11f)
    ), Row.Type.BOTTOM)), keyWidth = 1/11f, key = "11cols-tablet", nameStringKey = R.string.pref_method_soft_layout_11cols_tablet)

    val LAYOUT_11COLS_TABLET_WITH_NUM = Layout(listOf(Row(listOf(
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
            Key(73, "\\")
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
            Key(66, "RETURN", keyWidth = 1.5f/11f)
    ), Row.Type.EVEN, paddingLeft = 1/2f/11f), Row(listOf(
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
            Key(60, "SFT")
    ), Row.Type.ODD), Row(listOf(
            Key(keyCode = 57, label = "?12", keyWidth = 2/11f),
            Key(keyCode = 204, label = "ABC", keyWidth = 1/11f),
            Key(keyCode = 62, keyWidth = 5/11f),
            Key(keyCode = 76, label = "/", keyWidth = 1/11f),
            Key(keyCode = 57, label = "?12", keyWidth = 2/11f)
    ), Row.Type.BOTTOM)), keyWidth = 1/11f, key = "11cols-tablet-with-num", nameStringKey = R.string.pref_method_soft_layout_11cols_tablet_with_num)

    val LAYOUT_11COLS_TABLET_WITH_QUOTE = Layout(listOf(Row(listOf(
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
            Key(67, "DEL", repeatable = true)
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
            Key(75, "'", keyWidth = 1/2f/11f)
    ), Row.Type.EVEN, paddingLeft = 1/2f/11f), Row(listOf(
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
            Key(60, "SFT")
    ), Row.Type.ODD), Row(listOf(
            Key(keyCode = 57, label = "?12", keyWidth = 2/11f),
            Key(keyCode = 204, label = "ABC", keyWidth = 1/11f),
            Key(keyCode = 62, keyWidth = 5/11f),
            Key(keyCode = 76, label = "/", keyWidth = 1/11f),
            Key(66, "RETURN", keyWidth = 2/11f)
    ), Row.Type.BOTTOM)), keyWidth = 1/11f, key = "11cols-tablet-with-quote", nameStringKey = R.string.pref_method_soft_layout_11cols_tablet)

    val LAYOUT_11COLS_TABLET_WITH_QUOTE_NUM = Layout(listOf(Row(listOf(
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
            Key(73, "\\")
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
            Key(75, "'", keyWidth = 1/2f/11f)
    ), Row.Type.EVEN, paddingLeft = 1/2f/11f), Row(listOf(
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
            Key(60, "SFT")
    ), Row.Type.ODD), Row(listOf(
            Key(keyCode = 57, label = "?12", keyWidth = 2/11f),
            Key(keyCode = 204, label = "ABC", keyWidth = 1/11f),
            Key(keyCode = 62, keyWidth = 5/11f),
            Key(keyCode = 76, label = "/", keyWidth = 1/11f),
            Key(66, "RETURN", keyWidth = 2/11f)
    ), Row.Type.BOTTOM)), keyWidth = 1/11f, key = "11cols-tablet-with-quote-num", nameStringKey = R.string.pref_method_soft_layout_11cols_tablet_with_num)

}
