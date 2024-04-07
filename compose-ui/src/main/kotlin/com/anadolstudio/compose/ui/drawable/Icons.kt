package com.anadolstudio.compose.ui.drawable

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import com.anadolstudio.compose.ui.R.drawable.icon_back
import com.anadolstudio.compose.ui.R.drawable.icon_check
import com.anadolstudio.compose.ui.R.drawable.icon_check_mark
import com.anadolstudio.compose.ui.R.drawable.icon_checkbox_disabled
import com.anadolstudio.compose.ui.R.drawable.icon_close
import com.anadolstudio.compose.ui.R.drawable.icon_download
import com.anadolstudio.compose.ui.R.drawable.icon_edit
import com.anadolstudio.compose.ui.R.drawable.icon_filter
import com.anadolstudio.compose.ui.R.drawable.icon_search
import com.anadolstudio.compose.ui.R.drawable.icon_square_checkbox_disabled
import com.anadolstudio.compose.ui.R.drawable.icon_square_checkbox_enabled
import com.anadolstudio.compose.ui.R.drawable.icon_vertical_more
import com.anadolstudio.compose.ui.R.drawable.icon_warning

/**
 * Licard icons, sorted alphabetically.
 *
 * Icons should be named in PascalCase.
 */
object Icons {
    val Back: Painter @Composable get() = painterResource(icon_back)
    val Check: Painter @Composable get() = painterResource(icon_check)
    val CheckBoxDisabled: Painter @Composable get() = painterResource(icon_checkbox_disabled)
    val CheckMark: Painter @Composable get() = painterResource(icon_check_mark)
    val Close: Painter @Composable get() = painterResource(icon_close)
    val Download: Painter @Composable get() = painterResource(icon_download)
    val Edit: Painter @Composable get() = painterResource(icon_edit)
    val Filter: Painter @Composable get() = painterResource(icon_filter)
    val VerticalMore: Painter @Composable get() = painterResource(icon_vertical_more)
    val Search: Painter @Composable get() = painterResource(icon_search)
    val SquareCheckboxDisabled: Painter @Composable get() = painterResource(icon_square_checkbox_disabled)
    val SquareCheckboxEnabled: Painter @Composable get() = painterResource(icon_square_checkbox_enabled)
    val Warning: Painter @Composable get() = painterResource(icon_warning)
}
