package io.github.lee0701.lboard.preconverter

import io.github.lee0701.lboard.InputMethodModule

interface PreConverter: InputMethodModule {

    fun convert(text: ComposingText): ComposingText

}
