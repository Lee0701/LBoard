package io.github.lee0701.lboard.layouts.hangul

import io.github.lee0701.lboard.hangul.CombinationTable
import io.github.lee0701.lboard.hangul.VirtualJamoTable
import io.github.lee0701.lboard.hardkeyboard.CommonKeyboardLayout

import io.github.lee0701.lboard.hardkeyboard.CommonKeyboardLayout.*

object TwelveDubeolHangul {
    
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
            0x2004 to "ㄱㅋ",
            0x2006 to "ㄷㅌ",
            0x2007 to "ㅂㅍ",
            0x2008 to "ㅅㅎ",
            0x2009 to "ㅈㅊ"
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
            0x200b to "획추가",
            0x200c to "쌍자음"
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
    
}
