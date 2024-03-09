package com.anadolstudio.compose.ui.view.snackbar

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import com.anadolstudio.compose.ui.drawable.LicardIcon
import com.anadolstudio.compose.ui.theme.AdelaideTheme
import kotlinx.coroutines.CancellableContinuation
import kotlin.coroutines.resume
import androidx.compose.material.SnackbarHost as MaterialSnackbarHost
import androidx.compose.material.SnackbarHostState as MaterialSnackbarHostState

interface SnackbarStyle {

    val icon: Painter?
        @Composable get() = null
    val shape: Shape
        @Composable get() = RoundedCornerShape(0)
    val backgroundColor: Color
        @Composable get() = SnackbarDefaults.backgroundColor
    val contentColor: Color
        @Composable get() = Color.White

    object Default : SnackbarStyle {
        override val icon: Painter
            @Composable get() = LicardIcon.Check
        override val backgroundColor: Color
            @Composable get() = AdelaideTheme.colors.success
        override val contentColor: Color
            @Composable get() = AdelaideTheme.colors.textTertiary
    }

    object Error : SnackbarStyle {
        override val icon: Painter
            @Composable get() = LicardIcon.Warning
        override val backgroundColor: Color
            @Composable get() = AdelaideTheme.colors.error
        override val contentColor: Color
            @Composable get() = AdelaideTheme.colors.textTertiary
    }

    object AddToFavorites : SnackbarStyle {
        override val icon: Painter
            @Composable get() = LicardIcon.Heart
        override val backgroundColor: Color
            @Composable get() = AdelaideTheme.colors.success
        override val contentColor: Color
            @Composable get() = AdelaideTheme.colors.textTertiary
    }

    object DeleteFromFavorites : SnackbarStyle {
        override val icon: Painter
            @Composable get() = LicardIcon.Heart
        override val backgroundColor: Color
            @Composable get() = AdelaideTheme.colors.error
        override val contentColor: Color
            @Composable get() = AdelaideTheme.colors.textTertiary
    }
}

@Suppress("ForbiddenMethodCall")
@Composable
fun SnackbarHost(
    hostState: MaterialSnackbarHostState,
    modifier: Modifier = Modifier,
) {
    MaterialSnackbarHost(
        hostState = hostState,
        modifier = modifier,
        snackbar = { StyledSnackbar(it) },
    )
}

@Composable
private fun StyledSnackbar(snackbarData: SnackbarData) {
    if (snackbarData is StyledSnackbarData) {
        Snackbar(snackbarData = snackbarData)
    } else {
        Snackbar(snackbarData)
    }
}

@Stable
internal class StyledSnackbarData(
    override val message: String,
    override val actionLabel: String?,
    override val duration: SnackbarDuration,
    val style: SnackbarStyle,
    private val continuation: CancellableContinuation<SnackbarResult>,
) : SnackbarData {
    override fun dismiss() {
        if (continuation.isActive) continuation.resume(SnackbarResult.Dismissed)
    }

    override fun performAction() {
        if (continuation.isActive) continuation.resume(SnackbarResult.ActionPerformed)
    }
}
