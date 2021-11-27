package io.github.lee0701.lboard.layouts.hangul

import android.view.KeyEvent
import io.github.lee0701.lboard.hangul.CombinationTable
import io.github.lee0701.lboard.hangul.VirtualJamoTable
import io.github.lee0701.lboard.hardkeyboard.CommonKeyboardLayout

import io.github.lee0701.lboard.hardkeyboard.CommonKeyboardLayout.*
import io.github.lee0701.lboard.hardkeyboard.SystemCode
import io.github.lee0701.lboard.layouts.symbols.MoreKeys

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

    val LAYOUT_FIFTEEN_DUBEOL = MoreKeys.MOREKEYS_FIFTEEN_NUMBERS + CommonKeyboardLayout(mapOf(0 to LayoutLayer(mapOf(

            0x2001 to LayoutItem("ㅂㅛ".map { it.code }, "ㅃ".map { it.code }),
            0x2002 to LayoutItem("ㅈㅕ".map { it.code }, "ㅉ".map { it.code }),
            0x2003 to LayoutItem("ㄷㅑ".map { it.code }, "ㄸ".map { it.code }),
            0x2004 to LayoutItem("ㄱㅐ".map { it.code }, "ㄲㅒ".map { it.code }),
            0x2005 to LayoutItem("ㅅㅔ".map { it.code }, "ㅆㅖ".map { it.code }),
            0x2006 to LayoutItem("ㅁ".map { it.code }),
            0x2007 to LayoutItem("ㄴㅗ".map { it.code }),
            0x2008 to LayoutItem("ㅇㅓ".map { it.code }),
            0x2009 to LayoutItem("ㄹㅏ".map { it.code }),
            0x200a to LayoutItem("ㅎㅣ".map { it.code }),
            0x200b to LayoutItem("ㅋ".map { it.code }),
            0x200c to LayoutItem("ㅌㅠ".map { it.code }),
            0x200d to LayoutItem("ㅊㅜ".map { it.code }),
            0x200e to LayoutItem("ㅍㅡ".map { it.code }),

            // Flick layout
            0x2201 to LayoutItem('ㅃ'.code),
            0x2202 to LayoutItem('ㅉ'.code),
            0x2203 to LayoutItem('ㄸ'.code),
            0x2204 to LayoutItem('ㄲ'.code),
            0x2205 to LayoutItem('ㅆ'.code),

            0x2304 to LayoutItem('ㅒ'.code),
            0x2305 to LayoutItem('ㅖ'.code),

            0x2401 to LayoutItem('ㅂ'.code),
            0x2402 to LayoutItem('ㅈ'.code),
            0x2403 to LayoutItem('ㄷ'.code),
            0x2404 to LayoutItem('ㄱ'.code),
            0x2405 to LayoutItem('ㅅ'.code),
            0x2406 to LayoutItem('ㅁ'.code),
            0x2407 to LayoutItem('ㄴ'.code),
            0x2408 to LayoutItem('ㅇ'.code),
            0x2409 to LayoutItem('ㄹ'.code),
            0x240a to LayoutItem('ㅎ'.code),
            0x240b to LayoutItem('ㅋ'.code),
            0x240c to LayoutItem('ㅌ'.code),
            0x240d to LayoutItem('ㅊ'.code),
            0x240e to LayoutItem('ㅍ'.code),

            0x2501 to LayoutItem('ㅛ'.code),
            0x2502 to LayoutItem('ㅕ'.code),
            0x2503 to LayoutItem('ㅑ'.code),
            0x2504 to LayoutItem('ㅐ'.code),
            0x2505 to LayoutItem('ㅔ'.code),
            0x2506 to LayoutItem('ㅁ'.code),
            0x2507 to LayoutItem('ㅗ'.code),
            0x2508 to LayoutItem('ㅓ'.code),
            0x2509 to LayoutItem('ㅏ'.code),
            0x250a to LayoutItem('ㅣ'.code),
            0x250b to LayoutItem('ㅋ'.code),
            0x250c to LayoutItem('ㅠ'.code),
            0x250d to LayoutItem('ㅜ'.code),
            0x250e to LayoutItem('ㅡ'.code)

    ), labels = mapOf(
            0x2001 to ("ㅂㅛ" to "ㅃ"),
            0x2002 to ("ㅈㅕ" to "ㅉ"),
            0x2003 to ("ㄷㅑ" to "ㄸ"),
            0x2004 to ("ㄱㅐ" to "ㄲㅒ"),
            0x2005 to ("ㅅㅔ" to "ㅆㅖ"),
            0x2006 to ("ㅁ" to "ㅁ"),
            0x2007 to ("ㄴㅗ" to "ㄴㅗ"),
            0x2008 to ("ㅇㅓ" to "ㅇㅓ"),
            0x2009 to ("ㄹㅏ" to "ㄹㅏ"),
            0x200a to ("ㅎㅣ" to "ㅎㅣ"),
            0x200b to ("ㅋ" to "ㅋ"),
            0x200c to ("ㅌㅠ" to "ㅌㅠ"),
            0x200d to ("ㅊㅜ" to "ㅊㅜ"),
            0x200e to ("ㅍㅡ" to "ㅍㅡ")
    ))), timeout = true)

}
