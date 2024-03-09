package com.anadolstudio.compose.ui.view.stub

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.anadolstudio.compose.ui.drawable.LicardIllustration
import com.anadolstudio.compose.ui.theme.AdelaideTheme
import com.anadolstudio.compose.ui.theme.AdelaideTypography
import com.anadolstudio.compose.ui.theme.LicardDimension
import com.anadolstudio.compose.ui.theme.LicardTheme
import com.anadolstudio.compose.ui.theme.preview.ThemePreviewParameter
import com.anadolstudio.compose.ui.view.VSpacer
import com.anadolstudio.compose.ui.view.button.OutlineButtonLarge
import com.anadolstudio.compose.ui.view.button.PrimaryButtonLarge
import com.anadolstudio.compose.ui.view.state.Loader
import com.anadolstudio.compose.ui.view.text.Text

@Composable
fun PagingItemErrorStub(
    message: String,
    buttonTitle: String,
    onButtonClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = message,
            style = AdelaideTypography.captionBook16,
            color = AdelaideTheme.colors.textSecondary,
        )
        Text(
            text = buttonTitle,
            style = AdelaideTypography.captionBook16,
            color = AdelaideTheme.colors.buttonPrimary,
            modifier = Modifier
                .padding(top = 4.dp)
                .clickable(onClick = onButtonClick),
        )
    }
}

@Composable
fun PagingItemLoadingStub(
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxWidth()) {
        Loader(
            modifier = Modifier
                .padding(vertical = 24.dp)
                .align(Alignment.Center)
        )
    }
}

@Composable
fun EmptyStub(
    title: String,
    message: String,
    modifier: Modifier = Modifier,
    image: Painter = LicardIllustration.Empty,
    buttonTitle: String? = null,
    onButtonClick: () -> Unit = {},
    fillMaxSize: Boolean = true,
) = BaseStub(
    modifier = modifier,
    title = title,
    message = message,
    image = image,
    buttonTitle = buttonTitle,
    onButtonClick = onButtonClick,
    fillMaxSize = fillMaxSize,
)

@Composable
fun ErrorStub(
    errorTitle: String,
    errorMessage: String,
    buttonTitle: String,
    onRefreshClick: () -> Unit,
    modifier: Modifier = Modifier,
    image: Painter = LicardIllustration.Error,
    fillMaxSize: Boolean = true,
) = BaseStub(
    modifier = modifier,
    title = errorTitle,
    message = errorMessage,
    image = image,
    buttonTitle = buttonTitle,
    onButtonClick = onRefreshClick,
    fillMaxSize = fillMaxSize,
)

@Composable
fun SuccessStub(
    title: String,
    image: Painter,
    buttonTitle: String,
    onButtonClick: () -> Unit,
    modifier: Modifier = Modifier,
    message: String? = null,
) {
    Column(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Image(
                painter = image,
                contentDescription = null
            )
            VSpacer(size = 24.dp)
            Text(
                text = title,
                style = AdelaideTypography.titleBook28,
            )
            if (message != null) {
                Text(
                    text = message,
                    style = AdelaideTypography.textBook18,
                    modifier = Modifier.padding(top = 12.dp),
                    textAlign = TextAlign.Center,
                )
            }
        }
        PrimaryButtonLarge(
            text = buttonTitle,
            onClick = onButtonClick,
            modifier = Modifier.padding(LicardDimension.layoutMainMargin),
        )
    }
}

@Composable
fun BaseStub(
    title: String,
    message: String,
    image: Painter,
    buttonTitle: String?,
    onButtonClick: () -> Unit,
    modifier: Modifier = Modifier,
    fillMaxSize: Boolean = true,
) {
    Column(modifier = modifier) {
        val innerModifier = Modifier
            .padding(LicardDimension.layoutMainMargin)
            .align(Alignment.CenterHorizontally)
            .run {
                if (fillMaxSize) {
                    this
                        .fillMaxSize()
                        .weight(1f)
                } else {
                    this
                }
            }
        Column(
            modifier = innerModifier,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Image(
                painter = image,
                contentDescription = null
            )
            Text(
                text = title,
                style = AdelaideTypography.titleBook28,
                modifier = Modifier.padding(top = 24.dp),
                textAlign = TextAlign.Center,
            )
            Text(
                text = message,
                style = AdelaideTypography.textBook18,
                modifier = Modifier.padding(top = 12.dp),
                textAlign = TextAlign.Center,
            )
        }
        if (!buttonTitle.isNullOrBlank()) {
            OutlineButtonLarge(
                text = buttonTitle,
                onClick = onButtonClick,
                modifier = Modifier.padding(LicardDimension.layoutMainMargin),
            )
        }
    }
}

@Preview
@Composable
private fun PreviewPagingStubs(@PreviewParameter(ThemePreviewParameter::class) useDarkMode: Boolean) {
    LicardTheme(useDarkMode) {
        Column {
            PagingItemLoadingStub(
                modifier = Modifier.background(AdelaideTheme.colors.backgroundSecondary),
            )

            PagingItemErrorStub(
                modifier = Modifier.background(AdelaideTheme.colors.backgroundSecondary),
                message = "Не удалось загрузить список элементов",
                buttonTitle = "Повторить",
                onButtonClick = {}
            )
        }
    }
}

@Preview
@Composable
private fun PreviewEmptyStub(@PreviewParameter(ThemePreviewParameter::class) useDarkMode: Boolean) {
    LicardTheme(useDarkMode) {
        EmptyStub(
            modifier = Modifier.background(AdelaideTheme.colors.backgroundSecondary),
            title = "Нет топливных карт",
            message = "Здесь будут отображаться ваши\nтопливные карты",
            image = LicardIllustration.EmptyTransaction
        )
    }
}

@Preview
@Composable
private fun PreviewEmptyStubWithButton(@PreviewParameter(ThemePreviewParameter::class) useDarkMode: Boolean) {
    LicardTheme(useDarkMode) {
        EmptyStub(
            modifier = Modifier.background(AdelaideTheme.colors.backgroundSecondary),
            title = "Нет топливных карт",
            message = "Здесь будут отображаться ваши\nтопливные карты",
        )
    }
}

@Preview
@Composable
private fun PreviewErrorStub(@PreviewParameter(ThemePreviewParameter::class) useDarkMode: Boolean) {
    LicardTheme(useDarkMode) {
        ErrorStub(
            modifier = Modifier.background(AdelaideTheme.colors.backgroundSecondary),
            errorTitle = "Что-то пошло не так",
            errorMessage = "Попробуйте обновить или зайти чуть позже.",
            buttonTitle = "Обновить",
            onRefreshClick = {},
        )
    }
}

@Preview
@Composable
private fun PreviewSuccessStub(@PreviewParameter(ThemePreviewParameter::class) useDarkMode: Boolean) {
    LicardTheme(useDarkMode) {
        SuccessStub(
            modifier = Modifier.background(AdelaideTheme.colors.backgroundSecondary),
            image = LicardIllustration.EmptyTransaction,
            title = "Аккаунт успешно удален",
            buttonTitle = "Хорошо",
            onButtonClick = {},
        )
    }
}
