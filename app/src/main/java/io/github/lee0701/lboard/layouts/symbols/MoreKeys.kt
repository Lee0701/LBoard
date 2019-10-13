package io.github.lee0701.lboard.layouts.symbols

import io.github.lee0701.lboard.hardkeyboard.CommonKeyboardLayout
import io.github.lee0701.lboard.hardkeyboard.CommonKeyboardLayout.*

object MoreKeys {

    val MOREKEYS_NUMBERS = CommonKeyboardLayout(mapOf(
            CommonKeyboardLayout.LAYER_MORE_KEYS_KEYCODE to LayoutLayer(mapOf(
                    45 to LayoutItem(8),
                    51 to LayoutItem(9),
                    33 to LayoutItem(10),
                    46 to LayoutItem(11),
                    48 to LayoutItem(12),
                    53 to LayoutItem(13),
                    49 to LayoutItem(14),
                    37 to LayoutItem(15),
                    43 to LayoutItem(16),
                    44 to LayoutItem(7)
            ))
    ))

    val MOREKEYS_LATIN_SUPPLEMENT = CommonKeyboardLayout(mapOf(
            0 to LayoutLayer(mapOf(
                    // A
                    0x1041 to LayoutItem(0x00c6),
                    0x1141 to LayoutItem(0x00c3),
                    0x1241 to LayoutItem(0x00c5),
                    0x1341 to LayoutItem(0x0100),
                    0x1441 to LayoutItem(0x00c0),
                    0x1541 to LayoutItem(0x00c1),
                    0x1641 to LayoutItem(0x00c2),
                    0x1741 to LayoutItem(0x00c4),

                    // C
                    0x1043 to LayoutItem(0x00c7),

                    // E
                    0x1045 to LayoutItem(0x0112),
                    0x1145 to LayoutItem(0x00ca),
                    0x1245 to LayoutItem(0x00c9),
                    0x1345 to LayoutItem(0x00c8),
                    0x1445 to LayoutItem(0x00cb),

                    // I
                    0x1049 to LayoutItem(0x00cc),
                    0x1149 to LayoutItem(0x00cf),
                    0x1249 to LayoutItem(0x00cd),
                    0x1349 to LayoutItem(0x00ce),
                    0x1449 to LayoutItem(0x012a),

                    // N
                    0x104e to LayoutItem(0x00d1),

                    // O
                    0x104f to LayoutItem(0x014c),
                    0x114f to LayoutItem(0x0152),
                    0x124f to LayoutItem(0x00d8),
                    0x134f to LayoutItem(0x00d5),
                    0x144f to LayoutItem(0x00d6),
                    0x154f to LayoutItem(0x00d3),
                    0x164f to LayoutItem(0x00d4),
                    0x174f to LayoutItem(0x00d2),

                    // S
                    0x1053 to LayoutItem(0x1e9e),

                    // U
                    0x1055 to LayoutItem(0x016a),
                    0x1155 to LayoutItem(0x00dc),
                    0x1255 to LayoutItem(0x00da),
                    0x1355 to LayoutItem(0x00db),
                    0x1455 to LayoutItem(0x00d9),

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
            CommonKeyboardLayout.LAYER_MORE_KEYS_CHARCODE to LayoutLayer(mapOf(
                    0x0041 to LayoutItem(listOf(0x1041, 0x1141, 0x1241, 0x1341, 0x1441, 0x1541, 0x1641, 0x1741)),
                    0x0043 to LayoutItem(listOf(0x1043)),
                    0x0045 to LayoutItem(listOf(0x1045, 0x1145, 0x1245, 0x1345, 0x1445)),
                    0x0049 to LayoutItem(listOf(0x1049, 0x1149, 0x1249, 0x1349, 0x1449)),
                    0x004e to LayoutItem(listOf(0x104e)),
                    0x004f to LayoutItem(listOf(0x104f, 0x114f, 0x124f, 0x134f, 0x144f, 0x154f, 0x164f, 0x174f)),
                    0x0053 to LayoutItem(listOf(0x1053)),
                    0x0055 to LayoutItem(listOf(0x1055, 0x1155, 0x1255, 0x1355, 0x1455)),

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

    val MOREKEYS_ROMANIZATION = CommonKeyboardLayout(mapOf(
            0 to LayoutLayer(mapOf(
                    0x1045 to LayoutItem(0x00cb),
                    0x104f to LayoutItem(0x014e),
                    0x1055 to LayoutItem(0x016c),

                    0x1065 to LayoutItem(0x00eb),
                    0x106f to LayoutItem(0x014f),
                    0x1075 to LayoutItem(0x016d)
            )),
            CommonKeyboardLayout.LAYER_MORE_KEYS_CHARCODE to LayoutLayer((mapOf(
                    0x0045 to LayoutItem(listOf(0x1045)),   // E
                    0x004f to LayoutItem(listOf(0x104f)),   // O
                    0x0055 to LayoutItem(listOf(0x1055)),   // U

                    0x0065 to LayoutItem(listOf(0x1065)),   // e
                    0x006f to LayoutItem(listOf(0x106f)),   // o
                    0x0075 to LayoutItem(listOf(0x1075))    // u
            )))
    ))

    val MOREKEYS_FIFTEEN_NUMBERS = CommonKeyboardLayout(mapOf(0 to LayoutLayer(mapOf(
            7 to LayoutItem(0x0030, 0x0029),
            8 to LayoutItem(0x0031, 0x0021),
            9 to LayoutItem(0x0032, 0x0040),
            10 to LayoutItem(0x0033, 0x0023),
            11 to LayoutItem(0x0034, 0x0024),
            12 to LayoutItem(0x0035, 0x0025),
            13 to LayoutItem(0x0036, 0x005e),
            14 to LayoutItem(0x0037, 0x0026),
            15 to LayoutItem(0x0038, 0x002a),
            16 to LayoutItem(0x0039, 0x0028),

            56 to LayoutItem(listOf(0x2c, 0x2e))

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
    ))))

}
