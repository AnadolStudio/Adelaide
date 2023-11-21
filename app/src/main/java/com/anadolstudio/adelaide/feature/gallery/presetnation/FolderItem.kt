package com.anadolstudio.adelaide.feature.gallery.presetnation

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.core.math.MathUtils
import com.anadolstudio.adelaide.R
import com.anadolstudio.adelaide.base.adapter.BaseGroupItem
import com.anadolstudio.adelaide.databinding.ItemFolderBinding
import com.anadolstudio.core.data_source.media.Folder
import com.anadolstudio.core.util.common.throttleClick
import com.anadolstudio.core.view.animation.AnimateUtil
import com.bumptech.glide.Glide
import com.xwray.groupie.Item
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class FolderItem(
        private val folder: Folder,
        private val isCurrent: Boolean,
        private val onClick: (Folder) -> Unit
) : BaseGroupItem<ItemFolderBinding>(folder.hashCode().toLong()) {

    private companion object {
        const val TITLE_CURRENT_ALPHA = 0.5F
        const val TITLE_DEFAULT_ALPHA = 1F
        const val OVERLAY_CURRENT_ALPHA = 1F
        const val OVERLAY_DEFAULT_ALPHA = 0F
    }

    override fun getId(): Long = folder.hashCode().toLong()

    override fun initializeViewBinding(view: View): ItemFolderBinding = ItemFolderBinding.bind(view)

    override fun getLayout(): Int = R.layout.item_folder

    @SuppressLint("ClickableViewAccessibility")
    override fun bind(binding: ItemFolderBinding, item: BaseGroupItem<ItemFolderBinding>) = with(binding) {
        if (item !is FolderItem) return@with

        root.throttleClick { onClick.invoke(item.folder) }

        cardView.isEnabled = !item.isCurrent

        Glide.with(imageView)
                .asBitmap()
                .centerCrop()
                .load(item.folder.thumbPath)
                .into(imageView)

        title.text = item.folder.name
        count.text = item.folder.imageCount.toString()
    }

    override fun onSimpleBinding(binding: ItemFolderBinding, item: BaseGroupItem<ItemFolderBinding>) {
        if (item !is FolderItem) return
        binding.overlay.alpha = if (item.isCurrent) OVERLAY_CURRENT_ALPHA else OVERLAY_DEFAULT_ALPHA
        binding.title.alpha = if (item.isCurrent) TITLE_CURRENT_ALPHA else TITLE_DEFAULT_ALPHA
        binding.count.alpha = if (item.isCurrent) TITLE_CURRENT_ALPHA else TITLE_DEFAULT_ALPHA
    }

    override fun onPayloadBinding(binding: ItemFolderBinding, item: BaseGroupItem<ItemFolderBinding>) {
        if (item !is FolderItem) return

        val overlayAlphaStart = if (item.isCurrent) OVERLAY_DEFAULT_ALPHA else OVERLAY_CURRENT_ALPHA
        val overlayAlphaEnd = if (item.isCurrent) OVERLAY_CURRENT_ALPHA else OVERLAY_DEFAULT_ALPHA
        val overlayAlphaDelta = abs(overlayAlphaStart - overlayAlphaEnd)

        val textAlphaStart = if (item.isCurrent) TITLE_DEFAULT_ALPHA else TITLE_CURRENT_ALPHA
        val textAlphaEnd = if (item.isCurrent) TITLE_CURRENT_ALPHA else TITLE_DEFAULT_ALPHA
        val textAlphaDelta = abs(textAlphaStart - textAlphaEnd)

        // TODO автоматизировать и вынести в утил
        ValueAnimator.ofFloat(0F, 1F).apply {
            interpolator = DecelerateInterpolator()
            duration = AnimateUtil.DURATION_LONG

            addUpdateListener { valueAnimator ->
                val value = valueAnimator.animatedValue as Float
                var direction = direction(overlayAlphaStart, overlayAlphaEnd)

                binding.overlay.alpha = MathUtils.clamp(
                        overlayAlphaStart + direction * value * overlayAlphaDelta,
                        min(overlayAlphaStart, overlayAlphaEnd),
                        max(overlayAlphaStart, overlayAlphaEnd)
                )

                direction = direction(textAlphaStart, textAlphaEnd)

                binding.title.alpha = MathUtils.clamp(
                        textAlphaStart + direction * value * textAlphaDelta,
                        min(textAlphaStart, textAlphaEnd),
                        max(textAlphaStart, textAlphaEnd)
                )

                binding.count.alpha = MathUtils.clamp(
                        textAlphaStart + direction * value * textAlphaDelta,
                        min(textAlphaStart, textAlphaEnd),
                        max(textAlphaStart, textAlphaEnd)
                )
            }
            start()
        }
    }

    private fun direction(start: Float, end: Float): Float = if (start > end) -1F else 1F

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
