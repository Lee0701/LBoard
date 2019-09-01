package io.github.lee0701.lboard.dictionary

import java.io.*

class WritableTrieDictionary(
        val file: File
): EditableTrieDictionary(), WritableDictionary {

    override fun read() {
        val reader = BufferedReader(InputStreamReader(FileInputStream(file)))
        while(true) {
            val line = reader.readLine()?.split("\t") ?: break
            val text = line[0]
            val frequency = if(line.size >= 2) line[1].toFloat() else 0f
            val pos = if(line.size >= 3) line[2].toInt() else 0
            insert(Dictionary.Word(text, frequency, pos))
        }
    }

    override fun write() {
        val list = searchPrefix("", Integer.MAX_VALUE)
        if(list.isEmpty()) return
        val output = FileOutputStream(file)
        val data = list.map { "${it.text}\t${it.frequency}\t${it.pos}" }.joinToString("\n").toByteArray()
        output.write(data)
    }
}
