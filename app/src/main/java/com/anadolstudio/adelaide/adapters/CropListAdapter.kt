package com.anadolstudio.adelaide.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.anadolstudio.adelaide.R
import com.anadolstudio.adelaide.databinding.ItemCropBinding
import com.anadolstudio.adelaide.helpers.BitmapHelper
import com.anadolstudio.adelaide.helpers.RatioItem
import com.anadolstudio.adelaide.helpers.SelectableAdapterController
import com.anadolstudio.adelaide.interfaces.IDetailable

class CropListAdapter(
    val detailable: IDetailable<RatioItem>,
    var selectedRatioItem: RatioItem = RatioItem.FREE
) :
    RecyclerView.Adapter<CropListAdapter.CropViewHolder>() {
    companion object {
        private val TAG: String = CropListAdapter::class.java.name
    }

    private var mList: List<RatioItem> = RatioItem.values().toList()
    private val selectableController: SelectableAdapterController<CropViewHolder> =
        object : SelectableAdapterController<CropViewHolder>() {

            override fun updateView(state: Int) {
                notifyItemChanged(state)
            }

            override fun saveState(cropViewHolder: CropViewHolder): Int {
                return cropViewHolder.adapterPosition
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CropViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_crop, parent, false)
        return CropViewHolder(view)
    }

    override fun onBindViewHolder(holder: CropViewHolder, position: Int) {
        Log.d(TAG, "onBindViewHolder: $position")

        if (mList[position] == selectedRatioItem && !selectableController.selectableItemIsExist()) {
            selectableController.setStartItem(holder)
        }

        holder.onBind(mList[position], position == selectableController.state)
    }

    override fun getItemCount(): Int = mList.size

    inner class CropViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView), View.OnClickListener {
        private val binding: ItemCropBinding = ItemCropBinding.bind(itemView)
        lateinit var ratioItem: RatioItem

        init {
            binding.constraintLayout.setOnClickListener(this)
        }

        fun onBind(ratioItem: RatioItem, isSelected: Boolean) {
            Log.d(TAG, "onBind: $isSelected + $adapterPosition")
            this.ratioItem = ratioItem
            onBind(selectableController.state == adapterPosition)

            binding.iconContainer.layoutParams.width =
                BitmapHelper.dpToPx(itemView.context, ratioItem.density.w)
            binding.iconContainer.layoutParams.height =
                BitmapHelper.dpToPx(itemView.context, ratioItem.density.h)
            binding.iconContainer.requestLayout()

            binding.icon.setImageDrawable(
                ratioItem.drawableId?.let { ContextCompat.getDrawable(itemView.context, it) }
            )
            binding.text.setText(ratioItem.textId)
        }

        fun onBind(isSelected: Boolean) {
            binding.cardView.setCardBackgroundColor(
                itemView.context.getColor(
                    if (isSelected) R.color.colorSecondaryInverse else R.color.colorSecondary
                )
            )
            binding.icon.setColorFilter(
                itemView.context.getColor(
                    if (isSelected) R.color.colorAccentInverse else R.color.colorAccent
                )
            )
        }

        override fun onClick(v: View) {
            if (!selectableController.selectableItemIsExist()
                || selectableController.state != this.adapterPosition
            ) {
                selectableController.setCurrentSelectedItem(this)
                detailable.toDetail(ratioItem)
            }
        }

    }

}