package com.anadolstudio.adelaide.view.screens.edit.brush

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.anadolstudio.adelaide.R
import com.anadolstudio.adelaide.databinding.LayoutBrushListBinding
import com.anadolstudio.adelaide.view.adapters.ColorAdapter
import com.anadolstudio.adelaide.view.screens.BaseEditFragment
import com.anadolstudio.adelaide.view.screens.edit.DrawingViewModel
import com.anadolstudio.adelaide.view.screens.edit.main_edit_screen.EditActivityViewModel
import com.anadolstudio.adelaide.view.screens.edit.Settings.Companion.XLARGE
import com.anadolstudio.adelaide.view.screens.edit.Settings.Companion.XSMALL
import com.anadolstudio.core.adapters.ActionClick
import com.anadolstudio.photoeditorcore.domain.functions.FuncItem
import com.anadolstudio.photoeditorcore.domain.functions.brush.BrushFunction
import com.anadolstudio.photoeditorcore.domain.util.BitmapCommonUtil
import ja.burhanrashid52.photoeditor.PhotoEditor

class BrushEditFragment : BaseEditFragment(), ActionClick<String> {

    private lateinit var binding: LayoutBrushListBinding
    private lateinit var photoEditor: PhotoEditor
    private lateinit var func: BrushFunction
    private var currentState = State.DRAW
    private val activityViewModel: EditActivityViewModel by activityViewModels()
    private val viewModel: DrawingViewModel by viewModels()

    companion object {
        fun newInstance(): BrushEditFragment = BrushEditFragment()
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        binding = LayoutBrushListBinding.inflate(inflater, container, false)

        photoEditor = activityViewModel.viewController.photoEditor

        binding.editSliderView.setCancelIcon(R.drawable.ic_color_lens)
        binding.editSliderView.setCancelTint(viewModel.settings.color)
        binding.editSliderView.setCancelListener { setState(State.COLOR_PICK) }

        binding.editSliderView.setApplyIcon(R.drawable.ic_brush)

        binding.editSliderView.setApplyListener {
            val isBrush = viewModel.settings.isBrush

            binding.editSliderView.setApplyIcon(
                    if (isBrush) R.drawable.ic_eraser else R.drawable.ic_brush
            )

            viewModel.settings.isBrush = !isBrush
            activityViewModel.viewController.setupBrush(viewModel.settings)
        }

        binding.editSliderView.setupSlider(
                XSMALL, XLARGE, viewModel.settings.size
        )

        binding.editSliderView.setSliderListener { _, value, _ ->
            if (value != viewModel.settings.size) {
                activityViewModel.viewController.setupBrush(viewModel.settings, value)
            }
        }

        func = activityViewModel.getEditProcessor()
                .getFunction(FuncItem.MainFunctions.CUT) as BrushFunction?
                ?: BrushFunction()

        binding.recyclerView.adapter = ColorAdapter(com.anadolstudio.data.colors.Colors.getColors().toMutableList(), this)
        activityViewModel.viewController.setupBrush(viewModel.settings)
        return binding.root
    }

    private fun setState(currentState: State) {
        this.currentState = currentState
        binding.editSliderView.visibility =
                if (currentState == State.DRAW) View.VISIBLE else View.GONE
        binding.recyclerView.visibility =
                if (currentState == State.COLOR_PICK) View.VISIBLE else View.GONE
    }

    override fun action(data: String) {
        viewModel.settings.color = Color.parseColor(data)
        binding.editSliderView.setCancelTint(viewModel.settings.color)
        activityViewModel.viewController.setupBrush(viewModel.settings)
    }

    private enum class State {
        DRAW, COLOR_PICK
    }

    override fun apply(): Boolean {
        if (!isReadyToApply()) return false

        activityViewModel.getEditProcessor().addFunction(func)
        activityViewModel.viewController.photoEditor.clearHelperBox()

        activityViewModel.processPreview(
                BitmapCommonUtil.captureView(activityViewModel.viewController.photoEditorView)
        )

        return super.apply()
    }

    override fun isReadyToBackClick(): Boolean {
        return currentState != State.DRAW
    }

    override fun backClick(): Boolean = if (isReadyToBackClick()) {
        setState(State.DRAW)

        false
    } else {
        super.backClick()
    }

}
