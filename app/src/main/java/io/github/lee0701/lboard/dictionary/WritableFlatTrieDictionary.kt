package io.github.lee0701.lboard.dictionary

import java.io.File
import java.nio.ByteBuffer

class WritableFlatTrieDictionary(
        val file: File
): FlatTrieDictionary(), WritableDictionary {

    override fun read() {
        if(!file.exists()) file.createNewFile()
        val data = file.readBytes()
        this.buffer = ByteBuffer.wrap(data)
        this.root = buffer.getInt(data.size - 4)
    }

    override fun write() {
        if(!file.exists()) file.createNewFile()
        file.writeBytes(buffer.array())
    }
}
