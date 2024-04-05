package com.anadolstudio.compose.ui.view.sheet

import androidx.compose.foundation.layout.width
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.anadolstudio.compose.ui.theme.AdelaideTheme
import com.anadolstudio.compose.ui.theme.textShimmer

@Composable
fun BottomSheetHandle(modifier: Modifier = Modifier) {
    Divider(
        modifier = modifier
            .width(48.dp)
            .clip(MaterialTheme.shapes.textShimmer),
        thickness = 4.dp,
        color = AdelaideTheme.colors.handleSheetColor,
    )
}
