package io.github.lee0701.lboard.layouts.alphabet

import android.view.KeyEvent
import io.github.lee0701.lboard.hardkeyboard.CommonKeyboardLayout

import io.github.lee0701.lboard.hardkeyboard.CommonKeyboardLayout.*

object Alphabet {

    val LAYOUT_MOREKEYS = CommonKeyboardLayout(mapOf(
            0 to LayoutLayer(mapOf(
                    // a
                    0x1061 to LayoutItem(0x00e6),
                    0x1161 to LayoutItem(0x00e3),
                    0x1261 to LayoutItem(0x00e5),
                    0x1361 to LayoutItem(0x0101),
                    0x1461 to LayoutItem(0x00e0),
                    0x1561 to LayoutItem(0x00e1),
                    0x1661 to LayoutItem(0x00e2),
                    0x1761 to LayoutItem(0x00e4),

                    // c
                    0x1063 to LayoutItem(0x00e7),

                    // e
                    0x1065 to LayoutItem(0x0113),
                    0x1165 to LayoutItem(0x00ea),
                    0x1265 to LayoutItem(0x00e9),
                    0x1365 to LayoutItem(0x00e8),
                    0x1465 to LayoutItem(0x00eb),

                    // i
                    0x1069 to LayoutItem(0x00ec),
                    0x1169 to LayoutItem(0x00ef),
                    0x1269 to LayoutItem(0x00ed),
                    0x1369 to LayoutItem(0x00ee),
                    0x1469 to LayoutItem(0x012b),

                    // n
                    0x106e to LayoutItem(0x00f1),

                    // o
                    0x106f to LayoutItem(0x014d),
                    0x116f to LayoutItem(0x0153),
                    0x126f to LayoutItem(0x00f8),
                    0x136f to LayoutItem(0x00f5),
                    0x146f to LayoutItem(0x00f6),
                    0x156f to LayoutItem(0x00f3),
                    0x166f to LayoutItem(0x00f4),
                    0x176f to LayoutItem(0x00f2),

                    // s
                    0x1073 to LayoutItem(0x00df),

                    // u
                    0x1075 to LayoutItem(0x016b),
                    0x1175 to LayoutItem(0x00fc),
                    0x1275 to LayoutItem(0x00fa),
                    0x1375 to LayoutItem(0x00fb),
                    0x1475 to LayoutItem(0x00f9)

            )),
            // char code to list of keycodes
            CommonKeyboardLayout.LAYER_MORE_KEYS to LayoutLayer(mapOf(
                    0x0061 to LayoutItem(listOf(0x1061, 0x1161, 0x1261, 0x1361, 0x1461, 0x1561, 0x1661, 0x1761)),
                    0x0063 to LayoutItem(listOf(0x1063)),
                    0x0065 to LayoutItem(listOf(0x1065, 0x1165, 0x1265, 0x1365, 0x1465)),
                    0x0069 to LayoutItem(listOf(0x1069, 0x1169, 0x1269, 0x1369, 0x1469)),
                    0x006e to LayoutItem(listOf(0x106e)),
                    0x006f to LayoutItem(listOf(0x106f, 0x116f, 0x126f, 0x136f, 0x146f, 0x156f, 0x166f, 0x176f)),
                    0x0073 to LayoutItem(listOf(0x1073)),
                    0x0075 to LayoutItem(listOf(0x1075, 0x1175, 0x1275, 0x1375, 0x1475))

            ))
    ))

    val LAYOUT_QWERTY = LAYOUT_MOREKEYS + CommonKeyboardLayout(LayoutLayer(mapOf(
            68 to LayoutItem(0x0060, 0x007e),

            8 to LayoutItem(0x0031, 0x0021),
            9 to LayoutItem(0x0032, 0x0040),
            10 to LayoutItem(0x0033, 0x0023),
            11 to LayoutItem(0x0034, 0x0024),
            12 to LayoutItem(0x0035, 0x0025),
            13 to LayoutItem(0x0036, 0x005e),
            14 to LayoutItem(0x0037, 0x0026),
            15 to LayoutItem(0x0038, 0x002a),
            16 to LayoutItem(0x0039, 0x0028),
            7 to LayoutItem(0x0030, 0x0029),
            69 to LayoutItem(0x002d, 0x005f),
            70 to LayoutItem(0x003d, 0x002b),

            45 to LayoutItem(0x0071, 0x0051),
            51 to LayoutItem(0x0077, 0x0057),
            33 to LayoutItem(0x0065, 0x0045),
            46 to LayoutItem(0x0072, 0x0052),
            48 to LayoutItem(0x0074, 0x0054),
            53 to LayoutItem(0x0079, 0x0059),
            49 to LayoutItem(0x0075, 0x0055),
            37 to LayoutItem(0x0069, 0x0049),
            43 to LayoutItem(0x006f, 0x004f),
            44 to LayoutItem(0x0070, 0x0050),
            71 to LayoutItem(0x005b, 0x007b),
            72 to LayoutItem(0x005d, 0x007d),
            73 to LayoutItem(0x005c, 0x007c),

            29 to LayoutItem(0x0061, 0x0041),
            47 to LayoutItem(0x0073, 0x0053),
            32 to LayoutItem(0x0064, 0x0044),
            34 to LayoutItem(0x0066, 0x0046),
            35 to LayoutItem(0x0067, 0x0047),
            36 to LayoutItem(0x0068, 0x0048),
            38 to LayoutItem(0x006a, 0x004a),
            39 to LayoutItem(0x006b, 0x004b),
            40 to LayoutItem(0x006c, 0x004c),
            74 to LayoutItem(0x003b, 0x003a),
            75 to LayoutItem(0x0027, 0x0022),

            54 to LayoutItem(0x007a, 0x005a),
            52 to LayoutItem(0x0078, 0x0058),
            31 to LayoutItem(0x0063, 0x0043),
            50 to LayoutItem(0x0076, 0x0056),
            30 to LayoutItem(0x0062, 0x0042),
            42 to LayoutItem(0x006e, 0x004e),
            41 to LayoutItem(0x006d, 0x004d),

            55 to LayoutItem(0x002c, 0x003c),
            56 to LayoutItem(0x002e, 0x003e),
            76 to LayoutItem(0x002f, 0x003f)
    )))

    val LAYOUT_DVORAK = CommonKeyboardLayout(LayoutLayer(mapOf(
            68 to LayoutItem(0x0060, 0x207e),

            8  to LayoutItem(0x0031, 0x0021),
            9  to LayoutItem(0x0032, 0x0040),
            10 to LayoutItem(0x0033, 0x0023),
            11 to LayoutItem(0x0034, 0x0024),
            12 to LayoutItem(0x0035, 0x0025),
            13 to LayoutItem(0x0036, 0x005e),
            14 to LayoutItem(0x0037, 0x0026),
            15 to LayoutItem(0x0038, 0x002a),
            16 to LayoutItem(0x0039, 0x0028),
            7  to LayoutItem(0x0030, 0x0029),
            69 to LayoutItem(0x005b, 0x007b),
            70 to LayoutItem(0x005d, 0x007d),

            45 to LayoutItem(0x0027, 0x0022),
            51 to LayoutItem(0x002c, 0x003c),
            33 to LayoutItem(0x002e, 0x003e),
            46 to LayoutItem(0x0070, 0x0050),
            48 to LayoutItem(0x0079, 0x0059),
            53 to LayoutItem(0x0066, 0x0046),
            49 to LayoutItem(0x0067, 0x0047),
            37 to LayoutItem(0x0063, 0x0043),
            43 to LayoutItem(0x0072, 0x0052),
            44 to LayoutItem(0x006c, 0x004c),
            71 to LayoutItem(0x002f, 0x003f),
            72 to LayoutItem(0x003d, 0x002b),
            73 to LayoutItem(0x005c, 0x007c),

            29 to LayoutItem(0x0061, 0x0041),
            47 to LayoutItem(0x006f, 0x004f),
            32 to LayoutItem(0x0065, 0x0045),
            34 to LayoutItem(0x0075, 0x0055),
            35 to LayoutItem(0x0069, 0x0049),
            36 to LayoutItem(0x0064, 0x0044),
            38 to LayoutItem(0x0068, 0x0048),
            39 to LayoutItem(0x0074, 0x0054),
            40 to LayoutItem(0x006e, 0x004e),
            74 to LayoutItem(0x0073, 0x0053),
            75 to LayoutItem(0x002d, 0x005f),

            54 to LayoutItem(0x003b, 0x003a),
            52 to LayoutItem(0x0071, 0x0051),
            31 to LayoutItem(0x006a, 0x004a),
            50 to LayoutItem(0x006b, 0x004b),
            30 to LayoutItem(0x0078, 0x0058),
            42 to LayoutItem(0x0062, 0x0042),
            41 to LayoutItem(0x006d, 0x004d),

            55 to LayoutItem(0x0077, 0x0057),
            56 to LayoutItem(0x0076, 0x0056),
            76 to LayoutItem(0x007a, 0x005a)

    )))

    val LAYOUT_COLEMAK = LAYOUT_QWERTY + CommonKeyboardLayout(LayoutLayer(mapOf(
            8  to LayoutItem(0x31, 0x21),
            9  to LayoutItem(0x32, 0x40),
            10 to LayoutItem(0x33, 0x23),
            11 to LayoutItem(0x34, 0x24),
            12 to LayoutItem(0x35, 0x25),
            13 to LayoutItem(0x36, 0x5e),
            14 to LayoutItem(0x37, 0x26),
            15 to LayoutItem(0x38, 0x2a),
            16 to LayoutItem(0x39, 0x28),
            7  to LayoutItem(0x30, 0x29),
            
            45 to LayoutItem(0x71, 0x51),
            51 to LayoutItem(0x77, 0x57),
            33 to LayoutItem(0x66, 0x46),
            46 to LayoutItem(0x70, 0x50),
            48 to LayoutItem(0x67, 0x47),
            53 to LayoutItem(0x6a, 0x4a),
            49 to LayoutItem(0x6c, 0x4c),
            37 to LayoutItem(0x75, 0x55),
            43 to LayoutItem(0x79, 0x59),
            44 to LayoutItem(0x3b, 0x3a),

            29 to LayoutItem(0x61, 0x41),
            47 to LayoutItem(0x72, 0x52),
            32 to LayoutItem(0x73, 0x53),
            34 to LayoutItem(0x74, 0x54),
            35 to LayoutItem(0x64, 0x44),
            36 to LayoutItem(0x68, 0x48),
            38 to LayoutItem(0x6e, 0x4e),
            39 to LayoutItem(0x65, 0x45),
            40 to LayoutItem(0x69, 0x49),
            74 to LayoutItem(0x6f, 0x4f),

            54 to LayoutItem(0x7a, 0x5a),
            52 to LayoutItem(0x78, 0x58),
            31 to LayoutItem(0x63, 0x43),
            50 to LayoutItem(0x76, 0x56),
            30 to LayoutItem(0x62, 0x42),
            42 to LayoutItem(0x6b, 0x4b),
            41 to LayoutItem(0x6d, 0x4d)

    )))

    val LAYOUT_7COLS_WERT = CommonKeyboardLayout(LayoutLayer(mapOf(
            0x2010 to LayoutItem(listOf(0x0077, 0x0071), listOf(0x0057, 0x0051)),
            0x2011 to LayoutItem(0x0065, 0x0045),
            0x2012 to LayoutItem(0x0072, 0x0052),
            0x2013 to LayoutItem(0x0074, 0x0054),
            0x2014 to LayoutItem(listOf(0x0075, 0x0079), listOf(0x0055, 0x0059)),
            0x2015 to LayoutItem(listOf(0x0069, 0x006f), listOf(0x0049, 0x004f)),
            0x2016 to LayoutItem(0x0070, 0x0050),

            0x2020 to LayoutItem(0x0061, 0x0041),
            0x2021 to LayoutItem(0x0073, 0x0053),
            0x2022 to LayoutItem(0x0064, 0x0044),
            0x2023 to LayoutItem(0x0066, 0x0046),
            0x2024 to LayoutItem(listOf(0x0067, 0x006a), listOf(0x0047, 0x004a)),
            0x2025 to LayoutItem(listOf(0x0068, 0x006b), listOf(0x0048, 0x004b)),
            0x2026 to LayoutItem(0x006c, 0x004c),

            0x2030 to LayoutItem(0x60000000 or KeyEvent.KEYCODE_SHIFT_LEFT),
            0x2031 to LayoutItem(listOf(0x007a, 0x0078), listOf(0x005a, 0x0058)),
            0x2032 to LayoutItem(listOf(0x0063, 0x0076), listOf(0x0043, 0x0056)),
            0x2033 to LayoutItem(0x0062, 0x0042),
            0x2034 to LayoutItem(0x006e, 0x004e),
            0x2035 to LayoutItem(0x006d, 0x004d),

            56 to LayoutItem(listOf(0x002e, 0x002c), listOf(0x003f, 0x0021))
    ), labels = mapOf(
            0x2030 to "aA"
    )), cycle = false)

}
