package com.anadolstudio.adelaide.view.screens.edit.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.anadolstudio.adelaide.view.screens.main.MainActivity.Companion.EDIT_TYPE
import com.anadolstudio.adelaide.view.adapters.FunctionListAdapter
import com.anadolstudio.adelaide.databinding.FragmentListFunctionBinding
import com.anadolstudio.adelaide.domain.editphotoprocessor.TransformFunction
import com.anadolstudio.adelaide.domain.utils.FunctionItem
import com.anadolstudio.adelaide.domain.utils.MainFunctions
import com.anadolstudio.adelaide.view.screens.BaseEditFragment
import com.anadolstudio.adelaide.view.screens.edit.crop.CropEditFragment
import com.anadolstudio.core.interfaces.IDetailable

class FunctionListFragment : BaseEditFragment(), IDetailable<FunctionItem> {
    companion object {
        fun newInstance(key: String): FunctionListFragment {
            val args = Bundle()
            args.putString(EDIT_TYPE, key)
            val fragment = FunctionListFragment()
            fragment.arguments = args
            return fragment
        }

    }

    private lateinit var binding: FragmentListFunctionBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentListFunctionBinding.inflate(inflater)

        binding.recyclerView.adapter = FunctionListAdapter(MainFunctions.mainFunction, this)

        return binding.root
    }

    override fun toDetail(data: FunctionItem) {
        parent()?.currentFunction = data
        parent()?.showWorkspace(true)

        when (data) {
            FunctionItem.TRANSFORM -> {
                parent()?.let {
                    val function = it.editProcessor.getFunction(FunctionItem.TRANSFORM.name)
                    it.replaceFragment(
                        CropEditFragment.newInstance(
                            function as? TransformFunction ?: TransformFunction()
                        )
                    )
                }
            }
            else -> {
                parent()?.currentFunction = null
                parent()?.showWorkspace(false)
            }
        }
    }

}