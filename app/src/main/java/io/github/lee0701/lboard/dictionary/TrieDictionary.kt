package io.github.lee0701.lboard.dictionary

import kotlinx.coroutines.yield

open class TrieDictionary: Dictionary {

    val root = Node()

    override fun search(text: String): Iterable<Dictionary.Word> = sequence {
        var p = root
        text.forEach { c ->
            p = p.children[c] ?: return@sequence
        }
        p.words.forEach { yield(it) }
    }.asIterable()

    override fun searchPrefix(prefix: String, length: Int): Iterable<Dictionary.Word> {
        return searchRecursive(searchNode(prefix) ?: return listOf(), prefix, length)
    }

    override fun searchSequence(seq: List<Int>, layout: Map<Int, List<Int>>): Iterable<Dictionary.Word> {
        return searchSequenceRecursive(seq, layout, root, "", 0)
    }

    private fun searchNode(text: String): Node? {
        var p = root
        text.forEach { c ->
            p = p.children[c] ?: return null
        }
        return p
    }

    private fun searchRecursive(node: Node, current: String, length: Int): Iterable<Dictionary.Word> = sequence {
        val result = node.words
        result.forEach { yield(it) }
        if(current.length >= length) return@sequence
        node.children.forEach { searchRecursive(it.value, current + it.key, length).forEach { yield(it) } }
    }.asIterable()

    private fun searchSequenceRecursive(seq: List<Int>, layout: Map<Int, List<Int>>, node: Node, current: String, index: Int): Iterable<Dictionary.Word> = sequence {
        if(index >= seq.size) {
            node.words.forEach { yield(it) }
            return@sequence
        }
        val keyCode = seq[index]
        val chars = layout[keyCode] ?: listOf()
        val children = node.children
        children.filterKeys { chars.contains(it.toInt()) || layout.values.none { list -> list.contains(it.toInt()) } }
                .forEach { searchSequenceRecursive(seq, layout, it.value, current + it.key,
                        if(layout.values.none { list -> list.contains(it.key.toInt()) }) index else index + 1).forEach { yield(it) } }
    }.asIterable()

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
