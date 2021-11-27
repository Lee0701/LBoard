package io.github.lee0701.lboard.dictionary

import java.io.DataOutputStream
import java.io.File
import java.io.FileOutputStream

class FlatteningWritableTrieDictionary(
        val file: File
): EditableTrieDictionary(), WritableDictionary {

    private fun serialize(node: Node): List<Node> {
        return node.children.flatMap { serialize(it.value) } + node
    }

    override fun read() {
        if(!file.exists()) file.createNewFile()
        this.root = Node()
        val flatDictionary = FlatTrieDictionary(file.readBytes())
        flatDictionary.searchPrefix("", Int.MAX_VALUE).forEach { insert(it) }
    }

    override fun write() {
        if(!file.exists()) file.createNewFile()
        val dos = DataOutputStream(FileOutputStream(file))

        val addressMap: MutableMap<Node, Int> = mutableMapOf()
        var address: Int = 0

        val list = serialize(root)

        list.forEach { node ->
            addressMap += node to address

            dos.writeByte(node.words.size)
            address += 1
            node.words.forEach { word ->
                dos.writeByte(word.pos)
                dos.writeFloat(word.frequency)
                address += 5
            }
            dos.writeByte(node.children.size)
            address += 1
            node.children.forEach { entry ->
                dos.writeShort(entry.key.code)
                dos.writeInt(addressMap[entry.value] ?: 0)
                address += 6
            }
        }
        dos.writeInt(addressMap[list.last()] ?: 0)
    }

}
