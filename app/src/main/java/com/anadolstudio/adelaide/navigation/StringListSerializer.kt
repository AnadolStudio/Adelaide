package com.anadolstudio.adelaide.navigation

internal object StringListSerializer {
    private const val separator = ","

    fun List<String>.encodeToString(): String {
        return this.joinToString(separator)
    }

    fun String.decodeToStringList(): List<String> {
        return this.split(separator)
    }
}
