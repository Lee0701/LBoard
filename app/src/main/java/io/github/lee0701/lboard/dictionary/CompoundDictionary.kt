package io.github.lee0701.lboard.dictionary

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

    override fun search(text: String): List<Dictionary.Word> {
        return dictionaries.flatMap { it.search(text) }
    }

    override fun searchPrefix(prefix: String, length: Int): List<Dictionary.Word> {
        return dictionaries.flatMap { it.searchPrefix(prefix, length) }
    }

    override fun searchSequence(seq: List<Int>, layout: Map<Int, List<Int>>): List<Dictionary.Word> {
        return dictionaries.flatMap { it.searchSequence(seq, layout) }
    }

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
