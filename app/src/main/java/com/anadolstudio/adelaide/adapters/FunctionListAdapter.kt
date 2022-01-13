package com.anadolstudio.adelaide.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.anadolstudio.adelaide.R
import com.anadolstudio.adelaide.databinding.ItemFunctionBinding
import com.anadolstudio.adelaide.helpers.FunctionItem
import com.anadolstudio.adelaide.interfaces.IDetailable

class FunctionListAdapter(
    val mList: List<FunctionItem>,
    val detailable: IDetailable<FunctionItem>
) :
    RecyclerView.Adapter<FunctionListAdapter.CropViewHolder>() {
    companion object {
        private val TAG: String = FunctionListAdapter::class.java.name
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CropViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_function, parent, false)
        return CropViewHolder(view)
    }

    override fun onBindViewHolder(holder: CropViewHolder, position: Int) {
        holder.onBind(mList[position])
    }

    override fun getItemCount(): Int = mList.size

    inner class CropViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView), View.OnClickListener {
        private val binding: ItemFunctionBinding = ItemFunctionBinding.bind(itemView)
        lateinit var functionItem: FunctionItem

        init {
            binding.constraintLayout.setOnClickListener(this)
        }

        fun onBind(functionItem: FunctionItem) {
            this.functionItem = functionItem

            binding.icon.setImageDrawable(
                functionItem.drawableId.let { ContextCompat.getDrawable(itemView.context, it) }
            )
            binding.text.setText(functionItem.textId)
        }


        override fun onClick(v: View) {
            detailable.toDetail(functionItem)
        }

    }

}