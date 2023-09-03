package com.anadolstudio.adelaide.navigation

import android.os.Bundle
import androidx.core.os.bundleOf

data class NavigateData(
        val id: Int,
        val args: Bundle = bundleOf()
)
