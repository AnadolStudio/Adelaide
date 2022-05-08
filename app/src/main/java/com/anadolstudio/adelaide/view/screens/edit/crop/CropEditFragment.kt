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
import com.anadolstudio.adelaide.domain.editphotoprocessor.functions.implementation.TransformFunction
import com.anadolstudio.adelaide.domain.editphotoprocessor.crop.RatioItem
import com.anadolstudio.adelaide.domain.utils.BitmapUtil
import com.anadolstudio.adelaide.view.screens.BaseEditFragment
import com.anadolstudio.adelaide.view.screens.edit.EditActivityViewModel
import com.anadolstudio.adelaide.domain.editphotoprocessor.functions.FuncItem
import com.anadolstudio.adelaide.view.screens.edit.main.FunctionListAdapter
import com.anadolstudio.core.interfaces.IDetailable
import com.theartofdev.edmodo.cropper.CropImageView

class CropEditFragment : BaseEditFragment(), IDetailable<FuncItem> {

    companion object {
        private val TAG = CropEditFragment::class.java.name

        fun newInstance() = CropEditFragment()
    }

    private lateinit var defaultImage: Bitmap
    private var cropImage: Bitmap? = null
    private var currentRatioItem = RatioItem.FREE
    private lateinit var binding: FragmentEditCropBinding
    private lateinit var func: TransformFunction
    private val viewModel: EditActivityViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditCropBinding.inflate(inflater)

        binding.mainRecyclerView.adapter =
            FunctionListAdapter(FuncItem.MainFunctions.TRANSFORM.innerFunctions, this)

        func = viewModel.getEditProcessor()
            .getFunction(FuncItem.MainFunctions.TRANSFORM) as TransformFunction?
            ?: TransformFunction()

        binding.ratioRecyclerView.adapter =
            CropListAdapter(RatioItem.values().toList(), RatioDetailable())

        viewModel.getEditProcessor().getOriginalImage().also { bitmap ->
            defaultImage = func.getCopyWithoutCrop().process(bitmap)
            cropImage = func.process(bitmap)
            viewModel.viewController.setupCropImage(func)
            viewModel.viewController.showMainImage(false)
            viewModel.viewController.showCropImage(true)
        }


        viewModel.viewController.cropView.let { cropImageView ->
            cropImageView.setImageBitmap(cropImage)
            cropImageView.setFixedAspectRatio(false)
            selectWholeRect(cropImageView)
        }

        return binding.root
    }

    override fun isLocalApply(): Boolean {
        return binding.ratioRecyclerView.visibility == VISIBLE
    }

    override fun apply(): Boolean {
        if (isLocalApply) {
            val cropView = viewModel.viewController.cropView

            cropView.let {
                saveWindowCrop(it)
                func.ratioItem = currentRatioItem
            }

            viewModel.getEditProcessor().let { processor ->

                defaultImage = processor.getOriginalImage()
                    .let { bitmap -> func.getCopyWithoutCrop().process(bitmap) }

                cropImage = processor.getOriginalImage().let { b -> func.process(b) }

//                cropView?.let { rebootFlip(it) }
                cropView.setImageBitmap(cropImage)
                resetCropView()
            }
//            changeFlipHorizontal = false
//            changeFlipVertical = false
            showRatioView(false)
            return true
        }

        viewModel.getEditProcessor().addFunction(func)
        viewModel.processPreview()

        return super.apply()
    }

    fun resetCropView() {
        viewModel.viewController.cropView.resetCropRect()
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

    override fun isLocalBackClick(): Boolean {
        return binding.ratioRecyclerView.visibility == VISIBLE
    }

    override fun onBackClick(): Boolean {
        if (isLocalBackClick) {
            showRatioView(false)

            viewModel.viewController.cropView.let { cropView ->
                cropView.setImageBitmap(cropImage ?: func.process(defaultImage))
                val flipH = cropView.isFlippedHorizontally
                val flipV = cropView.isFlippedVertically
                val degrees = cropView.rotatedDegrees

                resetCropView()

                cropView.isFlippedHorizontally = flipH
                cropView.isFlippedVertically = flipV
                cropView.rotatedDegrees = degrees
            }

            viewModel.viewController.setupCropImage(func)

            /*val tmp = arguments?.getParcelable(FUNCTION) ?: TransformFunction()
            func.cropPoints = tmp.cropPoints
            func.cropWindow = tmp.cropWindow*/
            return true
        }

        return super.onBackClick()
    }

    fun showRatioView(show: Boolean) {
        viewModel.viewController.showWorkspace(true, show)
        binding.mainRecyclerView.visibility = if (show) GONE else VISIBLE
        binding.ratioRecyclerView.visibility = if (show) VISIBLE else GONE
        viewModel.viewController.cropView.isShowCropOverlay = show
    }

    private fun selectWholeRect(cropView: CropImageView) {
        cropView.cropRect = cropView.wholeImageRect
    }

    override fun toDetail(data: FuncItem) {
        val cropView = viewModel.viewController.cropView
        cropView.setFixedAspectRatio(false)

        when (data) {
            FuncItem.InnerFunctionItem.CROP -> {
//                rebootFlip(cropView)
                resetCropView()

                viewModel.getEditProcessor().apply {
                    cropView.setImageBitmap(
                        func.getCopyWithoutCrop().process(getOriginalImage())
                    )
                    cropImage = func.process(getOriginalImage())
                }

                showRatioView(true)
                cropView.setFixedAspectRatio(func.fixAspectRatio)
//                parent()?.setupWindowCropImage(func)
                viewModel.viewController.setupCropImage(func)
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
            val cropView = viewModel.viewController.cropView

            currentRatioItem = data
            cropView.setFixedAspectRatio(data != RatioItem.FREE)

            when (data) {
                RatioItem.FREE -> selectWholeRect(cropView)

                RatioItem.RATIO_AUTO -> {
                    val size = BitmapUtil.getRealSize(activity as AppCompatActivity?)
                    cropView.setAspectRatio(size.widthPixels, size.heightPixels)
                }

                else -> cropView.setAspectRatio(data.ratio.x, data.ratio.y)
            }
        }
    }
}

