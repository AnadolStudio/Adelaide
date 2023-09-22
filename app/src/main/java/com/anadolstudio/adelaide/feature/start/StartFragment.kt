package com.anadolstudio.adelaide.feature.start

import com.anadolstudio.adelaide.App
import com.anadolstudio.adelaide.R
import com.anadolstudio.adelaide.base.fragment.BaseActionFragment
import com.anadolstudio.adelaide.databinding.FragmentStartBinding
import com.anadolstudio.core.view.animation.AnimateUtil.scaleAnimationOnClick
import com.anadolstudio.core.viewbinding.viewBinding
import com.anadolstudio.core.viewmodel.obtainViewModel

class StartFragment : BaseActionFragment<StartViewModel, StartController>(R.layout.fragment_start) {

    private val binding: FragmentStartBinding by viewBinding { FragmentStartBinding.bind(requireView()) }

    override fun createViewModel(): StartViewModel = obtainViewModel(StartViewModel.Factory())

    // TODO
    //  1) hide statusBar (insetWindow)
    //  2) set WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = boolean

    override fun initView(controller: StartController) {
        binding.nightBtn.scaleAnimationOnClick { (requireActivity().application as App).changeTheme() } // TODO to VM
        binding.galleryButton.scaleAnimationOnClick(action = controller::onGalleryClicked)
        binding.takePhotoButton.scaleAnimationOnClick(action = controller::onTakePhotoClicked)
        binding.draftButton.scaleAnimationOnClick(action = controller::onDraftClicked)
        binding.infoButton.scaleAnimationOnClick(action = controller::onInfoClicked)
    }
}
