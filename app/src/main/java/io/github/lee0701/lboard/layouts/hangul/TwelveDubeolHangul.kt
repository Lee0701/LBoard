package io.github.lee0701.lboard.layouts.hangul

import io.github.lee0701.lboard.hangul.CombinationTable
import io.github.lee0701.lboard.hangul.VirtualJamoTable
import io.github.lee0701.lboard.hardkeyboard.TwelveKeyboardLayout

import io.github.lee0701.lboard.hardkeyboard.TwelveKeyboardLayout.LayoutItem

object TwelveDubeolHangul {
    
    val LAYOUT_CHEONJIIN = TwelveKeyboardLayout(mapOf(
            0x0201 to LayoutItem(listOf(0x3163)),
            0x0202 to LayoutItem(listOf(0x100318d)),
            0x0203 to LayoutItem(listOf(0x3161)),
            0x0204 to LayoutItem(listOf(0x3131, 0x314b, 0x3132)),
            0x0205 to LayoutItem(listOf(0x3134, 0x3139)),
            0x0206 to LayoutItem(listOf(0x3137, 0x314c, 0x3138)),
            0x0207 to LayoutItem(listOf(0x3142, 0x314d, 0x3143)),
            0x0208 to LayoutItem(listOf(0x3145, 0x314e, 0x3146)),
            0x0209 to LayoutItem(listOf(0x3148, 0x314a, 0x3149)),
            0x020a to LayoutItem(listOf(0x3147, 0x3141)),
            0x020b to LayoutItem(listOf())
    ), labelLength = 2)
    
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
    
}
