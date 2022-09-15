package com.anadolstudio.adelaide.view.screens.edit

import android.graphics.Color
import com.anadolstudio.data.colors.Colors

data class Settings(
        var isBrush: Boolean = true,
        var size: Float = NORMAL,
        var color: Int = Color.parseColor(com.anadolstudio.data.colors.Colors.COLOR_DEFAULT)
) {
    companion object {
        internal const val XSMALL = 5.0f
        internal const val XLARGE = 100.0f
        internal const val NORMAL = (XLARGE - XSMALL) / 2
    }
}
