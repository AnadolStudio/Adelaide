package com.anadolstudio.adelaide.fragments

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.anadolstudio.adelaide.view.screens.edit.EditActivity.Companion.FUNCTION
import com.anadolstudio.adelaide.view.adapters.CropListAdapter
import com.anadolstudio.adelaide.view.adapters.FunctionListAdapter
import com.anadolstudio.adelaide.databinding.FragmentEditCropBinding
import com.anadolstudio.adelaide.domain.editphotoprocessor.TransformFunction
import com.anadolstudio.adelaide.domain.utils.BitmapHelper
import com.anadolstudio.adelaide.domain.utils.FunctionItem
import com.anadolstudio.adelaide.domain.utils.RatioItem
import com.anadolstudio.core.interfaces.IDetailable
import com.theartofdev.edmodo.cropper.CropImageView

class CropEditFragment : BaseEditFragment(), IDetailable<FunctionItem> {

    companion object {
        private val TAG = CropEditFragment::class.java.name

        fun newInstance(function: TransformFunction): CropEditFragment {
            val fragment = CropEditFragment()
            val args = Bundle()
            args.putParcelable(FUNCTION, function)
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var defaultImage: Bitmap
    private var cropImage: Bitmap? = null
    private var currentRatioItem = RatioItem.FREE
    private lateinit var binding: FragmentEditCropBinding
    private lateinit var func: TransformFunction
//    private lateinit var newFunc: TransformFunction

    override fun onSaveInstanceState(outState: Bundle) {
//        outState.putParcelable(FUNCTION, newFunc)
        super.onSaveInstanceState(outState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditCropBinding.inflate(inflater)

        binding.mainRecyclerView.adapter =
            FunctionListAdapter(FunctionItem.TRANSFORM.innerFunctions, this)

        func = (arguments?.getParcelable(FUNCTION) as? TransformFunction)?.copy()
            ?: TransformFunction()

        binding.ratioRecyclerView.adapter = CropListAdapter(RatioDetailable(), func.ratioItem)

        parent()?.let {
            it.editProcessor.getOriginalImage()
                ?.let { b ->
                    defaultImage = func.getCopyWithoutCrop().process(b)
                    cropImage = func.process(b)
                }

            it.setupCropImage(func)
            it.showCropImage(true)
        }

        parent()?.cropView()?.let { cropImageView ->
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
            val cropView = parent()?.cropView()
            cropView?.let {
                saveWindowCrop(it)
                func.ratioItem = currentRatioItem
            }
            parent()?.editProcessor?.let { processor ->
                defaultImage = processor.getOriginalImage()
                    ?.let { b -> func.getCopyWithoutCrop().process(b) }!!
                cropImage = processor.getOriginalImage()?.let { b -> func.process(b) }

//                cropView?.let { rebootFlip(it) }
                cropView?.setImageBitmap(cropImage)
                resetCropView()
            }
//            changeFlipHorizontal = false
//            changeFlipVertical = false
            showRatioView(false)
            return true
        }

        parent()?.let {
            it.editProcessor.add(func)
            it.editProcessor.processPreview()
        }

        parent()?.showCropImage(false)

        return super.apply()
    }

    fun resetCropView() {
        parent()?.cropView()?.resetCropRect()
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


            parent()?.cropView()?.let {
                it.setImageBitmap(
                    cropImage
                        ?: func.process(defaultImage)
                )
                val flipH = it.isFlippedHorizontally
                val flipV = it.isFlippedVertically
                val degrees = it.rotatedDegrees
                resetCropView()
                it.isFlippedHorizontally = flipH
                it.isFlippedVertically = flipV
                it.rotatedDegrees = degrees
            }
            parent()?.setupCropImage(func)
            Log.d(
                TAG,
                "toDetailBack: crop ${parent()?.cropView()?.isFlippedVertically} ${parent()?.cropView()?.isFlippedHorizontally}"
            )

            /*val tmp = arguments?.getParcelable(FUNCTION) ?: TransformFunction()
            func.cropPoints = tmp.cropPoints
            func.cropWindow = tmp.cropWindow*/
            return true
        }
        parent()?.showCropImage(false)
        return super.onBackClick()
    }

    fun showRatioView(show: Boolean) {
        parent()?.showWorkspace(true, needMoreSpace = show)
        binding.mainRecyclerView.visibility = if (show) GONE else VISIBLE
        binding.ratioRecyclerView.visibility = if (show) VISIBLE else GONE
        parent()?.cropView()?.isShowCropOverlay = show
    }

    private fun selectWholeRect(cropView: CropImageView) {
        cropView.cropRect = cropView.wholeImageRect
    }

    override fun toDetail(t: FunctionItem) {
        val cropView = parent()?.cropView()
        cropView ?: return

        cropView.setFixedAspectRatio(false)

        when (t) {
            FunctionItem.CROP -> {
//                rebootFlip(cropView)
                resetCropView()
                parent()?.editProcessor?.let {
                    cropView.setImageBitmap(it.getOriginalImage()
                        ?.let { b -> func.getCopyWithoutCrop().process(b) }

                    )
                    cropImage = it.getOriginalImage()?.let { b -> func.process(b) }
                }
                showRatioView(true)
                cropView.setFixedAspectRatio(func.fixAspectRatio)
//                parent()?.setupWindowCropImage(func)
                parent()?.setupCropImage(func)


            }
            FunctionItem.TURN -> {
//                func.degrees -= DEGREES_ROTATE
//                cropView.rotatedDegrees = func.degrees
                //TODO с flip + rotate = работает криво
//                func.setDegree(-DEGREES_ROTATE, defaultImage.width, defaultImage.height)
            }
            FunctionItem.FLIP_HORIZONTAL -> {
                func.flipHorizontally(defaultImage.width, defaultImage.height)
                cropView.flipImageHorizontally()
            }
            FunctionItem.FLIP_VERTICAL -> {
                func.flipVertically(defaultImage.width, defaultImage.height)
                cropView.flipImageVertically()
            }
            else -> {}
        }
    }

    inner class RatioDetailable : IDetailable<RatioItem> {
        override fun toDetail(t: RatioItem) {
            val cropView = parent()?.cropView()
            cropView ?: return

            currentRatioItem = t
            cropView.setFixedAspectRatio(t != RatioItem.FREE)
            when (t) {
                RatioItem.FREE -> {
                    selectWholeRect(cropView)
                }
                RatioItem.RATIO_AUTO -> {
                    val size = BitmapHelper.getRealSize(activity as AppCompatActivity?)
                    cropView.setAspectRatio(size.widthPixels, size.heightPixels)
                }
                else -> {
                    cropView.setAspectRatio(t.ratio.x, t.ratio.y)
                }
            }
        }
    }
}

