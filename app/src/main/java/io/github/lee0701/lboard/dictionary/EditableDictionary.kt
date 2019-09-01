package io.github.lee0701.lboard.dictionary

interface EditableDictionary: Dictionary {
    fun insert(word: Dictionary.Word)
    fun remove(word: Dictionary.Word)
}
