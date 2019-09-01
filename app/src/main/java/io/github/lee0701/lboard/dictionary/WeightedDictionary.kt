package io.github.lee0701.lboard.dictionary

class WeightedDictionary(val dictionary: Dictionary, val weight: Float): Dictionary, EditableDictionary, WritableDictionary {

    override fun search(text: String): List<Dictionary.Word> {
        return dictionary.search(text).map { it.copy(frequency = it.frequency * weight) }
    }

    override fun searchPrefix(prefix: String, length: Int): List<Dictionary.Word> {
        return dictionary.searchPrefix(prefix, length).map { it.copy(frequency = it.frequency * weight) }
    }

    override fun searchSequence(seq: List<Int>, layout: Map<Int, List<Int>>): List<Dictionary.Word> {
        return dictionary.searchSequence(seq, layout).map { it.copy(frequency = it.frequency * weight) }
    }

    override fun insert(word: Dictionary.Word) {
        if(dictionary is EditableDictionary) dictionary.insert(word)
    }

    override fun remove(word: Dictionary.Word) {
        if(dictionary is EditableDictionary) dictionary.remove(word)
    }

    override fun read() {
        if(dictionary is WritableDictionary) dictionary.read()
    }

    override fun write() {
        if(dictionary is WritableDictionary) dictionary.write()
    }
}
