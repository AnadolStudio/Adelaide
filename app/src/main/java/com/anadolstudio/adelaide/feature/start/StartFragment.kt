package com.anadolstudio.adelaide.feature.start

import android.Manifest
import com.anadolstudio.adelaide.App
import com.anadolstudio.adelaide.R
import com.anadolstudio.adelaide.base.fragment.BaseActionFragment
import com.anadolstudio.adelaide.databinding.FragmentStartBinding
import com.anadolstudio.core.permission.TakePhoto
import com.anadolstudio.core.permission.registerPermissionListRequest
import com.anadolstudio.core.view.animation.AnimateUtil.scaleAnimationOnClick
import com.anadolstudio.core.viewbinding.viewBinding
import com.anadolstudio.core.viewmodel.livedata.SingleEvent
import com.anadolstudio.core.viewmodel.obtainViewModel

class StartFragment : BaseActionFragment<StartViewModel, StartController>(R.layout.fragment_start) {

    private val binding: FragmentStartBinding by viewBinding { FragmentStartBinding.bind(requireView()) }

    private val cameraPermissionsLauncher = registerPermissionListRequest(
            onAllGranted = { controller.onCameraPermissionsGranted() },
            onAnyDenied = { /*TODO*/ },
            onAnyNotAskAgain = { /*TODO*/ }
    )

    private val takePhotoLauncher = registerForActivityResult(TakePhoto()) { uri ->
        controller.onTakePhotoResult(uri)
    }

    override fun createViewModel(): StartViewModel = obtainViewModel(StartViewModel.Factory())

    override fun initView() {
        binding.nightBtn.scaleAnimationOnClick { (requireActivity().application as App).changeTheme() } // TODO to VM
        binding.galleryButton.scaleAnimationOnClick(action = controller::onGalleryClicked)
        binding.takePhotoButton.scaleAnimationOnClick(action = this::requestCameraPermissions)
        binding.draftButton.scaleAnimationOnClick(action = controller::onDraftClicked)
        binding.infoButton.scaleAnimationOnClick(action = controller::onInfoClicked)
    }

    private fun requestCameraPermissions() = cameraPermissionsLauncher.launch(
            arrayOf(Manifest.permission.CAMERA)
    )

    override fun handleEvent(event: SingleEvent) = when (event) {
        is StartFragmentEvents.TakePhotoEvent -> takePhotoLauncher.launch(event.uri)
        else -> super.handleEvent(event)
    }

}
