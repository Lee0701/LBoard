package io.github.lee0701.lboard.dictionary

class TrieDictionary: EditableDictionary {

    val root = Node()

    override fun insert(word: Dictionary.Word) {
        var p = root
        word.text.forEach { c ->
            val n = p.children[c] ?: Node()
            p.addChild(c, n)
            p = n
        }
        p.addWord(word)
    }

    override fun search(text: String): List<Dictionary.Word> {
        var p = root
        text.forEach { c ->
            p = p.children[c] ?: return listOf()
        }
        return p.words
    }

    override fun searchPrefix(prefix: String, length: Int): List<Dictionary.Word> {
        return searchRecursive(searchNode(prefix) ?: return listOf(), prefix, length)
    }

    override fun searchSequence(seq: List<Int>, layout: Map<Int, List<Int>>): List<Dictionary.Word> {
        return searchSequenceRecursive(seq, layout, root, "", 0)
    }

    private fun searchNode(text: String): Node? {
        var p = root
        text.forEach { c ->
            p = p.children[c] ?: return null
        }
        return p
    }

    private fun searchRecursive(node: Node, current: String, length: Int): List<Dictionary.Word> {
        val result = node.words
        if(current.length >= length) return result
        return result + node.children.flatMap { searchRecursive(it.value, current + it.key, length) }
    }

    private fun searchSequenceRecursive(seq: List<Int>, layout: Map<Int, List<Int>>, node: Node, current: String, index: Int): List<Dictionary.Word> {
        if(index >= seq.size) return node.words
        val keyCode = seq[index]
        val chars = layout[keyCode] ?: listOf()
        val children = node.children
        return children.filterKeys { chars.contains(it.toInt()) || layout.values.none { list -> list.contains(it.toInt()) } }
                .flatMap { searchSequenceRecursive(seq, layout, it.value, current + it.key, if(layout.values.none { list -> list.contains(it.key.toInt()) }) index else index + 1) }
    }

    data class Node(
            val words: MutableList<Dictionary.Word> = mutableListOf(),
            val children: MutableMap<Char, Node> = mutableMapOf()
    ) {
        fun addChild(key: Char, node: Node) {
            children[key] = node
        }
        fun addWord(word: Dictionary.Word) {
            words += word
        }
    }

}
