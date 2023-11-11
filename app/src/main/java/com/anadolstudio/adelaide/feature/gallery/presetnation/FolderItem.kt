package com.anadolstudio.adelaide.feature.gallery.presetnation

import android.annotation.SuppressLint
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.core.view.isVisible
import com.anadolstudio.adelaide.R
import com.anadolstudio.adelaide.base.adapter.BaseGroupItem
import com.anadolstudio.adelaide.databinding.ItemFolderBinding
import com.anadolstudio.core.data_source.media.Folder
import com.anadolstudio.core.view.animation.AnimateUtil
import com.anadolstudio.core.view.animation.AnimateUtil.scaleAnimationOnClick
import com.anadolstudio.core.view.animation.AnimateUtil.scaleOnTouch
import com.bumptech.glide.Glide
import com.xwray.groupie.Item

class FolderItem(
        private val folder: Folder,
        private val isCurrent: Boolean,
        private val onClick: (Folder) -> Unit
) : BaseGroupItem<ItemFolderBinding>(folder.hashCode().toLong()) {

    private companion object {
        const val CURRENT_SCALE = 1F
        const val NOT_CURRENT_SCALE = 0.8F
        const val TITLE_CURRENT_ALPHA = 1F
        const val TITLE_NOT_CURRENT_ALPHA = 0.5F
    }

    override fun getId(): Long = folder.hashCode().toLong()

    override fun initializeViewBinding(view: View): ItemFolderBinding = ItemFolderBinding.bind(view).apply {
        val scale = if (isCurrent) CURRENT_SCALE else NOT_CURRENT_SCALE
        cardView.scaleX = scale
        cardView.scaleY = scale
    }

    override fun getLayout(): Int = R.layout.item_folder

    @SuppressLint("ClickableViewAccessibility")
    override fun bind(viewBinding: ItemFolderBinding, item: BaseGroupItem<ItemFolderBinding>) = with(viewBinding) {
        if (item !is FolderItem) return@with

        cardView.scaleAnimationOnClick(
                onTouchScale = CURRENT_SCALE,
                defaultScale = NOT_CURRENT_SCALE,
                action = { onClick.invoke(folder) }
        )

        root.setOnClickListener { onClick.invoke(folder) }
        title.setOnTouchListener { _, event ->
            cardView.scaleOnTouch(
                    event = event,
                    onTouchScale = CURRENT_SCALE,
                    defaultScale = NOT_CURRENT_SCALE,
                    action = { onClick.invoke(folder) }
            )
            true
        }

        cardView.isEnabled = !item.isCurrent
        title.isEnabled = !item.isCurrent

        val scale = if (isCurrent) CURRENT_SCALE else NOT_CURRENT_SCALE

        cardView.animate().cancel()
        cardView.animate()
                .scaleX(scale)
                .scaleY(scale)
                .setDuration(AnimateUtil.DURATION_EXTRA_SHORT)
                .setInterpolator(DecelerateInterpolator())
                .start()

        Glide.with(imageView)
                .asBitmap()
                .centerCrop()
                .load(item.folder.thumbPath)
                .into(imageView)

        overlay.isVisible = !item.isCurrent
        title.text = item.folder.name
        title.alpha = if (item.isCurrent) TITLE_CURRENT_ALPHA else TITLE_NOT_CURRENT_ALPHA
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FolderItem

        if (folder != other.folder) return false
        if (isCurrent != other.isCurrent) return false

        return true
    }

    override fun hashCode(): Int {
        var result = folder.hashCode()
        result = 31 * result + isCurrent.hashCode()
        return result
    }

    override fun getChangePayload(newItem: Item<*>): Any = newItem

}
