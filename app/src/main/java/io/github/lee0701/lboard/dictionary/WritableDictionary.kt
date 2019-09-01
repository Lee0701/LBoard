package io.github.lee0701.lboard.dictionary

import java.io.InputStream
import java.io.OutputStream

interface WritableDictionary: Dictionary {

    fun read(inputStream: InputStream)
    fun write(outputStream: OutputStream)

}
