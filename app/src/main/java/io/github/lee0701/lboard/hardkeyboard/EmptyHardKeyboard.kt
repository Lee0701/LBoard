package io.github.lee0701.lboard.hardkeyboard

class EmptyHardKeyboard(name: String): HardKeyboard(name) {
    override fun onKey(keyCode: Int): Boolean {
        return false
    }
}
