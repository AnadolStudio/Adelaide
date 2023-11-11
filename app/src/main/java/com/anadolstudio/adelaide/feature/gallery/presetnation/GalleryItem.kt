package com.anadolstudio.adelaide.feature.gallery.presetnation

import android.view.View
import com.anadolstudio.adelaide.R
import com.anadolstudio.adelaide.base.adapter.BaseGroupItem
import com.anadolstudio.adelaide.databinding.ItemGalleryBinding
import com.anadolstudio.core.view.animation.AnimateUtil.scaleAnimationOnClick
import com.bumptech.glide.Glide

class GalleryItem(
        private val path: String,
        private val onClick: () -> Unit
) : BaseGroupItem<ItemGalleryBinding>(path.hashCode().toLong()) {

    override fun initializeViewBinding(view: View): ItemGalleryBinding = ItemGalleryBinding.bind(view)

    override fun getLayout(): Int = R.layout.item_gallery

    override fun bind(viewBinding: ItemGalleryBinding, item: BaseGroupItem<ItemGalleryBinding>) {
        Glide.with(viewBinding.imageView)
                .asBitmap()
                .centerCrop()
                .load(path)
                .into(viewBinding.imageView)
        viewBinding.cardView.scaleAnimationOnClick(action = onClick)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GalleryItem

        if (path != other.path) return false

        return true
    }

    override fun hashCode(): Int {
        return path.hashCode()
    }

}
