package com.anadolstudio.core.adapters

import androidx.recyclerview.widget.RecyclerView
import com.anadolstudio.core.interfaces.IDetailable
import com.anadolstudio.core.view.selectablecontroller.SelectableController

abstract class AbstractAdapter<Data, Holder : AbstractViewHolder<Data>>(
    protected var dataList: MutableList<Data> = mutableListOf(),
    protected val detailable: IDetailable<Data>
) : RecyclerView.Adapter<Holder>() {

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.onBind(dataList[position])
    }

    open fun setData(list: MutableList<Data>) {
        dataList = list
        notifyDataSetChanged()
    }

    open fun addData(list: List<Data>) {
        dataList.addAll(list)
        notifyItemRangeInserted(dataList.size - list.size, list.size)
    }

    override fun getItemCount(): Int = dataList.size

//    fun toDetail(e: Data) = detailable.toDetail(e)

    abstract class Selectable<Data, Holder : AbstractViewHolder<Data>>(
        dataList: MutableList<Data> = mutableListOf(),
        detailable: IDetailable<Data>
    ) : AbstractAdapter<Data, Holder>(dataList, detailable) {

        protected var selectableMode = true
            set(value) {
                field = value
                notifyDataSetChanged()
            }

        protected val selectableController: SelectableController.Abstract<Holder> =
            object : SelectableController.Abstract<Holder>() {
                override fun updateView(t: Holder, isSelected: Boolean, state: Int) = notifyItemChanged(state)

                override fun saveState(t: Holder): Int = t.adapterPosition
            }

        override fun onBindViewHolder(holder: Holder, position: Int) {
            holder.onBind(dataList[position], position == selectableController.getState(), selectableMode)
        }

        open fun clearSelectedItem() = selectableController.clear()

        override fun getItemCount(): Int = dataList.size
    }
}