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

    companion object {
        fun match(from: List<InputMethodInfo>, to: InputMethodInfo): List<InputMethodInfo> {
            val matchNotNull: (Any?, Any?) -> Boolean = { a, b -> a == null || b == null || a == b }
            var filtered = from.filter { it.language == to.language && it.direct == to.direct && it.device == to.device && it.type == to.type }
            if(filtered.isNotEmpty()) return filtered
            filtered = from.filter { matchNotNull(it.language, to.language) && matchNotNull(it.direct, to.direct) && matchNotNull(it.device, to.device) && matchNotNull(it.type, to.type) }
            return filtered
        }
    }

}
