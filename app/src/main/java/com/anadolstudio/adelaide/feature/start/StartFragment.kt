package com.anadolstudio.adelaide.feature.start

import android.os.Handler
import androidx.appcompat.app.AppCompatDelegate
import com.anadolstudio.adelaide.App
import com.anadolstudio.adelaide.R
import com.anadolstudio.adelaide.base.fragment.BaseActionFragment
import com.anadolstudio.adelaide.confetti.WinterConfettiGenerator
import com.anadolstudio.adelaide.databinding.FragmentStartBinding
import com.anadolstudio.core.view.animation.AnimateUtil.scaleAnimationOnClick
import com.anadolstudio.core.viewbinding.viewBinding
import com.anadolstudio.core.viewmodel.obtainViewModel

class StartFragment : BaseActionFragment<StartViewModel, StartController>(R.layout.fragment_start) {

    private val binding: FragmentStartBinding by viewBinding { FragmentStartBinding.bind(requireView()) }

    override fun createViewModel(): StartViewModel = obtainViewModel(StartViewModel.Factory())

    override fun initView(controller: StartController) {
        binding.nightBtn.setOnClickListener { (requireActivity().application as App).changeTheme() } // TODO to VM
        binding.photoCardView.scaleAnimationOnClick(controller::onPhotoClicked)
//        binding.collageCardView.setOnClickListener {  } TODO

       /* Handler(requireActivity().mainLooper).postDelayed(
                {
                    WinterConfettiGenerator(
                            binding.confettiContainer,
                            AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES
                    ).launchConfetti()
                },
                500)*/
    }
}
