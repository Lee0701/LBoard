package io.github.lee0701.lboard.dictionary

class CompoundDictionary(val dictionaries: List<Dictionary>): EditableDictionary {

    override fun insert(word: Dictionary.Word) {
        dictionaries.forEach {
            if(it is EditableDictionary) it.insert(word)
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

}
