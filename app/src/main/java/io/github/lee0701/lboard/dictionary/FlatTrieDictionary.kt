package io.github.lee0701.lboard.dictionary

import java.nio.ByteBuffer

open class FlatTrieDictionary(): Dictionary {
    var buffer: ByteBuffer = ByteBuffer.wrap(byteArrayOf(0, 0, 0, 0, 0, 0))
    var root: Int = 0

    constructor(data: ByteArray): this() {
        buffer = ByteBuffer.wrap(data)
        root = buffer.getInt(data.size - 4)
    }

    override fun search(text: String): Iterable<Dictionary.Word> {
        return listWords(searchAddress(text) ?: return listOf(), text)
    }

    override fun searchPrefix(prefix: String, length: Int): Iterable<Dictionary.Word> {
        return searchRecursive(searchAddress(prefix) ?: return listOf(), prefix, length)
    }

    override fun searchSequence(seq: List<Int>, layout: Map<Int, List<Int>>): Iterable<Dictionary.Word> {
        return searchSequenceRecursive(seq, layout, seq.size, root, "", 0)
    }

    override fun searchSequencePrefix(seqPrefix: List<Int>, layout: Map<Int, List<Int>>, length: Int): Iterable<Dictionary.Word> {
        return searchSequenceRecursive(seqPrefix, layout, length, root, "", 0)
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

    private fun searchRecursive(address: Int, current: String, length: Int): Iterable<Dictionary.Word> = sequence {
        val result = listWords(address, current)
        result.forEach { yield(it) }
        if(current.length >= length) return@sequence
        listChildren(address).forEach { searchRecursive(it.second, current + it.first, length).forEach { yield(it) } }
    }.asIterable()

    private fun searchSequenceRecursive(seq: List<Int>, layout: Map<Int, List<Int>>, length: Int, address: Int, current: String, index: Int): Iterable<Dictionary.Word> = sequence {
        if(index >= seq.size) {
            searchRecursive(address, current, length).forEach { yield(it) }
            return@sequence
        }
        val keyCode = seq[index]
        val chars = layout[keyCode] ?: listOf()
        val children = listChildren(address)
        children.filter { chars.contains(it.first.toInt()) || layout.values.none { list -> list.contains(it.first.toInt()) } }
                .forEach { searchSequenceRecursive(seq, layout, length, it.second, current + it.first,
                        if(layout.values.none { list -> list.contains(it.first.toInt()) }) index else index + 1).forEach { yield(it) } }
    }.asIterable()

    private fun listWords(address: Int, word: String): Iterable<Dictionary.Word> = sequence {
        var p = address
        val wordSize = buffer.get(p)
        p += 1
        for(i in 0 until wordSize) {
            val pos = buffer.get(p)
            p += 1
            val frequency = buffer.getFloat(p)
            p += 4
            yield(Dictionary.Word(word, frequency, pos.toInt()))
        }
    }.asIterable()

    private fun listChildren(address: Int): Iterable<Pair<Char, Int>> = sequence {
        var p = address
        val wordSize = buffer.get(p)
        p += 1 + wordSize * (1 + 4)
        val childrenSize = buffer.get(p)
        p += 1
        for(i in 0 until childrenSize) {
            val key = buffer.getShort(p)
            p += 2
            val childAddress = buffer.getInt(p)
            p += 4
            yield(key.toChar() to childAddress)
        }
    }.asIterable()

}
