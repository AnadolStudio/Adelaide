package com.anadolstudio.adelaide.view.screens.edit.crop

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.anadolstudio.adelaide.R
import com.anadolstudio.adelaide.databinding.ItemCropBinding
import com.anadolstudio.adelaide.domain.editphotoprocessor.RatioItem
import com.anadolstudio.adelaide.domain.utils.BitmapHelper
import com.anadolstudio.adelaide.view.adapters.SimpleSelectableAdapter
import com.anadolstudio.adelaide.view.adapters.SimpleViewHolder
import com.anadolstudio.core.interfaces.IDetailable
import com.anadolstudio.core.view.selectablecontroller.SelectableController

class CropListAdapter(
    data: List<RatioItem>,
    detailable: IDetailable<RatioItem>,
) : SimpleSelectableAdapter<RatioItem>(data, detailable) {

    override fun getViewHolder(parent: ViewGroup, viewType: Int): SimpleViewHolder<RatioItem> {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_crop, parent, false)
        return CropViewHolder(view, selectableController, detailable)
    }

    override fun onBindViewHolder(holder: SimpleViewHolder<RatioItem>, position: Int) {
        if (dataList[position] == RatioItem.FREE && !selectableController.selectableItemIsExist()) {
            selectableController.setStartItem(holder)
        }
        super.onBindViewHolder(holder, position)
    }

    inner class CropViewHolder(
        itemView: View,
        controller: SelectableController.Abstract<SimpleViewHolder<RatioItem>>,
        detailable: IDetailable<RatioItem>
    ) : SimpleViewHolder.Selectable<RatioItem>(itemView, controller, detailable), View.OnClickListener {

        private val binding: ItemCropBinding = ItemCropBinding.bind(itemView)

        override fun onBind(data: RatioItem, isSelected: Boolean, selectableMode: Boolean) {
            super.onBind(data, isSelected, selectableMode)

            binding.iconContainer.layoutParams.width =
                BitmapHelper.dpToPx(itemView.context, data.density.w)

            binding.iconContainer.layoutParams.height =
                BitmapHelper.dpToPx(itemView.context, data.density.h)
            binding.iconContainer.requestLayout()

            binding.icon.setImageDrawable(
                data.drawableId?.let { ContextCompat.getDrawable(itemView.context, it) }
            )

            binding.text.setText(data.textId)
        }

        override fun getSelectableView(): View = binding.cardView

        override fun getSelectableColor(isSelected: Boolean): Int =
            ContextCompat.getColor(
                itemView.context,
                if (isSelected) R.color.colorSecondary else R.color.colorSecondaryInverse
            )

        override fun onBind(isSelected: Boolean) {
            super.onBind(isSelected)
            binding.icon.setColorFilter(
                itemView.context.getColor(if (isSelected) R.color.colorAccent else R.color.colorAccentInverse)
            )
        }

    }
}