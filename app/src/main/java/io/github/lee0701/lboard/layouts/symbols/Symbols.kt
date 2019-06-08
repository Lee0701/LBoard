package io.github.lee0701.lboard.layouts.symbols

import io.github.lee0701.lboard.hardkeyboard.CommonKeyboardLayout

import io.github.lee0701.lboard.hardkeyboard.CommonKeyboardLayout.*
import io.github.lee0701.lboard.layouts.alphabet.Alphabet
import io.github.lee0701.lboard.layouts.soft.SoftLayout

object Symbols {

    val LAYOUT_SYMBOLS_A = CommonKeyboardLayout(10, LayoutLayer(mapOf(
            8 to LayoutItem(0x0021, 0x2460),
            9 to LayoutItem(0x0040, 0x2461),
            10 to LayoutItem(0x0023, 0x2462),
            11 to LayoutItem(0x0024, 0x2463),
            12 to LayoutItem(0x0025, 0x2464),
            13 to LayoutItem(0x005e, 0x2465),
            14 to LayoutItem(0x0026, 0x2466),
            15 to LayoutItem(0x002a, 0x2467),
            16 to LayoutItem(0x0028, 0x2468),
            7 to LayoutItem(0x0029, 0x24ea),

            45 to LayoutItem(0x0031, 0x0021),
            51 to LayoutItem(0x0032, 0x0040),
            33 to LayoutItem(0x0033, 0x0023),
            46 to LayoutItem(0x0034, 0x0024),
            48 to LayoutItem(0x0035, 0x0025),
            53 to LayoutItem(0x0036, 0x005e),
            49 to LayoutItem(0x0037, 0x0026),
            37 to LayoutItem(0x0038, 0x002a),
            43 to LayoutItem(0x0039, 0x0028),
            44 to LayoutItem(0x0030, 0x0029),

            29 to LayoutItem(0x007e, 0x203b),
            47 to LayoutItem(0x0027, 0x0060),
            32 to LayoutItem(0x005b, 0x007b),
            34 to LayoutItem(0x005d, 0x007d),
            35 to LayoutItem(0x002f, 0x005c),
            36 to LayoutItem(0x003c, 0x2190),
            38 to LayoutItem(0x003e, 0x2193),
            39 to LayoutItem(0x003a, 0x2191),
            40 to LayoutItem(0x003b, 0x2192),

            54 to LayoutItem(0x005f, 0x007c),
            52 to LayoutItem(0x00b7, 0x221a),
            31 to LayoutItem(0x003d, 0x00f7),
            50 to LayoutItem(0x002b, 0x00d7),
            30 to LayoutItem(0x003f, 0x03c0),
            42 to LayoutItem(0x002d, 0x300c),
            41 to LayoutItem(0x0022, 0x300d),

            56 to LayoutItem(0x002c)
    )))

    val LAYOUT_SYMBOLS_B = CommonKeyboardLayout(10, LayoutLayer(mapOf(
            8 to LayoutItem(0x0031, 0x2460),
            9 to LayoutItem(0x0032, 0x2461),
            10 to LayoutItem(0x0033, 0x2462),
            11 to LayoutItem(0x0034, 0x2463),
            12 to LayoutItem(0x0035, 0x2464),
            13 to LayoutItem(0x0036, 0x2465),
            14 to LayoutItem(0x0037, 0x2466),
            15 to LayoutItem(0x0038, 0x2467),
            16 to LayoutItem(0x0039, 0x2468),
            7 to LayoutItem(0x0030, 0x24ea),

            45 to LayoutItem(0x0021, 0x25cb),
            51 to LayoutItem(0x0040, 0x25cf),
            33 to LayoutItem(0x0023, 0x25ce),
            46 to LayoutItem(0x0024, 0x25a1),
            48 to LayoutItem(0x0025, 0x25a0),
            53 to LayoutItem(0x005e, 0x2661),
            49 to LayoutItem(0x0026, 0x2665),
            37 to LayoutItem(0x002a, 0x2606),
            43 to LayoutItem(0x0028, 0x2605),
            44 to LayoutItem(0x0029, 0x20a9),

            29 to LayoutItem(0x007e, 0x203b),
            47 to LayoutItem(0x0027, 0x0060),
            32 to LayoutItem(0x005b, 0x007b),
            34 to LayoutItem(0x005d, 0x007d),
            35 to LayoutItem(0x002f, 0x005c),
            36 to LayoutItem(0x003c, 0x2190),
            38 to LayoutItem(0x003e, 0x2193),
            39 to LayoutItem(0x003a, 0x2191),
            40 to LayoutItem(0x003b, 0x2192),

            54 to LayoutItem(0x005f, 0x007c),
            52 to LayoutItem(0x00b7, 0x221a),
            31 to LayoutItem(0x003d, 0x00f7),
            50 to LayoutItem(0x002b, 0x00d7),
            30 to LayoutItem(0x003f, 0x03c0),
            42 to LayoutItem(0x002d, 0x300c),
            41 to LayoutItem(0x0022, 0x300d),

            56 to LayoutItem(0x002c)
    )))

    val LAYOUT_SYMBOLS_GOOGLE = CommonKeyboardLayout(10, LayoutLayer(mapOf(

            45 to LayoutItem(0x0032),
            51 to LayoutItem(0x0033),
            33 to LayoutItem(0x0034),
            46 to LayoutItem(0x0035),
            48 to LayoutItem(0x0036),
            36 to LayoutItem(0x0037),
            43 to LayoutItem(0x0038),
            44 to LayoutItem(0x0039),

            29 to LayoutItem(0x0031),
            47 to LayoutItem(0x0027),
            32 to LayoutItem(0x0022),
            34 to LayoutItem(0x003c),
            35 to LayoutItem(0x003e),
            38 to LayoutItem(0x0028),
            39 to LayoutItem(0x0029),
            40 to LayoutItem(0x0030),

            54 to LayoutItem(0x002d),
            52 to LayoutItem(0x0021),
            31 to LayoutItem(0x003f),
            50 to LayoutItem(0x003a),
            42 to LayoutItem(0x003b),
            41 to LayoutItem(0x002f),

            56 to LayoutItem(0x002c)
    )))

    val LAYOUT_SYMBOLS_7COLS = CommonKeyboardLayout(mapOf(10 to LayoutLayer(mapOf(
            0x0210 to LayoutItem(0x0021),
            0x0211 to LayoutItem(0x0022),
            0x0212 to LayoutItem(0x0027),
            0x0213 to LayoutItem(0x0037),
            0x0214 to LayoutItem(0x0038),
            0x0215 to LayoutItem(0x0039),
            0x0216 to LayoutItem(0x0029),

            0x0220 to LayoutItem(0x003f),
            0x0221 to LayoutItem(0x003a),
            0x0222 to LayoutItem(0x003b),
            0x0223 to LayoutItem(0x0034),
            0x0224 to LayoutItem(0x0035),
            0x0225 to LayoutItem(0x0036),
            0x0226 to LayoutItem(0x0028),

            0x0230 to LayoutItem(0x002f),
            0x0231 to LayoutItem(0x002d),
            0x0232 to LayoutItem(0x0030),
            0x0233 to LayoutItem(0x0031),
            0x0234 to LayoutItem(0x0032),
            0x0235 to LayoutItem(0x0033),

            56 to LayoutItem(0x002c)
    ))))

}
