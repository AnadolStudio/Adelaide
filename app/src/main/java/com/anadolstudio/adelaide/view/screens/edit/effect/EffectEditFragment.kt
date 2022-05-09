package com.anadolstudio.adelaide.view.screens.edit.effect

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.anadolstudio.adelaide.databinding.LayoutListBinding
import com.anadolstudio.adelaide.domain.utils.ImageLoader
import com.anadolstudio.adelaide.view.screens.BaseEditFragment
import com.anadolstudio.adelaide.view.screens.edit.EditActivityViewModel
import com.anadolstudio.core.interfaces.IDetailable
import com.anadolstudio.core.tasks.Result
import com.google.android.material.slider.Slider
import kotlin.math.roundToInt

class EffectEditFragment : BaseEditFragment(), IDetailable<String>, Slider.OnChangeListener {

    companion object {
        private const val DEFAULT_ALPHA = 255F

        fun newInstance(): EffectEditFragment = EffectEditFragment()
    }

    private var currentEffect: Drawable? = null
    private var currentAlpha = 0
    private lateinit var binding: LayoutListBinding
    private val activityViewModel: EditActivityViewModel by activityViewModels()
    private val effectViewModel: EffectEditViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LayoutListBinding.inflate(inflater, container, false)

        init()

        effectViewModel.adapterDataCommunication.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Success -> binding.recyclerView.adapter = EffectAdapter(
                    result.data.thumbnail,
                    result.data.paths,
                    this
                )
                is Result.Error -> result.error.printStackTrace()
                else -> {}
            }
        }

        effectViewModel.loadData(
            requireContext(),
            activityViewModel.getEditProcessor().getCurrentImage()
        )

        return binding.root
    }

    private fun init() {
        binding.editSliderView.visibility = View.GONE
        binding.editSliderView.setupSlider(0F, 255F)
        currentAlpha = DEFAULT_ALPHA.toInt()
        setupSlider(false)
        binding.editSliderView.setSliderListener(this)
    }

    private fun setupSlider(isEnable: Boolean) {
        binding.editSliderView.visibility = if (isEnable) View.VISIBLE else View.GONE
        binding.editSliderView.setValue(DEFAULT_ALPHA)
    }

    override fun toDetail(data: String) {
        activityViewModel.viewController.showWorkspace(true, needMoreSpace = data.isNotEmpty())
        if (data.isEmpty()) {
            setEffect(null)
            return
        }

        ImageLoader.loadImageWithoutCache(requireContext(), data) { bitmap -> setEffect(bitmap) }
    }

    private fun setEffect(bitmap: Bitmap?) {
        currentEffect = bitmap?.let { BitmapDrawable(resources, bitmap) }

        bitmap?.also { selectEditObject() }
            ?: clearEditObject()

        currentEffect?.alpha = currentAlpha
        activityViewModel.viewController.setSupportImage(currentEffect)
        setupSlider(bitmap != null)
    }

    override fun apply(): Boolean {
        if (!isReadyToApply) {
            //TODO
            return false
        }

        return super.apply()
    }

    override fun isReadyToApply(): Boolean {
        if (!hasEditObject) {
            nothingIsSelectedToast()
            return false
        }

        return super.isReadyToApply()
    }


    @SuppressLint("RestrictedApi")
    override fun onValueChange(slider: Slider, value: Float, fromUser: Boolean) {
        currentAlpha = value.roundToInt()
        currentEffect?.alpha = currentAlpha
    }

}