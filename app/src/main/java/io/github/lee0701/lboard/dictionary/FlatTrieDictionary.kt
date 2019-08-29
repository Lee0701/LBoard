package io.github.lee0701.lboard.dictionary

import io.github.lee0701.lboard.prediction.Candidate
import java.nio.ByteBuffer

class FlatTrieDictionary(data: ByteArray): Dictionary {
    val buffer = ByteBuffer.wrap(data)
    val root: Int = buffer.getInt(data.size - 4)

    override fun search(word: String): List<Candidate> {
        var p = root
        word.forEach { c ->
            val wordSize = buffer.get(p)
            p += 1 + wordSize * (1 + 4)
            val childrenSize = buffer.get(p)
            p += 1
            for(i in 0 until childrenSize) {
                val key = buffer.getShort(p)
                p += 2
                val address = buffer.getInt(p)
                p += 4
                if(key.toChar() == c) {
                    p = address
                    return@forEach
                }
            }
            return listOf()
        }

        val result = mutableListOf<Candidate>()

        val wordSize = buffer.get(p)
        p += 1
        for(i in 0 until wordSize) {
            val pos = buffer.get(p)
            p += 1
            val frequency = buffer.getFloat(p)
            p += 4
            result += Candidate(0, word, pos.toString(), frequency)
        }
        return result
    }

    override fun searchPrefix(prefix: String): List<Candidate> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun searchSequence(seq: List<Int>, layout: Map<Int, List<Int>>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
