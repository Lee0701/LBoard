package io.github.lee0701.lboard.dictionary

import kotlinx.coroutines.yield

open class TrieDictionary: Dictionary {

    var root = Node()

    override fun search(text: String): Sequence<Dictionary.Word> = sequence {
        var p = root
        text.forEach { c ->
            p = p.children[c] ?: return@sequence
        }
        p.words.forEach { yield(it) }
    }

    override fun searchPrefix(prefix: String, length: Int): Sequence<Dictionary.Word> {
        return searchRecursive(searchNode(prefix) ?: return sequenceOf(), prefix, length)
    }

    override fun searchSequence(seq: List<Int>, layout: Map<Int, List<Int>>): Sequence<Dictionary.Word> {
        return searchSequenceRecursive(seq, layout, seq.size, root, "", 0)
    }

    override fun searchSequencePrefix(seqPrefix: List<Int>, layout: Map<Int, List<Int>>, length: Int): Sequence<Dictionary.Word> {
        return searchSequenceRecursive(seqPrefix, layout, length, root, "", 0)
    }

    private fun searchNode(text: String): Node? {
        var p = root
        text.forEach { c ->
            p = p.children[c] ?: return null
        }
        return p
    }

    private fun searchRecursive(node: Node, current: String, length: Int): Sequence<Dictionary.Word> = sequence {
        val result = node.words
        result.forEach { yield(it) }
        if(current.length >= length) return@sequence
        node.children.forEach { searchRecursive(it.value, current + it.key, length).forEach { yield(it) } }
    }

    private fun searchSequenceRecursive(seq: List<Int>, layout: Map<Int, List<Int>>, length: Int, node: Node, current: String, index: Int): Sequence<Dictionary.Word> = sequence {
        if(index >= seq.size) {
            searchRecursive(node, current, length).forEach { yield(it) }
            return@sequence
        }
        val keyCode = seq[index]
        val chars = layout[keyCode] ?: listOf()
        val children = node.children
        children.filterKeys { chars.contains(it.code) || layout.values.none { list -> list.contains(it.code) } }
                .forEach { searchSequenceRecursive(seq, layout, length, it.value, current + it.key,
                        if(layout.values.none { list -> list.contains(it.key.code) }) index else index + 1).forEach { yield(it) } }
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
