package com.anadolstudio.adelaide.navigation.bottomnavigation

import androidx.compose.ui.graphics.painter.Painter
import com.anadolstudio.adelaide.event.Text

internal data class NavigationTab(
    val route: String,
    val icon: Painter,
    val name: Text,
)
