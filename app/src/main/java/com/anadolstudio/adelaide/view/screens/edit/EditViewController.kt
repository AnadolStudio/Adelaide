package com.anadolstudio.adelaide.view.screens.edit

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Point
import android.graphics.PointF
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.anadolstudio.adelaide.databinding.ActivityEditBinding
import com.anadolstudio.adelaide.domain.utils.ViewSizeUtil
import com.anadolstudio.adelaide.view.animation.AnimateUtil
import com.anadolstudio.core.view.show
import com.anadolstudio.photoeditorprocessor.functions.transform.TransformFunction
import com.anadolstudio.photoeditorprocessor.processor.EditMode
import com.anadolstudio.photoeditorprocessor.util.DisplayUtil
import com.theartofdev.edmodo.cropper.CropImageView
import ja.burhanrashid52.photoeditor.PhotoEditor
import ja.burhanrashid52.photoeditor.shape.ShapeBuilder

class EditViewController(context: Context, private val binding: ActivityEditBinding) {
    //TODO нужен interface
    init {
        binding.cropImage.setMinCropResultSize(250, 250)
    }

    val cropView: CropImageView = binding.cropImage
    val supportImage: ImageView = binding.supportImage
    val mainImage: ImageView = binding.photoEditorView.source
    val photoEditorView = binding.photoEditorView

    val photoEditor = PhotoEditor.Builder(context, binding.photoEditorView)
        .setClipSourceImage(true)
        .build()

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
        binding.photoEditorView.source.show(show)
    }

    fun resetWorkSpace() {
        showWorkspace(false)
        showCropImageView(false)
        showMainImageView(true)
        setSupportImage(null)
        photoEditor.clearAllViews()
        photoEditor.setBrushDrawingMode(false)
        binding.photoEditorView.source.clearColorFilter()
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

    fun setupMainImage(activity: AppCompatActivity, bitmap: Bitmap) {
        val mainImage = binding.photoEditorView.source
        ViewSizeUtil.changeViewSize(bitmap, mainImage, workspaceSize(activity))
        mainImage.scaleType = ImageView.ScaleType.FIT_CENTER
    }

    fun setMainBitmap(activity: AppCompatActivity, bitmap: Bitmap) {
        binding.photoEditorView.source.setImageBitmap(bitmap)
        setupMainImage(activity, bitmap)
    }

    fun setupSupportImage() {
        ViewSizeUtil.changeViewSize(
            binding.supportImage,
            binding.photoEditorView.source.width,
            binding.photoEditorView.source.height
        )
    }

    fun setupSupportImage(editModeEdit: EditMode) {
        binding.supportImage.run {
            visibility = View.VISIBLE

            when (editModeEdit) {
                EditMode.EFFECT -> {
                    scaleType = ImageView.ScaleType.CENTER_CROP
                    adjustViewBounds = false

                    ViewSizeUtil.changeViewSize(
                        this,
                        binding.photoEditorView.source.width,
                        binding.photoEditorView.source.height
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

    fun workspaceSize(activity: AppCompatActivity): Point =
        DisplayUtil.workspaceSize(
            activity, binding.navigationToolbar, binding.toolbarFragment, binding.adView
        )

    fun currentSizeOfMainPanel() = PointF(mainImage.width.toFloat(), mainImage.height.toFloat())

    fun setupBrush(settings: Settings, size: Float = settings.size) {
        settings.size = size
        photoEditor.setBrushDrawingMode(true)

        val builder = ShapeBuilder()
            .withShapeColor(settings.color)
            .withShapeSize(settings.size)

        photoEditor.setShape(builder)

        if (!settings.isBrush) photoEditor.brushEraser()
    }

}