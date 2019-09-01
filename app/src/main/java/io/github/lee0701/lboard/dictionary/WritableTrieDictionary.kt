package io.github.lee0701.lboard.dictionary

import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream

class WritableTrieDictionary: EditableTrieDictionary(), WritableDictionary {

    override fun read(inputStream: InputStream) {
        val reader = BufferedReader(InputStreamReader(inputStream))
        while(true) {
            val line = reader.readLine()?.split("\t") ?: break
            val text = line[0]
            val frequency = if(line.size >= 2) line[1].toFloat() else 0f
            val pos = if(line.size >= 3) line[2].toInt() else 0
            insert(Dictionary.Word(text, frequency, pos))
        }
    }

    override fun write(outputStream: OutputStream) {
        val list = searchPrefix("", Integer.MAX_VALUE)
        val data = list.map { "${it.text}\t${it.frequency}\t${it.pos}" }.joinToString("\n").toByteArray()
        outputStream.write(data)
    }
}
