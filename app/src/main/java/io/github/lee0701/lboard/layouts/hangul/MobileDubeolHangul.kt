package io.github.lee0701.lboard.layouts.hangul

import android.view.KeyEvent
import io.github.lee0701.lboard.hangul.CombinationTable
import io.github.lee0701.lboard.hangul.VirtualJamoTable
import io.github.lee0701.lboard.hardkeyboard.CommonKeyboardLayout

import io.github.lee0701.lboard.hardkeyboard.CommonKeyboardLayout.*
import io.github.lee0701.lboard.hardkeyboard.SystemCode

object MobileDubeolHangul {
    
    val LAYOUT_CHEONJIIN = CommonKeyboardLayout(LayoutLayer(mapOf(
            0x2001 to LayoutItem(listOf(0x3163)),
            0x2002 to LayoutItem(listOf(0x100318d)),
            0x2003 to LayoutItem(listOf(0x3161)),
            0x2004 to LayoutItem(listOf(0x3131, 0x314b, 0x3132)),
            0x2005 to LayoutItem(listOf(0x3134, 0x3139)),
            0x2006 to LayoutItem(listOf(0x3137, 0x314c, 0x3138)),
            0x2007 to LayoutItem(listOf(0x3142, 0x314d, 0x3143)),
            0x2008 to LayoutItem(listOf(0x3145, 0x314e, 0x3146)),
            0x2009 to LayoutItem(listOf(0x3148, 0x314a, 0x3149)),
            0x200a to LayoutItem(listOf(0x3147, 0x3141)),
            0x200b to LayoutItem(listOf(0x002c, 0x002e, 0x003f, 0x0021)),
            0x200c to LayoutItem(SystemCode.KEYPRESS or KeyEvent.KEYCODE_SPACE),

            0x2204 to LayoutItem(0x3132),
            0x2304 to LayoutItem(0x314b),
            0x2404 to LayoutItem(0x314b),
            0x2504 to LayoutItem(0x314b),

            0x2205 to LayoutItem(0x3139),
            0x2305 to LayoutItem(0x3139),
            0x2405 to LayoutItem(0x3139),
            0x2505 to LayoutItem(0x3139),

            0x2206 to LayoutItem(0x3138),
            0x2306 to LayoutItem(0x314c),
            0x2406 to LayoutItem(0x314c),
            0x2506 to LayoutItem(0x314c),

            0x2207 to LayoutItem(0x3143),
            0x2307 to LayoutItem(0x314d),
            0x2407 to LayoutItem(0x314d),
            0x2507 to LayoutItem(0x314d),

            0x2208 to LayoutItem(0x3146),
            0x2308 to LayoutItem(0x314e),
            0x2408 to LayoutItem(0x314e),
            0x2508 to LayoutItem(0x314e),

            0x2209 to LayoutItem(0x3149),
            0x2309 to LayoutItem(0x314a),
            0x2409 to LayoutItem(0x314a),
            0x2509 to LayoutItem(0x314a),

            0x220a to LayoutItem(0x3141),
            0x230a to LayoutItem(0x3141),
            0x240a to LayoutItem(0x3141),
            0x250a to LayoutItem(0x3141),

            // Flick codes
            0x2401 to LayoutItem(0x3153),   // ㅓ
            0x2501 to LayoutItem(0x314f),   // ㅏ
            0x2203 to LayoutItem(0x3157),   // ㅗ
            0x2303 to LayoutItem(0x315c),   // ㅜ
            0x2202 to LayoutItem(0x315b),   // ㅛ
            0x2302 to LayoutItem(0x3160),   // ㅠ
            0x2402 to LayoutItem(0x3155),   // ㅕ
            0x2502 to LayoutItem(0x3151)    // ㅑ
    ), labels = mapOf(
            0x2004 to ("ㄱㅋ" to ""),
            0x2006 to ("ㄷㅌ" to ""),
            0x2007 to ("ㅂㅍ" to ""),
            0x2008 to ("ㅅㅎ" to ""),
            0x2009 to ("ㅈㅊ" to ""),
            0x200c to ("간격" to "")
    )), spaceForSeparation = true)
    
    val COMBINATION_CHEONJIIN = CombinationTable(mapOf(

            0x1175 to 0x100119e to 0x1161,	        // ㅏ
            0x1161 to 0x1175 to 0x1162,         	// ㅐ
            0x1161 to 0x100119e to 0x1163,	        // ㅑ
            0x1163 to 0x1175 to 0x1164,	            // ㅒ
            0x100119e to 0x100119e to 0x10011a2,	// ᆢ
            0x100119e to 0x1175 to 0x1165,	        // ㅓ
            0x1165 to 0x1175 to 0x1166,             // ㅔ
            0x10011a2 to 0x1175 to 0x1167,	        // ㅕ (··+ㅣ)
            0x100119e to 0x1165 to 0x1167,	        // ㅕ (·+ㅓ)
            0x1165 to 0x100119e to 0x1167,	        // ㅕ (ㅓ+·)
            0x1167 to 0x1175 to 0x1168,	            // ㅖ
            0x100119e to 0x1173 to 0x1169,	        // ㅗ
            0x1169 to 0x1162 to 0x116b,	            // ㅙ (ㅗ+ㅐ)
            0x116a to 0x1175 to 0x116b,         	// ㅙ
            0x1169 to 0x1161 to 0x116a,	            // ㅘ (ㅗ+ㅏ)
            0x116c to 0x100119e to 0x116a,	        // ㅘ (ㅚ+·)
            0x1169 to 0x1175 to 0x116c,         	// ㅚ
            0x10011a2 to 0x1173 to 0x116d,	        // ㅛ (··+ㅡ)
            0x100119e to 0x1169 to 0x116d,	        // ㅛ (·+ㅗ)
            0x1169 to 0x100119e to 0x116d,	        // ㅛ (ㅗ+·)
            0x1173 to 0x100119e to 0x116e,	        // ㅜ
            0x116e to 0x1166 to 0x1170,	            // ㅞ (ㅜ+ㅔ)
            0x116f to 0x1175 to 0x1170,	            // ㅞ
            0x1172 to 0x1175 to 0x116f,	            // ㅝ (ㅠ+ㅣ)
            0x116e to 0x1165 to 0x116f,	            // ㅝ (ㅜ+ㅓ)
            0x116e to 0x1175 to 0x1171,	            // ㅟ
            0x116e to 0x100119e to 0x1172,	        // ㅠ
            0x1173 to 0x1175 to 0x1174,         	// ㅢ

//            0x11a8 to 0x11a8 to 0x11a9,	// ㄲ
            0x11a8 to 0x11ba to 0x11aa,	// ㄳ
            0x11ab to 0x11bd to 0x11ac,	// ㄵ
//            0x11ab to 0x11ba to 0x11ad,	// ㄶ
            0x11ab to 0x11c2 to 0x11ad,	// ㄶ
            0x11af to 0x11a8 to 0x11b0,	// ㄺ
            0x11af to 0x11bc to 0x11b1,	// ㄻ
            0x11af to 0x11b8 to 0x11b2,	// ㄼ
            0x11af to 0x11ba to 0x11b3,	// ㄽ
            0x11af to 0x11c0 to 0x11b4,	// ㄾ
            0x11af to 0x11c1 to 0x11b5,	// ㄿ
            0x11af to 0x11c2 to 0x11b6,	// ㅀ
            0x11b8 to 0x11ba to 0x11b9	// ㅄ
//            0x11ba to 0x11ba to 0x11bb	// ㅆ
    ))

    val VIRTUAL_CHEONJIIN = VirtualJamoTable(mapOf(
            0x100119e to 0x00b7,
            0x10011a2 to 0x2025
    ))

    val LAYOUT_NARATGEUL = CommonKeyboardLayout(LayoutLayer(mapOf(
            0x2001 to LayoutItem(listOf(0x3131)),
            0x2002 to LayoutItem(listOf(0x3134)),
            0x2003 to LayoutItem(listOf(0x314f, 0x3153)),
            0x2004 to LayoutItem(listOf(0x3139)),
            0x2005 to LayoutItem(listOf(0x3141)),
            0x2006 to LayoutItem(listOf(0x3157, 0x315c)),
            0x2007 to LayoutItem(listOf(0x3145)),
            0x2008 to LayoutItem(listOf(0x3147)),
            0x2009 to LayoutItem(listOf(0x3163)),
            0x200a to LayoutItem(listOf(0x3161)),
            0x200b to LayoutItem(listOf(0x70000000)),
            0x200c to LayoutItem(listOf(0x70000001))
    ), labels = mapOf(
            0x200b to ("획추가" to ""),
            0x200c to ("쌍자음" to "")
    )), listOf(
            StrokeTable(mapOf(
                    0x3131 to 0x314b,   // ㄱ-ㅋ
                    0x3134 to 0x3137,   // ㄴ-ㄷ
                    0x3137 to 0x314c,   // ㄷ-ㅌ
                    0x3141 to 0x3142,   // ㅁ-ㅂ
                    0x3142 to 0x314d,   // ㅂ-ㅍ
                    0x3145 to 0x3148,   // ㅅ-ㅈ
                    0x3148 to 0x314a,   // ㅈ-ㅊ
                    0x3147 to 0x314e,   // ㅇ-ㅎ
                    0x3146 to 0x3149,   // ㅆ-ㅉ

                    0x314f to 0x3151,   // ㅑ
                    0x3153 to 0x3155,   // ㅕ
                    0x3157 to 0x315b,   // ㅛ
                    0x315c to 0x3160    // ㅠ
            )),
            StrokeTable(mapOf(
                    0x3131 to 0x3132,   // ㄲ
                    0x3137 to 0x3138,   // ㄸ
                    0x3142 to 0x3143,   // ㅃ
                    0x3145 to 0x3146,   // ㅆ
                    0x3148 to 0x3149    // ㅉ
            ))
    ))

    val COMBINATION_NARATGEUL = CombinationTable(mapOf(
            0x1161 to 0x1175 to 0x1162, // ㅐ
            0x1163 to 0x1175 to 0x1164, // ㅒ
            0x1165 to 0x1175 to 0x1166, // ㅔ
            0x1167 to 0x1175 to 0x1168, // ㅖ

            0x1169 to 0x1162 to 0x116b,	// ㅙ (ㅗ+ㅐ)
            0x116a to 0x1175 to 0x116b,	// ㅙ (ㅘ+ㅣ)
            0x1169 to 0x1161 to 0x116a,	// ㅘ
            0x1169 to 0x1175 to 0x116c,	// ㅚ
            0x116e to 0x1166 to 0x1170,	// ㅞ (ㅜ+ㅔ)
            0x116f to 0x1175 to 0x1170,	// ㅞ (ㅝ+ㅣ)
//            0x116e to 0x1161 to 0x116f,	// ㅝ (ㅜ+ㅏ)
            0x116e to 0x1165 to 0x116f,	// ㅝ (ㅜ+ㅓ)
            0x116e to 0x1175 to 0x1171,	// ㅟ
            0x1173 to 0x1175 to 0x1174,

//            0x11a8 to 0x11a8 to 0x11a9,	// ㄲ
            0x11a8 to 0x11ba to 0x11aa,	// ㄳ
//            0x11ab to 0x11ba to 0x11ac,	// ㄵ
            0x11ab to 0x11bd to 0x11ac,	// ㄵ
//            0x11ab to 0x11bc to 0x11ad,	// ㄶ
            0x11ab to 0x11c2 to 0x11ad,	// ㄶ
            0x11af to 0x11a8 to 0x11b0,	// ㄺ
            0x11af to 0x11b7 to 0x11b1,	// ㄻ
            0x11af to 0x11b8 to 0x11b2,	// ㄼ
            0x11af to 0x11ba to 0x11b3,	// ㄽ
//            0x11af to 0x11ab to 0x11b4,	// ㄾ
//            0x11af to 0x11ae to 0x11b4,	// ㄾ
            0x11af to 0x11c0 to 0x11b4,	// ㄾ
            0x11af to 0x11c1 to 0x11b5,	// ㄿ
//            0x11af to 0x11bc to 0x11b6,	// ㅀ
            0x11af to 0x11c2 to 0x11b6,	// ㅀ
            0x11b8 to 0x11ba to 0x11b9	// ㅄ
//            0x11ba to 0x11ba to 0x11bb	// ㅆ
    ))

    val LAYOUT_FIFTEEN_DUBEOL = CommonKeyboardLayout(mapOf(0 to LayoutLayer(mapOf(

            7 to LayoutItem(0x0030),
            8 to LayoutItem(0x0031),
            9 to LayoutItem(0x0032),
            10 to LayoutItem(0x0033),
            11 to LayoutItem(0x0034),
            12 to LayoutItem(0x0035),
            13 to LayoutItem(0x0036),
            14 to LayoutItem(0x0037),
            15 to LayoutItem(0x0038),
            16 to LayoutItem(0x0039),

            0x2001 to LayoutItem("ㅂㅛ".map { it.toInt() }, "ㅃ".map { it.toInt() }),
            0x2002 to LayoutItem("ㅈㅕ".map { it.toInt() }, "ㅉ".map { it.toInt() }),
            0x2003 to LayoutItem("ㄷㅑ".map { it.toInt() }, "ㄸ".map { it.toInt() }),
            0x2004 to LayoutItem("ㄱㅐ".map { it.toInt() }, "ㄲㅒ".map { it.toInt() }),
            0x2005 to LayoutItem("ㅅㅔ".map { it.toInt() }, "ㅆㅖ".map { it.toInt() }),
            0x2006 to LayoutItem("ㅁㅗ".map { it.toInt() }),
            0x2007 to LayoutItem("ㄴㅓ".map { it.toInt() }),
            0x2008 to LayoutItem("ㅇㅏ".map { it.toInt() }),
            0x2009 to LayoutItem("ㄹㅣ".map { it.toInt() }),
            0x200a to LayoutItem("ㅎ".map { it.toInt() }),
            0x200b to LayoutItem("ㅋㅠ".map { it.toInt() }),
            0x200c to LayoutItem("ㅌㅜ".map { it.toInt() }),
            0x200d to LayoutItem("ㅊㅡ".map { it.toInt() }),
            0x200e to LayoutItem("ㅍ".map { it.toInt() }),

            // Flick layout
            0x2201 to LayoutItem('ㅃ'.toInt()),
            0x2202 to LayoutItem('ㅉ'.toInt()),
            0x2203 to LayoutItem('ㄸ'.toInt()),
            0x2204 to LayoutItem('ㄲ'.toInt()),
            0x2205 to LayoutItem('ㅆ'.toInt()),
            0x220b to LayoutItem('ㅋ'.toInt()),

            0x2304 to LayoutItem('ㅒ'.toInt()),
            0x2305 to LayoutItem('ㅖ'.toInt()),
            0x230b to LayoutItem('ㅋ'.toInt()),

            0x2401 to LayoutItem('ㅂ'.toInt()),
            0x2402 to LayoutItem('ㅈ'.toInt()),
            0x2403 to LayoutItem('ㄷ'.toInt()),
            0x2404 to LayoutItem('ㄱ'.toInt()),
            0x2405 to LayoutItem('ㅅ'.toInt()),
            0x2406 to LayoutItem('ㅁ'.toInt()),
            0x2407 to LayoutItem('ㄴ'.toInt()),
            0x2408 to LayoutItem('ㅇ'.toInt()),
            0x2409 to LayoutItem('ㄹ'.toInt()),
            0x240a to LayoutItem('ㅎ'.toInt()),
            0x240b to LayoutItem('ㅋ'.toInt()),
            0x240c to LayoutItem('ㅌ'.toInt()),
            0x240d to LayoutItem('ㅊ'.toInt()),
            0x240e to LayoutItem('ㅍ'.toInt()),

            0x2501 to LayoutItem('ㅛ'.toInt()),
            0x2502 to LayoutItem('ㅕ'.toInt()),
            0x2503 to LayoutItem('ㅑ'.toInt()),
            0x2504 to LayoutItem('ㅐ'.toInt()),
            0x2505 to LayoutItem('ㅔ'.toInt()),
            0x2506 to LayoutItem('ㅗ'.toInt()),
            0x2507 to LayoutItem('ㅓ'.toInt()),
            0x2508 to LayoutItem('ㅏ'.toInt()),
            0x2509 to LayoutItem('ㅣ'.toInt()),
            0x250a to LayoutItem('ㅎ'.toInt()),
            0x250b to LayoutItem('ㅠ'.toInt()),
            0x250c to LayoutItem('ㅜ'.toInt()),
            0x250d to LayoutItem('ㅡ'.toInt()),
            0x250e to LayoutItem('ㅍ'.toInt())

    ), labels = mapOf(
            0x2001 to ("ㅂㅛ" to "ㅃ"),
            0x2002 to ("ㅈㅕ" to "ㅉ"),
            0x2003 to ("ㄷㅑ" to "ㄸ"),
            0x2004 to ("ㄱㅐ" to "ㄲㅒ"),
            0x2005 to ("ㅅㅔ" to "ㅆㅖ"),
            0x2006 to ("ㅁㅗ" to "ㅁㅗ"),
            0x2007 to ("ㄴㅓ" to "ㄴㅓ"),
            0x2008 to ("ㅇㅏ" to "ㅇㅏ"),
            0x2009 to ("ㄹㅣ" to "ㄹㅣ"),
            0x200a to ("ㅎ" to "ㅎ"),
            0x200b to ("ㅋㅠ" to "ㅋㅠ"),
            0x200c to ("ㅌㅜ" to "ㅌㅜ"),
            0x200d to ("ㅊㅡ" to "ㅊㅡ"),
            0x200e to ("ㅍ" to "ㅍ")
    )), CommonKeyboardLayout.LAYER_MORE_KEYS_KEYCODE to LayoutLayer(mapOf(
            0x2002 to LayoutItem(8),
            0x2003 to LayoutItem(9),
            0x2004 to LayoutItem(10),
            0x2007 to LayoutItem(11),
            0x2008 to LayoutItem(12),
            0x2009 to LayoutItem(13),
            0x200c to LayoutItem(14),
            0x200d to LayoutItem(15),
            0x200e to LayoutItem(16),
            62 to LayoutItem(7)
    ))), timeout = true)

}
