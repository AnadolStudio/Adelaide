package com.anadolstudio.adelaide.feature.gallery.presetnation

import android.Manifest
import android.content.pm.PackageManager.PERMISSION_GRANTED
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import com.anadolstudio.adelaide.R
import com.anadolstudio.adelaide.base.adapter.paging.GroupiePagingAdapter
import com.anadolstudio.adelaide.base.adapter.paging.PagingErrorItem
import com.anadolstudio.adelaide.base.adapter.paging.PagingLoadingItem
import com.anadolstudio.adelaide.base.fragment.BaseContentFragment
import com.anadolstudio.adelaide.databinding.FragmentGalleryBinding
import com.anadolstudio.core.common_extention.onTrue
import com.anadolstudio.core.common_util.throttleClick
import com.anadolstudio.core.presentation.fold
import com.anadolstudio.core.presentation.fragment.hasPermission
import com.anadolstudio.core.presentation.fragment.state_util.ViewStateDelegate
import com.anadolstudio.core.recycler.SpaceItemDecoration
import com.anadolstudio.core.recycler.SpaceItemDecoration.Companion.SMALL_SPACE
import com.anadolstudio.core.viewbinding.viewBinding
import com.anadolstudio.core.viewmodel.obtainViewModel
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.xwray.groupie.Section

class GalleryFragment : BaseContentFragment<GalleryState, GalleryViewModel, GalleryController>(R.layout.fragment_gallery) {

    companion object {
        private const val RENDER_FOLDERS = "RENDER_FOLDERS"
        private const val RENDER_CURRENT_FOLDER = "RENDER_CURRENT_FOLDER"
        private const val RENDER_PAGING = "RENDER_PAGING"
        const val REQUEST_STORAGE_PERMISSION = 1
        private val STORAGE_PERMISSION = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)

    }

    override val viewStateDelegate: ViewStateDelegate by lazy {
        ViewStateDelegate(
                contentViews = listOf(binding.recyclerView),
                loadingViews = listOf(binding.progressView),
                stubViews = listOf(binding.emptyText),
                errorViews = listOf(binding.emptyText),
        )
    }
    private val binding by viewBinding { FragmentGalleryBinding.bind(it) }
    private val popupMenu: PopupMenu by lazy { PopupMenu(requireContext(), binding.pupupButton) }
    private val mainListSection = Section()

    override fun createViewModel(): GalleryViewModel = obtainViewModel(GalleryViewModel.Factory()) // TODO requireArguments()

    override fun initView(controller: GalleryController) {
        binding.toolbar.setBackClickListener(controller::onBackClicked)
        binding.pupupButton.throttleClick { popupMenu.show() }

        popupMenu.setOnMenuItemClickListener { item ->
            popupMenu.dismiss()
            controller.onFolderChanged(item.title.toString())

            return@setOnMenuItemClickListener true
        }
        with(binding.recyclerView) {
            addItemDecoration(SpaceItemDecoration(SMALL_SPACE))
            layoutManager = GridLayoutManager(requireContext(), 3)
            setItemViewCacheSize(50)

            adapter = GroupiePagingAdapter(
                    onNeedLoadMoreData = { controller.onLoadMoreImages() },
                    sections = arrayOf(mainListSection)
            )
        }
    }

    override fun showContent(content: GalleryState) {
        super.showContent(content)

        render(RENDER_CURRENT_FOLDER to content.currentFolder) {
            binding.toolbar.setTitle(this.name)
        }

        render(RENDER_FOLDERS to content.unusedFolders) {
            fold(
                    onContent = {
                        binding.pupupButton.isVisible = true

                        provideRootView().post {
                            popupMenu.menu.clear()
                            this@render.forEach { popupMenu.menu.add(it.name) }
                        }
                    },
                    onEmpty = { binding.pupupButton.isVisible = false }
            )
        }

        render(RENDER_PAGING to content.imageListState) {
            fold(
                    transform = { GalleryItem(it) },
                    recyclerView = binding.recyclerView,
                    onNextPageError = { mainListSection.setFooter(PagingErrorItem()) },
                    onNextPageLoading = { mainListSection.setFooter(PagingLoadingItem()) },
                    onRefresh = { mainListSection.removeFooter() },
                    onAllData = { mainListSection.removeFooter() },
                    onPageData = { galleryItems -> mainListSection.addAll(galleryItems) },
                    onUpdateData = { galleryItems ->
                        mainListSection.update(galleryItems)
                        binding.recyclerView.post { binding.recyclerView.smoothScrollToPosition(0) }
                    },
            )
        }
    }

    override fun showFullScreenLoading(isFullScreenLoading: Boolean) {
        binding.progressView.isVisible = false
    }

    override fun showError(error: Throwable, controller: GalleryController) {
        super.showError(error, controller)
        binding.emptyText.text = error.message
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<String>,
            grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_STORAGE_PERMISSION ->
                if (grantResults.all { it == PERMISSION_GRANTED }) { // permission granted
                    controller.onPermissionGranted()
                } else { // permission denied
                    showSettingsSnackbar()
                }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    private fun showSettingsSnackbar() = Snackbar
            .make(
                    requireView().findViewById(android.R.id.content),
                    getText(R.string.gallery_error_miss_permission),
                    BaseTransientBottomBar.LENGTH_INDEFINITE
            )
            .setAction(R.string.gallery_snack_bar_settings) { controller.onNavigateToSettingsClicked() }
            .show()

    private fun withPermission(action: () -> Unit): Boolean =
            hasPermission(STORAGE_PERMISSION[0]).onTrue { action.invoke() }

}
