package com.anadolstudio.adelaide.feature.gallery.presetnation

import android.view.View
import com.anadolstudio.adelaide.R
import com.anadolstudio.adelaide.databinding.ItemGalleryBinding
import com.anadolstudio.core.view.animation.AnimateUtil.scaleAnimationOnClick
import com.bumptech.glide.Glide
import com.xwray.groupie.Item
import com.xwray.groupie.viewbinding.BindableItem

class GalleryItem(
        private val path: String,
        private val onClick: () -> Unit
) : BindableItem<ItemGalleryBinding>(path.hashCode().toLong()) {

    override fun initializeViewBinding(view: View): ItemGalleryBinding = ItemGalleryBinding.bind(view).apply {
        cardView.scaleAnimationOnClick(onClick)
    }

    override fun getLayout(): Int = R.layout.item_gallery

    override fun bind(viewBinding: ItemGalleryBinding, position: Int) {
        Glide.with(viewBinding.imageView)
                .asBitmap()
                .centerCrop()
                .load(path)
                .into(viewBinding.imageView)
    }

    override fun hasSameContentAs(other: Item<*>): Boolean = this.id == other.id

}
