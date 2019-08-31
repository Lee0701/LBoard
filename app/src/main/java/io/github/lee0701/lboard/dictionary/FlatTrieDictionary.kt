package io.github.lee0701.lboard.dictionary

import java.nio.ByteBuffer

class FlatTrieDictionary(data: ByteArray): Dictionary {
    val buffer = ByteBuffer.wrap(data)
    val root: Int = buffer.getInt(data.size - 4)

    override fun search(text: String): List<Dictionary.Word> {
        return listWords(searchAddress(text) ?: return listOf(), text)
    }

    override fun searchPrefix(prefix: String, length: Int): List<Dictionary.Word> {
        return searchRecursive(searchAddress(prefix) ?: return listOf(), prefix, length)
    }

    override fun searchSequence(seq: List<Int>, layout: Map<Int, List<Int>>): List<Dictionary.Word> {
        return searchSequenceRecursive(seq, layout, root, "", 0)
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

    private fun searchRecursive(address: Int, current: String, length: Int): List<Dictionary.Word> {
        val result = listWords(address, current)
        if(current.length >= length) return result
        return result + listChildren(address).flatMap { searchRecursive(it.value, current + it.key, length) }
    }

    private fun searchSequenceRecursive(seq: List<Int>, layout: Map<Int, List<Int>>, address: Int, current: String, index: Int): List<Dictionary.Word> {
        if(index >= seq.size) return listWords(address, current)
        val keyCode = seq[index]
        val chars = layout[keyCode] ?: listOf()
        val children = listChildren(address)
        return children.filterKeys { chars.contains(it.toInt()) || layout.values.none { list -> list.contains(it.toInt()) } }
                .flatMap { searchSequenceRecursive(seq, layout, it.value, current + it.key, if(layout.values.none { list -> list.contains(it.key.toInt()) }) index else index + 1) }
    }

    private fun listWords(address: Int, word: String): List<Dictionary.Word> {
        val result = mutableListOf<Dictionary.Word>()
        var p = address

        val wordSize = buffer.get(p)
        p += 1
        for(i in 0 until wordSize) {
            val pos = buffer.get(p)
            p += 1
            val frequency = buffer.getFloat(p)
            p += 4
            result += Dictionary.Word(word, frequency, pos.toInt())
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
