package io.github.lee0701.lboard.softkeyboard

import io.github.lee0701.lboard.R

data class BasicKeyboardSound(
        val down: Int,
        val up: Int?
) {
    companion object {
        val CLICK = BasicKeyboardSound(R.raw.type_click_down, R.raw.type_click_up)
        val POP = BasicKeyboardSound(R.raw.type_pop_down, R.raw.type_pop_up)

        val SOUNDS = mapOf<String, BasicKeyboardSound>(
                "click" to CLICK,
                "pop" to POP
        )
    }
}
