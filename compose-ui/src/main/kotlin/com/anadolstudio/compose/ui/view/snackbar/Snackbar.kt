package com.anadolstudio.compose.ui.view.snackbar

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProvideTextStyle
import androidx.compose.material.SnackbarDefaults
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.SnackbarResult
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.AlignmentLine
import androidx.compose.ui.layout.FirstBaseline
import androidx.compose.ui.layout.LastBaseline
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.anadolstudio.compose.ui.theme.AdelaideTypography
import com.anadolstudio.compose.ui.theme.LicardDimension
import com.anadolstudio.compose.ui.theme.LicardTheme
import com.anadolstudio.compose.ui.theme.preview.ThemePreviewParameter
import com.anadolstudio.compose.ui.view.HSpacer
import com.anadolstudio.compose.ui.view.VSpacer
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.CompletionHandler
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlin.coroutines.CoroutineContext
import kotlin.math.max

@Composable
internal fun Snackbar(
    snackbarData: StyledSnackbarData,
    modifier: Modifier = Modifier,
    elevation: Dp = 6.dp,
) {
    Snackbar(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = LicardDimension.layoutHorizontalMargin),
        content = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                snackbarData.style.icon?.let { icon ->
                    Icon(
                        painter = icon,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    HSpacer(12.dp)
                }
                Text(
                    text = snackbarData.message,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = AdelaideTypography.captionBook14,
                )
            }
        },
        shape = snackbarData.style.shape,
        backgroundColor = snackbarData.style.backgroundColor,
        contentColor = snackbarData.style.contentColor,
        elevation = elevation
    )
}

@Composable
private fun Snackbar(
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.small,
    backgroundColor: Color = SnackbarDefaults.backgroundColor,
    contentColor: Color = Color.White,
    elevation: Dp = 6.dp,
    content: @Composable BoxScope.() -> Unit,
) {
    Surface(
        modifier = modifier,
        shape = shape,
        elevation = elevation,
        color = backgroundColor,
        contentColor = contentColor
    ) {
        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.high) {
            val textStyle = MaterialTheme.typography.body2
            ProvideTextStyle(value = textStyle) {
                TextOnlySnackbar(content)
            }
        }
    }
}

/**
 * Slightly changed version of original Compose Snackbar's [androidx.compose.material.TextOnlySnackbar].
 * Text placed in the center of the view instead of start.
 */
@Composable
private fun TextOnlySnackbar(content: @Composable BoxScope.() -> Unit) {
    Layout({
        Box(
            modifier = Modifier.padding(
                horizontal = HorizontalSpacing,
                vertical = SnackbarVerticalPadding
            ),
            content = content,
        )
    }) { measurables, constraints ->
        require(measurables.size == 1) {
            "text for Snackbar expected to have exactly only one child"
        }
        val textPlaceable = measurables.first().measure(constraints)
        val firstBaseline = textPlaceable[FirstBaseline]
        val lastBaseline = textPlaceable[LastBaseline]
        require(firstBaseline != AlignmentLine.Unspecified) { "No baselines for text" }
        require(lastBaseline != AlignmentLine.Unspecified) { "No baselines for text" }

        val singleLine = firstBaseline == lastBaseline
        val minHeight = if (singleLine) SnackbarMinHeightOneLine else SnackbarMinHeightTwoLines
        val minWidth = if (singleLine) SnackbarMinWidth else constraints.maxWidth.dp

        val containerHeight = max(minHeight.roundToPx(), textPlaceable.height)
        val containerWidth = max(minWidth.roundToPx(), textPlaceable.width)

        layout(containerWidth, containerHeight) {
            val textPlaceY = (containerHeight - textPlaceable.height) / 2
            val textPlaceX = (containerWidth - textPlaceable.width) / 2
            textPlaceable.placeRelative(textPlaceX, textPlaceY)
        }
    }
}

private val HorizontalSpacing = 12.dp
private val SnackbarVerticalPadding = 10.dp
private val SnackbarMinHeightOneLine = 44.dp
private val SnackbarMinHeightTwoLines = 68.dp
private val SnackbarMinWidth = 64.dp

@Preview
@Composable
@Suppress("StringLiteralDuplication")
private fun Preview(@PreviewParameter(ThemePreviewParameter::class) useDarkMode: Boolean) {
    LicardTheme(useDarkMode) {
        val continuation = object : CancellableContinuation<SnackbarResult> {
            override val context: CoroutineContext
                get() = TODO("Not yet implemented")
            override val isActive: Boolean
                get() = TODO("Not yet implemented")
            override val isCancelled: Boolean
                get() = TODO("Not yet implemented")
            override val isCompleted: Boolean
                get() = TODO("Not yet implemented")

            override fun cancel(cause: Throwable?): Boolean {
                TODO("Not yet implemented")
            }

            @InternalCoroutinesApi
            override fun completeResume(token: Any) {
                TODO("Not yet implemented")
            }

            @InternalCoroutinesApi
            override fun initCancellability() {
                TODO("Not yet implemented")
            }

            override fun invokeOnCancellation(handler: CompletionHandler) {
                TODO("Not yet implemented")
            }

            @InternalCoroutinesApi
            override fun tryResumeWithException(exception: Throwable): Any? {
                TODO("Not yet implemented")
            }

            @ExperimentalCoroutinesApi
            override fun CoroutineDispatcher.resumeUndispatchedWithException(exception: Throwable) {
                TODO("Not yet implemented")
            }

            @ExperimentalCoroutinesApi
            override fun CoroutineDispatcher.resumeUndispatched(value: SnackbarResult) {
                TODO("Not yet implemented")
            }

            @InternalCoroutinesApi
            override fun tryResume(
                value: SnackbarResult,
                idempotent: Any?,
                onCancellation: ((cause: Throwable) -> Unit)?
            ): Any? {
                TODO("Not yet implemented")
            }

            @InternalCoroutinesApi
            override fun tryResume(value: SnackbarResult, idempotent: Any?): Any? {
                TODO("Not yet implemented")
            }

            @ExperimentalCoroutinesApi
            override fun resume(value: SnackbarResult, onCancellation: ((cause: Throwable) -> Unit)?) {
                TODO("Not yet implemented")
            }

            override fun resumeWith(result: Result<SnackbarResult>) {
                TODO("Not yet implemented")
            }
        }
        val defaultData = StyledSnackbarData(
            message = "Ссылка для смены пароля отправлена на почту",
            actionLabel = null,
            duration = SnackbarDuration.Short,
            style = SnackbarStyle.Default,
            continuation = continuation

        )
        val errorData = StyledSnackbarData(
            message = "Что-то пошло не так!",
            actionLabel = null,
            duration = SnackbarDuration.Short,
            style = SnackbarStyle.Error,
            continuation = continuation

        )
        Column {
            Snackbar(snackbarData = defaultData)
            VSpacer(8.dp)
            Snackbar(snackbarData = errorData)
        }
    }
}
