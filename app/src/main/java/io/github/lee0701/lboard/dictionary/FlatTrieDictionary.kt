package io.github.lee0701.lboard.dictionary

import io.github.lee0701.lboard.prediction.Candidate
import java.nio.ByteBuffer

class FlatTrieDictionary(data: ByteArray): Dictionary {
    val buffer = ByteBuffer.wrap(data)
    val root: Int = buffer.getInt(data.size - 4)

    override fun search(word: String): List<Candidate> {
        return listCandidates(searchAddress(word) ?: return listOf(), word)
    }

    override fun searchPrefix(prefix: String, length: Int): List<Candidate> {
        return searchRecursive(searchAddress(prefix) ?: return listOf(), prefix, length)
    }

    override fun searchSequence(seq: List<Int>, layout: Map<Int, List<Int>>): List<Candidate> {
        return searchSequenceRecursive(seq, layout, root, "")
    }

    private fun searchAddress(word: String): Int? {
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
            return null
        }
        return p
    }

    private fun searchRecursive(address: Int, current: String, length: Int): List<Candidate> {
        val result = listCandidates(address, current)
        if(current.length >= length) return result
        return result + listChildren(address).flatMap { searchRecursive(it.value, current + it.key, length) }
    }

    private fun searchSequenceRecursive(seq: List<Int>, layout: Map<Int, List<Int>>, address: Int, current: String): List<Candidate> {
        if(current.length >= seq.size) return listCandidates(address, current)
        val keyCode = seq[current.length]
        val chars = layout[keyCode] ?: return listOf()
        return listChildren(address).filterKeys { chars.contains(it.toInt()) }
                .flatMap { searchSequenceRecursive(seq, layout, it.value, current + it.key) }
    }

    private fun listCandidates(address: Int, word: String): List<Candidate> {
        val result = mutableListOf<Candidate>()
        var p = address

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

    private fun listChildren(address: Int): Map<Char, Int> {
        var p = address
        val result = mutableMapOf<Char, Int>()

        val wordSize = buffer.get(p)
        p += 1 + wordSize * (1 + 4)
        val childrenSize = buffer.get(p)
        p += 1
        for(i in 0 until childrenSize) {
            val key = buffer.getShort(p)
            p += 2
            val childAddress = buffer.getInt(p)
            p += 4
            result += key.toChar() to childAddress
        }
        return result
    }

}
