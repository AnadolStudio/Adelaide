package com.anadolstudio.adelaide.view.screens.edit

import android.graphics.Bitmap
import android.graphics.Point
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.anadolstudio.adelaide.databinding.ActivityEditBinding
import com.anadolstudio.photoeditorprocessor.processor.Mode
import com.anadolstudio.adelaide.domain.utils.ViewSizeUtil
import com.anadolstudio.adelaide.view.animation.AnimateUtil
import com.anadolstudio.core.view.show
import com.theartofdev.edmodo.cropper.CropImageView

class EditViewController(private val binding: ActivityEditBinding) {
    //TODO нужен interface
    init {
        binding.cropImage.setMinCropResultSize(250, 250)
    }

    val cropView: CropImageView = binding.cropImage
    val supportImage: ImageView = binding.supportImage

    fun showWorkspace(show: Boolean, needMoreSpace: Boolean = false) {
        //TODO не хватает плавности для mainContainer
        val visible = if (needMoreSpace) View.GONE else View.VISIBLE

        if (binding.adView.visibility != visible) {
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

    fun showMainImageView(show: Boolean) {
        binding.mainImage.show(show)
    }

    fun resetWorkSpace() {
        showWorkspace(false)
        showCropImageView(false)
        showMainImageView(true)
        setSupportImage(null)
    }

    fun showCropImageView(show: Boolean) {
        binding.cropImage.show(show)

        if (!show) {
            binding.cropImage.clearImage()
            binding.cropImage.resetCropRect()
            binding.cropImage.setOnCropWindowChangedListener(null)
            binding.cropImage.setOnSetCropOverlayMovedListener(null)
        }

        binding.cropImage.isShowCropOverlay = false
    }

    fun setupCropImage(function: com.anadolstudio.photoeditorprocessor.functions.implementation.TransformFunction) {
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

    fun setupWindowCropImage(function: com.anadolstudio.photoeditorprocessor.functions.implementation.TransformFunction) {
        binding.cropImage.cropRect = function.cropRect ?: binding.cropImage.wholeImageRect
    }

    fun setupMainImage(activity: AppCompatActivity, bitmap: Bitmap) {
        val mainImage = binding.mainImage
        ViewSizeUtil.changeViewSize(bitmap, mainImage, workspaceSize(activity))
        mainImage.scaleType = ImageView.ScaleType.FIT_CENTER
        Log.d(
            "EditViewController",
            "setupMainImage: ${mainImage.layoutParams.width} ${mainImage.layoutParams.height}"
        )
    }

    fun setMainBitmap(activity: AppCompatActivity, bitmap: Bitmap) {
        binding.mainImage.setImageBitmap(bitmap)
        setupMainImage(activity, bitmap)
    }

    fun setupSupportImage() {
        ViewSizeUtil.changeViewSize(
            binding.supportImage,
            binding.mainImage.width,
            binding.mainImage.height
        )
    }

    fun setupSupportImage(modeEdit: Mode) {
        binding.supportImage.run {
            visibility = View.VISIBLE

            when (modeEdit) {
                Mode.EFFECT -> {
                    scaleType = ImageView.ScaleType.CENTER_CROP
                    adjustViewBounds = false

                    ViewSizeUtil.changeViewSize(
                        this,
                        binding.mainImage.width,
                        binding.mainImage.height
                    )

                }

                else -> {}
            }
        }
    }

    fun setSupportImage(
        drawable: Drawable?,
    ) {
        binding.supportImage.setImageDrawable(drawable)
    }

/*
    fun resizeContainer(
        activity: AppCompatActivity,
        bitmap: Bitmap,
        block: ((Int, Int) -> Unit)? = null
    ) {
        val workspaceSize = workspaceSize(activity)

        binding.apply {
            ViewSizeUtil.changeViewSize(bitmap, supportImage, workspaceSize)
            supportImage.run {
                post {
                    block?.invoke(width, height)
                    ViewSizeUtil.changeViewSize(container, width, height)
                }
            }
        }
    }
*/

    private fun workspaceSize(activity: AppCompatActivity): Point = com.anadolstudio.photoeditorprocessor.util.DisplayUtil.workspaceSize(
        activity, binding.navigationToolbar, binding.toolbarFragment, binding.adView
    )
}