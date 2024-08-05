package com.anadolstudio.adelaide.feature.gallery.presetnation

import android.view.View
import com.anadolstudio.adelaide.R
import com.anadolstudio.adelaide.databinding.ItemGalleryBinding
import com.anadolstudio.ui.adapters.groupie.BaseGroupItem
import com.anadolstudio.utils.animation.AnimateUtil.scaleAnimationOnClick
import com.anadolstudio.utils.data_source.media.Image
import com.bumptech.glide.Glide

class GalleryItem(
        private val image: Image,
        private val onLongClick: (View, Image) -> Unit,
        private val onClick: (View, Image) -> Unit
) : BaseGroupItem<ItemGalleryBinding>(image.hashCode().toLong(), R.layout.item_gallery) {

    private companion object {
        const val SCALE = 1.1F
        const val DEFAULT_ELEVATION = 0F
        const val SELECT_ELEVATION = 10F
        const val LONG_ACTION_DELAY = 600L
    }

    override fun initializeViewBinding(view: View): ItemGalleryBinding = ItemGalleryBinding.bind(view)

    override fun bind(binding: ItemGalleryBinding, item: BaseGroupItem<ItemGalleryBinding>) {
        Glide.with(binding.imageView)
                .asBitmap()
                .centerCrop()
                .load(image.path)
                .into(binding.imageView)

        binding.root.scaleAnimationOnClick(
                onTouchScale = SCALE,
                actionDown = { binding.root.elevation = SELECT_ELEVATION },
                onAnimationEnd = { binding.root.elevation = DEFAULT_ELEVATION },
                longPressDelayMillis = LONG_ACTION_DELAY,
                onLongPress = { onLongClick.invoke(binding.root, image) },
                actionUp = { onClick.invoke(binding.root, image) }
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GalleryItem

        return image == other.image
    }

    override fun hashCode(): Int = image.hashCode()

}
