package com.anadolstudio.adelaide.feature.gallery.presetnation

import android.view.GestureDetector
import android.view.Gravity
import androidx.core.view.isVisible
import androidx.navigation.fragment.navArgs
import com.anadolstudio.adelaide.R
import com.anadolstudio.adelaide.base.adapter.BaseGroupAdapter
import com.anadolstudio.adelaide.base.adapter.paging.GroupiePagingAdapter
import com.anadolstudio.adelaide.base.fragment.BaseContentFragment
import com.anadolstudio.adelaide.databinding.FragmentGalleryBinding
import com.anadolstudio.core.data_source.media.Folder
import com.anadolstudio.core.permission.READ_MEDIA_PERMISSION
import com.anadolstudio.core.permission.registerPermissionListRequest
import com.anadolstudio.core.presentation.fold
import com.anadolstudio.core.presentation.fragment.state_util.ViewStateDelegate
import com.anadolstudio.core.util.paginator.PagingDataState
import com.anadolstudio.core.view.animation.AnimateUtil.DURATION_LONG
import com.anadolstudio.core.view.animation.AnimateUtil.animSlideBottomIn
import com.anadolstudio.core.view.gesture.HorizontalMoveGesture
import com.anadolstudio.core.viewbinding.viewBinding
import com.anadolstudio.core.viewmodel.livedata.SingleEvent
import com.anadolstudio.core.viewmodel.obtainViewModel
import com.github.rubensousa.gravitysnaphelper.GravitySnapHelper
import com.xwray.groupie.Section

class GalleryFragment : BaseContentFragment<GalleryState, GalleryViewModel, GalleryController>(R.layout.fragment_gallery) {

    private companion object {
        const val RENDER_FOLDERS = "RENDER_FOLDERS"
        const val RENDER_FOLDERS_VISIBLE = "RENDER_FOLDERS_VISIBLE"
        const val RENDER_CURRENT_FOLDER = "RENDER_CURRENT_FOLDER"
        const val RENDER_IMAGES = "RENDER_IMAGES"
        const val RENDER_SPAN = "RENDER_SPAN"
    }

    private val args: GalleryFragmentArgs by navArgs()
    override val viewStateDelegate: ViewStateDelegate by lazy {
        ViewStateDelegate(
                contentViews = listOf(binding.recyclerView),
                loadingViews = listOf(binding.progressView),
                stubViews = listOf(binding.emptyView),
                errorViews = listOf(binding.emptyView),
        )
    }

    private val binding by viewBinding { FragmentGalleryBinding.bind(it) }
    private val folderSection = Section()
    private val imageSection = Section()

    private val permissionLauncher = registerPermissionListRequest(
            onAllGranted = { controller.onPermissionGranted() },
            onAnyDenied = { viewStateDelegate.showError() },
            onAnyNotAskAgain = { viewStateDelegate.showError() }
    )

    private val horizontalMoveGestureDetector: GestureDetector by lazy {
        GestureDetector(
                context,
                HorizontalMoveGesture(
                        width = binding.recyclerView.width,
                        onSwipeLeft = { controller.onFolderClosed() },
                        onSwipeRight = { controller.onFolderOpened() }
                )
        )
    }

    override fun createViewModel(): GalleryViewModel = obtainViewModel(GalleryViewModel.Factory(args.editType))

    override fun initView(controller: GalleryController) = with(binding) {
        toolbar.setBackClickListener(controller::onBackClicked)
        binding.recyclerContainer.addDispatchTouchListener { _, event ->
            horizontalMoveGestureDetector.onTouchEvent(event)
        }

        with(recyclerView) {
            adapter = GroupiePagingAdapter(
                    imageSection,
                    onNeedLoadMoreData = controller::onLoadMoreImages
            )

            setZoomListener(
                    onZoomIncreased = { controller.onZoomIncreased() },
                    onZoomDecreased = { controller.onZoomDecreased() }
            )
        }
        with(foldersViewPager) {
            adapter = BaseGroupAdapter(folderSection)
            GravitySnapHelper(Gravity.CENTER).attachToRecyclerView(this)
        }
    }

    override fun handleEvent(event: SingleEvent) = when (event) {
        is RequestPermission -> permissionLauncher.launch(arrayOf(READ_MEDIA_PERMISSION))
        else -> super.handleEvent(event)
    }

    override fun render(state: GalleryState, controller: GalleryController) {
        renderFoldersVisible(state.folderVisible)
        renderFolders(state.folders, state.currentFolder)
        renderImages(state.imageListState)
        renderSpan(state.columnSpan)
    }

    private fun renderFoldersVisible(isVisible: Boolean) = isVisible.render(RENDER_FOLDERS_VISIBLE) {
        binding.foldersViewPager.isVisible = isVisible
        /*
        when (isVisible) {
            true -> binding.foldersViewPager.animSlideStartIn(DURATION_NORMAL)
            false -> binding.foldersViewPager.animSlideStartOut(DURATION_NORMAL)
        }
        */
    }

    private fun renderSpan(columnSpan: Int) = columnSpan.render(RENDER_SPAN) {
        binding.recyclerView.changeSpan(this)
    }

    private fun renderImages(imageListState: PagingDataState<String>) {
        imageListState.render(RENDER_IMAGES) {

            fold(
                    transform = { GalleryItem(it) { controller.onImageSelected(it) } },
                    recyclerView = binding.recyclerView,
                    onError = { viewStateDelegate.showError() },
                    onEmptyData = { viewStateDelegate.showStub() },
                    onLoading = { viewStateDelegate.showLoading() },
                    onContent = {
                        binding.recyclerView.animSlideBottomIn(DURATION_LONG)
                        viewStateDelegate.showContent()
                    },
                    onPageData = { galleryItems -> imageSection.addAll(galleryItems) },
                    onUpdateData = { galleryItems ->
                        imageSection.update(galleryItems, false)
                        binding.recyclerView.post { binding.recyclerView.smoothScrollToPosition(0) }
                    },
            )
        }
    }

    private fun renderFolders(folders: Set<Folder>, currentFolder: Folder?) {
        folders.render(RENDER_FOLDERS, RENDER_CURRENT_FOLDER to currentFolder) {
            fold(
                    onContent = { folders ->
                        val folderItems = folders.map {
                            FolderItem(
                                    folder = it,
                                    isCurrent = it == currentFolder,
                                    onClick = controller::onFolderChanged
                            )
                        }
                        folderSection.update(folderItems)
                    },
            )
        }
        renderToolbar(currentFolder)
    }

    private fun renderToolbar(currentFolder: Folder?) {
        if (currentFolder == null) return

        binding.toolbar.setTitle("${currentFolder.name} ${currentFolder.imageCount}")
        binding.collapsingTitle.text = currentFolder.name
        binding.collapsingDesctiption.text = currentFolder.imageCount.toString()
    }
}
