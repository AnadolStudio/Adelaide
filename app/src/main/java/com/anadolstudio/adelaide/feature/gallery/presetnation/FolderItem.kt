package com.anadolstudio.adelaide.feature.gallery.presetnation

import android.view.View
import androidx.core.view.isVisible
import com.anadolstudio.adelaide.R
import com.anadolstudio.adelaide.base.adapter.BaseGroupItem
import com.anadolstudio.adelaide.databinding.ItemFolderBinding
import com.anadolstudio.core.data_source.media.Folder
import com.anadolstudio.core.view.animation.AnimateUtil.scaleAnimationOnClick
import com.bumptech.glide.Glide

class FolderItem(
        private val folder: Folder,
        private val isCurrent: Boolean,
        private val onClick: (Folder) -> Unit
) : BaseGroupItem<ItemFolderBinding>(folder.hashCode().toLong() + isCurrent.hashCode()) {

    private companion object {
        const val CURRENT_SCALE = 1F
        const val NOT_CURRENT_SCALE = 0.8F
        const val TITLE_CURRENT_ALPHA = 1F
        const val TITLE_NOT_CURRENT_ALPHA = 0.5F
    }

    override fun initializeViewBinding(view: View): ItemFolderBinding = ItemFolderBinding.bind(view)

    override fun getLayout(): Int = R.layout.item_folder

    override fun bind(viewBinding: ItemFolderBinding, position: Int) = with(viewBinding) {
        cardView.scaleAnimationOnClick(
                scale = CURRENT_SCALE,
                scaleDefault = NOT_CURRENT_SCALE
        ) { onClick.invoke(folder) }

        cardView.scaleX = if (isCurrent) CURRENT_SCALE else NOT_CURRENT_SCALE
        cardView.scaleY = if (isCurrent) CURRENT_SCALE else NOT_CURRENT_SCALE
        cardView.isEnabled = !isCurrent

        Glide.with(imageView)
                .asBitmap()
                .centerCrop()
                .load(folder.thumbPath)
                .into(imageView)

        overlay.isVisible = !isCurrent
        title.text = folder.name
        title.alpha = if (isCurrent) TITLE_CURRENT_ALPHA else TITLE_NOT_CURRENT_ALPHA
    }
}
