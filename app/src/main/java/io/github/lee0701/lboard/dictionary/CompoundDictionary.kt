package io.github.lee0701.lboard.dictionary

import kotlinx.coroutines.yield

class CompoundDictionary(val dictionaries: List<Dictionary>): EditableDictionary, WritableDictionary {

    override fun insert(word: Dictionary.Word) {
        dictionaries.forEach {
            if(it is EditableDictionary) it.insert(word)
        }
    }

    override fun remove(word: Dictionary.Word) {
        dictionaries.forEach {
            if(it is EditableDictionary) it.remove(word)
        }
    }

    override fun search(text: String): Iterable<Dictionary.Word> = sequence {
        dictionaries.forEach { it.search(text).forEach { yield(it) } }
    }.asIterable()

    override fun searchPrefix(prefix: String, length: Int): Iterable<Dictionary.Word> = sequence {
        dictionaries.forEach { it.searchPrefix(prefix, length).forEach { yield(it) } }
    }.asIterable()

    override fun searchSequence(seq: List<Int>, layout: Map<Int, List<Int>>): Iterable<Dictionary.Word> = sequence {
        dictionaries.forEach { it.searchSequence(seq, layout).forEach { yield(it) } }
    }.asIterable()

    override fun searchSequencePrefix(seqPrefix: List<Int>, layout: Map<Int, List<Int>>, length: Int): Iterable<Dictionary.Word> = sequence {
        dictionaries.forEach { it.searchSequencePrefix(seqPrefix, layout, length).forEach { yield(it) } }
    }.asIterable()

    override fun read() {
        dictionaries.forEach {
            if(it is WritableDictionary) it.read()
        }
    }

    override fun write() {
        dictionaries.forEach {
            if(it is WritableDictionary) it.write()
        }
    }
}
