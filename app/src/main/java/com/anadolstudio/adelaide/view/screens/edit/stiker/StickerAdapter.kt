package com.anadolstudio.adelaide.view.screens.edit.stiker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.anadolstudio.adelaide.R
import com.anadolstudio.adelaide.databinding.ItemStickerBinding
import com.anadolstudio.adelaide.domain.utils.ImageLoader
import com.anadolstudio.core.adapters.AbstractAdapter
import com.anadolstudio.core.adapters.AbstractViewHolder
import com.anadolstudio.core.adapters.ActionClick

class StickerAdapter(data: MutableList<String>, detailable: ActionClick<String>) :
        AbstractAdapter.Base<String, StickerAdapter.StickerViewHolder>(data, detailable) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = StickerViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_sticker, parent, false),
            detailable
    )

    class StickerViewHolder(
            view: View,
            detailable: ActionClick<String>?
    ) : AbstractViewHolder.Base<String>(view, detailable) {

        private val binding: ItemStickerBinding = ItemStickerBinding.bind(itemView)

        override fun onBind(data: String) {
            ImageLoader.loadImage(binding.imageView, data, ImageLoader.ScaleType.FIT_CENTER, false)
            super.onBind(data)
        }
    }
}
