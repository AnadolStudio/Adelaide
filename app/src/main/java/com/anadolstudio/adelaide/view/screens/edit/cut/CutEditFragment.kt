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
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.anadolstudio.adelaide.R
import com.anadolstudio.adelaide.databinding.FragmentEditCutBinding
import com.anadolstudio.adelaide.domain.touchlisteners.ImageTouchListener
import com.anadolstudio.adelaide.view.screens.BaseEditFragment
import com.anadolstudio.adelaide.view.screens.edit.DrawingViewModel
import com.anadolstudio.adelaide.view.screens.edit.EditActivityViewModel
import com.anadolstudio.adelaide.view.screens.edit.cut.CutViewModel.Companion.CUSTOM
import com.anadolstudio.adelaide.view.screens.gallery.GalleryListActivity
import com.anadolstudio.core.interfaces.IDetailable
import com.anadolstudio.core.tasks.Result
import com.anadolstudio.core.view.ViewState
import com.anadolstudio.photoeditorprocessor.functions.FuncItem
import com.anadolstudio.photoeditorprocessor.functions.cut.CutFunction
import com.anadolstudio.photoeditorprocessor.util.BitmapCommonUtil
import com.anadolstudio.photoeditorprocessor.util.BitmapCutUtil
import com.anadolstudio.photoeditorprocessor.util.BitmapInfoUtil
import ja.burhanrashid52.photoeditor.PhotoEditor

class CutEditFragment : BaseEditFragment(), IDetailable<String> {

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
    private lateinit var viewState: ViewState
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

        viewModel.mask.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Success -> defaultDraw(result.data)
                is Result.Error -> showToast(R.string.cut_error_object_isnt_exist)
                is Result.Loading -> showLoadingDialog()
                else -> {}
            }

            if (result !is Result.Loading) hideLoadingDialog()
        }

        val sizePoint = activityViewModel.viewController.currentSizeOfMainPanel()
        viewModel.createMask(requireContext(), originalBitmap, sizePoint)

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
        viewState = ViewState(photoEditorView)
        touchListener = ImageTouchListener(photoEditorView, true, true)
        photoEditorView.setOnTouchListener(touchListener)
        initView()

        func = activityViewModel.getEditProcessor()
            .getFunction(FuncItem.MainFunctions.CUT) as CutFunction?
            ?: CutFunction()

        return binding.root
    }

    fun initView() {
        photoEditor.drawingView.setPorterDuffXferMode(PorterDuff.Mode.SRC)

        binding.editSliderView.setCancelListener {
            photoEditor.clearAllViews()

            with(viewModel.mask.getValue()) {
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
            viewModel.setupBrush(photoEditor)
        }

        val currentSize = viewModel.settings.size
        binding.editSliderView.setupSlider(
            DrawingViewModel.XSMALL, DrawingViewModel.XLARGE, currentSize
        )

        binding.editSliderView.setSliderListener { _, value, _ ->
            if (value != viewModel.settings.size) {
                viewModel.setupBrush(photoEditor, value)
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
        viewModel.setupBrush(photoEditor)
    }

    private fun defaultStateScalePanel() {
        viewState.rebootToDefaultWithAnim()
    }

    private fun clearDrawingPanel() {
        photoEditor.clearAllViews()
        photoEditor.setBrushDrawingMode(false)
    }

    override fun toDetail(data: String) {
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

        binding.editSliderView.visibility =
            if (currentState == State.CUTTING) View.VISIBLE else View.GONE

        binding.backgroundRecyclerView.visibility =
            if (currentState == State.CHOICE_BG) View.VISIBLE else View.GONE
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

        viewModel.cutByMask(requireContext(), mainBitmap, drawBitmap)
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