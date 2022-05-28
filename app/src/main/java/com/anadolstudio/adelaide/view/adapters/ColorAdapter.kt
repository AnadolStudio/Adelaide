package com.anadolstudio.adelaide.view.adapters

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.anadolstudio.adelaide.R
import com.anadolstudio.adelaide.databinding.ItemImageListBinding
import com.anadolstudio.adelaide.domain.utils.Colors
import com.anadolstudio.core.adapters.selectablecontroller.SelectableController
import com.anadolstudio.core.adapters.ActionClick

class ColorAdapter(data: MutableList<String>, detailable: ActionClick<String>?) :
    SimpleSelectableAdapter<String>(data, detailable) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SelectableViewHolder<String> = ColorPathViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_image_list, parent, false),
        selectableController, detailable
    )

    override fun onBindViewHolder(holder: SelectableViewHolder<String>, position: Int) {
        if (dataList[position] == Colors.COLOR_DEFAULT && !selectableController.selectableItemIsExist()) {
            selectableController.setStartItem(holder)
        }
        super.onBindViewHolder(holder, position)

    }

    private class ColorPathViewHolder(
        view: View,
        controller: SelectableController<SelectableViewHolder<String>>,
        detailable: ActionClick<String>?
    ) : SelectableViewHolder<String>(view, detailable, controller) {

        private val binding: ItemImageListBinding = ItemImageListBinding.bind(itemView)

        init {
            binding.supportImageView.visibility = View.VISIBLE
        }

        override fun onBind(data: String, isSelected: Boolean) {
            binding.supportImageView.setImageDrawable(ColorDrawable(Color.parseColor(data)))
            super.onBind(data, isSelected)
        }
    }
}

