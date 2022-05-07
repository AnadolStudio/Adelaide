package com.anadolstudio.adelaide.view.screens.edit

import android.view.View
import com.anadolstudio.adelaide.databinding.ActivityEditBinding
import com.anadolstudio.adelaide.domain.editphotoprocessor.TransformFunction
import com.anadolstudio.adelaide.view.animation.AnimateUtil
import com.anadolstudio.core.view.show
import com.theartofdev.edmodo.cropper.CropImageView

class EditViewController(private val binding: ActivityEditBinding) {
//TODO нужен interface
    init {
        binding.cropImage.setMinCropResultSize(250, 250)
    }

    val cropView: CropImageView = binding.cropImage

    fun showWorkspace(show: Boolean, needMoreSpace: Boolean = false) {
        //TODO не хватает плавности для mainContainer
        val visible = if (needMoreSpace) View.GONE else View.VISIBLE

        if (binding.adView.visibility != visible && show) {
            val height = binding.adView.height.toFloat()

            AnimateUtil.showAnimY(
                binding.adView,
                if (needMoreSpace) 0F else -height,
                if (needMoreSpace) -height else 0F,
                visible
            )
        }

        binding.saveBtn.show(!show)
        binding.applyBtn.show(show)
    }

    fun showMainImage(show: Boolean) {
        binding.mainImage.show(show)
    }

    fun resetWorkSpace() {
        showWorkspace(false)
        showCropImage(false)
        showMainImage(true)
    }

    fun showCropImage(show: Boolean) {
        binding.cropImage.show(show)

        if (!show) {
            binding.cropImage.clearImage()
            binding.cropImage.resetCropRect()
            binding.cropImage.setOnCropWindowChangedListener(null)
            binding.cropImage.setOnSetCropOverlayMovedListener(null)
        }

        binding.cropImage.isShowCropOverlay = false
    }

    fun setupCropImage(function: TransformFunction) {
        binding.cropImage.setAspectRatio(
            if (function.fixAspectRatio) function.ratioItem.ratio.x else 1,
            if (function.fixAspectRatio) function.ratioItem.ratio.y else 1
        )

        binding.cropImage.setFixedAspectRatio(false)
        setupWindowCropImage(function)
//        binding.cropImage.isFlippedVertically = function.flipVertical
//        binding.cropImage.isFlippedHorizontally = function.flipHorizontal
//        binding.cropImage.rotatedDegrees = function.degrees
    }

    fun setupWindowCropImage(function: TransformFunction) {
        binding.cropImage.cropRect = function.cropRect ?: binding.cropImage.wholeImageRect
    }
}