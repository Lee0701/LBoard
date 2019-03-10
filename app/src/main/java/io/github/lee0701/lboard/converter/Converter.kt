package io.github.lee0701.lboard.converter

import io.github.lee0701.lboard.InputMethodModule

interface Converter: InputMethodModule {

    fun convert(text: ComposingText): ComposingText

}
