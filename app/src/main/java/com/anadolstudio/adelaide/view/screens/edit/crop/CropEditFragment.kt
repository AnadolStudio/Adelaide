package com.anadolstudio.adelaide.view.screens.edit.crop

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import com.anadolstudio.adelaide.databinding.FragmentEditCropBinding
import com.anadolstudio.adelaide.view.screens.BaseEditFragment
import com.anadolstudio.adelaide.view.screens.edit.EditActivityViewModel
import com.anadolstudio.adelaide.view.screens.edit.main.FunctionListAdapter
import com.anadolstudio.core.interfaces.IDetailable
import com.anadolstudio.photoeditorprocessor.functions.FuncItem
import com.anadolstudio.photoeditorprocessor.functions.transform.RatioItem
import com.anadolstudio.photoeditorprocessor.functions.transform.TransformFunction
import com.anadolstudio.photoeditorprocessor.util.DisplayUtil
import com.theartofdev.edmodo.cropper.CropImageView

class CropEditFragment : BaseEditFragment(), IDetailable<FuncItem> {

    companion object {
        fun newInstance() = CropEditFragment()
    }

    enum class State { TRANSFORM, CROP }

    private lateinit var defaultImage: Bitmap
    private var cropImage: Bitmap? = null
    private var currentRatioItem = RatioItem.FREE
    private var currentState = State.TRANSFORM
    private lateinit var binding: FragmentEditCropBinding
    private lateinit var func: TransformFunction
    private val activityViewModel: EditActivityViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditCropBinding.inflate(inflater)

        binding.mainRecyclerView.adapter =
            FunctionListAdapter(FuncItem.MainFunctions.TRANSFORM.innerFunctions, this)

        func = activityViewModel.getEditProcessor()
            .getFunction(FuncItem.MainFunctions.TRANSFORM) as TransformFunction?
            ?: TransformFunction()

        binding.ratioRecyclerView.adapter =
            CropListAdapter(
                RatioItem.values().toList(),
                RatioDetailable()
            )

        activityViewModel.getEditProcessor().getCurrentImage().also { bitmap ->
            defaultImage = func.getCopyWithoutCrop().process(bitmap)
            cropImage = func.process(bitmap)
            activityViewModel.viewController.setupCropImage(func)
            activityViewModel.viewController.showMainImageView(false)
            activityViewModel.viewController.showCropImageView(true)
        }

        activityViewModel.viewController.cropView.let { cropImageView ->
            cropImageView.setImageBitmap(cropImage)
            cropImageView.setFixedAspectRatio(false)
            selectWholeRect(cropImageView)
        }

        return binding.root
    }

    private fun resetCropView() {
        activityViewModel.viewController.cropView.resetCropRect()
    }

    private fun rebootFlip(it: CropImageView) {
        it.isFlippedVertically = false
        it.isFlippedHorizontally = false
    }

    private fun saveWindowCrop(it: CropImageView) {
        func.cropPoints = it.cropPoints

        func.setCropWindow(
            it.cropRect,
            defaultImage.width,
            defaultImage.height
        )
    }

    override fun isReadyToApply(): Boolean {
        val isReady = currentState == State.TRANSFORM

        if (!isReady) {
            val cropView = activityViewModel.viewController.cropView

            cropView.let {
                saveWindowCrop(it)
                func.ratioItem = currentRatioItem
            }

            activityViewModel.getEditProcessor().let { processor ->

                defaultImage = processor.getCurrentImage()
                    .let { bitmap -> func.getCopyWithoutCrop().process(bitmap) }

                cropImage = processor.getCurrentImage().let { b -> func.process(b) }

//                cropView?.let { rebootFlip(it) }
                cropView.setImageBitmap(cropImage)
                resetCropView()
            }
//            changeFlipHorizontal = false
//            changeFlipVertical = false
            showRatioView(false)
        }

        return isReady
    }

    override fun apply(): Boolean {
        activityViewModel.getEditProcessor().addFunction(func)
        activityViewModel.processPreview()

        return super.apply()
    }

    override fun isReadyToBackClick(): Boolean = currentState == State.TRANSFORM

    override fun backClick(): Boolean {
        if (!isReadyToBackClick) {
            showRatioView(false)

            activityViewModel.viewController.cropView.let { cropView ->
                cropView.setImageBitmap(cropImage ?: func.process(defaultImage))
                val flipH = cropView.isFlippedHorizontally
                val flipV = cropView.isFlippedVertically
                val degrees = cropView.rotatedDegrees

                resetCropView()

                cropView.isFlippedHorizontally = flipH
                cropView.isFlippedVertically = flipV
                cropView.rotatedDegrees = degrees
            }

            activityViewModel.viewController.setupCropImage(func)

            /*val tmp = arguments?.getParcelable(FUNCTION) ?: TransformFunction()
            func.cropPoints = tmp.cropPoints
            func.cropWindow = tmp.cropWindow*/
            return false
        }

        return super.backClick()
    }

    private fun showRatioView(show: Boolean) {
        currentState = if (show) State.CROP else State.TRANSFORM
        activityViewModel.viewController.showWorkspace(true, show)
        binding.mainRecyclerView.visibility = if (show) GONE else VISIBLE
        binding.ratioRecyclerView.visibility = if (show) VISIBLE else GONE
        activityViewModel.viewController.cropView.isShowCropOverlay = show
    }

    private fun selectWholeRect(cropView: CropImageView) {
        cropView.cropRect = cropView.wholeImageRect
    }

    override fun toDetail(data: FuncItem) {

        val cropView = activityViewModel.viewController.cropView
        cropView.setFixedAspectRatio(false)

        when (data) {
            FuncItem.InnerFunctionItem.CROP -> {
//                rebootFlip(cropView)
                resetCropView()

                activityViewModel.getEditProcessor().apply {
                    cropView.setImageBitmap(
                        func.getCopyWithoutCrop().process(getCurrentImage())
                    )
                    cropImage = func.process(getCurrentImage())
                }

                showRatioView(true)
                cropView.setFixedAspectRatio(func.fixAspectRatio)
//                parent()?.setupWindowCropImage(func)
                activityViewModel.viewController.setupCropImage(func)
            }

            FuncItem.InnerFunctionItem.TURN -> {
//                func.degrees -= DEGREES_ROTATE
//                cropView.rotatedDegrees = func.degrees
                //TODO с flip + rotate = работает криво
//                func.setDegree(-DEGREES_ROTATE, defaultImage.width, defaultImage.height)
            }

            FuncItem.InnerFunctionItem.FLIP_HORIZONTAL -> {
                func.flipHorizontally(defaultImage.width, defaultImage.height)
                cropView.flipImageHorizontally()
            }

            FuncItem.InnerFunctionItem.FLIP_VERTICAL -> {
                func.flipVertically(defaultImage.width, defaultImage.height)
                cropView.flipImageVertically()
            }

            else -> {}
        }
    }

    inner class RatioDetailable : IDetailable<RatioItem> {

        override fun toDetail(data: RatioItem) {
            val cropView = activityViewModel.viewController.cropView

            currentRatioItem = data
            cropView.setFixedAspectRatio(data != RatioItem.FREE)

            when (data) {
                RatioItem.FREE -> selectWholeRect(cropView)
                RatioItem.RATIO_AUTO -> DisplayUtil.getDefaultSize(activity as AppCompatActivity).apply {
                    cropView.setAspectRatio(widthPixels, heightPixels)
                }
                else -> cropView.setAspectRatio(data.ratio.x, data.ratio.y)
            }
        }
    }
}

