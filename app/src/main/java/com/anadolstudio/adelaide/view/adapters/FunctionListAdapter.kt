package com.anadolstudio.adelaide.view.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.anadolstudio.adelaide.R
import com.anadolstudio.adelaide.databinding.ItemFunctionBinding
import com.anadolstudio.adelaide.domain.utils.FunctionItem
import com.anadolstudio.core.interfaces.IDetailable

class FunctionListAdapter(
    data: List<FunctionItem>,
    detailable: IDetailable<FunctionItem>
) : SimpleAdapter<FunctionItem>(data, detailable) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FunctionViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_function, parent, false)

        return FunctionViewHolder(view,detailable)
    }

    class FunctionViewHolder(
        view: View,
        detailable: IDetailable<FunctionItem>
    ) : SimpleViewHolder<FunctionItem>(view, detailable) {

        private val binding: ItemFunctionBinding = ItemFunctionBinding.bind(view)

        override fun onBind(data: FunctionItem) {
            super.onBind(data)

            binding.icon.setImageDrawable(
                data.drawableId.let { ContextCompat.getDrawable(itemView.context, it) }
            )

            binding.text.setText(data.textId)
        }
    }

}