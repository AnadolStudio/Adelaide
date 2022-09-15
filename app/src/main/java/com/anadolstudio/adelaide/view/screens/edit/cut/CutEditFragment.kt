package com.anadolstudio.adelaide.view.screens.edit.cut

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Point
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.toPoint
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.anadolstudio.adelaide.R
import com.anadolstudio.adelaide.databinding.FragmentEditCutBinding
import com.anadolstudio.adelaide.domain.utils.touchlisteners.ImageTouchListener
import com.anadolstudio.adelaide.view.screens.BaseEditFragment
import com.anadolstudio.adelaide.view.screens.edit.EditActivity
import com.anadolstudio.adelaide.view.screens.edit.EditActivityViewModel
import com.anadolstudio.adelaide.view.screens.edit.Settings.Companion.XLARGE
import com.anadolstudio.adelaide.view.screens.edit.Settings.Companion.XSMALL
import com.anadolstudio.adelaide.view.screens.edit.cut.CutViewModel.Companion.CUSTOM
import com.anadolstudio.adelaide.view.screens.gallery.GalleryListActivity
import com.anadolstudio.core.adapters.ActionClick
import com.anadolstudio.core.view.util.ViewPositionState
import com.anadolstudio.photoeditorprocessor.functions.FuncItem
import com.anadolstudio.photoeditorprocessor.functions.cut.CutFunction
import com.anadolstudio.photoeditorprocessor.util.BitmapCommonUtil
import com.anadolstudio.photoeditorprocessor.util.BitmapCutUtil
import com.anadolstudio.photoeditorprocessor.util.BitmapInfoUtil
import ja.burhanrashid52.photoeditor.PhotoEditor

class CutEditFragment : BaseEditFragment(), ActionClick<String> {

    companion object {
        const val CHOOSE_PHOTO = "choose_photo"
        fun newInstance(): CutEditFragment = CutEditFragment()
    }

    private enum class State {
        CUTTING, CHOICE_BG
    }

    private var currentState: State = State.CUTTING
    private lateinit var binding: FragmentEditCutBinding
    private val activityViewModel: EditActivityViewModel by activityViewModels()
    private val viewModel: CutViewModel by viewModels()
    private lateinit var sizeOriginal: Point
    private var touchListener: ImageTouchListener? = null
    private lateinit var viewState: ViewPositionState
    private lateinit var launcher: ActivityResultLauncher<String>
    private lateinit var photoEditor: PhotoEditor
    private lateinit var func: CutFunction
    private lateinit var adapter: BackgroundAdapter

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditCutBinding.inflate(inflater, container, false)
        viewModel.settings.color = requireContext().getColor(R.color.colorMask) //default
        photoEditor = activityViewModel.viewController.photoEditor
        val originalBitmap: Bitmap = activityViewModel.getEditProcessor().getCurrentImage()
        sizeOriginal = Point(originalBitmap.width, originalBitmap.height)

        viewModel.maskLiveData.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Success -> defaultDraw(result.data)
                is Result.Error -> showToast(R.string.cut_error_object_isnt_exist)
                is Result.Loading -> showLoadingDialog()
                else -> {}
            }

            if (result !is Result.Loading) hideLoadingDialog()
        }

        val sizePoint = activityViewModel.viewController.currentSizeOfMainPanel()
        viewModel.createMask(originalBitmap, sizePoint)

        viewModel.adapterDataCommunication.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Success -> adapter.setData(result.data)
                is Result.Error -> result.error.printStackTrace()
                else -> {}
            }

            if (result !is Result.Loading) hideLoadingDialog()
        }

        adapter = BackgroundAdapter(mutableListOf(), this)
        binding.backgroundRecyclerView.adapter = adapter
        viewModel.loadAdapterData(requireContext())

        val photoEditorView = activityViewModel.viewController.photoEditorView
        viewState = ViewPositionState(photoEditorView)
        touchListener = ImageTouchListener(photoEditorView, true, true)
        photoEditorView.setOnTouchListener(touchListener)
        setupView()

        func = activityViewModel.getEditProcessor()
                .getFunction(FuncItem.MainFunctions.CUT) as CutFunction?
                ?: CutFunction()

        return binding.root
    }

    fun setupView() {
        photoEditor.drawingView.setPorterDuffXferMode(PorterDuff.Mode.SRC)

        binding.editSliderView.setCancelListener {
            photoEditor.clearAllViews()

            with(viewModel.maskLiveData.value) {
                if (this is Result.Success) defaultDraw(this.data)
            }
        }

        binding.editSliderView.setApplyIcon(R.drawable.ic_brush)

        binding.editSliderView.setApplyListener {
            val isBrush = viewModel.settings.isBrush
            binding.editSliderView.setApplyIcon(
                    if (isBrush) R.drawable.ic_eraser else R.drawable.ic_brush
            )
            viewModel.settings.isBrush = !isBrush
            activityViewModel.viewController.setupBrush(viewModel.settings)
        }

        val currentSize = viewModel.settings.size
        binding.editSliderView.setupSlider(XSMALL, XLARGE, currentSize)

        binding.editSliderView.setSliderListener { _, value, _ ->
            if (value != viewModel.settings.size) {
                activityViewModel.viewController.setupBrush(viewModel.settings, value)
            }
        }

        launcher = registerForActivityResult(
                GalleryListActivity.GalleryResultContract()
        ) { path: String? -> this.setOwnBackground(path) }
    }

    private fun defaultDraw(bitmap: Bitmap) {
        defaultStateScalePanel()
        BitmapInfoUtil.getInfoOfBitmap(bitmap)
        photoEditor.drawingView.setDefaultBitmap(bitmap)
        photoEditor.drawingView.invalidate()
        photoEditor.setBrushDrawingMode(false)
        activityViewModel.viewController.setupBrush(viewModel.settings)
    }

    private fun defaultStateScalePanel() {
        viewState.rebootToDefaultWithAnim()
    }

    private fun clearDrawingPanel() {
        photoEditor.clearAllViews()
        photoEditor.setBrushDrawingMode(false)
    }

    override fun action(data: String) {
        when (data) {
            CUSTOM -> launcher.launch(CHOOSE_PHOTO)
            else -> {
                activityViewModel.setCurrentImage(
                        activity as AppCompatActivity, data, ImageView.ScaleType.FIT_CENTER
                )

                selectEditObject()
            }
        }
    }

    private fun setOwnBackground(path: String?) {
        if (path == null) return

        activityViewModel.setCurrentImage(
                activity as AppCompatActivity, path, ImageView.ScaleType.FIT_CENTER
        )

        selectEditObject()
    }

    private fun setCurrentState(state: State) {
        currentState = state

        binding.editSliderView.isVisible = currentState == State.CUTTING
        binding.backgroundRecyclerView.isVisible = currentState == State.CHOICE_BG
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun isReadyToApply(): Boolean {
        if (currentState == State.CUTTING) {
            defaultStateScalePanel()
            activityViewModel.viewController.photoEditorView.setOnTouchListener(null)
            cutByMask()
            return false
        }

        if (!hasEditObject) {
            nothingIsSelectedToast()
            return false
        }

        return super.isReadyToApply()
    }

    private fun cutByMask() {
        val mainBitmap = activityViewModel.getEditProcessor().getCurrentImage()
        val viewController = activityViewModel.viewController
        val drawBitmap = BitmapCommonUtil
                .captureView(viewController.photoEditorView.drawingView)

        activityViewModel.currentBitmapCommunication.map(Result.Loading())
        // TODO map должен быть только из ViewModel
        val processListener = (activity as? EditActivity)?.getProgressListener()
        viewModel.cutByMask(requireContext(), processListener, mainBitmap, drawBitmap)
                .onSuccess { cutBitmap ->
                    clearDrawingPanel()

                    val nullBackground = BitmapCutUtil.createNullBackground(
                            viewController.currentSizeOfMainPanel().toPoint()
                    )
                    activityViewModel.currentBitmapCommunication.map(Result.Success(nullBackground))

                    viewController.photoEditor.addImage(cutBitmap, false)
                    setCurrentState(State.CHOICE_BG)
                }
    }

    override fun apply(): Boolean {
        if (!isReadyToApply) return false

        activityViewModel.getEditProcessor().addFunction(func)
        activityViewModel.viewController.photoEditor.clearHelperBox()

        activityViewModel.processPreview(
                BitmapCommonUtil.captureView(activityViewModel.viewController.photoEditorView)
        )

        return super.apply()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun backClick(): Boolean {
        if (!isReadyToBackClick) return false

        defaultStateScalePanel()
        activityViewModel.viewController.photoEditorView.setOnTouchListener(null)
        activityViewModel.rebootCurrentImage()

        return super.backClick()
    }
}
