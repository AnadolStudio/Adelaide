package com.anadolstudio.adelaide.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.anadolstudio.adelaide.activities.MainActivity.Companion.EDIT_TYPE
import com.anadolstudio.adelaide.adapters.FunctionListAdapter
import com.anadolstudio.adelaide.databinding.FragmentListFunctionBinding
import com.anadolstudio.adelaide.editphotoprocessor.TransformFunction
import com.anadolstudio.adelaide.helpers.FunctionItem
import com.anadolstudio.adelaide.helpers.MainFunctions
import com.anadolstudio.adelaide.interfaces.IDetailable

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

    override fun toDetail(t: FunctionItem) {
        parent()?.currentFunction = t
        parent()?.showWorkspace(true)

        when (t) {
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