@file:OptIn(ExperimentalMaterial3Api::class)

package com.anadolstudio.adelaide.feature.gallery.presetnation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridItemScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.anadolstudio.adelaide.di.viewmodel.daggerViewModel
import com.anadolstudio.adelaide.feature.main.Navigator
import com.anadolstudio.compose.ui.drawable.Icons
import com.anadolstudio.compose.ui.theme.AdelaideTheme
import com.anadolstudio.compose.ui.theme.AdelaideTypography
import com.anadolstudio.compose.ui.theme.Dimension
import com.anadolstudio.compose.ui.theme.Shapes
import com.anadolstudio.compose.ui.theme.preview.ThemePreviewParameter
import com.anadolstudio.compose.ui.theme.textShimmer
import com.anadolstudio.compose.ui.view.snackbar.SnackbarHostState
import com.anadolstudio.compose.ui.view.text.Text
import com.anadolstudio.utils.data_source.media.Folder
import com.anadolstudio.utils.data_source.media.Image
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.components.rememberImageComponent
import com.skydoves.landscapist.glide.GlideImage
import kotlinx.coroutines.flow.MutableStateFlow
import org.joda.time.DateTime

@Composable
internal fun MediaScreen(
    navigator: Navigator,
    snackbarHostState: SnackbarHostState,
    viewModel: MediaViewModel = daggerViewModel()
) {
    val state by viewModel.stateLiveData.observeAsState()
    val images = viewModel.galleryFlow.collectAsLazyPagingItems()

    GalleryLayout(
        state = requireNotNull(state),
        controller = viewModel,
        images = images,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GalleryLayout(
    state: GalleryState,
    controller: MediaController,
    images: LazyPagingItems<String>,
) {
    BackHandler { controller.onBackClicked() }

    ImageList(
        images = images,
        controller = controller,
        state = state,
    )
}

@Composable
private fun ImageList(
    images: LazyPagingItems<String>,
    controller: MediaController,
    state: GalleryState,
) {
    Scaffold(
        modifier = Modifier
            .background(color = AdelaideTheme.colors.colorSecondary)
            .statusBarsPadding()
            .navigationBarsPadding(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {},
                actions = { ToolbarActions(controller) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors().copy(
                    containerColor = AdelaideTheme.colors.colorPrimary,
                    scrolledContainerColor = AdelaideTheme.colors.colorPrimary,
                ),
            )
        },
    ) { insetsPadding ->
        val map = remember { state.imageState.imageMap.entries }

        LazyVerticalGrid(
            modifier = Modifier
                .padding(top = insetsPadding.calculateTopPadding())
                .background(color = AdelaideTheme.colors.colorSecondary)
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTransformGestures { centroid, pan, zoom, rotation ->
                        when {
                            zoom > 1 -> controller.onZoomDecreased()
                            zoom < 1 -> controller.onZoomIncreased()
                            else -> Unit
                        }
                    }
                },
            contentPadding = PaddingValues(Dimension.mediumMargin),
            columns = GridCells.Fixed(state.columnSpan),
            verticalArrangement = Arrangement.spacedBy(Dimension.smallMargin),
            horizontalArrangement = Arrangement.spacedBy(Dimension.smallMargin),
        ) {

            map.forEach { (date, imageList) ->
                item(
                    span = { GridItemSpan(state.columnSpan) }
                ) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = Dimension.mediumMargin)
                            .animateItemPlacement(),
                        style = AdelaideTypography.captionBook14,
                        fontWeight = FontWeight(700),
                        textAlign = TextAlign.Start,
                        text = date.toString("dd MMM YYYY")
                    )
                }

                items(
                    count = imageList.size,
                    key = { imageList[it].path },
                    itemContent = { index -> ImageItem(imageList[index], controller) }
                )
            }
        }

    }
}

@Composable
private fun LazyGridItemScope.ImageItem(image: Image, controller: MediaController) {
    val imageRemember = remember { image }

    Box(
        modifier = Modifier
            .animateItemPlacement()
            .zIndex(1F)
    ) {
        GlideImage(
            imageModel = { imageRemember.path },
            requestOptions = { RequestOptions() .diskCacheStrategy(DiskCacheStrategy.ALL) },
            component = rememberImageComponent {
//            +PlaceholderPlugin.Loading(painterResource(id = R.drawable.ic_image))
//            +PlaceholderPlugin.Failure(painterResource(id = R.drawable.ic_image))
            },
            imageOptions = ImageOptions(
                contentScale = ContentScale.Crop,
                alignment = Alignment.Center
            ),
            modifier = Modifier
                .aspectRatio(1f)
                .background(color = AdelaideTheme.colors.shimmerGradient.colorCenter)
                .clip(Shapes.textShimmer),
            /*.clickable { controller.onImageSelected(path) }*/
        )
        Column(
            Modifier
                .background(color = AdelaideTheme.colors.colorOverlay)
                .align(Alignment.Center)
                .aspectRatio(1f)
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = imageRemember.format.orEmpty(),
                color = Color.White
            )
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = imageRemember.folder.orEmpty(),
                color = Color.White
            )
        }
    }
}

@Composable
private fun ToolbarActions(controller: MediaController) {
    IconButton(onClick = { controller.onSearchClicked() }) {
        Image(
            modifier = Modifier
                .padding(vertical = 4.dp)
                .size(24.dp),
            painter = Icons.Search,
            contentDescription = null,
        )
    }
    IconButton(onClick = { controller.onMoreClicked() }) {
        Image(
            modifier = Modifier
                .padding(vertical = 4.dp)
                .size(24.dp),
            painter = Icons.VerticalMore,
            contentDescription = null,
        )
    }
}

@Preview
@Composable
private fun PreviewMedia(@PreviewParameter(ThemePreviewParameter::class) useDarkMode: Boolean) {
    val state = remember {
        val date = DateTime.now()
        GalleryState(
            columnSpan = 3,
            imageState = ImageState(
                mapOf(
                    date to createExampleList(date)
                )
            )
        )
    }
    val exampleList = listOf("Set", "Set1", "Set2")
    val images = MutableStateFlow(PagingData.from(exampleList)).collectAsLazyPagingItems()

    val controller = object : MediaController {
        override fun onBackClicked() = Unit
        override fun onPermissionGranted() = Unit
        override fun onFolderChanged(folder: Folder) = Unit
        override fun onImageSelected(imageUri: String) = Unit
        override fun onNavigateToSettingsClicked() = Unit
        override fun onLoadMoreImages() = Unit
        override fun onZoomIncreased() = Unit
        override fun onZoomDecreased() = Unit
        override fun onFolderClosed() = Unit
        override fun onFolderOpened() = Unit
        override fun onSearchClicked() = Unit
        override fun onMoreClicked() = Unit
    }

    AdelaideTheme(useDarkMode) {
        GalleryLayout(
            state = state,
            controller = controller,
            images = images
        )
    }
}

private fun createExampleList(date: DateTime): List<Image> {
    val list = mutableListOf<Image>()

    repeat(10) {
        val item = Image(path = "", date = date, folder = "Folder", format = "png")
        list += item
    }

    return list
}

@Preview
@Composable
private fun Shimmer(@PreviewParameter(ThemePreviewParameter::class) useDarkMode: Boolean) {
    AdelaideTheme(useDarkMode) {

    }
}
