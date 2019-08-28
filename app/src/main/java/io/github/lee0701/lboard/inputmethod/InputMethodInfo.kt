package io.github.lee0701.lboard.inputmethod

data class InputMethodInfo(
        val language: String? = null,
        val device: Device? = null,
        val type: Type? = null,
        val direct: Boolean? = null
) {
    enum class Device {
        PHYSICAL, VIRTUAL
    }
    enum class Type {
        MAIN, SYMBOLS
    }

    fun match(from: List<InputMethodInfo>): List<InputMethodInfo> {
        val matchNotNull: (Any?, Any?) -> Boolean = { a, b -> a == null || b == null || a == b }
        var filtered = from.filter { it.language == this.language && it.direct == this.direct && it.device == this.device && it.type == this.type }
        if(filtered.isNotEmpty()) return filtered
        filtered = from.filter { matchNotNull(it.language, this.language) && matchNotNull(it.direct, this.direct) && matchNotNull(it.device, this.device) && matchNotNull(it.type, this.type) }
        return filtered
    }

    fun match(from: InputMethodInfo): Boolean {
        return this.match(listOf(from)).isNotEmpty()
    }

    companion object {
        fun match(from: List<InputMethodInfo>, to: InputMethodInfo): List<InputMethodInfo> {
            return to.match(from)
        }
    }

}
