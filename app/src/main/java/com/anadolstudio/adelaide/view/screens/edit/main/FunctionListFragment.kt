package com.anadolstudio.adelaide.view.screens.edit.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.anadolstudio.adelaide.databinding.LayoutListBinding
import com.anadolstudio.adelaide.view.screens.BaseEditFragment
import com.anadolstudio.photoeditorprocessor.functions.FuncItem
import com.anadolstudio.adelaide.view.screens.main.MainActivity.Companion.EDIT_TYPE
import com.anadolstudio.core.interfaces.IDetailable

class FunctionListFragment : BaseEditFragment() {

    companion object {

        fun newInstance(
            key: String,
            detailableListener: IDetailable<com.anadolstudio.photoeditorprocessor.functions.FuncItem>
        ): FunctionListFragment = FunctionListFragment().apply {

            arguments = Bundle().apply {
                putString(EDIT_TYPE, key)
            }

            listener = detailableListener
        }

    }

    private var listener: IDetailable<com.anadolstudio.photoeditorprocessor.functions.FuncItem>? = null
    private lateinit var binding: LayoutListBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LayoutListBinding.inflate(inflater)
        binding.recyclerView.adapter =
            FunctionListAdapter(com.anadolstudio.photoeditorprocessor.functions.FuncItem.MainFunctions.values().toList(), listener)

        return binding.root
    }

}