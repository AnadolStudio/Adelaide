package com.anadolstudio.adelaide.navigation

data class NavigateData(
        val id: Int,
        val args: Map<String, Any> = emptyMap()
)
