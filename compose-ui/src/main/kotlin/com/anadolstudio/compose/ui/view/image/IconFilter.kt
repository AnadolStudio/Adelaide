package com.anadolstudio.compose.ui.view.image

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.anadolstudio.compose.ui.animation.IconAnimatedVisibility
import com.anadolstudio.compose.ui.drawable.LicardIcon
import com.anadolstudio.compose.ui.theme.AdelaideTheme

@Composable
fun IconFilter(
    isActive: Boolean,
    onClick: () -> Unit,
) {
    Box {
        IconButton(
            onClick = onClick,
            content = { Icon(LicardIcon.Filter, contentDescription = null) }
        )

        Box(
            modifier = Modifier
                .padding(top = 12.dp, end = 12.dp)
                .align(Alignment.TopEnd),
        ) {
            IconAnimatedVisibility(isActive) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(AdelaideTheme.colors.activeFilterColor, CircleShape),
                )
            }
        }
    }
}
