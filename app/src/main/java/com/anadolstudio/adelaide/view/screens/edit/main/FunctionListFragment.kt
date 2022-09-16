package com.anadolstudio.adelaide.view.screens.edit.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.anadolstudio.adelaide.databinding.LayoutListBinding
import com.anadolstudio.adelaide.view.screens.BaseEditFragment
import com.anadolstudio.adelaide.view.screens.edit.main_edit_screen.EditActivity.Companion.EDIT_TYPE
import com.anadolstudio.adelaide.view.screens.main.EditType
import com.anadolstudio.core.adapters.ActionClick
import com.anadolstudio.photoeditorcore.domain.functions.FuncItem

class FunctionListFragment : BaseEditFragment() {

    companion object {

        fun newInstance(
                key: EditType,
                detailableListener: ActionClick<FuncItem>
        ): FunctionListFragment = FunctionListFragment().apply {

            arguments = Bundle().apply {
                putSerializable(EDIT_TYPE, key)
            }

            listener = detailableListener
        }

    }

    private var listener: ActionClick<FuncItem>? = null
    private lateinit var binding: LayoutListBinding

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        binding = LayoutListBinding.inflate(inflater)
        binding.recyclerView.adapter =
                FunctionListAdapter(FuncItem.MainFunctions.values().toList(), listener)

        return binding.root
    }

}
