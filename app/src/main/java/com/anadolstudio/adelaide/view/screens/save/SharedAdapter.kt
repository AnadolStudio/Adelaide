package com.anadolstudio.adelaide.view.screens.save

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.anadolstudio.adelaide.R
import com.anadolstudio.adelaide.databinding.ItemShareBinding
import com.anadolstudio.adelaide.domain.shareaction.SharedAction
import com.anadolstudio.adelaide.view.adapters.SimpleAdapter
import com.anadolstudio.core.adapters.AbstractViewHolder
import com.anadolstudio.core.adapters.ActionClick

class SharedAdapter(
    data: List<com.anadolstudio.adelaide.domain.shareaction.SharedAction.SharedItem>,
    detailable: ActionClick<SharedAction.SharedItem>?
) : SimpleAdapter<com.anadolstudio.adelaide.domain.shareaction.SharedAction.SharedItem>(data, detailable) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AbstractViewHolder.Base<com.anadolstudio.adelaide.domain.shareaction.SharedAction.SharedItem> = SharedViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_share, parent, false),
        detailable
    )

    private class SharedViewHolder(
        view: View,
        detailable: ActionClick<SharedAction.SharedItem>?
    ) : AbstractViewHolder.Base<com.anadolstudio.adelaide.domain.shareaction.SharedAction.SharedItem>(view, detailable) {

        val binding = ItemShareBinding.bind(view)

        override fun onBind(data: com.anadolstudio.adelaide.domain.shareaction.SharedAction.SharedItem) {
            binding.shareIcon.setImageDrawable(ContextCompat.getDrawable(itemView.context, data.drawable))
            super.onBind(data)
        }
    }
}