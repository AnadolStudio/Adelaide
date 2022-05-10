package com.anadolstudio.adelaide.view.screens.edit.stiker

import android.annotation.SuppressLint
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
import com.anadolstudio.photoeditorprocessor.functions.FuncItem
import com.anadolstudio.photoeditorprocessor.functions.sticker.StickerFunction
import com.anadolstudio.photoeditorprocessor.util.BitmapCommonUtil

class StickerEditFragment : BaseEditFragment(), IDetailable<String> {

    private val activityViewModel: EditActivityViewModel by activityViewModels()
    private val viewModel: StickerViewModel by viewModels()
    private lateinit var adapter: StickerAdapter
    private lateinit var func: StickerFunction

    companion object {
        fun newInstance(): StickerEditFragment = StickerEditFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: LayoutListBinding = LayoutListBinding.inflate(inflater, container, false)

        viewModel.adapterDataCommunication.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Success -> adapter.setData(result.data)
                is Result.Error -> result.error.printStackTrace()
            }
        }

        func = activityViewModel.getEditProcessor()
            .getFunction(FuncItem.MainFunctions.STICKER) as StickerFunction?
            ?: StickerFunction()

        adapter = StickerAdapter(mutableListOf(), this)
        binding.recyclerView.adapter = adapter
        viewModel.loadAdapterData(requireContext())
        return binding.root
    }

    @SuppressLint("CheckResult")
    override fun toDetail(path: String) {
        ImageLoader.loadImageWithoutCache(
            requireContext(),
            path,
        ) { sticker -> activityViewModel.viewController.photoEditor.addSticker(sticker) }
    }

    override fun apply(): Boolean {
        if (!isReadyToApply) return false

        activityViewModel.viewController.photoEditor.clearHelperBox()
        activityViewModel.getEditProcessor().addFunction(func)
        activityViewModel.processPreview(
            BitmapCommonUtil.captureView(activityViewModel.viewController.photoEditorView)
        )

        return super.apply()
    }

}