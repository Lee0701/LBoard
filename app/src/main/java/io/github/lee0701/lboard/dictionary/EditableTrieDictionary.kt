package io.github.lee0701.lboard.dictionary

open class EditableTrieDictionary: TrieDictionary(), EditableDictionary {

    override fun insert(word: Dictionary.Word) {
        var p = root
        word.text.forEach { c ->
            val n = p.children[c] ?: Node()
            p.addChild(c, n)
            p = n
        }
        p.addWord(word)
    }

}
