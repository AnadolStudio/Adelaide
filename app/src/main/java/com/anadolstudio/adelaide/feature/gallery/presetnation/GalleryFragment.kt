package com.anadolstudio.adelaide.feature.gallery.presetnation

import android.annotation.SuppressLint
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.navigation.fragment.navArgs
import com.anadolstudio.adelaide.R
import com.anadolstudio.adelaide.base.adapter.paging.GroupiePagingAdapter
import com.anadolstudio.adelaide.base.fragment.BaseContentFragment
import com.anadolstudio.adelaide.databinding.FragmentGalleryBinding
import com.anadolstudio.adelaide.feature.gallery.presetnation.model.Folder
import com.anadolstudio.core.permission.READ_MEDIA_PERMISSION
import com.anadolstudio.core.permission.registerPermissionRequest
import com.anadolstudio.core.presentation.fold
import com.anadolstudio.core.presentation.fragment.state_util.ViewStateDelegate
import com.anadolstudio.core.util.common.throttleClick
import com.anadolstudio.core.util.paginator.PagingDataState
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
    private val popupMenu: PopupMenu by lazy { PopupMenu(requireContext(), binding.pupupButton) }
    private val mainListSection = Section()

    private val permissionLauncher = registerPermissionRequest(
            permission = READ_MEDIA_PERMISSION,
            onGranted = { controller.onPermissionGranted() },
            onDenied = { viewStateDelegate.showError() },
            onDontAskAgain = { viewStateDelegate.showError() }
    )

    override fun createViewModel(): GalleryViewModel = obtainViewModel(GalleryViewModel.Factory(args.editType))

    override fun initView(controller: GalleryController) {
        binding.toolbar.setBackClickListener(controller::onBackClicked)
        binding.pupupButton.throttleClick { popupMenu.show() }

        popupMenu.setOnMenuItemClickListener { item ->
            controller.onFolderChanged(item.title.toString())

            return@setOnMenuItemClickListener true
        }

        with(binding.recyclerView) {
            adapter = GroupiePagingAdapter(
                    onNeedLoadMoreData = controller::onLoadMoreImages,
                    sections = arrayOf(mainListSection)
            )
            setZoomListener(
                    onZoomIncreased = { controller.onZoomIncreased() },
                    onZoomDecreased = { controller.onZoomDecreased() }
            )
        }
    }

    override fun handleEvent(event: SingleEvent) = when (event) {
        is RequestPermission -> permissionLauncher.launch(READ_MEDIA_PERMISSION)
        else -> super.handleEvent(event)
    }

    override fun render(state: GalleryState, controller: GalleryController) {
        renderToolbar(state.currentFolder)
        renderFolders(state.unusedFolders)
        renderList(state.imageListState)
        renderSpan(state.columnSpan)
    }

    private fun renderSpan(columnSpan: Int) {
        columnSpan.render(RENDER_SPAN) {
            binding.recyclerView.changeSpan(this)
        }
    }

    private fun renderList(imageListState: PagingDataState<String>) {
        imageListState.render(RENDER_PAGING) {
            fold(
                    transform = { GalleryItem(it) { controller.onImageSelected(it) } },
                    recyclerView = binding.recyclerView,
                    onError = { viewStateDelegate.showError() },
                    onEmptyData = { viewStateDelegate.showStub() },
                    onLoading = { viewStateDelegate.showLoading() },
                    onContent = { viewStateDelegate.showContent() },
                    onPageData = { galleryItems -> mainListSection.addAll(galleryItems) },
                    onUpdateData = { galleryItems ->
                        mainListSection.update(galleryItems)
                        binding.recyclerView.post { binding.recyclerView.smoothScrollToPosition(0) }
                    },
            )
        }
    }

    private fun renderToolbar(currentFolder: Folder) {
        currentFolder.render(RENDER_CURRENT_FOLDER) {
            binding.toolbar.setTitle(this.name)
        }
    }

    private fun renderFolders(unusedFolders: Set<Folder>) {
        unusedFolders.render(RENDER_FOLDERS) {
            fold(
                    onContent = {
                        provideRootView().post {
                            popupMenu.menu.clear()
                            forEach { popupMenu.menu.add(it.name) }
                        }
                    },
                    onEach = { isContent -> binding.pupupButton.isVisible = isContent }
            )
        }
    }

}
