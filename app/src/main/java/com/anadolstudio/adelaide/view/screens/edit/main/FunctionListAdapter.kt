package com.anadolstudio.adelaide.view.screens.edit.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.anadolstudio.adelaide.R
import com.anadolstudio.adelaide.databinding.ItemFunctionBinding
import com.anadolstudio.adelaide.view.adapters.SimpleAdapter
import com.anadolstudio.core.adapters.AbstractViewHolder
import com.anadolstudio.core.adapters.ActionClick
import com.anadolstudio.photoeditorprocessor.functions.FuncItem

class FunctionListAdapter(
        data: List<FuncItem>,
        detailable: ActionClick<FuncItem>?
) : SimpleAdapter<FuncItem>(data, detailable) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FunctionViewHolder =
            FunctionViewHolder(
                    LayoutInflater.from(parent.context).inflate(R.layout.item_function, parent, false),
                    detailable
            )

    class FunctionViewHolder(
            view: View,
            detailable: ActionClick<FuncItem>?
    ) : AbstractViewHolder.Base<FuncItem>(view, detailable) {

        private val binding: ItemFunctionBinding = ItemFunctionBinding.bind(view)

        override fun onBind(data: FuncItem) {
            super.onBind(data)

            binding.icon.setImageDrawable(
                    data.drawableId.let { ContextCompat.getDrawable(itemView.context, it) }
            )

            binding.text.setText(data.textId)
        }
    }

}
