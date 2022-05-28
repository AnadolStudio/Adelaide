package com.anadolstudio.adelaide.view.screens.edit.effect

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.anadolstudio.adelaide.R
import com.anadolstudio.adelaide.databinding.ItemImageListBinding
import com.anadolstudio.adelaide.domain.utils.ImageLoader
import com.anadolstudio.adelaide.view.adapters.SelectableViewHolder
import com.anadolstudio.adelaide.view.adapters.SimpleSelectableAdapter
import com.anadolstudio.core.adapters.selectablecontroller.SelectableController
import com.anadolstudio.core.adapters.ActionClick

class EffectAdapter(
    private val thumbnail: Bitmap, data: List<String>,
    detailable: ActionClick<String>?
) : SimpleSelectableAdapter<String>(data, detailable) {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SelectableViewHolder<String> = EffectPathViewHolder(
        thumbnail,
        LayoutInflater.from(parent.context).inflate(R.layout.item_image_list, parent, false),
        selectableController,
        detailable
    )

    inner class EffectPathViewHolder(
        thumbnail: Bitmap,
        view: View,
        controller: SelectableController<SelectableViewHolder<String>>,
        detailable: ActionClick<String>?
    ) : SelectableViewHolder<String>(view, detailable, controller) {

        private val binding: ItemImageListBinding = ItemImageListBinding.bind(view)

        init {
            binding.imageView.scaleType = ImageView.ScaleType.CENTER_CROP
            binding.imageView.setImageBitmap(thumbnail)
            binding.supportImageView.visibility = View.VISIBLE
        }

        override fun onBind(data: String, isSelected: Boolean) {
            super.onBind(data, isSelected)
            ImageLoader.loadImage(
                binding.supportImageView,
                data,
                ImageLoader.ScaleType.CENTER_CROP,
                false
            )
        }
    }
}