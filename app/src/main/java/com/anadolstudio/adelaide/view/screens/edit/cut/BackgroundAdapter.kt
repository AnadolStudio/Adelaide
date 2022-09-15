package com.anadolstudio.adelaide.view.screens.edit.cut

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.anadolstudio.adelaide.R
import com.anadolstudio.adelaide.databinding.ItemImageListBinding
import com.anadolstudio.adelaide.domain.utils.ImageLoader
import com.anadolstudio.adelaide.view.adapters.SelectableViewHolder
import com.anadolstudio.adelaide.view.adapters.SimpleSelectableAdapter
import com.anadolstudio.adelaide.view.screens.edit.cut.CutViewModel.Companion.CUSTOM
import com.anadolstudio.core.adapters.ActionClick
import com.anadolstudio.core.adapters.selectablecontroller.SelectableController

class BackgroundAdapter(
        data: MutableList<String>,
        detailable: ActionClick<String>?
) : SimpleSelectableAdapter<String>(data, detailable) {

    override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
    ): SelectableViewHolder<String> = BackgroundViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_image_list, parent, false),
            selectableController, detailable
    )

    class BackgroundViewHolder(
            view: View,
            controller: SelectableController<SelectableViewHolder<String>>,
            detailable: ActionClick<String>?
    ) : SelectableViewHolder<String>(view, detailable, controller) {

        private val binding = ItemImageListBinding.bind(itemView)

        override fun onBind(data: String, isSelected: Boolean) {

            with(binding) {
                if (data == CUSTOM) {
                    textView.setText(R.string.cut_func_own_background)
                    textView.visibility = View.VISIBLE
                    binding.imageView.setImageBitmap(null)
                } else {
                    textView.visibility = View.GONE
                    ImageLoader.loadImage(
                            binding.imageView, data, ImageLoader.ScaleType.CENTER_CROP, false
                    )
                }
            }
            super.onBind(data, isSelected)
        }

        override fun onClick(view: View) {
            if (data == CUSTOM) {
                data?.also { detailable?.action(it) }
                controller?.clear()
            } else super.onClick(view)
        }
    }
}
