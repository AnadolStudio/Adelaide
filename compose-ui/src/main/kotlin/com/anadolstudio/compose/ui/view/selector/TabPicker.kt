package com.anadolstudio.compose.ui.view.selector

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.TabPosition
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.anadolstudio.compose.ui.theme.AdelaideTheme
import com.anadolstudio.compose.ui.theme.AdelaideTypography
import com.anadolstudio.compose.ui.theme.LicardTheme
import com.anadolstudio.compose.ui.theme.preview.ThemePreviewParameter
import com.anadolstudio.compose.ui.view.text.Text

@Composable
fun TabPicker(
    tabs: List<String>,
    selectedIndex: Int,
    onTabSelect: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val indicator = @Composable { tabPositions: List<TabPosition> ->
        TabIndicator(
            Modifier.tabIndicatorOffset(tabPositions[selectedIndex]),
        )
    }

    TabRow(
        selectedTabIndex = selectedIndex,
        indicator = indicator,
        backgroundColor = Color.Transparent,
        modifier = modifier
    ) {
        tabs.forEachIndexed { index, tab ->
            TabItem(
                onClick = {
                    onTabSelect(index)
                },
                title = tab,
            )
        }
    }
}

@Composable
fun TabIndicator(
    modifier: Modifier = Modifier,
) {
    Spacer(
        modifier
            .padding(horizontal = 10.dp)
            .height(1.dp)
            .background(AdelaideTheme.colors.buttonPrimary)
    )
}

@Composable
@Suppress("UnnecessaryEventHandlerParameter")
private fun TabItem(
    title: String,
    onClick: (String) -> Unit
) {
    Column(
        Modifier
            .padding(10.dp)
            .wrapContentHeight()
            .wrapContentWidth()
            .clickable { onClick(title) },
    ) {
        Text(
            text = title,
            style = AdelaideTypography.captionBook16,
        )
    }
}

@Preview()
@Composable
private fun TabPickerPreview(@PreviewParameter(ThemePreviewParameter::class) useDarkMode: Boolean) {
    LicardTheme(useDarkMode) {
        Column(modifier = Modifier.background(AdelaideTheme.colors.backgroundPrimary)) {
            TabPicker(
                tabs = listOf("First", "Second", "Any"),
                selectedIndex = 2,
                onTabSelect = {},
            )
        }
    }
}
