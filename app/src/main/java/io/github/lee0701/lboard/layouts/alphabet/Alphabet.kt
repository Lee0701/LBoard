package io.github.lee0701.lboard.layouts.alphabet

import android.view.KeyEvent
import io.github.lee0701.lboard.hardkeyboard.CommonKeyboardLayout

import io.github.lee0701.lboard.hardkeyboard.CommonKeyboardLayout.*

object Alphabet {

    val LAYOUT_QWERTY = CommonKeyboardLayout(LayoutLayer(mapOf(
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
