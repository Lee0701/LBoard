package io.github.lee0701.lboard.softkeyboard

interface MoreKeysSupportedSoftKeyboard: SoftKeyboard {

    fun showMoreKeysKeyboard(keyCode: Int, moreKeys: List<Int>)
    fun closeMoreKeysKeyboard()

}
