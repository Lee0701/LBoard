package io.github.lee0701.lboard.dictionary

class WeightedDictionary(val dictionary: Dictionary, val weight: Float): Dictionary {

    override fun search(text: String): List<Dictionary.Word> {
        return dictionary.search(text).map { it.copy(frequency = it.frequency * weight) }
    }

    override fun searchPrefix(prefix: String, length: Int): List<Dictionary.Word> {
        return dictionary.searchPrefix(prefix, length).map { it.copy(frequency = it.frequency * weight) }
    }

    override fun searchSequence(seq: List<Int>, layout: Map<Int, List<Int>>): List<Dictionary.Word> {
        return dictionary.searchSequence(seq, layout).map { it.copy(frequency = it.frequency * weight) }
    }

}
