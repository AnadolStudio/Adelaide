package com.anadolstudio.adelaide.view.screens.edit.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.anadolstudio.adelaide.databinding.FragmentListFunctionBinding
import com.anadolstudio.adelaide.view.screens.edit.enumeration.FuncItem
import com.anadolstudio.adelaide.view.screens.edit.enumeration.MainFunctions
import com.anadolstudio.adelaide.view.screens.BaseEditFragment
import com.anadolstudio.adelaide.view.screens.main.MainActivity.Companion.EDIT_TYPE
import com.anadolstudio.core.interfaces.IDetailable

class FunctionListFragment : BaseEditFragment() {

    companion object {

        fun newInstance(
            key: String,
            detailableListener: IDetailable<FuncItem>
        ): FunctionListFragment = FunctionListFragment().apply {

            arguments = Bundle().apply {
                putString(EDIT_TYPE, key)
            }

            listener = detailableListener
        }

    }

    private var listener: IDetailable<FuncItem>? = null
    private lateinit var binding: FragmentListFunctionBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentListFunctionBinding.inflate(inflater)
        binding.recyclerView.adapter = FunctionListAdapter(MainFunctions.mainFunction, listener)

        return binding.root
    }

}