package com.anadolstudio.adelaide.feature.home.presentation

import androidx.fragment.app.viewModels
import com.anadolstudio.adelaide.R
import com.anadolstudio.adelaide.base.fragment.BaseActionFragment
import com.anadolstudio.adelaide.databinding.FragmentHomeBinding
import com.anadolstudio.adelaide.feature.gallery.presetnation.GalleryEvent.DetailPhotoEvent
import com.anadolstudio.adelaide.feature.gallery.presetnation.GalleryEvent.RequestPermissionEvent
import com.anadolstudio.ui.viewbinding.viewBinding
import com.anadolstudio.ui.viewmodel.livedata.SingleEvent
import com.anadolstudio.utils.permission.READ_MEDIA_PERMISSION
import com.anadolstudio.utils.permission.registerPermissionListRequest

class HomeFragment : BaseActionFragment<HomeViewModel, HomeController>(R.layout.fragment_home) {

    private val binding by viewBinding { FragmentHomeBinding.bind(it) }

    private val permissionLauncher = registerPermissionListRequest(
        onAllGranted = { controller.onPermissionGranted() },
        onAnyDenied = { /*viewStateDelegate.showError()*/ },
        onAnyNotAskAgain = { /*viewStateDelegate.showError()*/ }
    )

    override fun createViewModelLazy() = viewModels<HomeViewModel> { viewModelFactory }

    override fun initView() = Unit

    override fun handleEvent(event: SingleEvent) = when (event) {
        is RequestPermissionEvent -> permissionLauncher.launch(arrayOf(READ_MEDIA_PERMISSION))
        is DetailPhotoEvent -> Unit
        else -> super.handleEvent(event)
    }

}
