package com.anadolstudio.adelaide.feature.gallery.presetnation

import androidx.core.view.isVisible
import androidx.navigation.fragment.navArgs
import com.anadolstudio.adelaide.R
import com.anadolstudio.adelaide.base.adapter.BaseGroupAdapter
import com.anadolstudio.adelaide.base.adapter.paging.GroupiePagingAdapter
import com.anadolstudio.adelaide.base.fragment.BaseContentFragment
import com.anadolstudio.adelaide.databinding.FragmentGalleryBinding
import com.anadolstudio.core.data_source.media.Folder
import com.anadolstudio.core.permission.READ_MEDIA_PERMISSION
import com.anadolstudio.core.permission.registerPermissionRequest
import com.anadolstudio.core.presentation.fold
import com.anadolstudio.core.presentation.fragment.state_util.ViewStateDelegate
import com.anadolstudio.core.util.common.dpToPx
import com.anadolstudio.core.util.paginator.PagingDataState
import com.anadolstudio.core.view.animation.AnimateUtil.DURATION_LONG
import com.anadolstudio.core.view.animation.AnimateUtil.DURATION_SHORT
import com.anadolstudio.core.view.animation.AnimateUtil.animSlideBottomIn
import com.anadolstudio.core.view.animation.AnimateUtil.animSlideTopIn
import com.anadolstudio.core.view.animation.AnimateUtil.animSlideTopOut
import com.anadolstudio.core.view.recycler.ScrollListener
import com.anadolstudio.core.viewbinding.viewBinding
import com.anadolstudio.core.viewmodel.livedata.SingleEvent
import com.anadolstudio.core.viewmodel.obtainViewModel
import com.xwray.groupie.Section

class GalleryFragment : BaseContentFragment<GalleryState, GalleryViewModel, GalleryController>(R.layout.fragment_gallery) {

    private companion object {
        const val RENDER_FOLDERS = "RENDER_FOLDERS"
        const val RENDER_CURRENT_FOLDER = "RENDER_CURRENT_FOLDER"
        const val RENDER_PAGING = "RENDER_PAGING"
        const val RENDER_SPAN = "RENDER_SPAN"
        const val RENDER_REFRESH = "RENDER_REFRESH"
        val PROGRESS_END_TARGET = 160.dpToPx()
    }

    private val args: GalleryFragmentArgs by navArgs()
    override val viewStateDelegate: ViewStateDelegate by lazy {
        ViewStateDelegate(
                contentViews = listOf(binding.recyclerView, binding.foldersViewPager),
                loadingViews = listOf(binding.progressView),
                stubViews = listOf(binding.emptyView),
                errorViews = listOf(binding.emptyView),
        )
    }

    private val binding by viewBinding { FragmentGalleryBinding.bind(it) }
    private val folderSection = Section()
    private val imageSection = Section()

    private val permissionLauncher = registerPermissionRequest(
            permission = READ_MEDIA_PERMISSION,
            onGranted = { controller.onPermissionGranted() },
            onDenied = { viewStateDelegate.showError() },
            onDontAskAgain = { viewStateDelegate.showError() }
    )

    override fun createViewModel(): GalleryViewModel = obtainViewModel(GalleryViewModel.Factory(args.editType))

    override fun initView(controller: GalleryController) = with(binding) {
        toolbar.setBackClickListener(controller::onBackClicked)
        swipeRefresh.setOnRefreshListener(controller::onRefreshed)
        binding.swipeRefresh.setProgressViewEndTarget(false, PROGRESS_END_TARGET)

        with(recyclerView) {
            adapter = GroupiePagingAdapter(
                    imageSection,
                    onNeedLoadMoreData = controller::onLoadMoreImages
            )
            addOnScrollListener(
                    ScrollListener(
                            onScrollToBottom = { foldersViewPager.animSlideTopOut(DURATION_SHORT) },
                            onScrollToTop = { foldersViewPager.animSlideTopIn(DURATION_SHORT) }
                    )
            )

            setZoomListener(
                    onZoomIncreased = { controller.onZoomIncreased() },
                    onZoomDecreased = { controller.onZoomDecreased() }
            )
        }
        with(foldersViewPager) {
            adapter = BaseGroupAdapter(folderSection)
            itemAnimator = null // TODO add custom itemAnimator
        }
    }

    override fun handleEvent(event: SingleEvent) = when (event) {
        is RequestPermission -> permissionLauncher.launch(READ_MEDIA_PERMISSION)
        else -> super.handleEvent(event)
    }

    override fun render(state: GalleryState, controller: GalleryController) {
        renderFolders(state.folders, state.currentFolder)
        renderList(state.imageListState)
        renderSpan(state.columnSpan)
        renderRefresh(state.isRefreshing)
    }

    private fun renderRefresh(isRefreshing: Boolean) {
        isRefreshing.render(RENDER_REFRESH) {
            binding.swipeRefresh.isRefreshing = isRefreshing
        }
    }

    private fun renderSpan(columnSpan: Int) {
        columnSpan.render(RENDER_SPAN) {
            binding.recyclerView.changeSpan(this)
        }
    }

    private fun renderList(imageListState: PagingDataState<String>) {
        imageListState.render(RENDER_PAGING) {
            binding.swipeRefresh.isEnabled = this is PagingDataState.Content

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
                        imageSection.update(galleryItems)
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
                    onEach = { isNotEmpty -> binding.foldersViewPager.isVisible = isNotEmpty },
            )
        }
    }

}
