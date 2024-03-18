package com.anadolstudio.adelaide.feature.gallery.presetnation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.anadolstudio.adelaide.R
import com.anadolstudio.adelaide.di.viewmodel.daggerViewModel
import com.anadolstudio.adelaide.feature.main.LicardNavigator
import com.anadolstudio.adelaide.util.isEmpty
import com.anadolstudio.adelaide.util.isError
import com.anadolstudio.adelaide.util.isErrorNextPage
import com.anadolstudio.adelaide.util.isLoading
import com.anadolstudio.adelaide.util.isLoadingNextPage
import com.anadolstudio.compose.ui.drawable.LicardIcon
import com.anadolstudio.compose.ui.theme.AdelaideTheme
import com.anadolstudio.compose.ui.theme.LicardDimension
import com.anadolstudio.compose.ui.theme.preview.ThemePreviewParameter
import com.anadolstudio.compose.ui.view.snackbar.SnackbarHostState
import com.anadolstudio.compose.ui.view.toolbar.NavigationIcon
import com.anadolstudio.compose.ui.view.toolbar.Toolbar
import com.anadolstudio.utils.data_source.media.Folder
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
internal fun GalleryScreen(
    navigator: LicardNavigator,
    snackbarHostState: SnackbarHostState,
    viewModel: GalleryViewModel = daggerViewModel()
) {
    val state by viewModel.stateLiveData.observeAsState()
    val images = viewModel.galleryFlow.collectAsLazyPagingItems()

    GalleryLayout(
        state = requireNotNull(state),
        controller = viewModel,
        images = images,
    )
}

@Composable
private fun GalleryLayout(
    state: GalleryState,
    controller: GalleryController,
    images: LazyPagingItems<String>,
) {
    BackHandler { controller.onBackClicked() }

    Box {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(AdelaideTheme.colors.backgroundPrimary)
                .systemBarsPadding()
        ) {
            GalleryToolbar(
                controller = controller,
                state = state,
                images = images
            )
            ImageList(
                images = images,
                controller = controller,
                state = state
            )
        }
    }
}

@Composable
private fun ImageList(
    images: LazyPagingItems<String>,
    controller: GalleryController,
    state: GalleryState,
) {
    LazyVerticalGrid(
        modifier = Modifier
            .background(AdelaideTheme.colors.backgroundPrimary),
        columns = GridCells.Fixed(state.columnSpan)
    ) {
        for (index in 0 until images.itemCount) {
            images[index]?.let { path ->
                item {
                    CardItem(
                        imagePath = path,
                        onItemClick = controller::onImageSelected,
                        isSelected = false,
                        isSelectableMode = false
                    )
                }
            }
        }

        item {

            when {
                images.isLoadingNextPage() -> {
                }
                images.isErrorNextPage() -> {
                }
                images.isLoading() -> {
                }
                images.isEmpty() -> {
                }
                images.isError() -> {
                }
            }
        }
    }
}

@Composable
internal fun CardItem(
    imagePath: String,
    onItemClick: (imagePath: String) -> Unit,
    isSelected: Boolean,
    isSelectableMode: Boolean,
    onActionClick: (() -> Unit)? = null,
    onItemLongClick: (imagePath: String) -> Unit = {},
    enableClick: Boolean = true,
) {
    Row(
        modifier = Modifier
            .combinedClickable(
                onClick = { onItemClick.invoke(imagePath) },
                onLongClick = { onItemLongClick.invoke(imagePath) },
                enabled = enableClick
            )
            .padding(
                start = LicardDimension.layoutMainMargin,
                top = LicardDimension.layoutMainMargin,
                bottom = LicardDimension.layoutMainMargin,
            )
    ) {
        AnimatedVisibility(
            visible = isSelectableMode,
            modifier = Modifier.align(Alignment.CenterVertically)
        ) {
            val icon = if (isSelected) LicardIcon.SquareCheckboxEnabled else LicardIcon.SquareCheckboxDisabled

            Image(
                painter = icon,
                contentDescription = null,
                modifier = Modifier.padding(end = LicardDimension.layoutMainMargin)
            )
        }
    }
}

@Composable
private fun GalleryToolbar(
    controller: GalleryController,
    state: GalleryState,
    images: LazyPagingItems<String>,
) {
    Toolbar(
        navigationIcon = NavigationIcon.Back,
        title = stringResource(id = R.string.gallery_toolbar_title),
        onNavigationClick = controller::onBackClicked,
    )
}

@Preview
@Composable
private fun PreviewFuelCards(@PreviewParameter(ThemePreviewParameter::class) useDarkMode: Boolean) {
    val state = remember { GalleryState(columnSpan = 3) }
    val exampleList = listOf("Set", "Set1", "Set2")
    val images = MutableStateFlow( PagingData.from(exampleList)).collectAsLazyPagingItems()

    val controller = object : GalleryController {
        override fun onBackClicked()  = Unit
        override fun onPermissionGranted()  = Unit
        override fun onFolderChanged(folder: Folder)  = Unit
        override fun onImageSelected(imageUri: String)  = Unit
        override fun onNavigateToSettingsClicked()  = Unit
        override fun onLoadMoreImages()  = Unit
        override fun onZoomIncreased()  = Unit
        override fun onZoomDecreased()  = Unit
        override fun onFolderClosed()  = Unit
        override fun onFolderOpened()  = Unit
    }

    AdelaideTheme(useDarkMode) {
        GalleryLayout(
            state = state,
            controller = controller,
            images =  images
        )
    }
}

@Preview
@Composable
private fun Shimmer(@PreviewParameter(ThemePreviewParameter::class) useDarkMode: Boolean) {
    AdelaideTheme(useDarkMode) {

    }
}
